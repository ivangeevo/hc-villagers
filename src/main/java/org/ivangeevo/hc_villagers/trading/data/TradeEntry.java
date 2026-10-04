package org.ivangeevo.hc_villagers.trading.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.ComponentPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.util.math.random.Random;
import net.minecraft.entity.Entity;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import org.ivangeevo.hc_villagers.HCVillagersMod;

import java.util.Optional;
import java.util.stream.Stream;

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

    private static final float PRICE_MULTIPLIER = 0.05F;

    public Optional<TradeOffers.Factory> toFactory(String where) {
        Optional<Item> buyItem = resolve(buy, where);
        Optional<Item> sellItem = resolve(sell, where);
        Optional<Optional<Item>> buy2Item = buy2.map(spec -> resolve(spec, where));
        if (buyItem.isEmpty() || sellItem.isEmpty() || (buy2Item.isPresent() && buy2Item.get().isEmpty())) {
            return Optional.empty();
        }
        return Optional.of(new Factory(
                buyItem.get(), buy.activeCount(), buy,
                buy2Item.flatMap(item -> item).map(item -> new Factory.Side(item, buy2.get().activeCount(), buy2.get())),
                sellItem.get(), sell.activeCount(), sell));
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
    private record Factory(
            Item buy, CountRange buyCount, TradeItemSpec buySpec, Optional<Side> buy2,
            Item sell, CountRange sellCount, TradeItemSpec sellSpec
    ) implements TradeOffers.Factory {

        private record Side(Item item, CountRange count, TradeItemSpec spec) {}

        @Override
        public TradeOffer create(Entity entity, Random random) {
            var lookup = entity.getRegistryManager();

            boolean enchanting = sellSpec.enchantLevels().isPresent();

            TradedItem first = traded(buy, buyCount.roll(random, buy.getMaxCount()), buySpec.resolveComponents(lookup), enchanting && buy == sell);
            Optional<TradedItem> second = buy2.map(side ->
                    traded(side.item(), side.count().roll(random, side.item().getMaxCount()), side.spec().resolveComponents(lookup), false));

            ItemStack result = new ItemStack(sell, sellCount.roll(random, sell.getMaxCount()));
            result.applyChanges(sellSpec.resolveComponents(lookup));

            sellSpec.enchantLevels().ifPresent(range -> {
                int level = range.min() + random.nextInt(range.max() - range.min() + 1);
                var registries = entity.getRegistryManager();
                var pool = registries.get(RegistryKeys.ENCHANTMENT).getEntryList(EnchantmentTags.ON_TRADED_EQUIPMENT);
                EnchantmentHelper.enchant(random, result, level, registries, pool);
            });

            return new TradeOffer(first, second, result, 1, 0, PRICE_MULTIPLIER);
        }

        /** Buy side: components become a predicate the player's item must match. */
        private static TradedItem traded(Item item, int count, ComponentChanges changes, boolean unenchanted) {
            if (changes.isEmpty() && !unenchanted) return new TradedItem(item, count);
            ComponentMap.Builder builder = ComponentMap.builder();
            for (var entry : changes.entrySet()) {
                entry.getValue().ifPresent(value -> put(builder, entry.getKey(), value));
            }
            return new TradedItem(Registries.ITEM.getEntry(item), count, ComponentPredicate.of(builder.build()));
        }

        @SuppressWarnings("unchecked")
        private static <T> void put(ComponentMap.Builder builder, ComponentType<T> type, Object value) {
            builder.add(type, (T) value);
        }
    }
}