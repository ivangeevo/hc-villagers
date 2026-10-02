package org.ivangeevo.hc_villagers.trading.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.ivangeevo.hc_villagers.HCVillagersMod;
import org.ivangeevo.hc_villagers.trading.HCTradeTable;
import org.ivangeevo.hc_villagers.trading.HCTradeTables;

import java.io.Reader;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Loads BTW trade tables from {@code data/<namespace>/hc_villager_trades/*.json} on every data reload
 * (world load and {@code /reload}). See {@link ProfessionTradesFile} for the format.
 *
 * <p>Item IDs are resolved here, after all mods have registered their items, so trades can use items from
 * optional mods with a vanilla {@code fallback}.
 */
public final class HCTradeDataLoader implements SimpleSynchronousResourceReloadListener {
    public static final String DIRECTORY = "hc_villager_trades";
    private static final Identifier ID = Identifier.of(HCVillagersMod.MOD_ID, DIRECTORY);
    private static final int LEVELS = 5;

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void reload(ResourceManager manager) {
        Map<VillagerProfession, Builder> builders = new LinkedHashMap<>();
        // TreeMap: deterministic load order by file ID
        Map<Identifier, Resource> files = new TreeMap<>(manager.findResources(DIRECTORY, id -> id.getPath().endsWith(".json")));

        for (Map.Entry<Identifier, Resource> entry : files.entrySet()) {
            Identifier fileId = entry.getKey();
            try (Reader reader = entry.getValue().getReader()) {
                JsonElement json = JsonParser.parseReader(reader);
                ProfessionTradesFile file = ProfessionTradesFile.CODEC.parse(JsonOps.INSTANCE, json)
                        .getOrThrow(JsonParseException::new);

                Optional<VillagerProfession> profession = Registries.VILLAGER_PROFESSION.getOrEmpty(file.profession());
                if (profession.isEmpty()) {
                    // e.g. a profession from a mod that isn't installed
                    HCVillagersMod.LOGGER.info("[{}] Skipping {}: unknown profession '{}'", HCVillagersMod.MOD_ID, fileId, file.profession());
                    continue;
                }

                Builder builder = builders.computeIfAbsent(profession.get(), p -> new Builder());
                if (file.replace()) builder.clear();
                builder.add(file, fileId);
            } catch (Exception e) {
                HCVillagersMod.LOGGER.error("[{}] Couldn't load villager trades from {}: {}", HCVillagersMod.MOD_ID, fileId, e.getMessage());
            }
        }

        Map<VillagerProfession, HCTradeTable> tables = new IdentityHashMap<>();
        builders.forEach((profession, builder) -> tables.put(profession, builder.build()));
        HCTradeTables.replaceAll(tables);
        HCVillagersMod.LOGGER.info("[{}] Loaded BTW trades for {} profession(s)", HCVillagersMod.MOD_ID, tables.size());
    }

    /** Collects everything loaded for one profession, possibly from several files. */
    private static final class Builder {
        private final List<List<TradeOffers.Factory>> random = emptyLevels();
        private final List<List<TradeOffers.Factory>> guaranteed = emptyLevels();
        private final List<List<List<TradeOffers.Factory>>> exclusive = emptyExclusive();

        private TradeOffers.Factory[] levelUp = new TradeOffers.Factory[LEVELS - 1];
        private int[] slots = HCTradeTable.defaultSlots();
        private int[] required = HCTradeTable.defaultRequired();

        private static List<List<TradeOffers.Factory>> emptyLevels() {
            List<List<TradeOffers.Factory>> levels = new ArrayList<>();
            for (int i = 0; i < LEVELS; i++) levels.add(new ArrayList<>());
            return levels;
        }

        private static List<List<List<TradeOffers.Factory>>> emptyExclusive() {
            List<List<List<TradeOffers.Factory>>> levels = new ArrayList<>();
            for (int i = 0; i < LEVELS; i++) levels.add(new ArrayList<>());
            return levels;
        }

        void clear() {
            random.forEach(List::clear);
            guaranteed.forEach(List::clear);
            exclusive.forEach(List::clear);
            levelUp = new TradeOffers.Factory[LEVELS - 1];
            slots = HCTradeTable.defaultSlots();
            required = HCTradeTable.defaultRequired();
        }

        void add(ProfessionTradesFile file, Identifier fileId) {
            file.slots().ifPresent(values -> copyInto(values, slots));
            file.required().ifPresent(values -> copyInto(values, required));

            file.levels().forEach((key, trades) -> {
                int level;
                try {
                    level = Integer.parseInt(key);
                } catch (NumberFormatException e) {
                    level = -1;
                }
                if (level < 1 || level > LEVELS) {
                    HCVillagersMod.LOGGER.warn("[{}] {}: ignoring level '{}' (must be 1-{})", HCVillagersMod.MOD_ID, fileId, key, LEVELS);
                    return;
                }
                String where = fileId + " level " + level;
                int index = level - 1;
                trades.random().forEach(trade -> trade.toFactory(where).ifPresent(random.get(index)::add));
                trades.guaranteed().forEach(trade -> trade.toFactory(where).ifPresent(guaranteed.get(index)::add));
                trades.guaranteedOneOf().forEach(group -> {
                    List<TradeOffers.Factory> factories = new ArrayList<>();
                    group.forEach(trade -> trade.toFactory(where).ifPresent(factories::add));
                    // A group whose items all failed to resolve is dropped. Groups with at least one valid trade are kept.
                    if (!factories.isEmpty()) exclusive.get(index).add(factories);
                });
                if (trades.levelUp().isPresent()) {
                    if (level == LEVELS) {
                        HCVillagersMod.LOGGER.warn("[{}] {}: level {} is the max level and can't have a level_up trade", HCVillagersMod.MOD_ID, fileId, level);
                    } else {
                        trades.levelUp().get().toFactory(where).ifPresent(factory -> levelUp[index] = factory);
                    }
                }
            });
        }

        private static void copyInto(List<Integer> values, int[] target) {
            for (int i = 0; i < Math.min(values.size(), target.length); i++) {
                target[i] = Math.max(1, values.get(i));
            }
        }

        HCTradeTable build() {
            return new HCTradeTable(toArrays(random), toArrays(guaranteed), toGroupArrays(exclusive),
                    levelUp.clone(), slots.clone(), required.clone());
        }

        private static List<TradeOffers.Factory[]> toArrays(List<List<TradeOffers.Factory>> levels) {
            List<TradeOffers.Factory[]> result = new ArrayList<>();
            for (List<TradeOffers.Factory> level : levels) result.add(level.toArray(new TradeOffers.Factory[0]));
            return result;
        }

        private static List<TradeOffers.Factory[][]> toGroupArrays(List<List<List<TradeOffers.Factory>>> levels) {
            List<TradeOffers.Factory[][]> result = new ArrayList<>();
            for (List<List<TradeOffers.Factory>> groups : levels) {
                TradeOffers.Factory[][] arr = new TradeOffers.Factory[groups.size()][];
                for (int g = 0; g < groups.size(); g++) arr[g] = groups.get(g).toArray(new TradeOffers.Factory[0]);
                result.add(arr);
            }
            return result;
        }
    }
}
