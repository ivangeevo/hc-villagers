package org.ivangeevo.hc_villagers.trades;

import net.minecraft.item.Items;
import net.minecraft.village.TradeOffers;
import org.ivangeevo.hc_villagers.trading.HCTradeTable;

import java.util.List;

public class ModLibrarianTrades extends AbstractVillagerTradesProvider {

    private static final TradeOffers.Factory[] LEVEL_1_TRADES = {
            new TradeOffers.BuyItemFactory(Items.PAPER, arithmeticMean(27,38), 16, 2),
            new TradeOffers.BuyItemFactory(Items.INK_SAC, arithmeticMean(27,38), 16, 2),
            new TradeOffers.BuyItemFactory(Items.FEATHER, arithmeticMean(27,38), 16, 2),
    };

    private static final TradeOffers.Factory[] LEVEL_2_TRADES = {
            new TradeOffers.BuyItemFactory(Items.BOOKSHELF, 1, 12, 10),
            new TradeOffers.BuyItemFactory(Items.BOOK, arithmeticMean(1,3), 12, 10),
            new TradeOffers.BuyItemFactory(Items.WRITABLE_BOOK, arithmeticMean(1,3), 12, 10),
            lazy(() -> new TradeOffers.BuyItemFactory(modItem(AG, "bat_wing", Items.INK_SAC), arithmeticMean(14,16), 12, 10)),
            new TradeOffers.BuyItemFactory(Items.SPIDER_EYE, arithmeticMean(4,8), 12, 10),
            lazy(() -> new TradeOffers.BuyItemFactory(modItem(AG, "nitre", Items.GUNPOWDER), arithmeticMean(32,48), 12, 10)),
            new TradeOffers.BuyItemFactory(Items.GLOWSTONE, arithmeticMean(24,32), 12, 10),
            new TradeOffers.BuyItemFactory(Items.NETHER_WART, arithmeticMean(16,24), 12, 10),
    };

    private static final TradeOffers.Factory[] LEVEL_3_TRADES = {
            new TradeOffers.BuyItemFactory(Items.REDSTONE, arithmeticMean(32,48), 12, 20),
            //lazy(() -> new TradeOffers.BuyItemFactory(modItem(AG, "mysterious_gland", Items.GLOW_INK_SAC), arithmeticMean(14,16), 12, 20)), // ID unverified
            //lazy(() -> new TradeOffers.BuyItemFactory(modItem(AG, "venom_sack", Items.SPIDER_EYE), arithmeticMean(4,8), 12, 20)), // ID unverified
            //lazy(() -> new TradeOffers.BuyItemFactory(modItem(AG, "witch_wart", Items.SPIDER_EYE), arithmeticMean(4,8), 12, 20)), // ID unverified
            new TradeOffers.BuyItemFactory(Items.MAGMA_CREAM, arithmeticMean(8,12), 12, 20),
            new TradeOffers.BuyItemFactory(Items.BLAZE_POWDER, arithmeticMean(4,6), 12, 20),
            new TradeOffers.BuyItemFactory(Items.GHAST_TEAR, arithmeticMean(4,6), 12, 20),
    };

    private static final TradeOffers.Factory[] LEVEL_4_TRADES = {
            lazy(() -> new TradeOffers.BuyItemFactory(modItem(BWT, "detector_block", Items.DISPENSER), arithmeticMean(4,8), 12, 20)),
            lazy(() -> new TradeOffers.BuyItemFactory(modItem(BWT, "lens", Items.DAYLIGHT_DETECTOR), arithmeticMean(4,8), 12, 20)),
            lazy(() -> new TradeOffers.BuyItemFactory(modItem(BWT, "buddy_block", Items.OBSERVER), arithmeticMean(4,8), 12, 20)),
            lazy(() -> new TradeOffers.BuyItemFactory(modItem(BWT, "block_dispenser", Items.STICKY_PISTON), arithmeticMean(4,8), 12, 20)),
    };

    private static final TradeOffers.Factory[] LEVEL_5_TRADES = {
            lazy(() -> new TradeOffers.BuyItemFactory(modItem(BWT, "blood_wood_sapling", Items.CRIMSON_FUNGUS), arithmeticMean(8,16), 12, 20)),
            lazy(() -> new TradeOffers.BuyItemFactory(modItem(BWT, "nether_groth", Items.WARPED_FUNGUS), arithmeticMean(8,16), 12, 20)),
            lazy(() -> new TradeOffers.BuyItemFactory(modItem(BTWR_DS, "brimstone", Items.GUNPOWDER), arithmeticMean(16,32), 12, 20)),
            //lazy(() -> new TradeOffers.SellItemFactory(modItem(BWT, "arcane_scroll_power", Items.GUNPOWDER).asItem(), arithmeticMean(32,48), 1, 12, 20)), // ID unverified
            // Eye of Ender moved to the guaranteed level 5 trades below
    };

    public static List<TradeOffers.Factory[]> NEW_TRADES = List.of(LEVEL_1_TRADES, LEVEL_2_TRADES, LEVEL_3_TRADES, LEVEL_4_TRADES, LEVEL_5_TRADES);

    // ---- Guaranteed trades: always shown, never run out, no "+" (max uses / XP are set by HCTradeLogic) ----
    private static final TradeOffers.Factory[] NONE = {};

    private static final TradeOffers.Factory[] GUARANTEED_LEVEL_5 = {
            new TradeOffers.ProcessItemFactory(Items.ENDER_PEARL, 1, arithmeticMean(6,8), Items.ENDER_EYE, 1, 1, 0, 0.05F),
            // TODO: Arcane Scroll of Power (48-64 emeralds + paper) once BWT has arcane scrolls
    };

    // TODO: level 2 guaranteed Ancient Manuscript (book and quill + 2-3 emeralds) once the item exists
    public static List<TradeOffers.Factory[]> GUARANTEED_TRADES = List.of(NONE, NONE, NONE, NONE, GUARANTEED_LEVEL_5);

    // ---- "++" level-up trades: index 0 = level 1->2. Pays emeralds, levels the villager up when traded ----
    // Fallback items stand in for BTW items that don't exist yet (Ancient Manuscript, Ender Spectacles).
    private static final TradeOffers.Factory[] LEVEL_UP_TRADES = {
            new TradeOffers.BuyItemFactory(Items.WRITABLE_BOOK, 1, 1, 0, 2),        // Ancient Manuscript
            new TradeOffers.BuyItemFactory(Items.BREWING_STAND, 1, 1, 0, 2),
            lazy(() -> new TradeOffers.BuyItemFactory(modItem(BWT, "soul_forge", Items.NETHER_STAR), 1, 1, 0, 3)),
            new TradeOffers.BuyItemFactory(Items.SPYGLASS, 1, 1, 0, 4),             // Ender Spectacles
    };

    public static final HCTradeTable TABLE = new HCTradeTable(NEW_TRADES, GUARANTEED_TRADES, LEVEL_UP_TRADES);


}
