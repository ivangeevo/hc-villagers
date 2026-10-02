package org.ivangeevo.hc_villagers.trading;

/**
 * What an offer in a managed villager's list does when traded.
 * The ordinal is saved to NBT and sent to the client, so only append new values at the end.
 */
public enum HCTradeKind {
    /** Random slot from the villager's current level while the bar isn't full: shows "+", fills the bar. */
    PLUS,
    /** Random slot from a lower level, or any random slot once the bar is full: tradable, no progress. */
    NORMAL,
    /** Guaranteed trade: always present, never runs out, no progress. */
    FIXED,
    /** The "++" trade: only appears when the bar is full, and is the only thing that levels the villager up. */
    LEVEL_UP,
    /** Guaranteed trade: only one at a time. **/
    EXCLUSIVE;

    private static final HCTradeKind[] VALUES = values();

    public static HCTradeKind byId(int id) {
        return id >= 0 && id < VALUES.length ? VALUES[id] : NORMAL;
    }
}
