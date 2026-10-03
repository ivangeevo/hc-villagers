package org.ivangeevo.hc_villagers.datagen.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.ivangeevo.hc_villagers.trading.data.CountRange;
import org.ivangeevo.hc_villagers.trading.data.TradeItemSpec;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class ItemSpec {
    private final Identifier id;
    private Optional<Identifier> fallback = Optional.empty();
    private CountRange count = CountRange.ONE;
    private final Map<Identifier, JsonElement> components = new LinkedHashMap<>();
    private final Map<Identifier, JsonElement> fallbackComponents = new LinkedHashMap<>();

    private ItemSpec(Identifier id) {
        this.id = id;
    }

    public static ItemSpec of(String id) {
        return new ItemSpec(Identifier.of(id));
    }

    public static ItemSpec of(ItemConvertible item) {
        return new ItemSpec(Registries.ITEM.getId(item.asItem()));
    }

    public ItemSpec count(int amount) {
        return count(amount, amount);
    }

    public ItemSpec count(int min, int max) {
        this.count = new CountRange(min, max);
        return this;
    }

    public ItemSpec fallback(String id) {
        this.fallback = Optional.of(Identifier.of(id));
        return this;
    }

    public ItemSpec fallback(ItemConvertible item) {
        this.fallback = Optional.of(Registries.ITEM.getId(item.asItem()));
        return this;
    }

    /** For components from mods that this mod doesn't depend on. The value must match that component's codec. */
    public ItemSpec component(String id, JsonElement value) {
        components.put(Identifier.of(id), value);
        return this;
    }
    public ItemSpec component(String id, String value) {
        return component(id, new JsonPrimitive(value));
    }
    public ItemSpec component(String id, Number value) {
        return component(id, new JsonPrimitive(value));
    }
    public ItemSpec component(String id, boolean value) {
        return component(id, new JsonPrimitive(value));
    }

    /** Component applied only when the fallback item is used. */
    public ItemSpec fallbackComponent(String id, JsonElement value) {
        fallbackComponents.put(Identifier.of(id), value);
        return this;
    }

    /** For components that can be compiled against (vanilla ones). Encoded to JSON with the component's own codec. */
    public <T> ItemSpec with(ComponentType<T> type, T value) {
        Codec<T> codec = type.getCodecOrThrow();
        JsonElement json = codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow(IllegalStateException::new);
        components.put(Registries.DATA_COMPONENT_TYPE.getId(type), json);
        return this;
    }

    TradeItemSpec build() {
        return new TradeItemSpec(id, fallback, count, Map.copyOf(components), Map.copyOf(fallbackComponents));
    }

}