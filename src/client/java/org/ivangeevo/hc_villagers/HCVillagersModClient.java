package org.ivangeevo.hc_villagers;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.ivangeevo.hc_villagers.client.ClientTradeKinds;
import org.ivangeevo.hc_villagers.network.TradeKindsPayload;

public class HCVillagersModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(TradeKindsPayload.ID,
                (payload, context) -> ClientTradeKinds.set(payload.syncId(), payload.kinds()));
    }
}
