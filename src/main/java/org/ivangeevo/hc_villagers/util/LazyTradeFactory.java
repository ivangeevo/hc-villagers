package org.ivangeevo.hc_villagers.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;

import java.util.function.Supplier;

/**
 * Builds the wrapped factory the first time a trade is generated instead of at class load.
 *
 * <p>Needed for modded items: vanilla factories ({@code BuyItemFactory}, {@code SellItemFactory}, ...) call
 * {@code asItem()} in their constructors, and our trade arrays are created during our mod init, possibly
 * before the other mod has registered its items. By the time a villager generates trades, every mod has
 * finished registering, so the {@link ModItemRef} lookup is reliable.
 */
public final class LazyTradeFactory implements TradeOffers.Factory {
    private final Supplier<TradeOffers.Factory> supplier;
    private volatile TradeOffers.Factory delegate;

    public LazyTradeFactory(Supplier<TradeOffers.Factory> supplier) {
        this.supplier = supplier;
    }

    @Override
    public TradeOffer create(Entity entity, Random random) {
        TradeOffers.Factory factory = delegate;
        if (factory == null) {
            factory = supplier.get();
            delegate = factory;
        }
        return factory.create(entity, random);
    }
}