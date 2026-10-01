package org.ivangeevo.hc_villagers.client;

import org.ivangeevo.hc_villagers.trading.HCTradeKind;

/** Latest "+"/"++" markers received from the server, keyed by the screen's syncId. */
public final class ClientTradeKinds {
    private static int syncId = -1;
    private static byte[] kinds = new byte[0];

    private ClientTradeKinds() {}

    public static void set(int newSyncId, byte[] newKinds) {
        syncId = newSyncId;
        kinds = newKinds;
    }

    public static HCTradeKind get(int screenSyncId, int index) {
        if (screenSyncId != syncId || index < 0 || index >= kinds.length) return null;
        return HCTradeKind.byId(kinds[index]);
    }
}