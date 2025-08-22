package net.jasper.onlykeys.mod.keymovements;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.jasper.onlykeys.mixin.accessors.KeyBindingAccessors;
import net.jasper.onlykeys.mod.OnlyKeysModClient;
import net.jasper.onlykeys.mod.util.Keys;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import static net.jasper.onlykeys.mod.util.Keys.*;

public class PlayerMovement {

    private static final float MINIMUM_SENSITIVITY = 0.25f;

    private static boolean keyMouseLeft = false;
    private static boolean keyMouseWheel = false;
    private static boolean keyMouseRight = false;
    public static void register() {
        WorldRenderEvents.START.register(context -> {
            // Leave if mod is not enabled
            if (!OnlyKeysModClient.onlyKeysEnabled) {
                return;
            }

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

            // LEFT
            boolean pressedLeft = InputUtil.isKeyPressed(handle, leftClickCode);

            // FIXME: I suspect #6 has something to do with this bit,
            // if it hasn't already been fixed
            if (pressedLeft) client.player.swingHand(Hand.MAIN_HAND);
            else if (client.player.handSwinging) client.interactionManager.cancelBlockBreaking();
            client.options.attackKey.setPressed(pressedLeft);
            keyMouseLeft = pressedLeft;
            // Check if the player is attacking an entity
            if (pressedLeft && client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                EntityHitResult hitResult = (EntityHitResult) client.crosshairTarget;
                client.interactionManager.attackEntity(client.player, hitResult.getEntity());
            }

            // WHEEL
            boolean pressedWheel = InputUtil.isKeyPressed(handle, wheelClickCode);
            client.options.pickItemKey.setPressed(pressedWheel);
            keyMouseWheel = pressedWheel;

            // RIGHT
            boolean pressedRight = InputUtil.isKeyPressed(handle, rightClickCode);
            client.options.useKey.setPressed(pressedRight);
            keyMouseRight = pressedRight;
        });
    }

}
