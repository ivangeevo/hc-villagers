package org.ivangeevo.hc_villagers.trading.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Optional;

/**
 * One side of a trade in JSON. Either just an item ID ({@code "minecraft:paper"}, count 1), or:
 * <pre>
 * { "item": "bwt:mill_stone", "count": [1, 2], "fallback": "minecraft:grindstone" }
 * </pre>
 * Items are looked up by registry ID, so items from other mods need no code dependency. If {@code item} isn't
 * registered (that mod isn't installed), {@code fallback} is used; if there's no usable fallback either, the
 * trade is skipped.
 */
public record TradeItemSpec(Identifier item, Optional<Identifier> fallback, CountRange count) {

    private static final Codec<TradeItemSpec> FULL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("item").forGetter(TradeItemSpec::item),
            Identifier.CODEC.optionalFieldOf("fallback").forGetter(TradeItemSpec::fallback),
            CountRange.CODEC.optionalFieldOf("count", CountRange.ONE).forGetter(TradeItemSpec::count)
    ).apply(instance, TradeItemSpec::new));

    public static final Codec<TradeItemSpec> CODEC = Codec.either(Identifier.CODEC, FULL_CODEC).xmap(
            either -> either.map(id -> new TradeItemSpec(id, Optional.empty(), CountRange.ONE), spec -> spec),
            Either::right);

    /** The item to use: {@code item} if registered, else {@code fallback} if registered, else empty. */
    public Optional<Item> resolve() {
        Optional<Item> primary = Registries.ITEM.getOrEmpty(item);
        if (primary.isPresent()) return primary;
        return fallback.flatMap(Registries.ITEM::getOrEmpty);
    }
}
