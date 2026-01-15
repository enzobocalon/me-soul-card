package com.mesoulcard.mixins.compat.pprovider.advancedae;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.implementations.PatternProviderMenu;
import com.mesoulcard.common.interfaces.IPatternProviderScreenAccessor;
import com.mesoulcard.common.interfaces.ISoulSurgeScreenAccessor;
import com.mesoulcard.common.interfaces.IUpgradableMenu;
import com.mesoulcard.helper.SoulSurgeScreenHelper;
import com.mesoulcard.widgets.SoulSurgeButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.ItemLike;
import net.pedroksl.advanced_ae.client.gui.AdvPatternProviderScreen;
import net.pedroksl.advanced_ae.gui.advpatternprovider.AdvPatternProviderMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({AdvPatternProviderScreen.class})
public abstract class AdvPatternProviderScreenMixin<P extends AdvPatternProviderMenu> extends AEBaseScreen<P> implements ISoulSurgeScreenAccessor {
    @Shadow
    protected abstract void updateBeforeRender();

    @Unique
    private SoulSurgeScreenHelper soulHelper;

    public AdvPatternProviderScreenMixin(P menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(AdvPatternProviderMenu menu, Inventory playerInventory, Component title, ScreenStyle style, CallbackInfo ci) {
        soulHelper = new SoulSurgeScreenHelper(this);
        soulHelper.init();
    }

    @Inject(method = "updateBeforeRender", at = @At("TAIL"))
    private void onUpdateBeforeRender(CallbackInfo ci) {
        soulHelper.update();
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
        if (this instanceof IPatternProviderScreenAccessor accessor) {
            return accessor.mesoulcard$compatHasUpgradeInstalled(upgrade);
        }
        if (menu instanceof IUpgradableMenu upgradableMenu) {
            return upgradableMenu.hasUpgrade(upgrade);
        }
        return false;
    }
}
