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

    private static final String CATEGORY = "onlykeys.keybinds.category";

    // Moving Camera
    public static final KeyBinding cameraUp = new KeyBinding("onlykeys.keybinds.cameraUp", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, CATEGORY);
    public static final KeyBinding cameraDown = new KeyBinding("onlykeys.keybinds.cameraDown", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, CATEGORY);
    public static final KeyBinding cameraLeft = new KeyBinding("onlykeys.keybinds.cameraLeft", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, CATEGORY);
    public static final KeyBinding cameraRight = new KeyBinding("onlykeys.keybinds.cameraRight", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, CATEGORY);

    // Moving Inventory
    public static final KeyBinding slotUp = new KeyBinding("onlykeys.keybinds.slotUp", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, CATEGORY);
    public static final KeyBinding slotDown = new KeyBinding("onlykeys.keybinds.slotDown", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, CATEGORY);
    public static final KeyBinding slotLeft = new KeyBinding("onlykeys.keybinds.slotLeft", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, CATEGORY);
    public static final KeyBinding slotRight = new KeyBinding("onlykeys.keybinds.slotRight", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, CATEGORY);


    // Clicking Mouse
    public static final KeyBinding leftClick  = new KeyBinding("onlykeys.keybinds.leftClick", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Y, CATEGORY);
    public static final KeyBinding wheelClick = new KeyBinding("onlykeys.keybinds.wheelClick", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, CATEGORY);
    public static final KeyBinding rightClick = new KeyBinding("onlykeys.keybinds.rightClick", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_I, CATEGORY);

    public static final KeyBinding[] MOUSE_KEYBINDINGS = {
        leftClick,
        wheelClick,
        rightClick
    };

    // Scrolling via Keyboard
    public static KeyBinding scrollUp   = new KeyBinding("onlykeys.keybinds.scrollUp", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_PAGE_UP, CATEGORY);
    public static KeyBinding scrollDown = new KeyBinding("onlykeys.keybinds.scrollDown", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_PAGE_DOWN, CATEGORY);

    // Changing creative tab
    public static KeyBinding changeCreativeTab  = new KeyBinding("onlykeys.keybinds.changeCreativeTab", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_TAB, CATEGORY);

    public static final KeyBinding[] ONLYKEYS_KEYBINDINGS = new KeyBinding[] {
            cameraUp, cameraDown, cameraLeft, cameraRight, slotUp, slotDown, slotLeft, slotRight, leftClick, wheelClick, rightClick, scrollUp, scrollDown, changeCreativeTab
    };

    public static void register() {
        KeyBindingHelper.registerKeyBinding(cameraUp);
        KeyBindingHelper.registerKeyBinding(cameraDown);
        KeyBindingHelper.registerKeyBinding(cameraLeft);
        KeyBindingHelper.registerKeyBinding(cameraRight);
        KeyBindingHelper.registerKeyBinding(slotUp);
        KeyBindingHelper.registerKeyBinding(slotDown);
        KeyBindingHelper.registerKeyBinding(slotLeft);
        KeyBindingHelper.registerKeyBinding(slotRight);
        KeyBindingHelper.registerKeyBinding(leftClick);
        KeyBindingHelper.registerKeyBinding(wheelClick);
        KeyBindingHelper.registerKeyBinding(rightClick);
        KeyBindingHelper.registerKeyBinding(scrollUp);
        KeyBindingHelper.registerKeyBinding(scrollDown);
        KeyBindingHelper.registerKeyBinding(changeCreativeTab);
    }
}
