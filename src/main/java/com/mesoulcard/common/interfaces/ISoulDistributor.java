package com.mesoulcard.common.interfaces;

import appeng.api.networking.IGridNodeService;
import com.mesoulcard.common.SoulService;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public interface ISoulDistributor extends IGridNodeService {
    void accelerate();

    void setServiceHost(@Nullable SoulService service);

    default boolean isActive() {
        return true;
    }

    void updateSleep();

    void cleanup();

    default boolean isAwake() {
        return false;
    }

    default boolean isFastMode() {
        return false;
    }

    void setAccelerationMultiplier(int multiplier);

    int getAccelerationMultiplier();

    boolean isLocked();

    void writeToNBT(CompoundTag tag, HolderLookup.Provider registries);

    void readFromNBT(CompoundTag tag, HolderLookup.Provider registries);

}
