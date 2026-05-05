package com.mesoulcard.common.interfaces;

import net.minecraft.server.level.ServerPlayer;

public interface IAccelerationReceiver {
    void meSoulCard$receiveStates(int multiplier);

    void meSoulCard$syncClientState(ServerPlayer player);

    void meSoulCard$receiveClientSync(int multiplier, boolean locked);

    int meSoulCard$getClientMultiplier();

    boolean meSoulCard$getClientLockStatus();
}
