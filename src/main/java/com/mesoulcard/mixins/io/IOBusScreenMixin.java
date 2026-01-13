package com.mesoulcard.mixins.io;

import appeng.client.gui.implementations.IOBusScreen;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.implementations.IOBusMenu;
import com.mesoulcard.common.interfaces.IAccelerationReceiver;
import com.mesoulcard.core.Registration;
import com.mesoulcard.helper.IPatternProviderScreenAccessor;
import com.mesoulcard.helper.IUpgradableMenu;
import com.mesoulcard.widgets.SoulSurgeButton;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(IOBusScreen.class)
public class IOBusScreenMixin extends UpgradeableScreen<IOBusMenu> {
    @Unique
    private boolean lastUpgradeState = false;

    @Unique
    private SoulSurgeButton soulSurgeButton;

    public IOBusScreenMixin(IOBusMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(IOBusMenu menu, Inventory playerInventory, Component title, ScreenStyle style, CallbackInfo ci) {
        soulSurgeButton = new SoulSurgeButton(btn -> {
            System.out.println("Soul Surge Button Called");
        });

        this.addToLeftToolbar(soulSurgeButton);
        this.updateButtonVisibility();
    }

    @Inject(method = "updateBeforeRender", at = @At("TAIL"))
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
            if (soulSurgeButton.getMultiplier() != serverValue) {
                soulSurgeButton.setMultiplier(serverValue);
            }

            soulSurgeButton.setTooltip(getTooltip(clientLockStatus));
        }
    }

    private List<Component> getTooltip(boolean lockStatus) {
        Component status = lockStatus
                ? Component.translatable("gui.mesoulcard.status.inactive")
                .withStyle(ChatFormatting.RED)
                .withStyle(ChatFormatting.ITALIC)
                : Component.translatable("gui.mesoulcard.status.active")
                .withStyle(ChatFormatting.GREEN)
                .withStyle(ChatFormatting.ITALIC);

        return List.of(
                Component.literal(soulSurgeButton.getMultiplier() + "x"),
                status
        );
    }

    @Unique
    private boolean hasUpgradeInstalled() {
        if (this instanceof IPatternProviderScreenAccessor accessor) {
            return accessor.mesoulcard$hasUpgradeInstalled(Registration.SOUL_CARD.get());
        }

        if (menu instanceof IOBusMenu upgradableMenu) {
            return upgradableMenu.hasUpgrade(Registration.SOUL_CARD.get());
        }

        return false;
    }

    @Unique
    private void updateButtonVisibility() {
        soulSurgeButton.setVisibility(this.hasUpgradeInstalled());
    }
}
