package com.mesoulcard.common.interfaces;

public interface IAccelerationReceiver {
    void receiveStates(int multiplier);

    void receiveClientSync(int multiplier);

    int getClientMultiplier();
}
