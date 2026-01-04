package com.mesoulcard.helper;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.menu.ToolboxMenu;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public interface IUpgradableMenu {
    ToolboxMenu getToolbox();

    boolean hasUpgrade(ItemLike upgradeCard);

    IUpgradeInventory getUpgrades();
}
