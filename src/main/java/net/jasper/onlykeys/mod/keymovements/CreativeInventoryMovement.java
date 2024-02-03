package net.jasper.onlykeys.mod.keymovements;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.jasper.onlykeys.mixin.accessors.CreativeInventoryScreenAccessors;
import net.jasper.onlykeys.mixin.accessors.KeyBindingAccessors;
import net.jasper.onlykeys.mod.OnlyKeysModClient;
import net.jasper.onlykeys.mod.util.Keys;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

import static net.jasper.onlykeys.mod.util.Keys.*;
public class CreativeInventoryMovement {

    private static int itemGroupIndex = 0;
    private static final int itemGroupSize = ItemGroups.getGroups().size();

    private static int clickCooldown = 0;
    private static final int COOLDOWN = 4; // Ticks

    private static final int SKIP_GROUP = 12;

    private static void updateSearchBox(MinecraftClient client) {
        assert client.currentScreen != null;
        TextFieldWidget searchBox = ((CreativeInventoryScreenAccessors) client.currentScreen).getSearchBox();
        if (ItemGroups.getGroups().get(itemGroupIndex).getType() == ItemGroup.Type.SEARCH) {
            OnlyKeysModClient.LOGGER.info("SearchBox will be enabled");
            searchBox.setVisible(true);
            searchBox.setFocusUnlocked(false);
            searchBox.setFocused(true);
            searchBox.setEditable(true);
        } else {
            OnlyKeysModClient.LOGGER.info("SearchBox will be disabled");
            searchBox.setVisible(false);
            searchBox.setFocusUnlocked(true);
            searchBox.setFocused(false);
            searchBox.setEditable(false);
        }
    }

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
            int scrollUpCode = ((KeyBindingAccessors) scrollUp).getBoundKey().getCode();
            int scrollDownCode = ((KeyBindingAccessors) scrollDown).getBoundKey().getCode();

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

            int changeCreativeTabCode = ((KeyBindingAccessors) changeCreativeTab).getBoundKey().getCode();
            if (client.currentScreen instanceof CreativeInventoryScreen && InputUtil.isKeyPressed(handle, changeCreativeTabCode)) {
                clickCooldown = COOLDOWN;
                do {
                    if (Keys.shiftPressed(handle)) {
                        itemGroupIndex--;
                        if (itemGroupIndex < 0) itemGroupIndex = itemGroupSize - 1;
                    } else {
                        itemGroupIndex = (itemGroupIndex + 1) % itemGroupSize;
                    }
                } while(itemGroupIndex == SKIP_GROUP);
                OnlyKeysModClient.LOGGER.info("GroupIndex:" + itemGroupIndex);

                ItemGroup itemGroup = ItemGroups.getGroups().get(itemGroupIndex);
                CreativeInventoryScreenAccessors.setSelectedTab(itemGroup);
                updateSearchBox(client);

                // Update displayed Items
                Collection<ItemStack> toDisplay = itemGroup.getType() == ItemGroup.Type.SEARCH ? new ArrayList<>() : itemGroup.getDisplayStacks();
                CreativeInventoryScreenAccessors mixin = (CreativeInventoryScreenAccessors) client.currentScreen;
                mixin.refreshSelectedTabInvoker(toDisplay);
            }
        });
    }
}
