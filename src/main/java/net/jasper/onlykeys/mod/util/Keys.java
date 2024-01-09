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
    public static KeyBinding up    = new KeyBinding("Camera Up/Move Slot Up", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, "OnlyKeys");
    public static KeyBinding down  = new KeyBinding("Camera Down/Move Slot Down", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "OnlyKeys");
    public static KeyBinding left  = new KeyBinding("Camera Left/Move Slot Left", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "OnlyKeys");
    public static KeyBinding right = new KeyBinding("Camera Right/Move Slot Right", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "OnlyKeys");

    // Clicking Mouse
    public static KeyBinding leftClick  = new KeyBinding("Left Click", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Y, "OnlyKeys");
    public static KeyBinding wheelClick = new KeyBinding("Wheel Click", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_I, "OnlyKeys");
    public static KeyBinding rightClick = new KeyBinding("Right Click", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, "OnlyKeys");

    // Scrolling via Keyboard
    public static KeyBinding scrollUp   = new KeyBinding("Scroll Creative Menu Up", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_PAGE_UP, "OnlyKeys");
    public static KeyBinding scrollDown = new KeyBinding("Scroll Creative Menu Down", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_PAGE_DOWN, "OnlyKeys");

    // Changing creative tab
    public static KeyBinding changeCreativeTab  = new KeyBinding("Change Creative Tab", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_TAB, "OnlyKeys");

    public static final KeyBinding[] ONLYKEYS_KEYBINDINGS = new KeyBinding[] {
            up, down, left, right, leftClick, wheelClick, rightClick, scrollUp, scrollDown, changeCreativeTab
    };

    public static void register() {
        KeyBindingHelper.registerKeyBinding(up);
        KeyBindingHelper.registerKeyBinding(down);
        KeyBindingHelper.registerKeyBinding(left);
        KeyBindingHelper.registerKeyBinding(right);
        KeyBindingHelper.registerKeyBinding(leftClick);
        KeyBindingHelper.registerKeyBinding(wheelClick);
        KeyBindingHelper.registerKeyBinding(rightClick);
        KeyBindingHelper.registerKeyBinding(scrollUp);
        KeyBindingHelper.registerKeyBinding(scrollDown);
        KeyBindingHelper.registerKeyBinding(changeCreativeTab);
    }
}
