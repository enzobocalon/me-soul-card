package com.mesoulcard.mixins.compat.pprovider.screens;

import appeng.client.gui.AEBaseScreen;
import com.glodblock.github.appflux.util.helpers.IUpgradableMenu;
import com.mesoulcard.common.interfaces.IPatternProviderScreenAccessor;
import com.mesoulcard.common.interfaces.IPatternProviderSoulSlotMenu;
import net.minecraft.world.level.ItemLike;
import net.pedroksl.advanced_ae.client.gui.SmallAdvPatternProviderScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(SmallAdvPatternProviderScreen.class)
public abstract class AppFluxSmallAdvPatternProviderScreenMixin implements IPatternProviderScreenAccessor {

    @Override
    public boolean mesoulcard$compatHasUpgradeInstalled(ItemLike upgrade) {
        AEBaseScreen<?> screen = (AEBaseScreen<?>) (Object) this;
        if (screen.getMenu() instanceof IPatternProviderSoulSlotMenu menu && menu.meSoulCard$hasUpgrade(upgrade)) {
            return true;
        }
        if (screen.getMenu() instanceof IUpgradableMenu menu) {
            return menu.hasUpgrade(upgrade);
        }
        return false;
    }
}
