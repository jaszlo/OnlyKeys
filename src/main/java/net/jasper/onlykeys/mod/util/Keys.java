package net.jasper.onlykeys.mod.util;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Keys {
    public static void clear(KeyBinding key) {
        int c = 0;
        final int LIMIT = 100;
        while (key.isPressed() && c < LIMIT) {
            key.setPressed(false);
            c++;
        }
    }
    public static boolean shiftPressed(long handle) {
        return InputUtil.isKeyPressed(handle, InputUtil.GLFW_KEY_LEFT_SHIFT) || InputUtil.isKeyPressed(handle, InputUtil.GLFW_KEY_RIGHT_SHIFT);
    }

    // Moving Camera
    public static KeyBinding up    = new KeyBinding("Camera Up", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UP, "OnlyKeys");
    public static KeyBinding down  = new KeyBinding("Camera Down", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_DOWN, "OnlyKeys");
    public static KeyBinding left  = new KeyBinding("Camera Left", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT, "OnlyKeys");
    public static KeyBinding right = new KeyBinding("Camera Right", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT, "OnlyKeys");

    // Clicking Mouse
    public static KeyBinding leftClick = new KeyBinding("Left Click", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_DELETE, "OnlyKeys");
    public static KeyBinding wheelClick = new KeyBinding("Wheel Click", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_END, "OnlyKeys");
    public static KeyBinding rightClick = new KeyBinding("Right Click", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_PAGE_DOWN, "OnlyKeys");

    public static final KeyBinding[] ONLYKEYS_KEYBINDINGS = new KeyBinding[] { up, down, left, right, leftClick, wheelClick, rightClick };

    public static void register() {
        KeyBindingHelper.registerKeyBinding(up);
        KeyBindingHelper.registerKeyBinding(down);
        KeyBindingHelper.registerKeyBinding(left);
        KeyBindingHelper.registerKeyBinding(right);
        KeyBindingHelper.registerKeyBinding(leftClick);
        KeyBindingHelper.registerKeyBinding(wheelClick);
        KeyBindingHelper.registerKeyBinding(rightClick);
    }
}
