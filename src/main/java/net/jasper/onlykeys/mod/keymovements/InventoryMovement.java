package net.jasper.onlykeys.mod.keymovements;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.jasper.onlykeys.mixin.KeyBindingMixin;
import net.jasper.onlykeys.mod.OnlyKeysModClient;
import net.jasper.onlykeys.mod.util.Keys;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import static net.jasper.onlykeys.mod.util.Keys.*;

public class InventoryMovement {

    public static int selectedSlot = 36; // First Hotbar Slot

    private static boolean keyMouseLeft = false;
    private static boolean keyMouseWheel = false;
    private static boolean keyMouseRight = false;

    private static int clickCooldown = 0;
    private static final int COOLDOWN = 2; // Ticks

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


    private static void handleCreativeClick(long handle, ScreenHandler currentScreenHandler, MinecraftClient client) {
        assert client.player != null;
        if (Keys.shiftPressed(handle)) {
            ItemStack stack = client.player.getInventory().getStack(selectedSlot).copy();
            stack.setCount(stack.getMaxCount());
            currentScreenHandler.setCursorStack(stack);
        } else {
            currentScreenHandler.setCursorStack(client.player.getInventory().getStack(selectedSlot).copy());
        }
    }

    public static void register() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
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

                // If in creative inventory and currently searching for an item, do nothing unless search "finished" (?)
                // Todo: SearchBox needs to be toggled off/on focus somehow

                // Clearing Keys if they were pressed via OnlyKeys
                if (keyMouseLeft) Keys.clear(client.options.attackKey);
                if (keyMouseWheel) Keys.clear(client.options.pickItemKey);
                if (keyMouseRight) Keys.clear(client.options.useKey);

                long handle = client.getWindow().getHandle();

                // Moving selected Slot
                int upCode = ((KeyBindingMixin) up).getBoundKey().getCode();
                int downCode = ((KeyBindingMixin) down).getBoundKey().getCode();
                int leftCode = ((KeyBindingMixin) left).getBoundKey().getCode();
                int rightCode = ((KeyBindingMixin) right).getBoundKey().getCode();

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

                // Clicking Mouse
                int leftClickCode = ((KeyBindingMixin) leftClick).getBoundKey().getCode();
                int wheelClickCode = ((KeyBindingMixin) wheelClick).getBoundKey().getCode();
                int rightClickCode = ((KeyBindingMixin) rightClick).getBoundKey().getCode();

                if (InputUtil.isKeyPressed(handle, leftClickCode)) {
                    // If Shift is pressed use quick move instead
                    // Survival
                    if (!client.interactionManager.hasCreativeInventory()) {
                        if (Keys.shiftPressed(handle)) {
                            client.interactionManager.clickSlot(currentScreen.getScreenHandler().syncId, selectedSlot, 0, SlotActionType.QUICK_MOVE, client.player);
                        } else {
                            OnlyKeysModClient.LOGGER.info("Clicking Slot " + selectedSlot);
                            client.interactionManager.clickSlot(currentScreen.getScreenHandler().syncId, selectedSlot, 0, SlotActionType.PICKUP, client.player);
                        }
                    // Creative
                    } else {
                        handleCreativeClick(handle, currentScreen.getScreenHandler(), client);
                    }
                    clickCooldown = COOLDOWN;
                    keyMouseLeft = true;
                } else {
                    keyMouseLeft = false;
                }

                if (InputUtil.isKeyPressed(handle, wheelClickCode)) {
                    // Survival
                    if (!client.interactionManager.hasCreativeInventory()) {
                        // Create new Stack with ItemStack from selected slot on players cursorStack
                        client.interactionManager.clickSlot(currentScreen.getScreenHandler().syncId, selectedSlot, 2, SlotActionType.CLONE, client.player);
                        clickCooldown = COOLDOWN;
                        keyMouseWheel = true;
                    // Creative
                    } else {
                        client.interactionManager.pickFromInventory(selectedSlot);
                        //handleCreativeClick(handle, currentScreen.getScreenHandler(), client);
                    }
                } else {
                    keyMouseWheel = false;
                }

                if (InputUtil.isKeyPressed(handle, rightClickCode)) {
                    // Survival
                    if (!client.interactionManager.hasCreativeInventory()) {
                        // If Shift is pressed use quick move instead
                        if (Keys.shiftPressed(handle)) {
                            client.interactionManager.clickSlot(currentScreen.getScreenHandler().syncId, selectedSlot, 1, SlotActionType.QUICK_MOVE, client.player);
                        } else {
                            client.interactionManager.clickSlot(currentScreen.getScreenHandler().syncId, selectedSlot, 1, SlotActionType.PICKUP, client.player);
                        }
                    // Creative
                    } else {
                        handleCreativeClick(handle, currentScreen.getScreenHandler(), client);
                    }
                    clickCooldown = COOLDOWN;
                    keyMouseRight = true;
                } else {
                    keyMouseRight = false;
                }
            }
        });
    }
}
