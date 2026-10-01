package org.ivangeevo.hc_villagers.trading.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * One file in {@code data/<namespace>/hc_villager_trades/}. The file name doesn't matter; {@code profession} does.
 * <pre>
 * {
 *   "profession": "minecraft:farmer",
 *   "replace": false,                // optional: true = drop everything loaded earlier for this profession
 *   "slots": [2, 2, 2, 2, 2],         // optional: random offers shown at levels 1..5
 *   "required": [5, 7, 10, 15],       // optional: "+" trades that fill the bar at levels 1..4
 *   "levels": {
 *     "1": {
 *       "random":     [ trade, ... ], // pool for the random slots ("+" while at this level)
 *       "guaranteed": [ trade, ... ], // always shown from this level on, never runs out
 *       "level_up":   trade           // the "++" trade that levels the villager up to the next level
 *     },
 *     ...
 *   }
 * }
 * </pre>
 * Several files can target the same profession (e.g. an add-on data pack): their pools are appended, and their
 * {@code slots}, {@code required} and {@code level_up} replace earlier ones. Files load in ID order.
 */
public record ProfessionTradesFile(
        Identifier profession,
        boolean replace,
        Optional<List<Integer>> slots,
        Optional<List<Integer>> required,
        Map<String, LevelTrades> levels) {

    public static final Codec<ProfessionTradesFile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("profession").forGetter(ProfessionTradesFile::profession),
            Codec.BOOL.optionalFieldOf("replace", false).forGetter(ProfessionTradesFile::replace),
            Codec.INT.listOf().optionalFieldOf("slots").forGetter(ProfessionTradesFile::slots),
            Codec.INT.listOf().optionalFieldOf("required").forGetter(ProfessionTradesFile::required),
            Codec.unboundedMap(Codec.STRING, LevelTrades.CODEC).optionalFieldOf("levels", Map.of()).forGetter(ProfessionTradesFile::levels)
    ).apply(instance, ProfessionTradesFile::new));

    public record LevelTrades(List<TradeEntry> random, List<TradeEntry> guaranteed, Optional<TradeEntry> levelUp) {
        public static final Codec<LevelTrades> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                TradeEntry.CODEC.listOf().optionalFieldOf("random", List.of()).forGetter(LevelTrades::random),
                TradeEntry.CODEC.listOf().optionalFieldOf("guaranteed", List.of()).forGetter(LevelTrades::guaranteed),
                TradeEntry.CODEC.optionalFieldOf("level_up").forGetter(LevelTrades::levelUp)
        ).apply(instance, LevelTrades::new));
    }
}
