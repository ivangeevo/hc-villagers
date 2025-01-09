package org.ivangeevo.hcVillagers.trades;

import btwr.core.item.BTWR_Items;
import com.bwt.blocks.BwtBlocks;
import com.bwt.items.BwtItems;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.provider.TradeRebalanceEnchantmentProviders;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.ivangeevo.animageddon.item.ModItems;
import org.ivangeevo.btwr_ds.item.BTWRDS_Items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            //new TradeOffers.BuyItemFactory(getOptionalItem(AG, ModItems.BAT_WING, Items.INK_SAC), arithmeticMean(14,16), 12, 10),
            new TradeOffers.BuyItemFactory(Items.SPIDER_EYE, arithmeticMean(4,8), 12, 10),
            new TradeOffers.BuyItemFactory(getOptionalItem(AG, ModItems.NITRE, Items.GUNPOWDER), arithmeticMean(32,48), 12, 10),
            new TradeOffers.BuyItemFactory(Items.GLOWSTONE, arithmeticMean(24,32), 12, 10),
            new TradeOffers.BuyItemFactory(Items.NETHER_WART, arithmeticMean(16,24), 12, 10),
    };

    private static final TradeOffers.Factory[] LEVEL_3_TRADES = {
            new TradeOffers.BuyItemFactory(Items.REDSTONE, arithmeticMean(32,48), 12, 20),
            //new TradeOffers.BuyItemFactory(getOptionalItem(AG, ModItems.MYSTERIOUS_GLAND, Items.GLOW_INK_SAC, arithmeticMean(14,16), 12, 20)),
            //new TradeOffers.BuyItemFactory(getOptionalItem(AG, ModItems.VENOM_SACK, Items.SPIDER_EYE, arithmeticMean(4,8), 12, 20),
            //new TradeOffers.BuyItemFactory(getOptionalItem(AG, ModItems.WITCH_WART, Items.SPIDER_EYE, arithmeticMean(4,8), 12, 20),
            new TradeOffers.BuyItemFactory(Items.MAGMA_CREAM, arithmeticMean(8,12), 12, 20),
            new TradeOffers.BuyItemFactory(Items.BLAZE_POWDER, arithmeticMean(4,6), 12, 20),
            new TradeOffers.BuyItemFactory(Items.GHAST_TEAR, arithmeticMean(4,6), 12, 20),
    };

    private static final TradeOffers.Factory[] LEVEL_4_TRADES = {
            new TradeOffers.BuyItemFactory(getOptionalItem(BWT, BwtBlocks.detectorBlock, Items.DISPENSER), arithmeticMean(4,8), 12, 20),
            new TradeOffers.BuyItemFactory(getOptionalItem(BWT, BwtBlocks.lensBlock, Items.DAYLIGHT_DETECTOR), arithmeticMean(4,8), 12, 20),
            new TradeOffers.BuyItemFactory(getOptionalItem(BWT, BwtBlocks.buddyBlock, Items.OBSERVER), arithmeticMean(4,8), 12, 20),
            new TradeOffers.BuyItemFactory(getOptionalItem(BWT, BwtBlocks.blockDispenserBlock, Items.STICKY_PISTON), arithmeticMean(4,8), 12, 20),
    };

    private static final TradeOffers.Factory[] LEVEL_5_TRADES = {
            new TradeOffers.BuyItemFactory(getOptionalItem(BWT, BwtBlocks.bloodWoodBlocks.saplingItem, Items.CRIMSON_FUNGUS), arithmeticMean(8,16), 12, 20),
            //new TradeOffers.BuyItemFactory(getOptionalItem(BWT, BwtBlocks.netherGrothSpores, Items.WARPED_FUNGUS), arithmeticMean(8,16), 12, 20),
            new TradeOffers.BuyItemFactory(getOptionalItem(BWT, BTWRDS_Items.BRIMSTONE, Items.GUNPOWDER), arithmeticMean(16,32), 12, 20),
            //new TradeOffers.SellItemFactory(getOptionalItem(BWT, BwtItems.arcaneScrollPower, Items.GUNPOWDER).asItem(), arithmeticMean(32,48), 12, 20),
            new TradeOffers.ProcessItemFactory(Items.ENDER_PEARL, 1, arithmeticMean(6,8), Items.ENDER_EYE, 1, 3, 30, 0.05F),
    };

    public static List<TradeOffers.Factory[]> NEW_TRADES = List.of(LEVEL_1_TRADES, LEVEL_2_TRADES, LEVEL_3_TRADES, LEVEL_4_TRADES, LEVEL_5_TRADES);

}
