/*
 * Only loaded when app flux is not present
 * */

package com.mesoulcard.mixins.upgradeable.advancedae.pprovider;

import appeng.api.upgrades.Upgrades;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.layout.SlotGridLayout;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.SlotPosition;
import appeng.client.gui.style.WidgetStyle;
import appeng.client.gui.widgets.ToolboxPanel;
import appeng.client.gui.widgets.UpgradesPanel;
import appeng.core.localization.GuiText;
import appeng.menu.SlotSemantics;
import appeng.menu.ToolboxMenu;
import com.mesoulcard.common.interfaces.IPatternProviderScreenAccessor;
import com.mesoulcard.common.interfaces.IStyleAccessor;
import com.mesoulcard.common.interfaces.IUpgradableMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.ItemLike;
import net.pedroksl.advanced_ae.client.gui.SmallAdvPatternProviderScreen;
import net.pedroksl.advanced_ae.gui.advpatternprovider.AdvPatternProviderMenu;
import net.pedroksl.advanced_ae.gui.advpatternprovider.SmallAdvPatternProviderMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin({SmallAdvPatternProviderScreen.class})
public abstract class UpgradeableSmallAdvPatternProviderScreenMixin <P extends AdvPatternProviderMenu> extends AEBaseScreen<P> implements IPatternProviderScreenAccessor {
    public UpgradeableSmallAdvPatternProviderScreenMixin(P menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(SmallAdvPatternProviderMenu menu, Inventory playerInventory, Component title, ScreenStyle style, CallbackInfo ci) {
        this.widgets.add("upgrades", new UpgradesPanel(
                menu.getSlots(SlotSemantics.UPGRADE),
                this::meSoulCard$getCompatUpgrades));
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
        if (menu instanceof IUpgradableMenu upgradableMenu) {
            ToolboxMenu toolbox = upgradableMenu.getToolbox();
            if (toolbox != null && toolbox.isPresent()) {
                this.widgets.add("toolbox", new ToolboxPanel(style, toolbox.getName()));
            }
        }
    }

    @Unique
    private List<Component> meSoulCard$getCompatUpgrades() {
        List<Component> list = new ArrayList<>();
        list.add(GuiText.CompatibleUpgrade.text());
        list.addAll(Upgrades.getTooltipLinesForMachine(((IUpgradableMenu) menu).meSoulCard$getUpgrades().getUpgradableItem()));
        return list;
    }

    @Override
    public boolean mesoulcard$compatHasUpgradeInstalled(ItemLike upgrade) {
        if (menu instanceof IUpgradableMenu upgradableMenu) {
            return upgradableMenu.meSoulCard$hasUpgrade(upgrade);
        }
        return false;
    }
}
