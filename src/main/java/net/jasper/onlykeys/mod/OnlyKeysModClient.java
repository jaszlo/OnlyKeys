package net.jasper.onlykeys.mod;

import net.fabricmc.api.ClientModInitializer;
import net.jasper.onlykeys.mod.keymovements.InventoryMovement;
import net.jasper.onlykeys.mod.keymovements.PlayerMovement;
import net.jasper.onlykeys.mod.keymovements.CreativeInventoryMovement;
import net.jasper.onlykeys.mod.util.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnlyKeysModClient implements ClientModInitializer {

    public static final String MOD_ID = "onlykeys";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);



    @Override
    public void onInitializeClient() {
        // Register KeyBindings in Options
        Keys.register();

        // Register PlayerMouseMovements in Game like Camera and using Hands
        PlayerMovement.register();

        // Register selectedSlotMovement in Game for interacting with Inventories
        InventoryMovement.register();

        // Register ScreenScrollMovements in Game like scrolling in Inventories
        CreativeInventoryMovement.register();

        LOGGER.info("Mod client initialized!");
    }

}
