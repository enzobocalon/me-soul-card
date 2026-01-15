package com.mesoulcard.mixins.compat.pprovider.advancedae;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogic;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({AdvPatternProviderLogicHost.class})
public interface AdvPatternProviderLogicHostMixin extends IUpgradeableObject {
    @Shadow
    AdvPatternProviderLogic getLogic();

    @Override
    default IUpgradeInventory getUpgrades() {
        return ((IUpgradeableObject) this.getLogic()).getUpgrades();
    }
}
