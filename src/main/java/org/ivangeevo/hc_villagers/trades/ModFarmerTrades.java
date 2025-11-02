package org.ivangeevo.hc_villagers.trades;

import btwr.core.item.BTWR_Items;
import ivangeevo.sturdy_trees.item.SturdyTreesItems;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffers;
import org.ivangeevo.vegehenna.item.ModItems;
import org.tough_environment.block.ModBlocks;

import java.util.List;

public class ModFarmerTrades extends AbstractVillagerTradesProvider {

    private static final TradeOffers.Factory[] LEVEL_1_TRADES = {
            new TradeOffers.BuyItemFactory(getOptionalItem(TE, ModBlocks.DIRT_LOOSE, Items.DIRT), arithmeticMean(48, 64), 16, 2),
            new TradeOffers.BuyItemFactory(Items.OAK_LOG, arithmeticMean(32, 48), 16, 2),
            new TradeOffers.BuyItemFactory(Items.SPRUCE_LOG, arithmeticMean(32, 48), 16, 2),
            new TradeOffers.BuyItemFactory(Items.BIRCH_LOG, arithmeticMean(32, 48), 16, 2),
            new TradeOffers.BuyItemFactory(Items.JUNGLE_LOG, arithmeticMean(32, 48), 16, 2),
            new TradeOffers.BuyItemFactory(Items.ACACIA_LOG, arithmeticMean(32, 48), 16, 2),
            new TradeOffers.BuyItemFactory(Items.DARK_OAK_BOAT, arithmeticMean(32, 48), 16, 2),
            new TradeOffers.BuyItemFactory(Items.MANGROVE_LOG, arithmeticMean(32, 48), 16, 2),
            new TradeOffers.BuyItemFactory(Items.CHERRY_LOG, arithmeticMean(32, 48), 16, 2),
            new TradeOffers.BuyItemFactory(Items.BONE_MEAL, arithmeticMean(32, 48), 16, 2),
            new TradeOffers.BuyItemFactory(Items.BROWN_WOOL, arithmeticMean(48, 64), 6, 16)
    };

    private static final TradeOffers.Factory[] LEVEL_2_TRADES = {
            new TradeOffers.BuyItemFactory(Items.MILK_BUCKET, 1, 12, 10,2),
            //new TradeOffers.BuyItemFactory(getOptionalItem(BTWR, BwtItems.hempSeedsItem, Items.WHEAT_SEEDS), arithmeticMean(24,32), 4, 5),
            new TradeOffers.BuyItemFactory(Items.SUGAR, arithmeticMean(10,20), 8, 5),
            new TradeOffers.BuyItemFactory(Items.COCOA_BEANS, arithmeticMean(10,16), 4, 5),
            //new TradeOffers.BuyItemFactory(getOptionalItem(BWT, BwtItems.flourItem, Items.WHEAT), arithmeticMean(24,32), 8, 5),
            new TradeOffers.BuyItemFactory(Items.BROWN_MUSHROOM, arithmeticMean(10,16), 8, 5),
            new TradeOffers.BuyItemFactory(Items.GLASS_PANE, arithmeticMean(16,32), 8, 5),
            new TradeOffers.BuyItemFactory(Items.EGG, 12, 4, 5),

            new TradeOffers.SellItemFactory(Items.WHEAT, 1, arithmeticMean(8,16), 16, 5),
            new TradeOffers.SellItemFactory(Items.APPLE, 1, arithmeticMean(2,4), 16, 5)
    };

    private static final TradeOffers.Factory[] LEVEL_3_TRADES = {
            new TradeOffers.BuyItemFactory(Items.SHEARS, 1, 6, 20),
            new TradeOffers.BuyItemFactory(Items.FLINT_AND_STEEL, 1, 6, 10),
            new TradeOffers.BuyItemFactory(getOptionalItem(VG, ModItems.CHOCOLATE, Items.COCOA_BEANS), arithmeticMean(1,2), 8, 10),
            new TradeOffers.BuyItemFactory(Items.PUMPKIN, arithmeticMean(10,16), 8, 10),
            new TradeOffers.BuyItemFactory(Items.MELON, arithmeticMean(8,10), 8, 10),
            new TradeOffers.BuyItemFactory(getOptionalItem(ST, SturdyTreesItems.STUMP_REMOVER, Items.BOWL), arithmeticMean(8,12), 8, 10),
            //new TradeOffers.BuyItemFactory(BTWR_Items.STAKE, arithmeticMean(16,32), 2, 10)

            new TradeOffers.SellItemFactory(Items.BREAD, 1, arithmeticMean(4,6), 8, 10),
            new TradeOffers.SellItemFactory(getOptionalItem(BTWR, BTWR_Items.EGG_SCRAMBLED_COOKED, Items.EGG).asItem(), 1, arithmeticMean(8,12), 8, 10),
            new TradeOffers.SellItemFactory(getOptionalItem(BTWR, BTWR_Items.MUSHROOM_OMELETTE_COOKED, Items.WHEAT).asItem(), 1, arithmeticMean(8,12), 8, 10)
    };

    private static final TradeOffers.Factory[] LEVEL_4_TRADES = {
            //new TradeOffers.BuyItemFactory(getOptionalItem(BWT, BwtItems.cementBucketItem, Items.LAVA_BUCKET), 1, 3, 12, 15),
            //new TradeOffers.BuyItemFactory(getOptionalItem(BWT, BwtBlocks.lightBlockBlock, Items.REDSTONE_LAMP), arithmeticMean(2,4), 1, 12, 15),

            new TradeOffers.SellItemFactory(Items.PUMPKIN_PIE, arithmeticMean(1,2), 1, 8, 15),
            new TradeOffers.SellItemFactory(Items.COOKIE, arithmeticMean(8,16), 1, 8, 15),
            new TradeOffers.SellItemFactory(Items.CAKE, 1, 1, 8, 15),
    };

    private static final TradeOffers.Factory[] LEVEL_5_TRADES = {
            new TradeOffers.SellItemFactory(Items.MYCELIUM, arithmeticMean(10,20), 1, 30),
            //new TradeOffers.SellItemFactory(Items.ENCHANTED_BOOK.getDefaultStack(), 4, 1, 30)
    };

    public static List<TradeOffers.Factory[]> NEW_TRADES = List.of(LEVEL_1_TRADES, LEVEL_2_TRADES, LEVEL_3_TRADES, LEVEL_4_TRADES, LEVEL_5_TRADES);
}
