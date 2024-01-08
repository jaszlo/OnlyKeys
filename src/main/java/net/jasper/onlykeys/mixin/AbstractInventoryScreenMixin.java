package net.jasper.onlykeys.mixin;

import net.jasper.onlykeys.mod.keymovements.InventoryMovement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractInventoryScreen.class)
public class AbstractInventoryScreenMixin {

    @Unique
    private static final Identifier HOTBAR_SELECTION_TEXTURE = new Identifier("hud/hotbar_selection");

    @Inject(method="render", at=@At("RETURN"))
    private void injected(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // Render a rectangle around the selected slot in the inventory
        // It should be the same as the one in the hotbar but in a different color

        int[] XY = InventoryMovement.getXYForSlot();
        if (XY == null) {
            return;
        }

        // -1 to account for the border
        int x = ((HandledScreenMixin) this).getX() - 1;
        int y = ((HandledScreenMixin) this).getY() - 1;
        // Slot Index (in the player inventory)
        // 0    = craftingResult
        // 1-4  = craftingGrid
        // 5-8  = armor
        // 9-35 = inventory
        // 36   = offhand
        // 37-44= hotbar
        // Slot Offset
        x += XY[0];
        y += XY[1];
        context.getMatrices().push();
        context.drawGuiTexture(HOTBAR_SELECTION_TEXTURE,  x, y, 18, 18);
        context.getMatrices().pop();
    }
}
