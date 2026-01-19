/*
 * Only loaded when app flux is not present
 * */

package com.mesoulcard.mixins.upgradeable.advancedae.pprovider;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.menu.AEBaseMenu;
import appeng.menu.ToolboxMenu;
import com.mesoulcard.common.interfaces.IUpgradableMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.ItemLike;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogic;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;
import net.pedroksl.advanced_ae.gui.advpatternprovider.AdvPatternProviderMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AdvPatternProviderMenu.class)
public class UpgradeableAdvPatternProviderMenuMixin extends AEBaseMenu implements IUpgradableMenu {

    @Final
    @Shadow(remap = false)
    protected AdvPatternProviderLogic logic;

    @Unique
    private ToolboxMenu toolbox;

    public UpgradeableAdvPatternProviderMenuMixin(MenuType<? extends AdvPatternProviderMenu> menuType, int id, Inventory playerInventory, AdvPatternProviderLogicHost host) {
        super(menuType, id, playerInventory, host);
    }

    @Inject(
            method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;"
                    + "Lnet/pedroksl/advanced_ae/common/logic/AdvPatternProviderLogicHost;)V",
            at =
            @At(
                    value = "INVOKE",
                    target = "Lnet/pedroksl/advanced_ae/gui/advpatternprovider/AdvPatternProviderMenu;"
                            + "createPlayerInventorySlots(Lnet/minecraft/world/entity/player/Inventory;)V"),
            remap = false)
    private void onInit(MenuType<?> menuType, int id, Inventory playerInventory, AdvPatternProviderLogicHost host, CallbackInfo ci) {
        this.toolbox = new ToolboxMenu(this);
        IUpgradeInventory upgrades = ((IUpgradeableObject) host).getUpgrades();
        this.setupUpgrades(upgrades);
    }

    @Override
    public boolean meSoulCard$hasUpgrade(ItemLike upgradeCard) {
        return this.meSoulCard$getUpgrades().isInstalled(upgradeCard);
    }

    @Override
    public IUpgradeInventory meSoulCard$getUpgrades() {
        return ((IUpgradeableObject) this.logic).getUpgrades();
    }

    @Override
    public ToolboxMenu getToolbox() {
        return this.toolbox;
    }
}
