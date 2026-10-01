package org.ivangeevo.hc_villagers.trades;

import net.minecraft.village.TradeOffers;
import net.minecraft.item.Items;

import org.ivangeevo.hc_villagers.trading.HCTradeTable;

import java.util.List;

public class ModFarmerTrades extends AbstractVillagerTradesProvider {

    private static final TradeOffers.Factory[] LEVEL_1_TRADES = {
            lazy(() -> new TradeOffers.BuyItemFactory(modItem(TE, "dirt_loose", Items.DIRT), arithmeticMean(48, 64), 16, 2)),
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
            //lazy(() -> new TradeOffers.BuyItemFactory(modItem(BWT, "hemp_seeds", Items.WHEAT_SEEDS), arithmeticMean(24,32), 4, 5)),
            new TradeOffers.BuyItemFactory(Items.SUGAR, arithmeticMean(10,20), 8, 5),
            new TradeOffers.BuyItemFactory(Items.COCOA_BEANS, arithmeticMean(10,16), 4, 5),
            //lazy(() -> new TradeOffers.BuyItemFactory(modItem(BWT, "flour", Items.WHEAT), arithmeticMean(24,32), 8, 5)),
            new TradeOffers.BuyItemFactory(Items.BROWN_MUSHROOM, arithmeticMean(10,16), 8, 5),
            new TradeOffers.BuyItemFactory(Items.GLASS_PANE, arithmeticMean(16,32), 8, 5),
            new TradeOffers.BuyItemFactory(Items.EGG, 12, 4, 5),

            new TradeOffers.SellItemFactory(Items.WHEAT, 1, arithmeticMean(8,16), 16, 5),
            new TradeOffers.SellItemFactory(Items.APPLE, 1, arithmeticMean(2,4), 16, 5)
    };

    private static final TradeOffers.Factory[] LEVEL_3_TRADES = {
            new TradeOffers.BuyItemFactory(Items.SHEARS, 1, 6, 20),
            new TradeOffers.BuyItemFactory(Items.FLINT_AND_STEEL, 1, 6, 10),
            lazy(() -> new TradeOffers.BuyItemFactory(modItem(VG, "chocolate", Items.COCOA_BEANS), arithmeticMean(1,2), 8, 10)),
            new TradeOffers.BuyItemFactory(Items.PUMPKIN, arithmeticMean(10,16), 8, 10),
            new TradeOffers.BuyItemFactory(Items.MELON, arithmeticMean(8,10), 8, 10),
            lazy(() -> new TradeOffers.BuyItemFactory(modItem(ST, "stump_remover", Items.BOWL), arithmeticMean(8,12), 8, 10)),
            //lazy(() -> new TradeOffers.BuyItemFactory(modItem(BTWR, "stake", Items.STICK), arithmeticMean(16,32), 2, 10)), // ID unverified

            new TradeOffers.SellItemFactory(Items.BREAD, 1, arithmeticMean(4,6), 8, 10),
            lazy(() -> new TradeOffers.SellItemFactory(modItem(BTWR, "egg_scrambled_cooked", Items.EGG).asItem(), 1, arithmeticMean(8,12), 8, 10)),
            lazy(() -> new TradeOffers.SellItemFactory(modItem(BTWR, "mushroom_omelette_cooked", Items.WHEAT).asItem(), 1, arithmeticMean(8,12), 8, 10))
    };

    private static final TradeOffers.Factory[] LEVEL_4_TRADES = {
            //lazy(() -> new TradeOffers.BuyItemFactory(modItem(BWT, "cement_bucket", Items.LAVA_BUCKET), 1, 12, 15, 3)),
            //lazy(() -> new TradeOffers.BuyItemFactory(modItem(BWT, "light_block", Items.REDSTONE_LAMP), arithmeticMean(2,4), 12, 15)),

            new TradeOffers.SellItemFactory(Items.PUMPKIN_PIE, arithmeticMean(1,2), 1, 8, 15),
            new TradeOffers.SellItemFactory(Items.COOKIE, arithmeticMean(8,16), 1, 8, 15),
            new TradeOffers.SellItemFactory(Items.CAKE, 1, 1, 8, 15),
    };

    // No new random trades at level 5 (BTW: "Nothing new"); random slots roll from levels 1-4 without "+".
    // Mycelium moved to the guaranteed level 5 trades below.
    private static final TradeOffers.Factory[] LEVEL_5_TRADES = {
            //new TradeOffers.SellItemFactory(Items.ENCHANTED_BOOK.getDefaultStack(), 4, 1, 30)
    };

    public static List<TradeOffers.Factory[]> NEW_TRADES = List.of(LEVEL_1_TRADES, LEVEL_2_TRADES, LEVEL_3_TRADES, LEVEL_4_TRADES, LEVEL_5_TRADES);

    // ---- Guaranteed trades: always shown, never run out, no "+" (max uses / XP are set by HCTradeLogic) ----
    private static final TradeOffers.Factory[] NONE = {};

    private static final TradeOffers.Factory[] GUARANTEED_LEVEL_2 = {
            // TODO: BTW sells Sugar Cane Roots here; swap in the real item once it exists
            new TradeOffers.SellItemFactory(Items.SUGAR_CANE, arithmeticMean(2, 3), 1, 1, 0),
    };

    private static final TradeOffers.Factory[] GUARANTEED_LEVEL_5 = {
            new TradeOffers.SellItemFactory(Items.MYCELIUM, arithmeticMean(12, 16), 1, 1, 0),
            // TODO: Arcane Scroll of Looting (48-64 emeralds + paper) once BWT has arcane scrolls
    };

    public static List<TradeOffers.Factory[]> GUARANTEED_TRADES = List.of(NONE, GUARANTEED_LEVEL_2, NONE, NONE, GUARANTEED_LEVEL_5);

    // ---- "++" level-up trades: index 0 = level 1->2. Pays emeralds, levels the villager up when traded ----
    private static final TradeOffers.Factory[] LEVEL_UP_TRADES = {
            new TradeOffers.BuyItemFactory(Items.IRON_HOE, 1, 1, 0, 1),
            lazy(() -> new TradeOffers.BuyItemFactory(modItem(BWT, "mill_stone", Items.GRINDSTONE), 1, 1, 0, 2)),
            lazy(() -> new TradeOffers.BuyItemFactory(modItem(BWT, "water_wheel", Items.WATER_BUCKET), 1, 1, 0, 3)),
            new BuyVariableItemCountFactory(modItem(BWT, "planter", Items.FLOWER_POT), 16, 24, 1, 0, 1), // resolves the item in create(), no lazy() needed
    };

    public static final HCTradeTable TABLE = new HCTradeTable(NEW_TRADES, GUARANTEED_TRADES, LEVEL_UP_TRADES);
}
