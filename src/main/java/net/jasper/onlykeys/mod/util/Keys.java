package net.jasper.onlykeys.mod.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.jasper.onlykeys.mixin.accessors.KeyBindingAccessors;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Keys {

    public static final int[] MOUSE_BUTTONS = {0, 1, 2};
    public static final int LEFT_CLICK = 0;
    public static final int RIGHT_CLICK = 1;
    public static final int WHEEL_CLICK = 2;

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
    public static final KeyBinding[] slotMoveKeys = new KeyBinding[]{
            slotUp, slotDown, slotLeft, slotRight
    };

    // Clicking Mouse
    public static final KeyBinding leftClick  = new KeyBinding("onlykeys.keybinds.leftClick", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Y, CATEGORY);
    public static final KeyBinding rightClick = new KeyBinding("onlykeys.keybinds.rightClick", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_I, CATEGORY);
    public static final KeyBinding wheelClick = new KeyBinding("onlykeys.keybinds.wheelClick", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, CATEGORY);

    // Open QuickSlot menu
    public static final KeyBinding toggleQuickSlotMenu = new KeyBinding("onlykeys.keybinds.openQuickSlotMenu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_L, CATEGORY);

    public static final KeyBinding[] MOUSE_KEYBINDINGS = {
        leftClick,
        rightClick,
        wheelClick,
    };

    // Scrolling via Keyboard
    public static KeyBinding scrollUp   = new KeyBinding("onlykeys.keybinds.scrollUp", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_PAGE_UP, CATEGORY);
    public static KeyBinding scrollDown = new KeyBinding("onlykeys.keybinds.scrollDown", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_PAGE_DOWN, CATEGORY);

    // Changing creative tab
    public static KeyBinding changeCreativeTab  = new KeyBinding("onlykeys.keybinds.changeCreativeTab", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_TAB, CATEGORY);

    public static final KeyBinding[] ONLYKEYS_KEYBINDINGS = new KeyBinding[] {
            cameraUp, cameraDown, cameraLeft, cameraRight, slotUp, slotDown, slotLeft, slotRight, leftClick, wheelClick, rightClick, scrollUp, scrollDown, changeCreativeTab, toggleQuickSlotMenu
    };


    private static final int MENU_TOGGLE_COOLDOWN = 5;
    private static int currentMenuToggleCooldown = 0;

    public static void register() {
        for (KeyBinding b : ONLYKEYS_KEYBINDINGS) {
            KeyBindingHelper.registerKeyBinding(b);
        }

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (currentMenuToggleCooldown > 0) {
                currentMenuToggleCooldown--;
                return;
            }

            if (client.player == null || client.player.isCreative()) {
                return;
            }

            int toggleMenuCode = ((KeyBindingAccessors)toggleQuickSlotMenu).getBoundKey().getCode();
            if (InputUtil.isKeyPressed(client.getWindow().getHandle(), toggleMenuCode)) {
                ScreenOverlay.toggle();
                currentMenuToggleCooldown = MENU_TOGGLE_COOLDOWN;
            }

            // Just to make sure it's not rendered by accident
            if (client.currentScreen == null) {
                ScreenOverlay.open = false;
            }

        });
    }
}
