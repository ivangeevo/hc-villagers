package org.ivangeevo.hc_villagers.trading.data;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.loader.api.FabricLoader;
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
                            Map<Identifier, JsonElement> components, Map<Identifier, JsonElement> fallbackComponents,
                            Optional<CountRange> enchantLevels, Optional<String> requiredMod, Optional<CountRange> fallbackCount
) {

    private static final Codec<TradeItemSpec> FULL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("item").forGetter(TradeItemSpec::item),
            Identifier.CODEC.optionalFieldOf("fallback").forGetter(TradeItemSpec::fallback),
            CountRange.CODEC.optionalFieldOf("count", CountRange.ONE).forGetter(TradeItemSpec::count),
            Codec.unboundedMap(Identifier.CODEC, Codecs.JSON_ELEMENT)
                    .optionalFieldOf("components", Map.of()).forGetter(TradeItemSpec::components),
            Codec.unboundedMap(Identifier.CODEC, Codecs.JSON_ELEMENT)
                    .optionalFieldOf("fallback_components", Map.of()).forGetter(TradeItemSpec::fallbackComponents),
            CountRange.CODEC.optionalFieldOf("enchant_levels").forGetter(TradeItemSpec::enchantLevels),
            Codec.STRING.optionalFieldOf("required_mod").forGetter(TradeItemSpec::requiredMod),
            CountRange.CODEC.optionalFieldOf("fallback_count").forGetter(TradeItemSpec::fallbackCount)
    ).apply(instance, TradeItemSpec::new));

    public static final Codec<TradeItemSpec> CODEC = Codec.either(Identifier.CODEC, FULL_CODEC).xmap(
            either -> either.map(id -> new TradeItemSpec(
                    id,
                    Optional.empty(),
                    CountRange.ONE,
                    Map.of(),
                    Map.of(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()
            ), spec -> spec),
            Either::right);

    /** True if the primary item is usable; registered and the required mod (if any) is loaded **/
    public boolean usesPrimary() {
        return Registries.ITEM.getOrEmpty(item).isPresent()
                && requiredMod.map(FabricLoader.getInstance()::isModLoaded).orElse(true);
    }

    public Optional<Item> resolve() {
        if (usesPrimary()) return Registries.ITEM.getOrEmpty(item);
        return fallback.flatMap(Registries.ITEM::getOrEmpty);
    }

    public ComponentChanges resolveComponents(RegistryWrapper.WrapperLookup lookup) {
        Map<Identifier, JsonElement> source = usesPrimary() ? components : fallbackComponents;
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

    /** Count for whichever item is actually used. **/
    public CountRange activeCount() {
        return usesPrimary() ? count : fallbackCount.orElse(count);
    }
}