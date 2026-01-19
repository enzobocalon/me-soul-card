package com.mesoulcard.mixins.io;

import appeng.api.parts.IPartItem;
import appeng.core.settings.TickRates;
import appeng.parts.automation.IOBusPart;
import appeng.parts.automation.UpgradeablePart;
import com.mesoulcard.common.SoulDistributor;
import com.mesoulcard.common.interfaces.ISoulDistributor;
import com.mesoulcard.common.interfaces.ISoulDistributorAccessor;
import com.mesoulcard.helper.PatternProviderMixinHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(IOBusPart.class)
public abstract class IOBusPartMixin extends UpgradeablePart implements ISoulDistributorAccessor {
    @Unique
    private SoulDistributor meSoulCard$distributor;

    public IOBusPartMixin(IPartItem<?> partItem) {
        super(partItem);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void IOBusPart(TickRates tickRates, Set<?> supportedKeyTypes, IPartItem<?> partItem, CallbackInfo ci) {
        this.meSoulCard$distributor = new SoulDistributor(this.getMainNode(),
                () -> this.getUpgrades().isInstalled(com.mesoulcard.core.Registration.SOUL_CARD.get()),
                this);
        this.getMainNode().addService(ISoulDistributor.class, this.meSoulCard$distributor);
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"), remap = false)
    private void onSave(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (this.meSoulCard$distributor != null) {
            var distTag = new CompoundTag();
            this.meSoulCard$distributor.writeToNBT(distTag, registries);
            tag.put("MeSoulCard", distTag);
        }
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"), remap = false)
    private void onLoad(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (this.meSoulCard$distributor != null && tag.contains("MeSoulCard")) {
            var distTag = tag.getCompound("MeSoulCard");
            this.meSoulCard$distributor.readFromNBT(distTag, registries);
        }
    }

    @Inject(method = "upgradesChanged", at = @At("TAIL"))
    private void onUpgradesChanged(CallbackInfo ci) {
        if (this.meSoulCard$distributor != null) {
            PatternProviderMixinHelper.updateSoulDistributor(this.getMainNode());
        }
    }

    @Override
    @Unique
    public SoulDistributor meSoulCard$getDistributor() {
        return this.meSoulCard$distributor;
    }
}
