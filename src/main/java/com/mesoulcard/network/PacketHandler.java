package com.mesoulcard.network;

import com.mesoulcard.network.payloads.AccelerationPacket;
import com.mesoulcard.network.payloads.SyncAccelerationPacket;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class PacketHandler {
    private static final String PROTOCOL = "1";

    private PacketHandler() {}

    public static void init (IEventBus bus) {
        bus.addListener(PacketHandler::handlePacketRegistration);
    }

    private static void handlePacketRegistration(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(PROTOCOL);

        registrar.playToServer(
                AccelerationPacket.TYPE,
                AccelerationPacket.STREAM_CODEC,
                AccelerationPacket::handle
        );

        registrar.playToClient(
                SyncAccelerationPacket.TYPE,
                SyncAccelerationPacket.STREAM_CODEC,
                SyncAccelerationPacket::handle
        );
    }
}
