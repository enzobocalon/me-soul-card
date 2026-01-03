package com.mesoulcard.common;

import appeng.api.config.Actionable;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.me.helpers.MachineSource;
import appeng.parts.AEBasePart;
import com.buuz135.industrialforegoingsouls.config.ConfigSoulSurge;
import com.buuz135.soulplied_energistics.applied.SoulKey;
import com.mesoulcard.helper.SoulAccelerationHelper;
import com.mesoulcard.helper.TargetInfo;
import net.minecraft.core.BlockPos;
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
    private static final int ACCELERATION_MULTIPLIER = 6; // for now

    private int tickingTime = 0;

    public SoulDistributor(IManagedGridNode mainNode, BooleanSupplier hasUpgrade, AEBasePart part) {
        this.mainNode = mainNode;
        this.hasUpgrade = hasUpgrade;
        this.part = part;
        this.actionSource = new MachineSource(this.part);
    }

    @Override
    public void accelerate() {
        if (!hasUpgrade.getAsBoolean()) return;

        if (tickingTime <= 0) {
            if (!consumeSoulsFromNetwork()) return;
            tickingTime = SOUL_TIME;
            System.out.println(tickingTime);
        }

        if (tickingTime > 0) {
            TargetInfo target = this.getTargetInfo();
            if (target == null) return;
            boolean didAccelerate = false;
            if (SoulAccelerationHelper.accelerate(target.level(), target.pos(), target.state(), ACCELERATION_MULTIPLIER)) {
                didAccelerate = true;
            }
            if (didAccelerate) {
                tickingTime -= 1;
                System.out.println(tickingTime);
            }
        }
    }

    private long getAvailableSouls() {
        if (!this.mainNode.isActive()) return 0;
        var grid = this.mainNode.getGrid();
        if (grid == null) return 0;
        var storageService = grid.getStorageService();
        if (storageService == null) return 0;

        var inv = storageService.getInventory();
        var availableStacks = inv.getAvailableStacks();

        return availableStacks.get(SoulKey.INSTANCE);
    }

    private boolean consumeSoulsFromNetwork() {
        if (!this.mainNode.isActive()) return false;
        var grid = this.mainNode.getGrid();
        if (grid == null) return false;

        var storageService = grid.getStorageService();
        if (storageService == null) return false;

        var inv = storageService.getInventory();

        long extracted = inv.extract(
                SoulKey.INSTANCE,
                ACCELERATION_MULTIPLIER,
                Actionable.MODULATE,
                actionSource
        );

        return extracted >= 1;
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

    @Override
    public boolean isAwake() {
        return this.hasUpgrade.getAsBoolean();
    }

    public void updateSleep() {
        if (this.service != null) {
            boolean hasCard = this.hasUpgrade.getAsBoolean();
            if (hasCard) {
                this.service.wake(this);
            } else {
                this.service.sleep(this);
            }
        }
    }

    private TargetInfo getTargetInfo() {
        if (this.targetEntity == null && this.service != null) {
            this.targetEntity = this.part.getBlockEntity();
        }

        if (this.targetEntity != null) {
            Level level = this.targetEntity.getLevel();
            if (level == null) return null;

            BlockPos targetPos = this.targetEntity.getBlockPos().relative(this.part.getSide());
            BlockState state = level.getBlockState(targetPos);

            return new TargetInfo(level, targetPos, state);
        }

        return null;
    }
}