package net.jasper.onlykeys.mod.keymovements;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.jasper.onlykeys.mixin.screens.HandledScreenAccessors;
import net.jasper.onlykeys.mixin.KeyBindingAccessors;
import net.jasper.onlykeys.mixin.mouse.MouseAccessors;
import net.jasper.onlykeys.mod.util.Keys;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import static net.jasper.onlykeys.mod.util.Keys.*;

public class InventoryMovement {

    public static boolean cancelMouse = false;

    public static int selectedSlot = 36; // First Hotbar Slot

    private static int clickCooldown = 0;
    private static final int COOLDOWN = 2; // Ticks

    private static final int[] MOUSE_BUTTONS = {0, 1, 2};
    private static final int LEFT_CLICK = 0;
    private static final int WHEEL_CLICK = 1;
    private static final int RIGHT_CLICK = 2;
    private static final boolean[] mouseButtonsPressed = { false, false, false };
    private static final int[] mouseButtonClickCodes = { 0, 0, 0 };

    public static int[] getXYForSlot() {
        if (MinecraftClient.getInstance().currentScreen == null) {
            return null;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.player != null;
        Slot s = client.player.currentScreenHandler.getSlot(selectedSlot);
        return new int[]{ s.x, s.y };
    }

    private enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    private static float squaredDistance(Slot s1, Slot s2) {
        return (float) (Math.pow(s1.x - s2.x, 2) + Math.pow(s1.y - s2.y, 2));
    }

    private static int findClosestSlot(Direction dir, Slot currentSlot, ScreenHandler currentScreenHandler) {
        int currentBest = selectedSlot;
        float currentDist = Float.MAX_VALUE;
        // Create a list with all slots that have a lower y value than the current slot (lower = higher on screen)
        for (int i = 0; i < currentScreenHandler.slots.size(); i++) {
            Slot s = currentScreenHandler.slots.get(i);
            // Skip same slot
            if (s == currentSlot) {
                continue;
            }
            // Find best match
            if (dir == Direction.UP && s.y < currentSlot.y) {
                float dist = squaredDistance(currentSlot, s);
                if (dist < currentDist) {
                    currentBest = i;
                    currentDist = (int) dist;
                }
            } else if (dir == Direction.DOWN && s.y > currentSlot.y) {
                float dist = squaredDistance(currentSlot, s);
                if (dist < currentDist) {
                    currentBest = i;
                    currentDist = (int) dist;
                }

            } else if (dir == Direction.LEFT && s.x < currentSlot.x) {
                float dist = squaredDistance(currentSlot, s);
                if (dist < currentDist) {
                    currentBest = i;
                    currentDist = (int) dist;
                }
            } else if (dir == Direction.RIGHT && s.x > currentSlot.x) {
                float dist = squaredDistance(currentSlot, s);
                if (dist < currentDist) {
                    currentBest = i;
                    currentDist = (int) dist;
                }
            }
        }
        return currentBest;
    }

    public static void register() {
        // If in creative inventory and currently searching for an item, do nothing unless search "finished" (?)
        // Todo: SearchBox needs to be toggled off/on focus somehow
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            cancelMouse = false;
            // Reduce cooldown
            clickCooldown = Math.max(0, clickCooldown - 1);
            if (clickCooldown > 0) {
                return;
            }

            if (client.player == null || client.currentScreen == null) {
                return;
            }

            if (client.currentScreen instanceof AbstractInventoryScreen<?> currentScreen) {
                assert client.interactionManager != null;

                int[] xy = getXYForSlot();
                if (xy == null) { return; }
                HandledScreenAccessors mixin = (HandledScreenAccessors) client.currentScreen;
                double x = (double) xy[0] + (double) mixin.getX() + 2.0;
                double y = (double) xy[1] + (double) mixin.getY() + 2.0;
                cancelMouse = true;
                int scale = client.options.getGuiScale().getValue();
                MouseAccessors accessibleMouse = (MouseAccessors) client.mouse;
                accessibleMouse.setX(scale * x); accessibleMouse.setY(scale * y);

                // This will only take effect in the next tick therefore continue with all else being executed in the next tick
                InputUtil.setCursorParameters(client.getWindow().getHandle(), InputUtil.GLFW_CURSOR_DISABLED, x, y);

                // Clearing Keys if they were pressed via OnlyKeys
                if (mouseButtonsPressed[LEFT_CLICK]) Keys.clear(client.options.attackKey);
                if (mouseButtonsPressed[WHEEL_CLICK]) Keys.clear(client.options.pickItemKey);
                if (mouseButtonsPressed[RIGHT_CLICK]) Keys.clear(client.options.useKey);

                long handle = client.getWindow().getHandle();

                // Moving selected Slot
                int upCode = ((KeyBindingAccessors) slotUp).getBoundKey().getCode();
                int downCode = ((KeyBindingAccessors) slotDown).getBoundKey().getCode();
                int leftCode = ((KeyBindingAccessors) slotLeft).getBoundKey().getCode();
                int rightCode = ((KeyBindingAccessors) slotRight).getBoundKey().getCode();

                Slot currentSlot = currentScreen.getScreenHandler().getSlot(selectedSlot);
                ScreenHandler currentScreenHandler = currentScreen.getScreenHandler();

                if (InputUtil.isKeyPressed(handle, upCode)) {
                    clickCooldown = COOLDOWN;
                    selectedSlot = findClosestSlot(Direction.UP, currentSlot, currentScreenHandler);
                }
                if (InputUtil.isKeyPressed(handle, downCode)) {
                    clickCooldown = COOLDOWN;
                    selectedSlot = findClosestSlot(Direction.DOWN, currentSlot, currentScreenHandler);
                }
                if (InputUtil.isKeyPressed(handle, leftCode)) {
                    clickCooldown = COOLDOWN;
                    selectedSlot = findClosestSlot(Direction.LEFT, currentSlot, currentScreenHandler);
                }
                if (InputUtil.isKeyPressed(handle, rightCode)) {
                    clickCooldown = COOLDOWN;
                    selectedSlot = findClosestSlot(Direction.RIGHT, currentSlot, currentScreenHandler);
                }

                // Get KeyCode for MouseButtons
                for (int button : MOUSE_BUTTONS) {
                    mouseButtonClickCodes[button] = ((KeyBindingAccessors) MOUSE_KEYBINDINGS[button]).getBoundKey().getCode();
                }

                ScreenHandler handler = client.player.currentScreenHandler;

                for (int button : MOUSE_BUTTONS) {
                    boolean pressed = InputUtil.isKeyPressed(handle, mouseButtonClickCodes[button]);
                    mouseButtonsPressed[button] = pressed;
                    if (!pressed) { continue; }

                    accessibleMouse.onMouseButtonInvoker(handle, button, 1, 0);
                    // Unset mouse in next tick to complete MouseClick event
                    client.execute(() -> accessibleMouse.onMouseButtonInvoker(handle, button, 0, 0));
                    clickCooldown = COOLDOWN;

                    // In Creative use left/wheel to drop stack or single
                    if (client.player.isCreative() && !handler.getCursorStack().isEmpty()) {
                        if (button == RIGHT_CLICK) {
                            client.player.dropItem(handler.getCursorStack(), true);
                            client.interactionManager.dropCreativeStack(handler.getCursorStack());
                            handler.setCursorStack(ItemStack.EMPTY);
                        } else if (button == WHEEL_CLICK) {
                            ItemStack itemStack = handler.getCursorStack().split(1);
                            client.player.dropItem(itemStack, true);
                            client.interactionManager.dropCreativeStack(itemStack);
                        }
                    }
                }
            }
        });
    }
}
