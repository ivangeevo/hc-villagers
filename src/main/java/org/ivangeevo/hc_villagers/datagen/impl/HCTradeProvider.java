package org.ivangeevo.hc_villagers.datagen.impl;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;
import org.ivangeevo.hc_villagers.trading.data.HCTradeDataLoader;
import org.ivangeevo.hc_villagers.trading.data.ProfessionTradesFile;
import org.ivangeevo.hc_villagers.trading.data.ProfessionTradesFile.LevelTrades;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class HCTradeProvider implements DataProvider {
    private final String modId;
    private final DataOutput.PathResolver professionPaths;
    private final DataOutput.PathResolver levelPaths;

    protected HCTradeProvider(FabricDataOutput output) {
        this.modId = output.getModId();
        this.professionPaths = output.getResolver(DataOutput.OutputType.DATA_PACK, HCTradeDataLoader.DIRECTORY);
        this.levelPaths = output.getResolver(DataOutput.OutputType.DATA_PACK, HCTradeDataLoader.LEVEL_DIRECTORY);
    }

    protected abstract void generate(Consumer<ProfessionDefinition> professionConsumer);

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        List<ProfessionDefinition> defs = new ArrayList<>();
        generate(defs::add);

        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (ProfessionDefinition def : defs) {
            Map<String, Either<Identifier, LevelTrades>> levelEntries = new LinkedHashMap<>();

            for (Map.Entry<Integer, LevelDefinition> e : def.levels.entrySet()) {
                int level = e.getKey();
                LevelTrades trades = e.getValue().build();
                if (def.inline) {
                    levelEntries.put(String.valueOf(level), Either.right(trades));
                } else {
                    Identifier ref = Identifier.of(modId, def.name + "/level_" + level);
                    levelEntries.put(String.valueOf(level), Either.left(ref));
                    Path path = levelPaths.resolveJson(ref);
                    futures.add(DataProvider.writeToPath(writer, encode(LevelTrades.CODEC, trades), path));
                }
            }

            ProfessionTradesFile file = new ProfessionTradesFile(def.profession, def.replace, def.slots, def.required, levelEntries);
            Path path = professionPaths.resolveJson(Identifier.of(modId, def.name));
            futures.add(DataProvider.writeToPath(writer, encode(ProfessionTradesFile.CODEC, file), path));
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private static <T> JsonElement encode(Codec<T> codec, T value) {
        return codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow(IllegalStateException::new);
    }

    @Override
    public String getName() {
        return "Villager Trades";
    }
}
