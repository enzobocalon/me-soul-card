package com.mesoulcard.mixins;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.core.settings.TickRates;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import com.mesoulcard.common.ISoulDistributor;
import com.mesoulcard.common.SoulDistributor;
import com.mesoulcard.core.Registration;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PatternProviderLogic.class})
public class PatternProviderLogicMixin implements IUpgradeableObject, IGridTickable {
    @Shadow @Final private IManagedGridNode mainNode;

    @Shadow @Final private PatternProviderLogicHost host;

    @Unique
    private SoulDistributor distributor;

    @Unique
    private final TickRates tickRates = TickRates.ExportBus;

    @Inject(
            method = "<init>*",
            at = @At("TAIL")
    )
    private void init(IManagedGridNode node, PatternProviderLogicHost host, int invSize, CallbackInfo ci) {
        this.distributor = new SoulDistributor(this.mainNode, () ->
            getUpgrades().isInstalled(Registration.SOUL_CARD.get())
        );
        this.mainNode.addService(ISoulDistributor.class, this.distributor);
        this.mainNode.addService(IGridTickable.class, this);
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(tickRates.getMin(), tickRates.getMax(), false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        this.distributor.updateSleep();
        if (this.distributor.isAwake()) {
            return TickRateModulation.FASTER;
        } else {
            return TickRateModulation.IDLE;
        }
    }
}
