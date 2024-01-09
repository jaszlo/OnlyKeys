package net.jasper.onlykeys.mod.keymovements;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.jasper.onlykeys.mixin.CreativeInventoryScreenMixin;
import net.jasper.onlykeys.mixin.KeyBindingMixin;
import net.jasper.onlykeys.mod.OnlyKeysModClient;
import net.jasper.onlykeys.mod.util.Keys;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;

import java.util.Collection;

import static net.jasper.onlykeys.mod.util.Keys.*;
public class CreativeInventoryMovement {

    private static int itemGroupIndex = 0;
    private static final int itemGroupSize = ItemGroups.getGroups().size();

    private static int clickCooldown = 0;
    private static final int COOLDOWN = 2; // Ticks
    public static void register() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            // Reduce cooldown
            clickCooldown = Math.max(0, clickCooldown - 1);

            // If no screen is open do nothing
            if (client.currentScreen == null || client.interactionManager == null) {
                return;
            }

            long handle = client.getWindow().getHandle();

            // Scrolling
            // Get KeyCode for scrollUp/Down
            int scrollUpCode = ((KeyBindingMixin) scrollUp).getBoundKey().getCode();
            int scrollDownCode = ((KeyBindingMixin) scrollDown).getBoundKey().getCode();

            if (InputUtil.isKeyPressed(handle, scrollUpCode)) {
                client.currentScreen.mouseScrolled(client.mouse.getX(), client.mouse.getY(), 1, 1);
                OnlyKeysModClient.LOGGER.info("Scrolling up");
            } else if (InputUtil.isKeyPressed(handle, scrollDownCode)) {
                client.currentScreen.mouseScrolled(client.mouse.getX(), client.mouse.getY(), -1, -1);
                OnlyKeysModClient.LOGGER.info("Scrolling down");
            }

            // Changing Creative Tab if hasCreativeInventory
            // Get KeyCode for changeCreativeTab
            if (!client.interactionManager.hasCreativeInventory()) {
                return;
            }

            // Check cooldown only for tabbing - scrolling has no cooldown
            if (clickCooldown > 0) {
                return;
            }

            int changeCreativeTabCode = ((KeyBindingMixin) changeCreativeTab).getBoundKey().getCode();
            if (InputUtil.isKeyPressed(handle, changeCreativeTabCode)) {
                clickCooldown = COOLDOWN;
                // Update itemGroup
                if (Keys.shiftPressed(handle)) {
                    itemGroupIndex--;
                    if (itemGroupIndex < 0) itemGroupIndex = itemGroupSize - 1;
                } else {
                    itemGroupIndex = (itemGroupIndex + 1) % itemGroupSize;
                }

                ItemGroup itemGroup = ItemGroups.getGroups().get(itemGroupIndex);
                CreativeInventoryScreenMixin.setSelectedTab(itemGroup);

                // Update displayed Items
                Collection<ItemStack> toDisplay = itemGroup.getDisplayStacks();
                CreativeInventoryScreenMixin mixin = (CreativeInventoryScreenMixin) client.currentScreen;
                mixin.refreshSelectedTabMixin(toDisplay);
            }
        });
    }
}
