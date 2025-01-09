package org.ivangeevo.hcVillagers;

import net.fabricmc.api.ModInitializer;

public class HCVillagersMod implements ModInitializer {

    @Override
    public void onInitialize() {
        VillagerTradesManager.register();
    }


}
