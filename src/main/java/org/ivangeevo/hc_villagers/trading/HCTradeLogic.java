package org.ivangeevo.hc_villagers.trading;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerData;
import org.ivangeevo.hc_villagers.mixin.MerchantScreenHandlerAccessor;
import org.ivangeevo.hc_villagers.network.TradeKindsPayload;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * BTW-style trading for managed professions:
 * <ul>
 *   <li>1-2 random offers; a traded one is crossed out and replaced by a new roll the next time the screen is opened.</li>
 *   <li>Only "+" trades (random offers from the current level) fill the bar.</li>
 *   <li>A full bar adds the "++" trade; only that trade levels the villager up.</li>
 *   <li>Guaranteed trades are always there and never run out.</li>
 * </ul>
 * Changes from a trade (rerolls, the "++" offer appearing, leveling up) are queued and applied once nobody is
 * trading with the villager, so the open screen never changes under the player (same as vanilla 1.21.1).
 * Live updating was dropped on purpose: vanilla does that itself in newer versions.
 */
public final class HCTradeLogic {
    /** Effectively unlimited uses for guaranteed trades. */
    public static final int FIXED_MAX_USES = 999_999;
    private static final int REGEN_TICKS = 200;
    private static final byte HAPPY_PARTICLES_STATUS = 14;

    private HCTradeLogic() {}

    @Nullable
    public static HCTradeTable tableFor(VillagerEntity villager) {
        return HCTradeTables.get(villager.getVillagerData().getProfession());
    }

    public static boolean isManaged(VillagerEntity villager) {
        return tableFor(villager) != null;
    }

    private static HCTradeState state(VillagerEntity villager) {
        return ((HCTradingVillager) villager).hcVillagers$getTradeState();
    }

    // ------------------------------------------------------------------------------------------------
    // Building the offer list
    // ------------------------------------------------------------------------------------------------

    /** Builds the whole list for the villager's current level. Called from {@code fillRecipes()}. */
    public static void rebuildAll(VillagerEntity villager, TradeOfferList offers) {
        HCTradeTable table = tableFor(villager);
        if (table == null) return;
        HCTradeState st = state(villager);
        int level = villager.getVillagerData().getLevel();
        Random rnd = villager.getRandom();

        offers.clear();
        st.kinds.clear();
        st.slotLevels.clear();
        st.pendingRerolls.clear();

        Carry carried = (carry != null && carry.villager() == villager) ? carry : null;

        boolean reserveLevelUp = st.levelUpReady && table.hasLevelUp(level);
        int randomSlots = table.slots(level) - (reserveLevelUp ? 1 : 0);

        // 1) keep what the villager already offered
        if (carried != null) {
            for (int i = 0; i < carried.offers().size() && offers.size() < randomSlots; i++) {
                offers.add(carried.offers().get(i));
                st.slotLevels.add(carried.levels().getInt(i));
                st.kinds.add(HCTradeKind.NORMAL.ordinal());
            }
        }

        // 2) roll only the missing slots
        while (offers.size() < randomSlots) {
            HCTradeTable.Rolled rolled = table.rollRandom(villager, level, rnd, offers);
            if (rolled == null) break;
            offers.add(rolled.offer());
            st.slotLevels.add(rolled.level());
            st.kinds.add(HCTradeKind.NORMAL.ordinal());
        }

        // 3) "++" trade takes one of the random slots when the bar is already full
        if (reserveLevelUp) {
            TradeOffer up = table.createLevelUp(villager, level, rnd);
            if (up != null) {
                offers.add(up);
                st.slotLevels.add(level);
                st.kinds.add(HCTradeKind.LEVEL_UP.ordinal());
            }
        }

        // 4) always-shown trades
        for (TradeOffer offer : table.createGuaranteed(villager, level, rnd)) {
            offers.add(offer);
            st.kinds.add(HCTradeKind.FIXED.ordinal());
        }

        // 5) "only one at a time" groups (must stay last: tick() finds them via index - firstExclusive)
        for (TradeOffer offer : table.createExclusive(villager, level, rnd, st.exclusivePicks)) {
            offers.add(offer);
            st.kinds.add(HCTradeKind.EXCLUSIVE.ordinal());
        }

        retag(villager, table, st, offers);
        villager.setExperience(toVanillaXp(level, st, table));
    }

