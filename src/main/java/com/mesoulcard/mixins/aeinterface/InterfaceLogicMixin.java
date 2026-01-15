package com.mesoulcard.mixins.aeinterface;

import appeng.api.networking.IManagedGridNode;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.helpers.InterfaceLogic;
import appeng.helpers.InterfaceLogicHost;
import appeng.parts.AEBasePart;
import com.mesoulcard.common.SoulDistributor;
import com.mesoulcard.common.interfaces.ISoulDistributor;
import com.mesoulcard.common.interfaces.ISoulDistributorAccessor;
import com.mesoulcard.core.Registration;
import com.mesoulcard.helper.PatternProviderMixinHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InterfaceLogic.class, remap = false)
public class InterfaceLogicMixin implements IUpgradeableObject, ISoulDistributorAccessor {
    @Shadow
    @Final
    private IManagedGridNode mainNode;

    @Unique
    private SoulDistributor distributor;

    @Inject(method = "<init>(Lappeng/api/networking/IManagedGridNode;Lappeng/helpers/InterfaceLogicHost;Lnet/minecraft/world/item/Item;)V", at = @At("TAIL"))
    private void init3Params(IManagedGridNode gridNode, InterfaceLogicHost host, Item is, CallbackInfo ci) {
        initDistributor(host);
    }

    @Inject(method = "<init>(Lappeng/api/networking/IManagedGridNode;Lappeng/helpers/InterfaceLogicHost;Lnet/minecraft/world/item/Item;I)V", at = @At("TAIL"))
    private void init4Params(IManagedGridNode gridNode, InterfaceLogicHost host, Item is, int slots, CallbackInfo ci) {
        initDistributor(host);
    }

    @Unique
    private void initDistributor(InterfaceLogicHost host) {
        if (host instanceof AEBasePart part) {
            this.distributor = new SoulDistributor(this.mainNode,
                    () -> getUpgrades().isInstalled(Registration.SOUL_CARD.get()),
                    part);
            this.mainNode.addService(ISoulDistributor.class, this.distributor);
        }
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"), remap = false)
    private void onSave(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (this.distributor != null) {
            var distTag = new CompoundTag();
            this.distributor.writeToNBT(distTag, registries);
            tag.put("MeSoulCard", distTag);
        }
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"), remap = false)
    private void onLoad(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (this.distributor != null && tag.contains("MeSoulCard")) {
            var distTag = tag.getCompound("MeSoulCard");
            this.distributor.readFromNBT(distTag, registries);
        }
    }

    @Inject(method = "onUpgradesChanged", at = @At("TAIL"))
    private void onUpgradesChanged(CallbackInfo ci) {
        if (this.distributor != null) {
            PatternProviderMixinHelper.updateSoulDistributor(this.mainNode);
        }
    }

    @Override
    @Unique
    public SoulDistributor getDistributor() {
        return this.distributor;
    }
}
