package com.mesoulcard.common;

import appeng.api.networking.IManagedGridNode;
import com.mesoulcard.MESoulCard;
import com.mesoulcard.helper.SoulAccelerationHelper;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

public class SoulDistributor implements ISoulDistributor {
    private SoulService service;
    private BooleanSupplier hasUpgrade;
    private final IManagedGridNode mainNode;

    public SoulDistributor(IManagedGridNode mainNode, BooleanSupplier hasUpgrade) {
        this.mainNode = mainNode;
        this.hasUpgrade = hasUpgrade;
    }

    @Override
    public void accelerate() {
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
}