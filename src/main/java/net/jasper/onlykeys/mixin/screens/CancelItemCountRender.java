package net.jasper.onlykeys.mixin.screens;


import net.jasper.onlykeys.mod.util.ScreenOverlay;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(HandledScreen.class)
public class CancelItemCountRender {
    @ModifyVariable(method="drawSlot", at=@At("STORE"), ordinal=0)
    private ItemStack changeStackInSlot(ItemStack itemStack) {
        if (ScreenOverlay.open) {
            ItemStack copy = itemStack.copy();
            copy.setCount(1);
            return copy;
        }
        return itemStack;
    }
}
