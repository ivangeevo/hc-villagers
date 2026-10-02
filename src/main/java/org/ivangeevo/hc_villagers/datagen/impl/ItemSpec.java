package org.ivangeevo.hc_villagers.datagen.impl;

import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.ivangeevo.hc_villagers.trading.data.CountRange;
import org.ivangeevo.hc_villagers.trading.data.TradeItemSpec;

import java.util.Optional;

public class ItemSpec {
    private final Identifier id;
    private Optional<Identifier> fallback = Optional.empty();
    private CountRange count = CountRange.ONE;

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

    TradeItemSpec build() {
        return new TradeItemSpec(id, fallback, count);
    }
}
