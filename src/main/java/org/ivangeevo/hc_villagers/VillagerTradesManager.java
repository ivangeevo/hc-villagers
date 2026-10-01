package org.ivangeevo.hc_villagers;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import org.ivangeevo.hc_villagers.trading.data.HCTradeDataLoader;

public class VillagerTradesManager {

    /**
     * Trades are data-driven: they're loaded from {@code data/<namespace>/hc_villager_trades/*.json} on every data
     * reload. A profession uses the BTW trading system only if some data pack defines trades for it.
     * This mod's own tables are in {@code src/main/resources/data/hc_villagers/hc_villager_trades/}.
     */
    public static void register() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new HCTradeDataLoader());
    }
}
