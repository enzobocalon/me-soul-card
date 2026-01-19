package com.mesoulcard.mixins.pprovider;

import appeng.api.networking.IManagedGridNode;
import appeng.api.upgrades.*;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.parts.AEBasePart;
import com.mesoulcard.common.interfaces.ISoulDistributor;
import com.mesoulcard.common.interfaces.ISoulDistributorAccessor;
import com.mesoulcard.common.SoulDistributor;
import com.mesoulcard.core.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ PatternProviderLogic.class })
public class PatternProviderLogicMixin implements IUpgradeableObject, ISoulDistributorAccessor {
    @Shadow
    @Final
    private IManagedGridNode mainNode;

    @Unique
    private SoulDistributor meSoulCard$distributor;

    @Inject(method = "<init>*", at = @At("TAIL"))
    private void init(IManagedGridNode node, PatternProviderLogicHost host, int invSize, CallbackInfo ci) {
        // Should not change the Block version of Pattern Provider. Only Parts can have Soul Distribution.
        if (host instanceof AEBasePart part) {
            this.meSoulCard$distributor = new SoulDistributor(this.mainNode,
                    () -> getUpgrades().isInstalled(Registration.SOUL_CARD.get()),
                    part);
            this.mainNode.addService(ISoulDistributor.class, this.meSoulCard$distributor);
        }
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

    @Override
    @Unique
    public SoulDistributor meSoulCard$getDistributor() {
        return this.meSoulCard$distributor;
    }
}
