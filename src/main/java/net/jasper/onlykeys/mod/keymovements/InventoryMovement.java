package net.jasper.onlykeys.mod.keymovements;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.jasper.onlykeys.mixin.accessors.HandledScreenAccessors;
import net.jasper.onlykeys.mixin.accessors.KeyBindingAccessors;
import net.jasper.onlykeys.mixin.accessors.MouseAccessors;
import net.jasper.onlykeys.mod.util.Direction;
import net.jasper.onlykeys.mod.util.Keys;
import net.jasper.onlykeys.mod.util.ScreenOverlay;
import net.jasper.onlykeys.mod.util.SurvivalInventorySlots;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import static net.jasper.onlykeys.mod.util.Keys.*;

public class InventoryMovement {

    public static boolean cancelMouse = false;

    public static int selectedSlot = 36; // First Hotbar Slot

    // Cooldown is given in game ticks
    private static int currentClickCooldown = 0;
    private static final int CLICK_COOLDOWN = 4;
    private static int currentMoveCooldown = 0;
    private static final int MOVE_COOLDOWN = 3;

    public static void resetClickCooldown() {
        currentClickCooldown = CLICK_COOLDOWN;
    }

    private static final boolean[] mouseButtonsPressed = { false, false, false };

    public static int[] getXYForSlot() {
        if (MinecraftClient.getInstance().currentScreen == null) {
            return null;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.player != null;
        Slot s = client.player.currentScreenHandler.getSlot(selectedSlot);
        return new int[]{ s.x, s.y };
    }

    private static float squaredDistance(Slot s1, Slot s2) {
        return (float) (Math.pow(s1.x - s2.x, 2) + Math.pow(s1.y - s2.y, 2));
    }


    private static int findClosestSlot(Direction dir, Slot currentSlot, ScreenHandler currentScreenHandler) {
        assert MinecraftClient.getInstance().player != null;
        if (!MinecraftClient.getInstance().player.isCreative()) {
            int slot = SurvivalInventorySlots.checkEdgeCase(dir, currentSlot, currentScreenHandler);
            if (slot > 0) return slot;
        }

        int currentBest = selectedSlot;
        float currentDist = Float.MAX_VALUE;

        for (int i = 0; i < currentScreenHandler.slots.size(); i++) {
            Slot s = currentScreenHandler.slots.get(i);
            if (s == currentSlot) continue;

            float dist = squaredDistance(currentSlot, s);

            boolean updateBest = switch (dir) {
                case UP -> s.y < currentSlot.y && dist < currentDist;
                case DOWN -> s.y > currentSlot.y && dist < currentDist;
                case LEFT -> s.x < currentSlot.x && dist < currentDist;
                case RIGHT -> s.x > currentSlot.x && dist < currentDist;
            };

            if (updateBest) {
                currentBest = i;
                currentDist = dist;
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
            currentClickCooldown = Math.max(0, currentClickCooldown - 1);
            currentMoveCooldown = Math.max(0, currentMoveCooldown - 1);
            boolean moveSlotReady = currentMoveCooldown == 0;
            boolean clickSlotReady = currentClickCooldown == 0;

            if (client.player == null || client.currentScreen == null) {
                return;
            }

            if (ScreenOverlay.isAllowedScreen(client.currentScreen)) {
                HandledScreen<?> handledScreen = (HandledScreen<?>) client.currentScreen;
                assert client.interactionManager != null;

                int[] xy = getXYForSlot();
                if (xy == null) { return; }
                HandledScreenAccessors mixin = (HandledScreenAccessors) client.currentScreen;
                double x = (double) xy[0] + (double) mixin.getX() + 2.0;
                double y = (double) xy[1] + (double) mixin.getY() + 2.0;

                // Cancel usual mouse handling and set position to be over selectedSlot
                cancelMouse = true;
                int scale = client.options.getGuiScale().getValue();
                scale = scale == 0 ? client.getWindow().getWidth() / client.getWindow().getScaledWidth() : scale;
                MouseAccessors accessibleMouse = (MouseAccessors) client.mouse;
                accessibleMouse.setX(scale * x); accessibleMouse.setY(scale * y);
                // Hide the cursor, position doesn't matter as it was set above and this will only take effect in the next tick where it will already be overwritten again
                // Currently disabled for debugging
                // InputUtil.setCursorParameters(client.getWindow().getHandle(), InputUtil.GLFW_CURSOR_DISABLED, x, y);

                // Clearing Keys if they were pressed via OnlyKeys
                if (mouseButtonsPressed[LEFT_CLICK]) Keys.clear(client.options.attackKey);
                if (mouseButtonsPressed[WHEEL_CLICK]) Keys.clear(client.options.pickItemKey);
                if (mouseButtonsPressed[RIGHT_CLICK]) Keys.clear(client.options.useKey);

                long handle = client.getWindow().getHandle();

                if (moveSlotReady) { // Moving Selected Slot
                    Slot currentSlot = handledScreen.getScreenHandler().getSlot(selectedSlot);
                    ScreenHandler currentScreenHandler = handledScreen.getScreenHandler();

                    for (Direction dir : Direction.values()) {
                        int keyCode = ((KeyBindingAccessors) Keys.slotMoveKeys[dir.ordinal()]).getBoundKey().getCode();
                        if (InputUtil.isKeyPressed(handle, keyCode)) {
                            currentMoveCooldown = MOVE_COOLDOWN;
                            selectedSlot = findClosestSlot(dir, currentSlot, currentScreenHandler);
                        }
                    }
                }
                if (clickSlotReady) { // Clicking Slot

                    ScreenHandler handler = client.player.currentScreenHandler;
                    for (int button : MOUSE_BUTTONS) {
                        int keyCode = ((KeyBindingAccessors) MOUSE_KEYBINDINGS[button]).getBoundKey().getCode();
                        boolean pressed = InputUtil.isKeyPressed(handle, keyCode);
                        mouseButtonsPressed[button] = pressed;
                        if (!pressed) { continue; }

                        accessibleMouse.onMouseButtonInvoker(handle, button, 1, 0);
                        // Unset mouse in next tick to complete MouseClick event
                        client.execute(() -> accessibleMouse.onMouseButtonInvoker(handle, button, 0, 0));
                        currentClickCooldown = CLICK_COOLDOWN;

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
            }
        });
    }
}
