package com.mesoulcard.common;

import appeng.api.networking.IManagedGridNode;
import appeng.parts.AEBasePart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

public class SoulDistributor implements ISoulDistributor {
    private SoulService service;
    private BooleanSupplier hasUpgrade;
    private final IManagedGridNode mainNode;
    private AEBasePart part;
    private BlockEntity targetEntity;

    public SoulDistributor(IManagedGridNode mainNode, BooleanSupplier hasUpgrade, AEBasePart part) {
        this.mainNode = mainNode;
        this.hasUpgrade = hasUpgrade;
        this.part = part;
    }

    @Override
    public void accelerate() {
        System.out.println(this.getTargetBlock());
        System.out.println(">>> ACCELERATE CALLED - hasUpgrade: " + hasUpgrade.getAsBoolean() + " <<<");
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

    private BlockState getTargetBlock() {
        if (this.targetEntity == null && this.service != null) {
            this.targetEntity = this.part.getBlockEntity();
        }
        if (this.targetEntity != null) {
            BlockPos targetPos = this.targetEntity.getBlockPos().relative(this.part.getSide());
            return this.targetEntity.getLevel().getBlockState(targetPos);
        }
        return null;
    }
}