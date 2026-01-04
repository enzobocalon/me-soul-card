package com.mesoulcard.network.payloads;

import com.mesoulcard.MESoulCard;
import com.mesoulcard.common.interfaces.IAccelerationReceiver;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

// Client -> Server
public record AccelerationPacket(int value) implements CustomPacketPayload {
    public static final Type<AccelerationPacket> TYPE = new Type<>(MESoulCard.makeId("acceleration_multiplier"));

    public static final StreamCodec<ByteBuf, AccelerationPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, AccelerationPacket::value,
            AccelerationPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(AccelerationPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var menu = context.player().containerMenu;

            if (menu instanceof IAccelerationReceiver receiver) {
                receiver.receiveStates(packet.value());
            }
        });
    }
}
