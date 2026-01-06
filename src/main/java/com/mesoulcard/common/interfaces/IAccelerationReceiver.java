package com.mesoulcard.common.interfaces;

public interface IAccelerationReceiver {
    void receiveStates(int multiplier);

    void receiveClientSync(int multiplier, boolean locked);

    int getClientMultiplier();

    boolean getClientLockStatus();
}
