package org.ivangeevo.hcVillagers.trades;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemConvertible;

public abstract class AbstractVillagerTradesProvider {

    protected static final String TE = "tough_environment";
    protected static final String BTWR = "btwr";
    protected static final String BWT = "bwt";
    protected static final String ST = "sturdy_trees";
    protected static final String VG = "vegehenna";
    protected static final String AG = "animageddon";

    // Helper method to select either modded or vanilla items based on mod loading
    protected static ItemConvertible getOptionalItem(String modId, ItemConvertible item, ItemConvertible fallbackItem) {
        if (FabricLoader.getInstance().isModLoaded(modId)) {
            // Try to get modded item from the registry using modId and itemId
            try {
                return item;
            } catch (Exception e) {
                // If the modded item isn't found, fallback to the vanilla item
                return fallbackItem;
            }
        } else {
            // If the mod isn't loaded, fallback to the vanilla item
            return fallbackItem;
        }
    }

    // bandaid for until (or if) we decide to rework the required count of ingredients for village trades
    // Returns the average value between the two numbers passed
    protected static int arithmeticMean(int a, int b) {
        return (a + b) / 2;
    }
}