    /** Puts the "++" trade into an existing random slot (a just-traded one if possible) */
    private static void placeLevelUpOffer(VillagerEntity villager, HCTradeTable table, HCTradeState st,
                                          TradeOfferList offers, int level, Random rnd) {
        TradeOffer offer = table.createLevelUp(villager, level, rnd);
        if (offer == null) return;

        int slot = -1;
        // Prefer a slot that is about to be rerolled anyway
        for (int j = 0; j < st.pendingRerolls.size(); j++) {
            int idx = st.pendingRerolls.getInt(j);
            if (idx < st.slotLevels.size()) {
                slot = idx;
                st.pendingRerolls.removeInt(j);
                break;
            }
        }
        // Otherwise take the last random slot that isn't already special
        if (slot < 0) {
            for (int i = st.slotLevels.size() - 1; i >= 0; i--) {
                HCTradeKind k = st.kindAt(i);
                if (k == HCTradeKind.NORMAL || k == HCTradeKind.PLUS) { slot = i; break; }
            }
        }

        if (slot < 0) { // no random slots at all: fall back to appending
            offers.add(offer);
            st.kinds.add(HCTradeKind.LEVEL_UP.ordinal());
        } else {
            offers.set(slot, offer);
            st.kinds.set(slot, HCTradeKind.LEVEL_UP.ordinal());
            st.slotLevels.set(slot, level);
        }
    }

    /**
     * Sets PLUS vs NORMAL on random slots and gives every offer the right max uses and villager XP.
     * The XP value is what the vanilla screen uses for the bar preview, and lets the client draw "+".
     */
    private static void retag(VillagerEntity villager, HCTradeTable table, HCTradeState st, TradeOfferList offers) {
        int level = villager.getVillagerData().getLevel();
        boolean canEarn = !st.levelUpReady && VillagerData.canLevelUp(level);
        int step = plusStep(level, table);

        for (int i = 0; i < offers.size(); i++) {
            HCTradeKind kind = st.kindAt(i);
            if (kind == HCTradeKind.PLUS || kind == HCTradeKind.NORMAL) {
                boolean currentLevelSlot = i < st.slotLevels.size() && st.slotLevels.getInt(i) == level;
                kind = canEarn && currentLevelSlot ? HCTradeKind.PLUS : HCTradeKind.NORMAL;
                st.kinds.set(i, kind.ordinal());
            }

            //int maxUses = (kind == HCTradeKind.FIXED || kind == HCTradeKind.EXCLUSIVE) ? FIXED_MAX_USES : 1;
            int maxUses = 1;
            int xp = kind == HCTradeKind.PLUS ? step : 0;
            TradeOffer offer = offers.get(i);
            if (offer.getMaxUses() != maxUses || offer.getMerchantExperience() != xp) {
                offers.set(i, withUsesAndXp(offer, maxUses, xp));
            }
        }
    }

    private static TradeOffer withUsesAndXp(TradeOffer offer, int maxUses, int xp) {
        return new TradeOffer(
                offer.getFirstBuyItem(),
                offer.getSecondBuyItem(),
                offer.copySellItem(),
                Math.min(offer.getUses(), maxUses),
                maxUses,
                xp,
                offer.getPriceMultiplier(),
                offer.getDemandBonus());
    }

    // ------------------------------------------------------------------------------------------------
    // Trading
    // ------------------------------------------------------------------------------------------------

    /**
     * Called from {@code afterUsing} (replacing vanilla XP/leveling). Only updates counters and queues
     * changes; the list itself is changed after the trading screen is closed.
     *
     * @return true if this trade is going to level the villager up (for the extra XP orb).
     */
    public static boolean onTrade(VillagerEntity villager, TradeOffer offer) {
        HCTradeTable table = tableFor(villager);
        if (table == null) return false;
        HCTradeState st = state(villager);
        int level = villager.getVillagerData().getLevel();
        TradeOfferList offers = villager.getOffers();

        st.dirty = true;
        int index = indexOfIdentity(offers, offer);
        if (index < 0 || st.kinds.size() != offers.size()) {
            return false; // list is out of sync (old save / another mod); rebuilt when the screen closes
        }

        boolean levelingUp = false;
        switch (st.kindAt(index)) {
            case PLUS -> {
                // Markers aren't refreshed while the screen is open, so a "+" offer can still be traded after
                // the bar filled up. Only count it while the bar isn't full yet.
                if (!st.levelUpReady) {
                    st.plusDone++;
                }
                if (!st.levelUpReady && st.plusDone >= table.required(level)) {
                    st.levelUpReady = true;
                    // No "++" trade defined for this level: level up straight away instead of getting stuck
                    if (!table.hasLevelUp(level)) {
                        st.pendingLevelUp = true;
                        levelingUp = true;
                    }
                }
                st.pendingRerolls.add(index);
            }
            case NORMAL, EXCLUSIVE, FIXED -> st.pendingRerolls.add(index);
            case LEVEL_UP -> {
                st.pendingLevelUp = true;
                levelingUp = true;
            }
        }

        villager.setExperience(toVanillaXp(level, st, table));
        return levelingUp;
    }

