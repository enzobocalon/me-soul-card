/*
* Only loaded when app flux is not present
* */

package com.mesoulcard.mixins.upgradeable.advancedae.pprovider;

import appeng.api.networking.IManagedGridNode;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogic;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AdvPatternProviderLogic.class)
public class UpgradeableAdvPatternProviderLogicMixin implements IUpgradeableObject {
    @Final
    @Shadow
    private AdvPatternProviderLogicHost host;

    @Unique
    private IUpgradeInventory upgrades = UpgradeInventories.empty();

    @Unique
    private void onUpgradesChanged() {
        this.host.saveChanges();
        this.host.getBlockEntity().invalidateCapabilities();
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return this.upgrades;
    }

    @Inject(
            method = "<init>(Lappeng/api/networking/IManagedGridNode;Lnet/pedroksl/advanced_ae/common/logic/AdvPatternProviderLogicHost;I)V",
            at = @At("TAIL")
    )
    private void init(IManagedGridNode mainNode, AdvPatternProviderLogicHost host, int patternInventorySize, CallbackInfo ci) {
        this.upgrades = UpgradeInventories.forMachine(host.getTerminalIcon().getItem(), 1, this::onUpgradesChanged);
    }

    @Inject(
            method = "writeToNBT",
            at = @At("TAIL")
    )
    private void saveUpgrade(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        this.upgrades.writeToNBT(tag, "upgrades", registries);
    }

    @Inject(
            method = "readFromNBT",
            at = @At("TAIL")
    )
    private void loadUpgrade(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        this.upgrades.readFromNBT(tag, "upgrades", registries);
    }

    @Inject(
            method = "addDrops",
            at = @At("TAIL")
    )
    private void addUpgradeDrops(List<ItemStack> drops, CallbackInfo ci) {
        for (var itemStack : this.upgrades) {
            if (!itemStack.isEmpty()) {
                drops.add(itemStack);
            }
        }
    }

    @Inject(
            method = "clearContent",
            at = @At("TAIL")
    )
    private void clearUpgrades(CallbackInfo ci) {
        this.upgrades.clear();
    }
}
