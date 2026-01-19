package com.mesoulcard.common.interfaces;

public interface IAccelerationReceiver {
    void meSoulCard$receiveStates(int multiplier);

    void meSoulCard$receiveClientSync(int multiplier, boolean locked);

    int meSoulCard$getClientMultiplier();

    boolean meSoulCard$getClientLockStatus();
}
