package com.mesoulcard.mixins.compat.pprovider.screens;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.PatternProviderScreen;
import appeng.menu.implementations.PatternProviderMenu;
import com.glodblock.github.appflux.util.helpers.IUpgradableMenu;
import com.mesoulcard.common.interfaces.IPatternProviderScreenAccessor;
import com.mesoulcard.common.interfaces.IPatternProviderSoulSlotMenu;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(PatternProviderScreen.class)
public abstract class AppFluxPatternProviderScreenMixin<P extends PatternProviderMenu>
        implements IPatternProviderScreenAccessor {

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
