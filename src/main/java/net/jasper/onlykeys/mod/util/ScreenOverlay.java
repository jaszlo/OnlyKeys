package net.jasper.onlykeys.mod.util;

import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.widget.TextFieldWidget;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ScreenOverlay {

    public static boolean open = false;
    public static TextFieldWidget slotEntryField;

    public static final Class<?>[] ALLOWED_SCREENS = {
            AbstractFurnaceScreen.class,
            AbstractInventoryScreen.class,
            AnvilScreen.class,
            BlastFurnaceScreen.class,
            BeaconScreen.class,
            BrewingStandScreen.class,
            CartographyTableScreen.class,
            CraftingScreen.class,
            CreativeInventoryScreen.class,
            EnchantmentScreen.class,
            ForgingScreen.class,
            FurnaceScreen.class,
            GenericContainerScreen.class,
            Generic3x3ContainerScreen.class,
            GrindstoneScreen.class,
            HopperScreen.class,
            HorseScreen.class,
            InventoryScreen.class,
            LoomScreen.class,
            MerchantScreen.class,
            MinecartCommandBlockScreen.class,
            ShulkerBoxScreen.class,
            SmithingScreen.class,
            SmokerScreen.class,
            StonecutterScreen.class,

    };


    public static final Map<Class<?>, Integer> screenSlotMapping = new HashMap<>();
    static {
        // Hotbar first slot
        screenSlotMapping.put(InventoryScreen.class, 36);
        screenSlotMapping.put(CreativeInventoryScreen.class, 36);
    }

    public static boolean isAllowedScreen (Object screen) {
        return Arrays.asList(ALLOWED_SCREENS).contains(screen.getClass());
    }

    public static void toggle() {
        open = !open;
    }



}
