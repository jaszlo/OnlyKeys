package net.jasper.onlykeys.mixin.screens;

import net.jasper.onlykeys.mixin.accessors.ScreenAccessor;
import net.jasper.onlykeys.mod.keymovements.InventoryMovement;
import net.jasper.onlykeys.mod.util.ScreenOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class QuickSlotTextFieldCreator {
    @Inject(method="close", at=@At("HEAD"))
    private void setLastSelectedSlot(CallbackInfo ci) {
        ScreenOverlay.screenSlotMapping.put(this.getClass(), InventoryMovement.selectedSlot);
    }

    @Inject(method="init", at=@At("RETURN"))
    private void injected(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.currentScreen != null;
        if (!ScreenOverlay.isAllowedScreen(this)) {
            return;
        }
        // When creating a new allowed screen set selected slot to 0 or the last selected slot which is set on close
        InventoryMovement.selectedSlot = ScreenOverlay.screenSlotMapping.getOrDefault(this.getClass(), 0);

        // Also reset cooldown to prevent from clicking slot instantly
        InventoryMovement.resetClickCooldown();

        if (client.player == null) {
            return;
        }

        int width = 32;
        int height = 12;
        // Edge cases for different screens
        int yDiff = 96;
        if (client.currentScreen instanceof CreativeInventoryScreen) {
            yDiff = 108;
        } else if (client.currentScreen.getTitle().toString().contains("chestDouble")) {
            yDiff = 124;
        }
        ScreenOverlay.slotEntryField = new TextFieldWidget(
                client.textRenderer,
                client.getWindow().getScaledWidth() / 2 - width / 2,
                client.getWindow().getScaledHeight() / 2  - yDiff,
                width,
                height,
                Text.of("")
        );
        ScreenOverlay.slotEntryField.setEditable(true);
        ScreenOverlay.slotEntryField.active = true;
        ScreenOverlay.slotEntryField.setFocused(true);
        ScreenOverlay.slotEntryField.setMaxLength(2);
        ScreenOverlay.slotEntryField.setPlaceholder(Text.of("Enter Slot ID"));
        ((ScreenAccessor) this).addDrawableChildInvoker(ScreenOverlay.slotEntryField);
    }
}
