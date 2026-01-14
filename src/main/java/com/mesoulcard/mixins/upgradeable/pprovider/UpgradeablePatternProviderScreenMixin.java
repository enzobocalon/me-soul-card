/*
* This is only loaded when App Flux or ExpandedAE is not installed
*
* Reference:https://github.com/GlodBlock/ExtendedAE/blob/appflux/1.21.1-neoforge/src/main/java/com/glodblock/github/appflux/mixins/MixinPatternProviderScreen.java
* */

package com.mesoulcard.mixins.upgradeable.pprovider;

import appeng.api.upgrades.Upgrades;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.PatternProviderScreen;
import appeng.client.gui.layout.SlotGridLayout;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.SlotPosition;
import appeng.client.gui.style.WidgetStyle;
import appeng.client.gui.widgets.ToolboxPanel;
import appeng.client.gui.widgets.UpgradesPanel;
import appeng.core.localization.GuiText;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.PatternProviderMenu;
import com.mesoulcard.common.interfaces.IStyleAccessor;
import com.mesoulcard.common.interfaces.IUpgradableMenu;
import com.mesoulcard.common.interfaces.IPatternProviderScreenAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin({ PatternProviderScreen.class })
public class UpgradeablePatternProviderScreenMixin<P extends PatternProviderMenu> extends AEBaseScreen<P>
        implements IPatternProviderScreenAccessor {
    public UpgradeablePatternProviderScreenMixin(P menu, Inventory playerInventory, Component title,
            ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(PatternProviderMenu menu, Inventory inv, Component title, ScreenStyle style, CallbackInfo ci) {
        this.widgets.add("upgrades", new UpgradesPanel(
                menu.getSlots(SlotSemantics.UPGRADE),
                this::getCompatUpgrades));
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
        ((IStyleAccessor) style).getImages().put("toolbox",
                Blitter.texture("guis/extra_panels.png", 128, 128).src(69, 62, 59, 66));
        ((IStyleAccessor) style).getWidgets().put("toolbox", ws);
        if (((IUpgradableMenu) menu).getToolbox().isPresent()) {
            this.widgets.add("toolbox", new ToolboxPanel(style, ((IUpgradableMenu) menu).getToolbox().getName()));
        }
    }

    @Unique
    private List<Component> getCompatUpgrades() {
        List<Component> list = new ArrayList<>();
        list.add(GuiText.CompatibleUpgrade.text());
        list.addAll(Upgrades.getTooltipLinesForMachine(((IUpgradableMenu) menu).getUpgrades().getUpgradableItem()));
        return list;
    }

    @Override
    public boolean mesoulcard$compatHasUpgradeInstalled(ItemLike upgrade) {
        if (menu instanceof IUpgradableMenu upgradableMenu) {
            return upgradableMenu.hasUpgrade(upgrade);
        }
        return false;
    }
}
