package com.mesoulcard.mixins.pprovider;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.PatternProviderScreen;
import appeng.client.gui.layout.SlotGridLayout;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.SlotPosition;
import appeng.client.gui.style.WidgetStyle;
import appeng.client.gui.widgets.ToolboxPanel;
import appeng.client.gui.widgets.UpgradesPanel;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.PatternProviderMenu;
import com.mesoulcard.common.interfaces.ISoulSurgeScreenAccessor;
import com.mesoulcard.common.interfaces.IStyleAccessor;
import com.mesoulcard.common.interfaces.IUpgradableMenu;
import com.mesoulcard.helper.ModCompatHelper;
import com.mesoulcard.helper.SoulSurgeScreenHelper;
import com.mesoulcard.widgets.SoulSurgeButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mesoulcard.common.interfaces.IPatternProviderScreenAccessor;
import net.neoforged.fml.ModList;

@Mixin({ PatternProviderScreen.class })
public abstract class PatternProviderScreenMixin<P extends PatternProviderMenu> extends AEBaseScreen<P>
        implements ISoulSurgeScreenAccessor {

    @Shadow
    protected abstract void updateBeforeRender();

    @Unique
    private SoulSurgeScreenHelper meSoulCard$soulHelper;

    public PatternProviderScreenMixin(P menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(PatternProviderMenu menu, Inventory inv, Component title, ScreenStyle style, CallbackInfo ci) {
        meSoulCard$soulHelper = new SoulSurgeScreenHelper(this);
        meSoulCard$soulHelper.init();

        if (!ModCompatHelper.hasCompatModsLoaded()
                && menu instanceof IUpgradableMenu upgradableMenu) {
            this.widgets.add("upgrades", new UpgradesPanel(menu.getSlots(SlotSemantics.UPGRADE)));

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
            if (upgradableMenu.getToolbox().isPresent()) {
                this.widgets.add("toolbox", new ToolboxPanel(style, upgradableMenu.getToolbox().getName()));
            }
        }
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
        if (this instanceof IPatternProviderScreenAccessor accessor) {
            return accessor.mesoulcard$compatHasUpgradeInstalled(upgrade);
        }
        if (menu instanceof IUpgradableMenu upgradableMenu) {
            return upgradableMenu.meSoulCard$hasUpgrade(upgrade);
        }
        return false;
    }
}
