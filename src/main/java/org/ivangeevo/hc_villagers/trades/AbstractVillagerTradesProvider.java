package org.ivangeevo.hc_villagers.trades;

import net.minecraft.item.ItemConvertible;
import net.minecraft.village.TradeOffers;
import org.ivangeevo.hc_villagers.util.LazyTradeFactory;
import org.ivangeevo.hc_villagers.util.ModItemRef;

import java.util.function.Supplier;

public abstract class AbstractVillagerTradesProvider {

    // Mod IDs (= registry namespaces) of the optional BTWR mods. None of them are required at runtime.
    protected static final String TE = "tough_environment";
    protected static final String BTWR = "btwr";
    protected static final String BTWR_DS = "btwr_ds";
    protected static final String BWT = "bwt";
    protected static final String ST = "sturdy_trees";
    protected static final String VG = "vegehenna";
    protected static final String AG = "animageddon";

    /**
     * An item from another mod, looked up by registry ID ({@code modId:path}) when trades are generated.
     * Falls back to {@code fallback} if that mod isn't loaded or doesn't have the item.
     * No classes from the other mod are referenced, so it can be missing at runtime.
     *
     * <p>Always wrap the factory that uses it in {@link #lazy}, because vanilla factories resolve the item
     * in their constructor.
     */
    protected static ModItemRef modItem(String modId, String path, ItemConvertible fallback) {
        return ModItemRef.of(modId, path, fallback);
    }

    /** Creates the factory on first use instead of now. Use for every trade that contains a {@link #modItem}. */
    protected static TradeOffers.Factory lazy(Supplier<TradeOffers.Factory> factory) {
        return new LazyTradeFactory(factory);
    }

    // bandaid for until (or if) we decide to rework the required count of ingredients for village trades
    // Returns the average value between the two numbers passed
    protected static int arithmeticMean(int a, int b) {
        return (a + b) / 2;
    }
}
