package net.jasper.onlykeys.mod.keymovements;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.jasper.onlykeys.mixin.KeyBindingMixin;
import net.jasper.onlykeys.mod.OnlyKeysModClient;
import net.jasper.onlykeys.mod.util.Keys;
import net.jasper.onlykeys.mod.util.SlotsUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Arrays;
import java.util.List;

import static net.jasper.onlykeys.mod.util.Keys.*;

public class InventoryMovement {

    public static int selectedSlot = 36; // First Hotbar Slot

    private static boolean keyMouseLeft = false;
    private static boolean keyMouseWheel = false;
    private static boolean keyMouseRight = false;

    private static int clickCooldown = 0;
    private static final int COOLDOWN = 3;

    public static int[] getXYForSlot() {
        if (MinecraftClient.getInstance().currentScreen == null) {
            return null;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.player != null;
        Slot s = client.player.currentScreenHandler.getSlot(selectedSlot);
        return new int[]{ s.x, s.y };
    }

    private static final List<Integer> MOVE_UP_TO_AMOR = Arrays.stream(new int[]{ 9, 10, 11 }).boxed().toList();
    private static final List<Integer> MOVE_UP_TO_OFFHAND = Arrays.stream(new int[]{ 12, 13 }).boxed().toList();
    private static final List<Integer> MOVE_UP_TO_CRAFTING_GRID_LEFT = Arrays.stream(new int[]{ 14 }).boxed().toList();
    private static final List<Integer> MOVE_UP_TO_CRAFTING_GRID_RIGHT = Arrays.stream(new int[]{ 15, 16 }).boxed().toList();
    private static final List<Integer> MOVE_UP_TO_CRAFTING_RESULT = Arrays.stream(new int[]{ 17 }).boxed().toList();

    private static void handleHighestInventorySlots(MinecraftClient client) {
        if (MOVE_UP_TO_AMOR.contains(selectedSlot)) {
            selectedSlot = SlotsUtil.AMOR_SLOT_BOTTOM;
        } else if (MOVE_UP_TO_OFFHAND.contains(selectedSlot)) {
            selectedSlot = SlotsUtil.OFFHAND_SLOT;
        } else if (MOVE_UP_TO_CRAFTING_GRID_LEFT.contains(selectedSlot)) {
            selectedSlot = SlotsUtil.CRAFTING_GRID_LEFT_SLOT;
        } else if (MOVE_UP_TO_CRAFTING_GRID_RIGHT.contains(selectedSlot)) {
            selectedSlot = SlotsUtil.CRAFTING_GRID_RIGHT_SLOT;
        } else if (MOVE_UP_TO_CRAFTING_RESULT.contains(selectedSlot)) {
            selectedSlot = SlotsUtil.CRAFTING_RESULT_SLOT;
        }
    }


    public static void register() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player == null || client.currentScreen == null) {
                clickCooldown = Math.max(0, clickCooldown - 1);
                return;
            }

