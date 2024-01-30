package net.jasper.onlykeys.mod.keymovements;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.jasper.onlykeys.mixin.KeyBindingAccessors;
import net.jasper.onlykeys.mod.util.Keys;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import static net.jasper.onlykeys.mod.util.Keys.*;

public class MouseMovement {

    private static final float MINIMUM_SENSITIVITY = 0.25f;

    private static boolean keyMouseLeft = false;
    private static boolean keyMouseWheel = false;
    private static boolean keyMouseRight = false;
    public static void register() {
        WorldRenderEvents.START.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            // If any screen is open do nothing
            if (client.currentScreen != null) {
                return;
            }

            assert client.player != null;
            assert client.interactionManager != null;

            // Clearing Keys if they were pressed via OnlyKeys
            if (keyMouseLeft) Keys.clear(client.options.attackKey);
            if (keyMouseWheel) Keys.clear(client.options.pickItemKey);
            if (keyMouseRight) Keys.clear(client.options.useKey);


            long handle = client.getWindow().getHandle();
            float sensitivity = client.options.getMouseSensitivity().getValue().floatValue() * 2 + MINIMUM_SENSITIVITY;

            // Moving Camera
            int upCode = ((KeyBindingAccessors) cameraUp).getBoundKey().getCode();
            int downCode = ((KeyBindingAccessors) cameraDown).getBoundKey().getCode();
            int leftCode = ((KeyBindingAccessors) cameraLeft).getBoundKey().getCode();
            int rightCode = ((KeyBindingAccessors) cameraRight).getBoundKey().getCode();


            if (InputUtil.isKeyPressed(handle, upCode)) client.player.setPitch(client.player.getPitch() - sensitivity);
            if (InputUtil.isKeyPressed(handle, downCode)) client.player.setPitch(client.player.getPitch() + sensitivity);
            if (InputUtil.isKeyPressed(handle, leftCode)) client.player.setYaw(client.player.getYaw() - sensitivity);
            if (InputUtil.isKeyPressed(handle, rightCode)) client.player.setYaw(client.player.getYaw() + sensitivity);

            // Clicking Mouse
            int leftClickCode = ((KeyBindingAccessors) leftClick).getBoundKey().getCode();
            int wheelClickCode = ((KeyBindingAccessors) wheelClick).getBoundKey().getCode();
            int rightClickCode = ((KeyBindingAccessors) rightClick).getBoundKey().getCode();


            if (InputUtil.isKeyPressed(handle, leftClickCode)) {
                client.player.swingHand(Hand.MAIN_HAND);
                client.options.attackKey.setPressed(true);
                // Check if the player is attacking an entity
                if (client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                    EntityHitResult hitResult = (EntityHitResult) client.crosshairTarget;
                    client.interactionManager.attackEntity(client.player, hitResult.getEntity());
                }
                keyMouseLeft = true;
            } else {
                keyMouseLeft = false;
            }

            if (InputUtil.isKeyPressed(handle, wheelClickCode)) {
                client.options.pickItemKey.setPressed(true);
                keyMouseWheel = true;
            } else {
                keyMouseWheel = false;
            }

            if (InputUtil.isKeyPressed(handle, rightClickCode)) {
                client.options.useKey.setPressed(true);
                keyMouseRight = true;
            } else {
                keyMouseRight = false;
            }
        });
    }

}
