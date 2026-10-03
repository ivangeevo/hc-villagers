package org.ivangeevo.hc_villagers.trading.data;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.ivangeevo.hc_villagers.HCVillagersMod;

import java.util.Map;
import java.util.Optional;

public record TradeItemSpec(Identifier item, Optional<Identifier> fallback, CountRange count,
                            Map<Identifier, JsonElement> components, Map<Identifier, JsonElement> fallbackComponents) {

    private static final Codec<TradeItemSpec> FULL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("item").forGetter(TradeItemSpec::item),
            Identifier.CODEC.optionalFieldOf("fallback").forGetter(TradeItemSpec::fallback),
            CountRange.CODEC.optionalFieldOf("count", CountRange.ONE).forGetter(TradeItemSpec::count),
            Codec.unboundedMap(Identifier.CODEC, Codecs.JSON_ELEMENT)
                    .optionalFieldOf("components", Map.of()).forGetter(TradeItemSpec::components),
            Codec.unboundedMap(Identifier.CODEC, Codecs.JSON_ELEMENT)
                    .optionalFieldOf("fallback_components", Map.of()).forGetter(TradeItemSpec::fallbackComponents)
    ).apply(instance, TradeItemSpec::new));

    public static final Codec<TradeItemSpec> CODEC = Codec.either(Identifier.CODEC, FULL_CODEC).xmap(
            either -> either.map(id -> new TradeItemSpec(id, Optional.empty(), CountRange.ONE, Map.of(), Map.of()), spec -> spec),
            Either::right);

    /** The item to use: {@code item} if registered, else {@code fallback} if registered, else empty. */
    public Optional<Item> resolve() {
        Optional<Item> primary = Registries.ITEM.getOrEmpty(item);
        if (primary.isPresent()) return primary;
        return fallback.flatMap(Registries.ITEM::getOrEmpty);
    }

    public ComponentChanges resolveComponents(RegistryWrapper.WrapperLookup lookup) {
        Map<Identifier, JsonElement> source =
                Registries.ITEM.getOrEmpty(item).isPresent() ? components : fallbackComponents;
        if (source.isEmpty()) return ComponentChanges.EMPTY;

        RegistryOps<JsonElement> ops = RegistryOps.of(JsonOps.INSTANCE, lookup);
        ComponentChanges.Builder builder = ComponentChanges.builder();
        source.forEach((id, json) -> {
            Optional<ComponentType<?>> type = Registries.DATA_COMPONENT_TYPE.getOrEmpty(id);
            if (type.isEmpty()) {
                HCVillagersMod.LOGGER.debug("[{}] Ignoring unknown component '{}'", HCVillagersMod.MOD_ID, id);
                return;
            }
            add(builder, type.get(), json, id, ops);
        });
        return builder.build();
    }

    private static <T> void add(ComponentChanges.Builder builder, ComponentType<T> type, JsonElement json,
                                Identifier id, RegistryOps<JsonElement> ops) {
        Codec<T> codec = type.getCodec();
        if (codec == null) {
            HCVillagersMod.LOGGER.debug("[{}] Component '{}' can't be loaded from JSON", HCVillagersMod.MOD_ID, id);
            return;
        }
        codec.parse(ops, json)
                .resultOrPartial(err -> HCVillagersMod.LOGGER.warn("[{}] Bad value for component '{}': {}",
                        HCVillagersMod.MOD_ID, id, err))
                .ifPresent(value -> builder.add(type, value));
    }
}