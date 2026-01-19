package com.mesoulcard.mixins.io;

import appeng.client.gui.implementations.IOBusScreen;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.implementations.IOBusMenu;
import com.mesoulcard.core.Registration;
import com.mesoulcard.common.interfaces.ISoulSurgeScreenAccessor;
import com.mesoulcard.helper.SoulSurgeScreenHelper;
import com.mesoulcard.widgets.SoulSurgeButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IOBusScreen.class)
public class IOBusScreenMixin extends UpgradeableScreen<IOBusMenu> implements ISoulSurgeScreenAccessor {
    @Unique
    private SoulSurgeScreenHelper meSoulCard$soulHelper;

    public IOBusScreenMixin(IOBusMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(IOBusMenu menu, Inventory playerInventory, Component title, ScreenStyle style, CallbackInfo ci) {
        meSoulCard$soulHelper = new SoulSurgeScreenHelper(this);

        meSoulCard$soulHelper.init();
    }

    @Inject(method = "updateBeforeRender", at = @At("TAIL"))
    private void onUpdateBeforeRender(CallbackInfo ci) {
        meSoulCard$soulHelper.update();
    }

    @Override
    public void mesoulcard$addToLeftToolbar(SoulSurgeButton button) {
        this.addToLeftToolbar(button);
    }

    @Override
    public void mesoulcard$repositionElements() {
        this.repositionElements();
    }

    @Override
    public AbstractContainerMenu mesoulcard$getMenu() {
        return this.menu;
    }

    @Override
    public boolean mesoulcard$hasUpgradeInstalled(ItemLike upgrade) {
        return menu.hasUpgrade(Registration.SOUL_CARD.get());
    }
}