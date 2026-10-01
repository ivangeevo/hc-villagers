package org.ivangeevo.hc_villagers.trading;

import net.minecraft.village.VillagerProfession;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Professions that have a trade table use the BTW trading system.
 * Everything else (vanilla professions without a table, other mods' professions) keeps vanilla trading untouched.
 *
 * <p>Tables come from data packs ({@code data/<namespace>/hc_villager_trades/*.json}) and are replaced as a whole
 * on every data reload (world load, {@code /reload}) by {@link org.ivangeevo.hc_villagers.trading.data.HCTradeDataLoader}.
 */
public final class HCTradeTables {
    private static volatile Map<VillagerProfession, HCTradeTable> tables = Map.of();

    private HCTradeTables() {}

    /** Replaces every table at once. Called by the data loader after a reload. */
    public static void replaceAll(Map<VillagerProfession, HCTradeTable> newTables) {
        tables = new IdentityHashMap<>(newTables);
    }

    @Nullable
    public static HCTradeTable get(VillagerProfession profession) {
        return tables.get(profession);
    }

    public static int size() {
        return tables.size();
    }
}
