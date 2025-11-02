package org.ivangeevo.hc_villagers.trades.original;

import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffers;

public class VanillaFarmerTrades {

    public static final TradeOffers.Factory[] LEVEL_1_TRADES = {
            new TradeOffers.BuyItemFactory(Items.WHEAT, 20, 16, 2),
            new TradeOffers.BuyItemFactory(Items.POTATO, 26, 16, 2),
            new TradeOffers.BuyItemFactory(Items.CARROT, 22, 16, 2),
            new TradeOffers.BuyItemFactory(Items.BEETROOT, 15, 16, 2),
            new TradeOffers.SellItemFactory(Items.BREAD, 1, 6, 16, 1)
    };

    public static final TradeOffers.Factory[] LEVEL_2_TRADES = {
            new TradeOffers.BuyItemFactory(Blocks.PUMPKIN, 6, 12, 10),
            new TradeOffers.SellItemFactory(Items.PUMPKIN_PIE, 1, 4, 5),
            new TradeOffers.SellItemFactory(Items.APPLE, 1, 4, 16, 5)
    };

    public static final TradeOffers.Factory[] LEVEL_3_TRADES = {
            new TradeOffers.SellItemFactory(Items.COOKIE, 3, 18, 10),
            new TradeOffers.BuyItemFactory(Blocks.MELON, 4, 12, 20)
    };

    public static final TradeOffers.Factory[] LEVEL_4_TRADES = {
            new TradeOffers.SellItemFactory(Blocks.CAKE, 1, 1, 12, 15),
            new TradeOffers.SellSuspiciousStewFactory(StatusEffects.NIGHT_VISION, 100, 15),
            new TradeOffers.SellSuspiciousStewFactory(StatusEffects.JUMP_BOOST, 160, 15),
            new TradeOffers.SellSuspiciousStewFactory(StatusEffects.WEAKNESS, 140, 15),
            new TradeOffers.SellSuspiciousStewFactory(StatusEffects.BLINDNESS, 120, 15),
            new TradeOffers.SellSuspiciousStewFactory(StatusEffects.POISON, 280, 15),
            new TradeOffers.SellSuspiciousStewFactory(StatusEffects.SATURATION, 7, 15)
    };

    public static final TradeOffers.Factory[] LEVEL_5_TRADES = {
            new TradeOffers.SellItemFactory(Items.GOLDEN_CARROT, 3, 3, 30),
            new TradeOffers.SellItemFactory(Items.GLISTERING_MELON_SLICE, 4, 3, 30)
    };
}
