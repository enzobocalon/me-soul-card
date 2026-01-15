package com.mesoulcard.mixins.compat.pprovider.screens;

import appeng.client.gui.AEBaseScreen;
import com.glodblock.github.appflux.util.helpers.IUpgradableMenu;
import com.mesoulcard.common.interfaces.IPatternProviderScreenAccessor;
import net.minecraft.world.level.ItemLike;
import net.pedroksl.advanced_ae.client.gui.AdvPatternProviderScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(AdvPatternProviderScreen.class)
public abstract class AppFluxAdvPatternProviderScreenMixin implements IPatternProviderScreenAccessor {

  @Override
  public boolean mesoulcard$compatHasUpgradeInstalled(ItemLike upgrade) {
    AEBaseScreen<?> screen = (AEBaseScreen<?>) (Object) this;
    if (screen.getMenu() instanceof IUpgradableMenu menu) {
      return menu.hasUpgrade(upgrade);
    }
    return false;
  }
}
