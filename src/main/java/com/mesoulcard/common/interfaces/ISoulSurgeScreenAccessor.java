package com.mesoulcard.common.interfaces;

import com.mesoulcard.widgets.SoulSurgeButton;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.ItemLike;


public interface ISoulSurgeScreenAccessor {
  void mesoulcard$addToLeftToolbar(SoulSurgeButton button);

  void mesoulcard$repositionElements();

  AbstractContainerMenu mesoulcard$getMenu();

  boolean mesoulcard$hasUpgradeInstalled(ItemLike upgrade);
}
