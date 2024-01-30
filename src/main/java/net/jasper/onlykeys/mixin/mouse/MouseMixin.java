package net.jasper.onlykeys.mixin.mouse;

import net.jasper.onlykeys.mod.keymovements.InventoryMovement;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method="onCursorPos", at=@At("HEAD"), cancellable = true)
    private void injected(long window, double x, double y, CallbackInfo ci) {
        if (InventoryMovement.cancelMouse) {
            ci.cancel();
        }
    }
}
