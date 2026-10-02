package org.ivangeevo.hc_villagers.datagen.impl;

import net.minecraft.item.ItemConvertible;
import org.ivangeevo.hc_villagers.trading.data.TradeEntry;
import org.ivangeevo.hc_villagers.trading.data.TradeItemSpec;

import java.util.Optional;

public final class Trade {
    private final TradeItemSpec buy;
    private Optional<TradeItemSpec> buy2 = Optional.empty();

    private Trade(TradeItemSpec buy) {
        this.buy = buy;
    }

    public static Trade buy(ItemSpec spec) {
        return new Trade(spec.build());
    }
    public static Trade buy(String id) {
        return buy(ItemSpec.of(id));
    }
    public static Trade buy(String id, int count) {
        return buy(ItemSpec.of(id).count(count));
    }
    public static Trade buy(String id, int min, int max) {
        return buy(ItemSpec.of(id).count(min, max));
    }
    public static Trade buy(ItemConvertible item) {
        return buy(ItemSpec.of(item));
    }
    public static Trade buy(ItemConvertible item, int count) {
        return buy(ItemSpec.of(item).count(count));
    }
    public static Trade buy(ItemConvertible item, int min, int max) {
        return buy(ItemSpec.of(item).count(min, max));
    }

    public Trade and(ItemSpec spec) {
        this.buy2 = Optional.of(spec.build());
        return this;
    }
    public Trade and(String id, int count) {
        return and(ItemSpec.of(id).count(count));
    }
    public Trade and(String id, int min, int max) {
        return and(ItemSpec.of(id).count(min, max));
    }
    public Trade and(ItemConvertible item) {
        return and(ItemSpec.of(item));
    }
    public Trade and(ItemConvertible item, int count) {
        return and(ItemSpec.of(item).count(count));
    }
    public Trade and(ItemConvertible item, int min, int max) {
        return and(ItemSpec.of(item).count(min, max));
    }

    public TradeEntry sell(ItemSpec spec) {
        return new TradeEntry(buy, buy2, spec.build());
    }
    public TradeEntry sell(String id) {
        return sell(ItemSpec.of(id));
    }
    public TradeEntry sell(String id, int count) {
        return sell(ItemSpec.of(id).count(count));
    }
    public TradeEntry sell(String id, int min, int max) {
        return sell(ItemSpec.of(id).count(min, max));
    }
    public TradeEntry sell(ItemConvertible item) {
        return sell(ItemSpec.of(item));
    }
    public TradeEntry sell(ItemConvertible item, int count) {
        return sell(ItemSpec.of(item).count(count));
    }
    public TradeEntry sell(ItemConvertible item, int min, int max) {
        return sell(ItemSpec.of(item).count(min, max));
    }

}