package net.jasper.onlykeys.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

import static net.jasper.onlykeys.mod.util.Keys.ONLYKEYS_KEYBINDINGS;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(method="keyPressed", at=@At("HEAD"), cancellable = true)
    private void injected(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        // Cancel normal keypress handling if the key is one of the onlykeys keybindings was used
        if (((Screen)(Object)this) instanceof AbstractInventoryScreen<?> currentScreen) {
            if (Arrays.stream(ONLYKEYS_KEYBINDINGS).anyMatch(keyBinding -> keyBinding.matchesKey(keyCode, scanCode))) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }
}
