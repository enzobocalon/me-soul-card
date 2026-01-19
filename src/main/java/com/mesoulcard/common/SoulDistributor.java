package com.mesoulcard.common;

import appeng.api.config.Actionable;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.me.helpers.MachineSource;
import appeng.parts.AEBasePart;
import com.buuz135.industrialforegoingsouls.config.ConfigSoulSurge;
import com.buuz135.soulplied_energistics.applied.SoulKey;
import com.mesoulcard.MESoulCard;
import com.mesoulcard.common.interfaces.ISoulDistributor;
import com.mesoulcard.helper.SoulAccelerationHelper;
import com.mesoulcard.helper.TargetInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

/*
* Planned Features:
* Adds interface button to:
* 1. Allow user to control how many soul surges the upgrade will simulate (1-6);
* 2. Adds compatibility with App Flux to allow energy output;
* */

public class SoulDistributor implements ISoulDistributor {
    private SoulService service;
    private final BooleanSupplier hasUpgrade;
    private final IManagedGridNode mainNode;
    private final AEBasePart part;
    private BlockEntity targetEntity;

    private final IActionSource actionSource;
    private static final int SOUL_TIME = ConfigSoulSurge.SOUL_TIME;
    private int accelerationMultiplier = 1;

    private BlockPos lastTargetPos = null;
    private int tickingTime = 0;

    private String cachedDistributorId = null;

    public SoulDistributor(IManagedGridNode mainNode, BooleanSupplier hasUpgrade, AEBasePart part) {
        this.mainNode = mainNode;
        this.hasUpgrade = hasUpgrade;
        this.part = part;
        this.actionSource = new MachineSource(this.part);
    }

    @Override
    public void accelerate() {
        // System.out.println("current multiplier " + accelerationMultiplier);
        if (!hasUpgrade.getAsBoolean()) {
            releaseCurrentLock();
            return;
        }

        TargetInfo target = this.getTargetInfo();
        if (target == null) {
            releaseCurrentLock();
            return;
        }

        // Check if target is valid (has a block entity that can be accelerated)
        if (!isValidTarget(target)) {
            releaseCurrentLock();
            return;
        }

        String myId = getDistributorId();

        if (!SoulAccelerationManager.tryAcquire(target.level(), target.pos(), myId)) {
            tickingTime = 0;
            return;
        }

        lastTargetPos = target.pos();
        tickAccelerate(target);
    }

    private boolean isValidTarget(TargetInfo target) {
        if (!target.level().isLoaded(target.pos())) {
            return false;
        }

        BlockState state = target.state();
        if (state.isAir()) {
            return false;
        }

        // Check if there's a block entity with a ticker
        BlockEntity be = target.level().getBlockEntity(target.pos());
        return be != null;
    }

    private void tickAccelerate(TargetInfo target) {
        if (tickingTime <= 0) {
            if (!consumeSoulsFromNetwork()) {
                releaseCurrentLock();
                return;
            }
            tickingTime = SOUL_TIME;
        }

        if (tickingTime > 0) {
            boolean didAccelerate = SoulAccelerationHelper.accelerate(
                    target.level(),
                    target.pos(),
                    target.state(),
                    accelerationMultiplier);

            if (didAccelerate) {
                tickingTime -= 1;
            } else {
                releaseCurrentLock();
            }
        }
    }

    private String getDistributorId() {
        if (cachedDistributorId == null) {
            BlockPos pos = this.part.getBlockEntity().getBlockPos();
            Direction side = this.part.getSide();
            cachedDistributorId = pos.toShortString() + "_" + side.getName();
        }
        return cachedDistributorId;
    }

    private void releaseCurrentLock() {
        if (lastTargetPos == null)
            return;

        Level level = this.part.getLevel();
        if (level == null || level.isClientSide)
            return;

        SoulAccelerationManager.release(level, lastTargetPos, getDistributorId());
        lastTargetPos = null;
    }

    private boolean consumeSoulsFromNetwork() {
        if (!this.mainNode.isActive())
            return false;

        var grid = this.mainNode.getGrid();
        if (grid == null)
            return false;

        var storageService = grid.getStorageService();
        if (storageService == null)
            return false;

        var inv = storageService.getInventory();
        long extracted = inv.extract(
                SoulKey.INSTANCE,
                accelerationMultiplier,
                Actionable.MODULATE,
                actionSource);

        return extracted >= 1;
    }

    @Override
    public void setAccelerationMultiplier(int multiplier) {
        if (multiplier >= 1 && multiplier <= 6) {
            this.accelerationMultiplier = multiplier;
        } else if (multiplier > 6) {
            this.accelerationMultiplier = 1;
        }

        if (this.part.getHost() != null) {
            this.part.getHost().markForSave();
        }
    }

    @Override
    public int getAccelerationMultiplier() {
        return this.accelerationMultiplier;
    }

    @Override
    public void setServiceHost(@Nullable SoulService service) {
        this.service = service;
        this.updateSleep();
    }

    @Override
    public boolean isActive() {
        return this.mainNode.isActive();
    }

    public void updateSleep() {
        if (this.service != null) {
            boolean hasCard = this.hasUpgrade.getAsBoolean();
            if (hasCard) {
                this.service.wake(this);
            } else {
                this.service.sleep(this);
                releaseCurrentLock();
            }
        }
    }

    @Override
    public boolean isLocked() {
        return this.lastTargetPos == null;
    }

    @Override
    public void cleanup() {
        releaseCurrentLock();
    }

    private TargetInfo getTargetInfo() {
        if (this.targetEntity == null && this.service != null) {
            this.targetEntity = this.part.getBlockEntity();
        }

        if (this.targetEntity != null) {
            Level level = this.targetEntity.getLevel();
            if (level == null)
                return null;

            BlockPos targetPos = this.targetEntity.getBlockPos().relative(this.part.getSide());
            BlockState state = level.getBlockState(targetPos);

            return new TargetInfo(level, targetPos, state);
        }

        return null;
    }

    public void writeToNBT(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("soulcard_multiplier", this.accelerationMultiplier);
    }

    public void readFromNBT(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains("soulcard_multiplier")) {
            this.accelerationMultiplier = tag.getInt("soulcard_multiplier");
        }
    }
}