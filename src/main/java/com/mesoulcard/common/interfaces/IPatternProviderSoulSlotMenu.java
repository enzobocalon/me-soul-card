package com.mesoulcard.common.interfaces;

import appeng.api.upgrades.IUpgradeInventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.ItemLike;

public interface IPatternProviderSoulSlotMenu {
    IUpgradeInventory meSoulCard$getSoulUpgradeInventory();

    boolean meSoulCard$hasUpgrade(ItemLike upgradeCard);

    boolean meSoulCard$isSoulSlot(Slot slot);
}
