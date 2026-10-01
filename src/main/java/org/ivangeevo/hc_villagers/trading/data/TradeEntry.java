package org.ivangeevo.hc_villagers.trading.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import net.minecraft.entity.Entity;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import org.ivangeevo.hc_villagers.HCVillagersMod;

import java.util.Optional;

/**
 * One trade in JSON:
 * <pre>
 * { "buy": { "item": "minecraft:oak_log", "count": [32, 48] }, "sell": "minecraft:emerald" }
 * { "buy": { "item": "minecraft:emerald", "count": [6, 8] }, "buy_2": "minecraft:ender_pearl", "sell": "minecraft:ender_eye" }
 * </pre>
 * {@code buy} is what the player gives, {@code buy_2} an optional second item, {@code sell} what the villager gives.
 * Max uses and villager XP aren't set here: the BTW trading logic decides those from where the trade is listed.
 */
public record TradeEntry(TradeItemSpec buy, Optional<TradeItemSpec> buy2, TradeItemSpec sell) {

    public static final Codec<TradeEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TradeItemSpec.CODEC.fieldOf("buy").forGetter(TradeEntry::buy),
            TradeItemSpec.CODEC.optionalFieldOf("buy_2").forGetter(TradeEntry::buy2),
            TradeItemSpec.CODEC.fieldOf("sell").forGetter(TradeEntry::sell)
    ).apply(instance, TradeEntry::new));

    /** Price multiplier used by vanilla demand pricing; BTW trades don't restock, so this barely matters. */
    private static final float PRICE_MULTIPLIER = 0.05F;

    /**
     * Resolves the item IDs (registries are complete when data packs load) and builds the factory.
     * Empty if an item and its fallback are both missing.
     */
    public Optional<TradeOffers.Factory> toFactory(String where) {
        Optional<Item> buyItem = resolve(buy, where);
        Optional<Item> sellItem = resolve(sell, where);
        Optional<Optional<Item>> buy2Item = buy2.map(spec -> resolve(spec, where));
        if (buyItem.isEmpty() || sellItem.isEmpty() || (buy2Item.isPresent() && buy2Item.get().isEmpty())) {
            return Optional.empty();
        }
        return Optional.of(new Factory(
                buyItem.get(), buy.count(),
                buy2Item.flatMap(item -> item).map(item -> new Factory.Side(item, buy2.get().count())),
                sellItem.get(), sell.count()));
    }

    private static Optional<Item> resolve(TradeItemSpec spec, String where) {
        Optional<Item> item = spec.resolve();
        if (item.isEmpty()) {
            HCVillagersMod.LOGGER.debug("[{}] Skipping trade in {}: '{}' isn't registered and has no usable fallback",
                    HCVillagersMod.MOD_ID, where, spec.item());
        }
        return item;
    }

    /** Creates a fresh offer with rolled counts every time a trade slot is filled. */
    private record Factory(Item buy, CountRange buyCount, Optional<Side> buy2, Item sell, CountRange sellCount)
            implements TradeOffers.Factory {

        private record Side(Item item, CountRange count) {}

        @Override
        public TradeOffer create(Entity entity, Random random) {
            TradedItem first = new TradedItem(buy, buyCount.roll(random, buy.getMaxCount()));
            Optional<TradedItem> second = buy2.map(side -> new TradedItem(side.item(), side.count().roll(random, side.item().getMaxCount())));
            ItemStack result = new ItemStack(sell, sellCount.roll(random, sell.getMaxCount()));
            // maxUses / xp are overwritten by HCTradeLogic.retag()
            return new TradeOffer(first, second, result, 1, 0, PRICE_MULTIPLIER);
        }
    }
}
