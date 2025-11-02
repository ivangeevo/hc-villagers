package org.ivangeevo.hc_villagers.trades.original;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffers;

public class VanillaWeaponsmithTrades {

    public static final TradeOffers.Factory[] LEVEL_1_TRADES = {
            new TradeOffers.BuyItemFactory(Items.COAL, 15, 16, 2),
            new TradeOffers.SellItemFactory(new ItemStack(Items.IRON_AXE), 3, 1, 12, 1, 0.2F),
            new TradeOffers.SellEnchantedToolFactory(Items.IRON_SWORD, 2, 3, 1)
    };

    public static final TradeOffers.Factory[] LEVEL_2_TRADES = {
            new TradeOffers.BuyItemFactory(Items.IRON_INGOT, 4, 12, 10),
            new TradeOffers.SellItemFactory(new ItemStack(Items.BELL), 36, 1, 12, 5, 0.2F)
    };

    public static final TradeOffers.Factory[] LEVEL_3_TRADES = {
            new TradeOffers.BuyItemFactory(Items.FLINT, 24, 12, 20)
    };

    public static final TradeOffers.Factory[] LEVEL_4_TRADES = {
            new TradeOffers.BuyItemFactory(Items.DIAMOND, 1, 12, 30),
            new TradeOffers.SellEnchantedToolFactory(Items.DIAMOND_AXE, 12, 3, 15, 0.2F)
    };

    public static final TradeOffers.Factory[] LEVEL_5_TRADES = {
            new TradeOffers.SellEnchantedToolFactory(Items.DIAMOND_SWORD, 8, 3, 30, 0.2F)
    };
}
