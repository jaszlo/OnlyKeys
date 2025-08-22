package net.jasper.onlykeys.mixin.screens;

import net.jasper.onlykeys.mixin.accessors.HandledScreenAccessors;
import net.jasper.onlykeys.mod.OnlyKeysModClient;
import net.jasper.onlykeys.mod.keymovements.InventoryMovement;
import net.jasper.onlykeys.mod.util.ScreenOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

import static net.jasper.onlykeys.mod.util.Keys.ONLYKEYS_KEYBINDINGS;

@Mixin(Screen.class)
public class ScreenAdditions {
    @Shadow @Nullable protected MinecraftClient client;

    @Unique
    private static final Identifier HOTBAR_SELECTION_TEXTURE = Identifier.of("hud/hotbar_selection");

    @Inject(method="render", at=@At("RETURN"))
    private void inventoryOverlay(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // Leave if mod is not enabled
        if (!OnlyKeysModClient.onlyKeysEnabled) {
            return;
        }

        // Render a rectangle around the selected slot in the inventory
        // It should be the same as the one in the hotbar but in a different color
        assert client != null;
        assert client.currentScreen != null;
        if (client.player == null || !ScreenOverlay.isAllowedScreen(this)) {
            return;
        }

        int[] XY = InventoryMovement.getXYForSlot();
        if (XY == null) {
            return;
        }
        // -1 to account for the border
        HandledScreenAccessors mixin = (HandledScreenAccessors) client.currentScreen;
        int x = mixin.getX() - 1;
        int y = mixin.getY() - 1;

        // Slot Offset
        x += XY[0];
        y += XY[1];
        int finalY = y;
        int finalX = x;
        MinecraftClient.getInstance().execute(() -> {
            context.getMatrices().pushMatrix();
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HOTBAR_SELECTION_TEXTURE, finalX, finalY, 18, 18);
            context.getMatrices().popMatrix();
        });

        MinecraftClient client = MinecraftClient.getInstance();
        // Disable textField and do not draw numbers
        assert client.player != null;
        if (!ScreenOverlay.open) {
            ScreenOverlay.slotEntryField.setVisible(false);
            return;
        }

        // Draw indices
        int globalSlotIndex = 0;
        for (Slot s : client.player.currentScreenHandler.slots) {
            context.drawText(
                    client.textRenderer,
                    String.valueOf(globalSlotIndex++),
                    s.x + mixin.getX() + 1,
                    s.y + mixin.getY() + 1,
                    0xFFFFFFFF, // Newer versions require alpha to be specified
                    true
            );
        }

        // Draw textField
        ScreenOverlay.slotEntryField.setVisible(true);
    }


    @Inject(method="keyPressed", at=@At("HEAD"), cancellable = true)
    private void quickSlotSelection(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (client == null || client.player == null || !ScreenOverlay.isAllowedScreen(this)) {
            return;
        }

        // Cancel normal keypress handling if the key is one of the onlykeys keybindings was used
        if (Arrays.stream(ONLYKEYS_KEYBINDINGS).anyMatch(keyBinding -> keyBinding.matchesKey(keyCode, scanCode))) {
            OnlyKeysModClient.LOGGER.info("Cancelling keypress");
            cir.setReturnValue(true);
            cir.cancel();
        } else {
            char c = (char)keyCode;
            TextFieldWidget slotField = ScreenOverlay.slotEntryField;
            // Write number to textField
            if (slotField.isVisible() && Character.isDigit(c)) {
                slotField.write(c + "");
            // Delete number
            } else if (c == GLFW.GLFW_KEY_BACKSPACE) {
                slotField.eraseCharactersTo(slotField.getCursor() - 1);
            // Set selected Slot
            } else if (c == GLFW.GLFW_KEY_ENTER && !slotField.getText().isEmpty()) {
                ScreenOverlay.toggle();
                int slotToSelect = Integer.parseInt(slotField.getText());
                slotField.setText("");
                if (0 <= slotToSelect && slotToSelect < client.player.currentScreenHandler.slots.size()) {
                    InventoryMovement.selectedSlot = slotToSelect;
                }
            }
        }
    }
}
