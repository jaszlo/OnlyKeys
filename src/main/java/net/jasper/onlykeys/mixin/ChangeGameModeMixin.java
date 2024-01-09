package net.jasper.onlykeys.mixin;

import net.jasper.onlykeys.mod.keymovements.InventoryMovement;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ChangeGameModeMixin {

    @Inject(method = "setGameMode", at = @At("HEAD"))
    private void injected(GameMode gameMode, CallbackInfo ci) {
        // Update selected slot as the slotIndex changes when switching gameModes.
        // Else this can crash the game as the selectedSlot is out of bounds.
        // Just reset to first hotbar slot by default for now
        InventoryMovement.selectedSlot = 36;
    }
}
