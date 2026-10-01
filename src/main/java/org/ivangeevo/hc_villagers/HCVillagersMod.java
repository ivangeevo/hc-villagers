package org.ivangeevo.hc_villagers;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.ivangeevo.hc_villagers.network.TradeKindsPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HCVillagersMod implements ModInitializer {
    public static final String MOD_ID = "hc_villagers";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(TradeKindsPayload.ID, TradeKindsPayload.CODEC);
        VillagerTradesManager.register();
    }
}