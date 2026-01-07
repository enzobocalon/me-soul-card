package com.mesoulcard.mixins.upgradeable.pprovider;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.ToolboxMenu;
import appeng.menu.implementations.PatternProviderMenu;
import com.mesoulcard.helper.IUpgradableMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PatternProviderMenu.class})
public class UpgradeablePatternProviderMenuMixin extends AEBaseMenu implements IUpgradableMenu {
    public UpgradeablePatternProviderMenuMixin(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
        super(menuType, id, playerInventory, host);
    }

    @Final
    @Shadow
    protected PatternProviderLogic logic;

    @Unique
    private ToolboxMenu toolbox;

    @Inject(
            method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lappeng/helpers/patternprovider/PatternProviderLogicHost;)V",
            at = @At("TAIL"),
            remap = true
    )
    private void onInit(MenuType<?> menuType, int id, Inventory playerInventory, PatternProviderLogicHost host, CallbackInfo ci) {
        this.toolbox = new ToolboxMenu(this);
        IUpgradeInventory upgrades = ((IUpgradeableObject) this.logic).getUpgrades();
        this.setupUpgrades(upgrades);
    }

    @Override
    public ToolboxMenu getToolbox() {
        return this.toolbox;
    }

    @Override
    public boolean hasUpgrade(ItemLike upgradeCard) {
        return getUpgrades().isInstalled(upgradeCard);
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return ((IUpgradeableObject) this.logic).getUpgrades();
    }
}
