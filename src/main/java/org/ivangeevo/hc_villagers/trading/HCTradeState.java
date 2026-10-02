package org.ivangeevo.hc_villagers.trading;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.nbt.NbtCompound;

/**
 * Per-villager BTW trading state, stored on the villager by {@code VillagerTradingMixin}.
 *
 * <p>Offer list layout is always: [random slots...] [guaranteed...] [level-up, if the bar is full].
 * <p>The offers themselves are saved by vanilla ("Offers" tag); this only saves what vanilla doesn't know.
 */
public final class HCTradeState {
    private static final String PLUS_DONE = "PlusDone";
    private static final String LEVEL_UP_READY = "LevelUpReady";
    private static final String KINDS = "Kinds";
    private static final String SLOT_LEVELS = "SlotLevels";

    /** "+" trades done at the current level. */
    public int plusDone;
    /** Bar is full; the "++" trade is (or will be) offered. */
    public boolean levelUpReady;
    /** {@link HCTradeKind} ordinal for each offer index. */
    public final IntArrayList kinds = new IntArrayList();
    /** Pool level each random slot was rolled from (index = offer index). */
    public final IntArrayList slotLevels = new IntArrayList();

    /** Chosen alternative for each exclusive group, flattened in level order then group order. */
    public final IntArrayList exclusivePicks = new IntArrayList();

    // Runtime only: changes queued by a trade and applied on the villager's next tick
    public final IntArrayList pendingRerolls = new IntArrayList();
    public boolean pendingLevelUp;
    public boolean dirty;

    public HCTradeKind kindAt(int index) {
        return index >= 0 && index < kinds.size() ? HCTradeKind.byId(kinds.getInt(index)) : HCTradeKind.NORMAL;
    }

    public boolean hasLevelUpOffer() {
        return kinds.contains(HCTradeKind.LEVEL_UP.ordinal());
    }

    public byte[] kindsAsBytes() {
        byte[] bytes = new byte[kinds.size()];
        for (int i = 0; i < bytes.length; i++) bytes[i] = (byte) kinds.getInt(i);
        return bytes;
    }

    public void reset() {
        plusDone = 0;
        levelUpReady = false;
        kinds.clear();
        slotLevels.clear();
        exclusivePicks.clear();
        pendingRerolls.clear();
        pendingLevelUp = false;
        dirty = false;
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt(PLUS_DONE, plusDone);
        nbt.putBoolean(LEVEL_UP_READY, levelUpReady);
        nbt.putIntArray(KINDS, kinds.toIntArray());
        nbt.putIntArray(SLOT_LEVELS, slotLevels.toIntArray());
        return nbt;
    }

    public void fromNbt(NbtCompound nbt) {
        reset();
        plusDone = nbt.getInt(PLUS_DONE);
        levelUpReady = nbt.getBoolean(LEVEL_UP_READY);
        kinds.addElements(0, nbt.getIntArray(KINDS));
        slotLevels.addElements(0, nbt.getIntArray(SLOT_LEVELS));
    }
}
