package com.mesoulcard.mixins.compat.pprovider.advancedae;

import appeng.api.networking.IManagedGridNode;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.parts.AEBasePart;
import com.mesoulcard.common.SoulDistributor;
import com.mesoulcard.common.interfaces.IPatternProviderSoulSlotHost;
import com.mesoulcard.common.interfaces.ISoulDistributor;
import com.mesoulcard.common.interfaces.ISoulDistributorAccessor;
import com.mesoulcard.core.Registration;
import com.mesoulcard.helper.PatternProviderSoulSlotHelper;
import com.mesoulcard.helper.SoulCardSlotInventory;
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

@Mixin({ AdvPatternProviderLogic.class })
public class AdvPatternProviderLogicMixin implements IUpgradeableObject, ISoulDistributorAccessor, IPatternProviderSoulSlotHost {
    @Shadow
    @Final
    private IManagedGridNode mainNode;

    @Shadow
    @Final
    private AdvPatternProviderLogicHost host;

    @Unique
    private SoulDistributor meSoulCard$distributor;

    @Unique
    private IUpgradeInventory meSoulCard$soulUpgradeInventory;

    @Inject(method = "<init>*", at = @At("TAIL"))
    private void init(IManagedGridNode node, AdvPatternProviderLogicHost host, int invSize, CallbackInfo ci) {
        this.meSoulCard$soulUpgradeInventory = new SoulCardSlotInventory(
                host.getTerminalIcon().getItem(),
                this::meSoulCard$onSoulSlotChanged);

        if (host instanceof AEBasePart part) {
            this.meSoulCard$distributor = new SoulDistributor(this.mainNode,
                    () -> PatternProviderSoulSlotHelper.hasUpgrade(this, Registration.SOUL_CARD.get()),
                    part);
            this.mainNode.addService(ISoulDistributor.class, this.meSoulCard$distributor);
        }
    }

    @Unique
    private void meSoulCard$onSoulSlotChanged() {
        this.host.saveChanges();
        this.host.getBlockEntity().invalidateCapabilities();
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"), remap = false)
    private void onSave(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (this.meSoulCard$soulUpgradeInventory != null) {
            this.meSoulCard$soulUpgradeInventory.writeToNBT(tag, PatternProviderSoulSlotHelper.SOUL_SLOT_NBT_KEY, registries);
        }

        if (this.meSoulCard$distributor != null) {
            var distTag = new CompoundTag();
            this.meSoulCard$distributor.writeToNBT(distTag, registries);
            tag.put("MeSoulCard", distTag);
        }
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"), remap = false)
    private void onLoad(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (this.meSoulCard$soulUpgradeInventory != null) {
            this.meSoulCard$soulUpgradeInventory.readFromNBT(tag, PatternProviderSoulSlotHelper.SOUL_SLOT_NBT_KEY, registries);
        }

        if (this.meSoulCard$distributor != null && tag.contains("MeSoulCard")) {
            var distTag = tag.getCompound("MeSoulCard");
            this.meSoulCard$distributor.readFromNBT(distTag, registries);
        }
    }

    @Inject(method = "addDrops", at = @At("TAIL"), remap = false)
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

    @Inject(method = "clearContent", at = @At("TAIL"), remap = false)
    private void meSoulCard$clearSoulSlot(CallbackInfo ci) {
        if (this.meSoulCard$soulUpgradeInventory != null) {
            this.meSoulCard$soulUpgradeInventory.clear();
        }
    }

    @Override
    @Unique
    public SoulDistributor meSoulCard$getDistributor() {
        return this.meSoulCard$distributor;
    }

    @Override
    public IUpgradeInventory meSoulCard$getSoulUpgradeInventory() {
        return this.meSoulCard$soulUpgradeInventory;
    }
}
