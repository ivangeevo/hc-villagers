package org.ivangeevo.hc_villagers.trading.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.math.random.Random;

import java.util.List;

/**
 * An item count in a trade JSON. Written either as a fixed number ({@code 12}) or as an inclusive
 * range ({@code [32, 48]}), which is rolled every time the trade is generated.
 */
public record CountRange(int min, int max) {
    public static final CountRange ONE = new CountRange(1, 1);

    private static final Codec<CountRange> RANGE_CODEC = Codec.INT.listOf().comapFlatMap(
            list -> list.size() == 2
                    ? DataResult.success(new CountRange(list.get(0), list.get(1)))
                    : DataResult.error(() -> "Count range must be [min, max], got " + list),
            range -> List.of(range.min, range.max));

    public static final Codec<CountRange> CODEC = Codec.either(Codec.INT, RANGE_CODEC)
            .xmap(
                    either -> either.map(n -> new CountRange(n, n), range -> range),
                    range -> range.min == range.max ? Either.left(range.min) : Either.right(range))
            .validate(range -> range.min >= 1 && range.max >= range.min
                    ? DataResult.success(range)
                    : DataResult.error(() -> "Invalid count " + range.min + ".." + range.max + " (min must be >= 1 and <= max)"));

    /** Rolls a count, capped at {@code maxStack} so it always fits in one trade slot. */
    public int roll(Random random, int maxStack) {
        int count = min == max ? min : min + random.nextInt(max - min + 1);
        return Math.max(1, Math.min(count, maxStack));
    }
}
