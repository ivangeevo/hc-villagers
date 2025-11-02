package org.ivangeevo.hc_villagers.trades.original;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.village.TradeOffers;

public class VanillaLibrarianTrades {

    public static final TradeOffers.Factory[] LEVEL_1_TRADES = {
            new TradeOffers.BuyItemFactory(Items.PAPER, 24, 16, 2),
            new TradeOffers.EnchantBookFactory(1, EnchantmentTags.TRADEABLE),
            new TradeOffers.SellItemFactory(Blocks.BOOKSHELF, 9, 1, 12, 1)
    };

    public static final TradeOffers.Factory[] LEVEL_2_TRADES = {
            new TradeOffers.BuyItemFactory(Items.BOOK, 4, 12, 10),
            new TradeOffers.EnchantBookFactory(5, EnchantmentTags.TRADEABLE),
            new TradeOffers.SellItemFactory(Items.LANTERN, 1, 1, 5)
    };

    public static final TradeOffers.Factory[] LEVEL_3_TRADES = {
            new TradeOffers.BuyItemFactory(Items.INK_SAC, 5, 12, 20),
            new TradeOffers.EnchantBookFactory(10, EnchantmentTags.TRADEABLE),
            new TradeOffers.SellItemFactory(Items.GLASS, 1, 4, 10)
    };

    public static final TradeOffers.Factory[] LEVEL_4_TRADES = {
            new TradeOffers.BuyItemFactory(Items.WRITABLE_BOOK, 2, 12, 30),
            new TradeOffers.EnchantBookFactory(15, EnchantmentTags.TRADEABLE),
            new TradeOffers.SellItemFactory(Items.CLOCK, 5, 1, 15),
            new TradeOffers.SellItemFactory(Items.COMPASS, 4, 1, 15)
    };

    public static final TradeOffers.Factory[] LEVEL_5_TRADES = {
            new TradeOffers.SellItemFactory(Items.NAME_TAG, 20, 1, 30)
    };
}
