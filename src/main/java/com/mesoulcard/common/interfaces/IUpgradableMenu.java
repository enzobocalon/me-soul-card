package com.mesoulcard.common.interfaces;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.menu.ToolboxMenu;
import net.minecraft.world.level.ItemLike;

public interface IUpgradableMenu {
    ToolboxMenu getToolbox();

    boolean meSoulCard$hasUpgrade(ItemLike upgradeCard);

    IUpgradeInventory meSoulCard$getUpgrades();
}
