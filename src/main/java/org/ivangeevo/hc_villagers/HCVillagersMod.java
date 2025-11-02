package org.ivangeevo.hc_villagers;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HCVillagersMod implements ModInitializer {

    public static final String MOD_ID = "hc_villagers";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        VillagerTradesManager.register();
    }


}
