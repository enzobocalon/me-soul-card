/*
* This mixin is only loaded when App Flux or Expanded AE is installed.
* */

package com.mesoulcard.mixins;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.PatternProviderScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.implementations.PatternProviderMenu;
import com.mesoulcard.common.interfaces.IAccelerationReceiver;
import com.mesoulcard.core.Registration;
import com.mesoulcard.widgets.SoulSurgeButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PatternProviderScreen.class})
public abstract class PatternProviderScreenMixin<P extends PatternProviderMenu> extends AEBaseScreen<P> {

    @Shadow protected abstract void updateBeforeRender();

    @Unique
    private boolean lastUpgradeState = false;

    @Unique
    private SoulSurgeButton soulSurgeButton;

    public PatternProviderScreenMixin(P menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void onInit(PatternProviderMenu menu, Inventory inv, Component title, ScreenStyle style, CallbackInfo ci) {

        soulSurgeButton = new SoulSurgeButton(btn -> {
            System.out.println("Soul Surge Button Called");
        });

        this.addToLeftToolbar(soulSurgeButton);
        this.updateButtonVisibility();
    }

    @Inject(
            method = "updateBeforeRender",
            at = @At("TAIL")
    )
    private void onUpdateBeforeRender(CallbackInfo ci) {
        boolean currentState = hasUpgradeInstalled();
        if (currentState != lastUpgradeState) {
            lastUpgradeState = currentState;
            this.updateButtonVisibility();
            this.repositionElements();
        }

        if (menu instanceof IAccelerationReceiver receiver) {
            int serverValue = receiver.getClientMultiplier();
            boolean clientLockStatus = receiver.getClientLockStatus();
            // TODO: handle client lock status
            System.out.println("client lock status " + clientLockStatus);
            if (soulSurgeButton.getMultiplier() != serverValue) {
                soulSurgeButton.setMultiplier(serverValue);
            }
            soulSurgeButton.setMessage(Component.literal(soulSurgeButton.getMultiplier() + "x"));

        }
    }

    @Unique
    private boolean hasUpgradeInstalled() {
        if (menu instanceof com.glodblock.github.appflux.util.helpers.IUpgradableMenu) {
            return ((com.glodblock.github.appflux.util.helpers.IUpgradableMenu) menu).hasUpgrade(Registration.SOUL_CARD.get());
        } else if (menu instanceof lu.kolja.expandedae.helper.pattern.IUpgradableMenu) {
            return ((lu.kolja.expandedae.helper.pattern.IUpgradableMenu) menu).expandedae$hasUpgrade(Registration.SOUL_CARD.get());
        }
        return false;
    }

    @Unique
    private void updateButtonVisibility() {
        soulSurgeButton.setVisibility(this.hasUpgradeInstalled());
    }
}
