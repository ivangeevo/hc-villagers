package org.ivangeevo.hc_villagers.trades.original;

import net.minecraft.item.Items;
import net.minecraft.village.TradeOffers;

public class VanillaButcherTrades {

    public static final TradeOffers.Factory[] LEVEL_1_TRADES = {
            new TradeOffers.BuyItemFactory(Items.CHICKEN, 14, 16, 2),
            new TradeOffers.BuyItemFactory(Items.PORKCHOP, 7, 16, 2),
            new TradeOffers.BuyItemFactory(Items.RABBIT, 4, 16, 2),
            new TradeOffers.SellItemFactory(Items.RABBIT_STEW, 1, 1, 1)
    };

    public static final TradeOffers.Factory[] LEVEL_2_TRADES = {
            new TradeOffers.BuyItemFactory(Items.COAL, 15, 16, 2),
            new TradeOffers.SellItemFactory(Items.COOKED_PORKCHOP, 1, 5, 16, 5),
            new TradeOffers.SellItemFactory(Items.COOKED_CHICKEN, 1, 8, 16, 5)
    };

    public static final TradeOffers.Factory[] LEVEL_3_TRADES = {
            new TradeOffers.BuyItemFactory(Items.MUTTON, 7, 16, 20),
            new TradeOffers.BuyItemFactory(Items.BEEF, 10, 16, 20)
    };

    public static final TradeOffers.Factory[] LEVEL_4_TRADES = {
            new TradeOffers.BuyItemFactory(Items.DRIED_KELP_BLOCK, 10, 12, 30)
    };

    public static final TradeOffers.Factory[] LEVEL_5_TRADES = {
            new TradeOffers.BuyItemFactory(Items.SWEET_BERRIES, 10, 12, 30)
    };
}
