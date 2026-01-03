package com.mesoulcard.common;

import appeng.api.networking.IGridNodeService;
import org.jetbrains.annotations.Nullable;

public interface ISoulDistributor extends IGridNodeService {
    default void accelerate() {}

    default void setServiceHost(@Nullable SoulService service) {}

    default boolean isActive() {
        return true;
    }

    default void updateSleep() {}

    default boolean isAwake() {
        return false;
    }

    default boolean isFastMode() {
        return false;
    }

    default void setFastMode(boolean mode) {}
}