    /**
     * Applies queued changes. Called every server tick; does nothing unless a trade happened, and waits
     * until the trading screen is closed.
     */
    public static void tick(VillagerEntity villager) {
        HCTradeState st = state(villager);
        if (!st.dirty || villager.hasCustomer()) return;
        st.dirty = false;

        HCTradeTable table = tableFor(villager);
        if (table == null) return;
        TradeOfferList offers = villager.getOffers();
        int level = villager.getVillagerData().getLevel();

        if (st.pendingLevelUp && VillagerData.canLevelUp(level)) {
            carry = collectCarried(villager, st, offers); // must run before pendingRerolls is cleared

            st.pendingLevelUp = false;
            st.plusDone = 0;
            st.levelUpReady = false;
            st.pendingRerolls.clear();
            // Vanilla levelUp(): level + 1, then fillRecipes() -> rebuildAll() for the new level
            try {
                ((HCTradingVillager) villager).hcVillagers$levelUp();
            } finally {
                carry = null;
            }
            villager.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, REGEN_TICKS, 0));
            villager.getWorld().sendEntityStatus(villager, HAPPY_PARTICLES_STATUS);
        } else if (st.kinds.size() != offers.size()) {
            st.pendingLevelUp = false;
            rebuildAll(villager, offers);
        } else {
            st.pendingLevelUp = false;
            Random rnd = villager.getRandom();

            if (st.levelUpReady && !st.hasLevelUpOffer() && table.hasLevelUp(level)) {
                placeLevelUpOffer(villager, table, st, offers, level, rnd);
            }

            int firstExclusive = -1;
            for (int k = 0; k < st.kinds.size(); k++) {
                if (st.kindAt(k) == HCTradeKind.EXCLUSIVE) { firstExclusive = k; break; }
            }

            for (int i = 0; i < st.pendingRerolls.size(); i++) {
                int index = st.pendingRerolls.getInt(i);
                HCTradeKind k = st.kindAt(index);

                if (k == HCTradeKind.EXCLUSIVE && firstExclusive >= 0) {
                    TradeOffer swapped = table.rerollExclusive(villager, level, index - firstExclusive, st.exclusivePicks, rnd);
                    if (swapped != null) offers.set(index, swapped);
                    continue;
                }

                if (k == HCTradeKind.FIXED) {
                    offers.get(index).resetUses(); // the same trade comes back available
                    continue;
                }

                if (index >= st.slotLevels.size()) continue;
                if (k != HCTradeKind.NORMAL && k != HCTradeKind.PLUS) continue;
                HCTradeTable.Rolled rolled = table.rollRandom(villager, level, rnd, offers);
                if (rolled != null) {
                    offers.set(index, rolled.offer());
                    st.slotLevels.set(index, rolled.level());
                }
            }
            st.pendingRerolls.clear();

            retag(villager, table, st, offers);
            villager.setExperience(toVanillaXp(level, st, table));
        }
    }

    /**
     * The pending queue only lives in memory. If the game stopped while the trading screen was open, used offers
     * are still saved as used up; turn them back into pending rerolls / level-up so nothing gets stuck.
     */
    private static void queueUsedOffers(HCTradeState st, TradeOfferList offers) {
        if (st.kinds.size() != offers.size()) return;
        for (int i = 0; i < offers.size(); i++) {
            if (!offers.get(i).isDisabled()) continue;
            HCTradeKind kind = st.kindAt(i);
            if (kind == HCTradeKind.LEVEL_UP) {
                st.pendingLevelUp = true;
                st.dirty = true;
            } else if (!st.pendingRerolls.contains(i)) {
                st.pendingRerolls.add(i);
                st.dirty = true;
            }
        }
    }

    /** Called right before the trading screen opens. Applies queued changes and fixes up legacy / out-of-sync villagers. */
    public static void prepareForTrading(VillagerEntity villager) {
        HCTradeTable table = tableFor(villager);
        if (table == null) return;
        HCTradeState st = state(villager);

        TradeOfferList offers = villager.getOffers(); // may run fillRecipes() -> rebuildAll()
        queueUsedOffers(st, offers);
        if (st.dirty) {
            tick(villager);
            offers = villager.getOffers();
        }

        if (st.kinds.size() != offers.size()) {
            rebuildAll(villager, offers);
        } else {
            retag(villager, table, st, offers);
            villager.setExperience(toVanillaXp(villager.getVillagerData().getLevel(), st, table));
        }
    }

    // ------------------------------------------------------------------------------------------------
    // "+"/"++" markers for the trading screen
    // ------------------------------------------------------------------------------------------------

    @Nullable
    private static MerchantScreenHandler openHandler(VillagerEntity villager, ServerPlayerEntity player) {
        if (player.currentScreenHandler instanceof MerchantScreenHandler handler
                && ((MerchantScreenHandlerAccessor) handler).hcVillagers$getMerchant() == villager) {
            return handler;
        }
        return null;
    }

    /** Sends the "+"/"++" markers. Called right after the trading screen opens. */
    public static void sendKindsToCustomer(VillagerEntity villager) {
        if (!(villager.getCustomer() instanceof ServerPlayerEntity player)) return;
        MerchantScreenHandler handler = openHandler(villager, player);
        if (handler != null) sendKinds(player, handler.syncId, state(villager));
    }

    private static void sendKinds(ServerPlayerEntity player, int syncId, HCTradeState st) {
        // Players without the mod on their client can still trade, they just don't see the markers
        if (ServerPlayNetworking.canSend(player, TradeKindsPayload.ID)) {
            ServerPlayNetworking.send(player, new TradeKindsPayload(syncId, st.kindsAsBytes()));
        }
    }

    // ------------------------------------------------------------------------------------------------
    // Level bar mapping onto vanilla XP (so the vanilla screen draws it without changes)
    // ------------------------------------------------------------------------------------------------

    public static int toVanillaXp(int level, HCTradeState st, HCTradeTable table) {
        if (!VillagerData.canLevelUp(level)) {
            return VillagerData.getLowerLevelExperience(Math.max(1, Math.min(level, 5)));
        }
        int lower = VillagerData.getLowerLevelExperience(level);
        int upper = VillagerData.getUpperLevelExperience(level);
        if (st.levelUpReady) return upper; // full bar, waiting for "++"
        int required = table.required(level);
        return lower + (upper - lower) * Math.min(st.plusDone, required) / required;
    }

    /** XP put on a "+" offer; only used for the bar preview when the player hovers/selects it. */
    public static int plusStep(int level, HCTradeTable table) {
        if (!VillagerData.canLevelUp(level)) return 0;
        int span = VillagerData.getUpperLevelExperience(level) - VillagerData.getLowerLevelExperience(level);
        int required = table.required(level);
        return Math.max(1, (span + required - 1) / required);
    }

    private static int indexOfIdentity(TradeOfferList offers, TradeOffer offer) {
        for (int i = 0; i < offers.size(); i++) {
            if (offers.get(i) == offer) return i;
        }
        return -1;
    }

    /** Random offers that survive a level-up. Only set for the duration of the levelUp() call (server thread). */
    private record Carry(VillagerEntity villager, List<TradeOffer> offers, IntArrayList levels) {}
    private static Carry carry;

    /** Random offers worth keeping: untraded NORMAL/PLUS slots. Traded ones and the "++" offer are dropped. */
    private static Carry collectCarried(VillagerEntity villager, HCTradeState st, TradeOfferList offers) {
        List<TradeOffer> kept = new ArrayList<>();
        IntArrayList levels = new IntArrayList();
        if (st.kinds.size() != offers.size()) return new Carry(villager, kept, levels);

        for (int i = 0; i < offers.size() && i < st.slotLevels.size(); i++) {
            HCTradeKind kind = st.kindAt(i);
            if (kind != HCTradeKind.NORMAL && kind != HCTradeKind.PLUS) continue;
            if (st.pendingRerolls.contains(i) || offers.get(i).isDisabled()) continue;
            kept.add(offers.get(i));
            levels.add(st.slotLevels.getInt(i));
        }
        return new Carry(villager, kept, levels);
    }
}
