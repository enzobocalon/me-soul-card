package com.mesoulcard.common.interfaces;

import appeng.api.networking.IGridNodeService;
import com.mesoulcard.common.SoulService;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
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

    void setAccelerationMultiplier(int multiplier);

    int getAccelerationMultiplier();

    default void setFastMode(boolean mode) {}

    default void writeToNBT(CompoundTag tag, HolderLookup.Provider registries) {}
    default void readFromNBT(CompoundTag tag, HolderLookup.Provider registries) {}
}
