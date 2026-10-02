package org.ivangeevo.hc_villagers.datagen.impl;

import org.ivangeevo.hc_villagers.trading.data.ProfessionTradesFile;
import org.ivangeevo.hc_villagers.trading.data.TradeEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.ivangeevo.hc_villagers.trading.data.ProfessionTradesFile.LevelTrades;

public class LevelDefinition {
    private final List<TradeEntry> random = new ArrayList<>();
    private final List<TradeEntry> guaranteed = new ArrayList<>();
    private final List<List<TradeEntry>> guaranteedOneOf = new ArrayList<>();
    private TradeEntry levelUp;

    public LevelDefinition random(TradeEntry... trades) {
        random.addAll(List.of(trades));
        return this;
    }

    public LevelDefinition guaranteed(TradeEntry... trades) {
        guaranteed.addAll(List.of(trades));
        return this;
    }

    public LevelDefinition guaranteedOneOf(TradeEntry... trades) {
        guaranteedOneOf.add(List.of(trades));
        return this;
    }

    public LevelDefinition levelUp(TradeEntry trade) {
        this.levelUp = trade;
        return this;
    }

    LevelTrades build() {
        return new LevelTrades(
                List.copyOf(random),
                List.copyOf(guaranteed),
                List.copyOf(guaranteedOneOf),
                Optional.ofNullable(levelUp)
        );
    }

}
