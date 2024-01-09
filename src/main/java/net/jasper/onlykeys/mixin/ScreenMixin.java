package net.jasper.onlykeys.mixin;

import net.jasper.onlykeys.mod.OnlyKeysModClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

import static net.jasper.onlykeys.mod.util.Keys.ONLYKEYS_KEYBINDINGS;

@Mixin(Screen.class)
public class ScreenMixin {
    @Shadow @Nullable protected MinecraftClient client;

    @Inject(method="keyPressed", at=@At("HEAD"), cancellable = true)
    private void injected(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        // Cancel normal keypress handling if the key is one of the onlykeys keybindings was used
        if (((Screen)(Object)this) instanceof AbstractInventoryScreen) {
            if (Arrays.stream(ONLYKEYS_KEYBINDINGS).anyMatch(keyBinding -> keyBinding.matchesKey(keyCode, scanCode))) {
                OnlyKeysModClient.LOGGER.info("Cancelling keypress");
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }
}