            if (client.currentScreen instanceof AbstractInventoryScreen<?> currentScreen) {
                // Handle Key presses to change/move the selected slot

                assert client.interactionManager != null;

                // Clearing Keys if they were pressed via OnlyKeys
                if (keyMouseLeft) Keys.clear(client.options.attackKey);
                if (keyMouseWheel) Keys.clear(client.options.pickItemKey);
                if (keyMouseRight) Keys.clear(client.options.useKey);


                long handle = client.getWindow().getHandle();

                // Moving Camera
                int upCode = ((KeyBindingMixin) up).getBoundKey().getCode();
                int downCode = ((KeyBindingMixin) down).getBoundKey().getCode();
                int leftCode = ((KeyBindingMixin) left).getBoundKey().getCode();
                int rightCode = ((KeyBindingMixin) right).getBoundKey().getCode();

                if (clickCooldown > 0) {
                    clickCooldown = Math.max(0, clickCooldown - 1);
                    return;
                }

                if (InputUtil.isKeyPressed(handle, upCode)) {
                    clickCooldown = COOLDOWN;
                    OnlyKeysModClient.LOGGER.info("Pressed up");
                    // Edge case for better use with amor/crafting slots.

                    if (9 <= selectedSlot && selectedSlot <= 17) {
                        handleHighestInventorySlots(client);
                    } else {
                        selectedSlot -= 9; // Down one row
                        if (selectedSlot < 0) {
                            int diff = Math.abs(selectedSlot - 9); //
                            selectedSlot = currentScreen.getScreenHandler().slots.size() - diff;
                        }
                    }

                    OnlyKeysModClient.LOGGER.info("selectedSlot = " + selectedSlot + " pos = " + currentScreen.getScreenHandler().slots.get(selectedSlot).x + ", " + currentScreen.getScreenHandler().slots.get(selectedSlot).y);
                    return;
                }
                if (InputUtil.isKeyPressed(handle, downCode)) {
                    clickCooldown = COOLDOWN;
                    OnlyKeysModClient.LOGGER.info("Pressed down");
                    selectedSlot += 9; // Up one row
                    if (selectedSlot >= currentScreen.getScreenHandler().slots.size()) {
                        selectedSlot = Math.abs(selectedSlot - currentScreen.getScreenHandler().slots.size());
                    }
                    OnlyKeysModClient.LOGGER.info("selectedSlot = " + selectedSlot + " pos = " + currentScreen.getScreenHandler().slots.get(selectedSlot).x + ", " + currentScreen.getScreenHandler().slots.get(selectedSlot).y);
                    return;
                }
                if (InputUtil.isKeyPressed(handle, leftCode)) {
                    clickCooldown = COOLDOWN;
                    OnlyKeysModClient.LOGGER.info("Pressed left");
                    selectedSlot -= 1; // Left one column
                    if (selectedSlot < 0) {
                        selectedSlot = currentScreen.getScreenHandler().slots.size() - 1;
                    }
                    OnlyKeysModClient.LOGGER.info("selectedSlot = " + selectedSlot + " pos = " + currentScreen.getScreenHandler().slots.get(selectedSlot).x + ", " + currentScreen.getScreenHandler().slots.get(selectedSlot).y);
                    return;
                }
                if (InputUtil.isKeyPressed(handle, rightCode)) {
                    clickCooldown = COOLDOWN;
                    OnlyKeysModClient.LOGGER.info("Pressed right");
                    selectedSlot += 1; // Right one column
                    if (selectedSlot >= currentScreen.getScreenHandler().slots.size()) {
                        selectedSlot = 0;
                    }
                    OnlyKeysModClient.LOGGER.info("selectedSlot = " + selectedSlot + " pos = " + currentScreen.getScreenHandler().slots.get(selectedSlot).x + ", " + currentScreen.getScreenHandler().slots.get(selectedSlot).y);
                    return;
                }



                // Clicking Mouse
                int leftClickCode = ((KeyBindingMixin) leftClick).getBoundKey().getCode();
                int wheelClickCode = ((KeyBindingMixin) wheelClick).getBoundKey().getCode();
                int rightClickCode = ((KeyBindingMixin) rightClick).getBoundKey().getCode();


                if (InputUtil.isKeyPressed(handle, leftClickCode)) {
                    // If Shift is pressed use quick move instead
                    if (Keys.shiftPressed(handle)) {
                        client.interactionManager.clickSlot(currentScreen.getScreenHandler().syncId, selectedSlot, 0, SlotActionType.QUICK_MOVE, client.player);
                    } else {
                        client.interactionManager.clickSlot(currentScreen.getScreenHandler().syncId, selectedSlot, 0, SlotActionType.PICKUP, client.player);
                    }
                    client.interactionManager.clickSlot(currentScreen.getScreenHandler().syncId, selectedSlot, 0, SlotActionType.PICKUP, client.player);
                    keyMouseLeft = true;
                } else {
                    keyMouseLeft = false;
                }

                if (InputUtil.isKeyPressed(handle, wheelClickCode)) {
                    // Create new Stack with ItemStack from selected slot on players cursorStack
                    client.interactionManager.clickSlot(currentScreen.getScreenHandler().syncId, selectedSlot, 2, SlotActionType.CLONE, client.player);
                    keyMouseWheel = true;
                } else {
                    keyMouseWheel = false;
                }

                if (InputUtil.isKeyPressed(handle, rightClickCode)) {
                    // If Shift is pressed use quick move instead
                    if (Keys.shiftPressed(handle)) {
                        client.interactionManager.clickSlot(currentScreen.getScreenHandler().syncId, selectedSlot, 1, SlotActionType.QUICK_MOVE, client.player);
                    } else {
                        client.interactionManager.clickSlot(currentScreen.getScreenHandler().syncId, selectedSlot, 1, SlotActionType.PICKUP, client.player);
                    }
                    keyMouseRight = true;
                } else {
                    keyMouseRight = false;
                }
            }
        });
    }
}
