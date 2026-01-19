package com.mesoulcard.network.payloads;

import com.mesoulcard.MESoulCard;
import com.mesoulcard.common.interfaces.IAccelerationReceiver;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

// Server -> Client
public record SyncAccelerationPacket(int value, boolean locked) implements CustomPacketPayload {
    public static final Type<SyncAccelerationPacket> TYPE = new Type<>(MESoulCard.makeId("sync_acceleration"));

    public static final StreamCodec<ByteBuf, SyncAccelerationPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncAccelerationPacket::value,
            ByteBufCodecs.BOOL, SyncAccelerationPacket::locked,
            SyncAccelerationPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncAccelerationPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (player.containerMenu instanceof IAccelerationReceiver receiver) {
                receiver.meSoulCard$receiveClientSync(packet.value(), packet.locked());
            }
        });
    }
}
