package net.jasper.onlykeys.mod;

import net.fabricmc.api.ClientModInitializer;
import net.jasper.onlykeys.mod.keymovements.InventoryMovement;
import net.jasper.onlykeys.mod.keymovements.MouseMovement;
import net.jasper.onlykeys.mod.util.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnlyKeysModClient implements ClientModInitializer {

    public static final String MOD_ID = "onlykeys";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {

        Keys.register();
        MouseMovement.register();
        InventoryMovement.register();

        LOGGER.info("Mod client initialized!");
    }

}
