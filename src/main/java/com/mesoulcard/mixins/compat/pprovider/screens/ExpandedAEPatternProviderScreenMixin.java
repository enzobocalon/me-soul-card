package com.mesoulcard.mixins.compat.pprovider.screens;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.PatternProviderScreen;
import appeng.client.gui.layout.SlotGridLayout;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.SlotPosition;
import appeng.client.gui.style.WidgetStyle;
import appeng.menu.implementations.PatternProviderMenu;
import com.mesoulcard.common.interfaces.IPatternProviderScreenAccessor;
import com.mesoulcard.common.interfaces.IStyleAccessor;
import lu.kolja.expandedae.helper.pattern.IUpgradableMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(PatternProviderScreen.class)
public abstract class ExpandedAEPatternProviderScreenMixin<P extends PatternProviderMenu>
    implements IPatternProviderScreenAccessor {

  @Inject(method = "<init>", at = @At("TAIL"), remap = false)
  private void addToolboxImage(PatternProviderMenu menu, Inventory playerInventory, Component title, ScreenStyle style, CallbackInfo ci) {
    var sp = new SlotPosition();
    sp.setBottom(84);
    sp.setRight(1);
    sp.setGrid(SlotGridLayout.BREAK_AFTER_3COLS);

    var ws = new WidgetStyle();
    ws.setRight(2);
    ws.setBottom(90);
    ws.setWidth(59);
    ws.setHeight(66);

    style.getSlots().put("TOOLBOX", sp);
    ((IStyleAccessor) style).meSoulCard$getImages().put("toolbox",
            Blitter.texture("guis/extra_panels.png", 128, 128).src(69, 62, 59, 66));
    ((IStyleAccessor) style).meSoulCard$getWidgets().put("toolbox", ws);
  }

  @Override
  public boolean mesoulcard$compatHasUpgradeInstalled(ItemLike upgrade) {
    AEBaseScreen<?> screen = (AEBaseScreen<?>) (Object) this;
    if (screen.getMenu() instanceof IUpgradableMenu menu) {
      return menu.expandedae$hasUpgrade(upgrade);
    }
    return false;
  }
}
