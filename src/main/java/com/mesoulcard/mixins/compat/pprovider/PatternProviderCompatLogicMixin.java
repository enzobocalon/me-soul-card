package com.mesoulcard.mixins.compat.pprovider;

import appeng.api.networking.IManagedGridNode;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import com.mesoulcard.common.interfaces.IPatternProviderSoulSlotHost;
import com.mesoulcard.helper.PatternProviderSoulSlotHelper;
import com.mesoulcard.helper.SoulCardSlotInventory;
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

/*
* Modifies PatternProviderLogic to add support por SoulCardSlotInventory
* */

@Mixin(value = PatternProviderLogic.class, remap = false)
public class PatternProviderCompatLogicMixin implements IPatternProviderSoulSlotHost {
    @Shadow
    @Final
    private PatternProviderLogicHost host;

    @Unique
    private IUpgradeInventory meSoulCard$soulUpgradeInventory;

    @Inject(method = "<init>(Lappeng/api/networking/IManagedGridNode;Lappeng/helpers/patternprovider/PatternProviderLogicHost;I)V", at = @At("TAIL"))
    private void meSoulCard$initSoulSlot(IManagedGridNode mainNode, PatternProviderLogicHost host, int patternInventorySize,
            CallbackInfo ci) {
        this.meSoulCard$soulUpgradeInventory = new SoulCardSlotInventory(
                host.getTerminalIcon().getItem(),
                this::meSoulCard$onSoulSlotChanged);
    }

    @Unique
    private void meSoulCard$onSoulSlotChanged() {
        this.host.saveChanges();
        this.host.getBlockEntity().invalidateCapabilities();
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"))
    private void meSoulCard$saveSoulSlot(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (this.meSoulCard$soulUpgradeInventory != null) {
            this.meSoulCard$soulUpgradeInventory.writeToNBT(tag, PatternProviderSoulSlotHelper.SOUL_SLOT_NBT_KEY, registries);
        }
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"))
    private void meSoulCard$loadSoulSlot(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (this.meSoulCard$soulUpgradeInventory != null) {
            this.meSoulCard$soulUpgradeInventory.readFromNBT(tag, PatternProviderSoulSlotHelper.SOUL_SLOT_NBT_KEY, registries);
        }
    }

    @Inject(method = "addDrops", at = @At("TAIL"))
    private void meSoulCard$dropSoulSlot(List<ItemStack> drops, CallbackInfo ci) {
        if (this.meSoulCard$soulUpgradeInventory == null) {
            return;
        }

        for (var itemStack : this.meSoulCard$soulUpgradeInventory) {
            if (!itemStack.isEmpty()) {
                drops.add(itemStack);
            }
        }
    }

    @Inject(method = "clearContent", at = @At("TAIL"))
    private void meSoulCard$clearSoulSlot(CallbackInfo ci) {
        if (this.meSoulCard$soulUpgradeInventory != null) {
            this.meSoulCard$soulUpgradeInventory.clear();
        }
    }

    @Override
    public IUpgradeInventory meSoulCard$getSoulUpgradeInventory() {
        return this.meSoulCard$soulUpgradeInventory;
    }
}
