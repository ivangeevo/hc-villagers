package org.ivangeevo.hc_villagers;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.ivangeevo.hc_villagers.trades.ModFarmerTrades;
import org.ivangeevo.hc_villagers.trades.ModLibrarianTrades;

import java.util.List;

import static net.minecraft.village.TradeOffers.PROFESSION_TO_LEVELED_TRADE;

public class VillagerTradesManager {

    public static void register() {
        setNewTrades(VillagerProfession.FARMER, ModFarmerTrades.NEW_TRADES);
        setNewTrades(VillagerProfession.LIBRARIAN, ModLibrarianTrades.NEW_TRADES);
    }

    // Clears the original map and puts with our custom trades.
    private static void setNewTrades(VillagerProfession profession, List<TradeOffers.Factory[]> newTradesList) {
        Int2ObjectMap<TradeOffers.Factory[]> villagerTrades = PROFESSION_TO_LEVELED_TRADE.get(profession);

        if (villagerTrades != null) {
            for (int level = 1; level <= 5; level++) {
                // Clear the vanilla trades for this level
                villagerTrades.put(level, new TradeOffers.Factory[]{}); // Empty the array

                // Add the custom modded trades for each level
                switch (level) {
                    case 1:
                        villagerTrades.put(level, newTradesList.getFirst());
                        break;
                    case 2:
                        villagerTrades.put(level, newTradesList.get(1));
                        break;
                    case 3:
                        villagerTrades.put(level, newTradesList.get(2));
                        break;
                    case 4:
                        villagerTrades.put(level, newTradesList.get(3));
                        break;
                    case 5:
                        villagerTrades.put(level, newTradesList.get(4));
                        break;
                    default:
                        break;
                }
            }
        }
    }


}
