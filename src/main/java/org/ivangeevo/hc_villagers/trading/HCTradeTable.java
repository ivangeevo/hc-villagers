package org.ivangeevo.hc_villagers.trading;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * All BTW-style trade data for one profession.
 *
 * <ul>
 *   <li>{@code random}: pools per level (index 0 = level 1). Random slots roll from these.</li>
 *   <li>{@code guaranteed}: always-present trades per level; every level up to the current one is shown.</li>
 *   <li>{@code levelUp}: the "++" trade for levels 1..4 (index 0 = the 1->2 trade).</li>
 *   <li>{@code slots}: how many random offers are shown per level.</li>
 *   <li>{@code required}: how many "+" trades fill the bar for levels 1..4.</li>
 * </ul>
 */
public final class HCTradeTable {
    private static final int[] DEFAULT_SLOTS = {2, 2, 2, 2, 2};
    private static final int[] DEFAULT_REQUIRED = {5, 7, 10, 15};

    /** Default number of random offers shown per level (index 0 = level 1). Returns a copy. */
    public static int[] defaultSlots() {
        return DEFAULT_SLOTS.clone();
    }

    /** Default number of "+" trades that fill the bar for levels 1..4. Returns a copy. */
    public static int[] defaultRequired() {
        return DEFAULT_REQUIRED.clone();
    }
    /** Chance that a random slot rolls from the current level's pool instead of any unlocked level. */
    private static final float CURRENT_LEVEL_WEIGHT = 0.75F;
    private static final int ROLL_ATTEMPTS = 12;
    private static final TradeOffers.Factory[] EMPTY = new TradeOffers.Factory[0];

    private final List<TradeOffers.Factory[]> random;
    private final List<TradeOffers.Factory[]> guaranteed;
    private final TradeOffers.Factory[] levelUp;
    private final int[] slots;
    private final int[] required;

    public HCTradeTable(List<TradeOffers.Factory[]> random, List<TradeOffers.Factory[]> guaranteed, TradeOffers.Factory[] levelUp) {
        this(random, guaranteed, levelUp, DEFAULT_SLOTS, DEFAULT_REQUIRED);
    }

    public HCTradeTable(List<TradeOffers.Factory[]> random, List<TradeOffers.Factory[]> guaranteed,
                        TradeOffers.Factory[] levelUp, int[] slots, int[] required) {
        this.random = random;
        this.guaranteed = guaranteed;
        this.levelUp = levelUp;
        this.slots = slots;
        this.required = required;
    }

    public int slots(int level) {
        return level >= 1 && level <= slots.length ? slots[level - 1] : 1;
    }

    /** Number of "+" trades that fill the bar at this level. */
    public int required(int level) {
        return level >= 1 && level <= required.length ? Math.max(1, required[level - 1]) : Integer.MAX_VALUE;
    }

    public boolean hasLevelUp(int level) {
        return level >= 1 && level <= levelUp.length && levelUp[level - 1] != null;
    }

    private static TradeOffers.Factory[] at(List<TradeOffers.Factory[]> list, int level) {
        if (level < 1 || level > list.size()) return EMPTY;
        TradeOffers.Factory[] pool = list.get(level - 1);
        return pool != null ? pool : EMPTY;
    }

    public record Rolled(TradeOffer offer, int level) {}

    /**
     * Rolls one random offer for a villager at {@code level}, avoiding offers that look the same as
     * anything in {@code avoid} (so a traded slot turns into something different).
     */
    @Nullable
    public Rolled rollRandom(Entity merchant, int level, Random rnd, List<TradeOffer> avoid) {
        for (int attempt = 0; attempt < ROLL_ATTEMPTS; attempt++) {
            int poolLevel = pickPoolLevel(level, rnd);
            if (poolLevel < 1) return null;

            TradeOffers.Factory[] pool = at(random, poolLevel);
            TradeOffer offer = pool[rnd.nextInt(pool.length)].create(merchant, rnd);
            if (offer == null) continue;
            // On the last attempt accept a duplicate rather than leaving the slot empty
            if (attempt < ROLL_ATTEMPTS - 1 && isDuplicate(offer, avoid)) continue;

            return new Rolled(offer, poolLevel);
        }
        return null;
    }

    private int pickPoolLevel(int level, Random rnd) {
        if (at(random, level).length > 0 && rnd.nextFloat() < CURRENT_LEVEL_WEIGHT) {
            return level;
        }
        IntArrayList candidates = new IntArrayList();
        for (int l = 1; l <= level; l++) {
            if (at(random, l).length > 0) candidates.add(l);
        }
        return candidates.isEmpty() ? -1 : candidates.getInt(rnd.nextInt(candidates.size()));
    }

    private static boolean isDuplicate(TradeOffer offer, List<TradeOffer> avoid) {
        for (TradeOffer other : avoid) {
            if (other != null
                    && ItemStack.areItemsEqual(other.getOriginalFirstBuyItem(), offer.getOriginalFirstBuyItem())
                    && ItemStack.areItemsEqual(other.getSellItem(), offer.getSellItem())) {
                return true;
            }
        }
        return false;
    }

    /** Guaranteed offers for every level up to and including {@code level}. */
    public List<TradeOffer> createGuaranteed(Entity merchant, int level, Random rnd) {
        List<TradeOffer> result = new ArrayList<>();
        for (int l = 1; l <= level; l++) {
            for (TradeOffers.Factory factory : at(guaranteed, l)) {
                TradeOffer offer = factory.create(merchant, rnd);
                if (offer != null) result.add(offer);
            }
        }
        return result;
    }

    @Nullable
    public TradeOffer createLevelUp(Entity merchant, int level, Random rnd) {
        return hasLevelUp(level) ? levelUp[level - 1].create(merchant, rnd) : null;
    }
}
