package org.ivangeevo.hc_villagers.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.ivangeevo.hc_villagers.HCVillagersMod;

/**
 * Server -> client: the {@link org.ivangeevo.hc_villagers.trading.HCTradeKind} of each offer in the
 * open trading screen, so the client can draw "+" and "++".
 */
public record TradeKindsPayload(int syncId, byte[] kinds) implements CustomPayload {
    public static final Id<TradeKindsPayload> ID = new Id<>(Identifier.of(HCVillagersMod.MOD_ID, "trade_kinds"));

    public static final PacketCodec<ByteBuf, TradeKindsPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, TradeKindsPayload::syncId,
            PacketCodecs.BYTE_ARRAY, TradeKindsPayload::kinds,
            TradeKindsPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}