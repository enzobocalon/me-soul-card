package com.mesoulcard.mixins.upgradeable.pprovider;

import appeng.api.networking.IManagedGridNode;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin({PatternProviderLogic.class})
public class UpgradeablePatternProviderLogicMixin implements IUpgradeableObject {

    @Final
    @Shadow
    private PatternProviderLogicHost host;

    @Unique
    private IUpgradeInventory meSoulCard$upgrades = UpgradeInventories.empty();

    @Unique
    private void meSoulCard$onUpgradesChanged() {
        this.host.saveChanges();
        this.host.getBlockEntity().invalidateCapabilities();
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return this.meSoulCard$upgrades;
    }

    @Inject(
            method = "<init>(Lappeng/api/networking/IManagedGridNode;Lappeng/helpers/patternprovider/PatternProviderLogicHost;I)V",
            at = @At("TAIL")
    )
    private void init(IManagedGridNode node, PatternProviderLogicHost host, int invSize, CallbackInfo ci) {
        this.meSoulCard$upgrades = UpgradeInventories.forMachine(host.getTerminalIcon().getItem(), 1, this::meSoulCard$onUpgradesChanged);
    }

    @Inject(
            method = "writeToNBT",
            at = @At("TAIL")
    )
    private void saveUpgrade(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        this.meSoulCard$upgrades.writeToNBT(tag, "upgrades", registries);
    }

    @Inject(
            method = "readFromNBT",
            at = @At("TAIL")
    )
    private void loadUpgrade(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        this.meSoulCard$upgrades.readFromNBT(tag, "upgrades", registries);
    }

    @Inject(
            method = "addDrops",
            at = @At("TAIL")
    )
    private void addUpgradeDrops(List<ItemStack> drops, CallbackInfo ci) {
        for (var itemStack : this.meSoulCard$upgrades) {
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
        this.meSoulCard$upgrades.clear();
    }
}
