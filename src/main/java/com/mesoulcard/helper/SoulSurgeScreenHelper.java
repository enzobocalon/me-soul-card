package com.mesoulcard.helper;

import com.mesoulcard.common.interfaces.IAccelerationReceiver;
import com.mesoulcard.common.interfaces.ISoulSurgeScreenAccessor;
import com.mesoulcard.core.Registration;
import com.mesoulcard.widgets.SoulSurgeButton;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;


public class SoulSurgeScreenHelper {
  private boolean lastUpgradeState = false;
  private SoulSurgeButton soulSurgeButton;
  private final ISoulSurgeScreenAccessor screen;

  public SoulSurgeScreenHelper(ISoulSurgeScreenAccessor screen) {
    this.screen = screen;
  }

  public void init() {
    soulSurgeButton = new SoulSurgeButton(btn -> {});
    screen.mesoulcard$addToLeftToolbar(soulSurgeButton);
    updateButtonVisibility();
  }

  public void update() {
    boolean currentState = screen.mesoulcard$hasUpgradeInstalled(Registration.SOUL_CARD.get());

    if (currentState != lastUpgradeState) {
      lastUpgradeState = currentState;
      updateButtonVisibility();
      screen.mesoulcard$repositionElements();
    }

    var menu = screen.mesoulcard$getMenu();
    if (menu instanceof IAccelerationReceiver receiver) {
      int serverValue = receiver.meSoulCard$getClientMultiplier();
      boolean clientLockStatus = receiver.meSoulCard$getClientLockStatus();

      if (soulSurgeButton.getMultiplier() != serverValue) {
        soulSurgeButton.setMultiplier(serverValue);
      }

      soulSurgeButton.setTooltip(getTooltip(clientLockStatus));
    }
  }

  private List<Component> getTooltip(boolean lockStatus) {
    Component status = lockStatus
        ? Component.translatable("gui.mesoulcard.status.inactive")
            .withStyle(ChatFormatting.RED)
            .withStyle(ChatFormatting.ITALIC)
        : Component.translatable("gui.mesoulcard.status.active")
            .withStyle(ChatFormatting.GREEN)
            .withStyle(ChatFormatting.ITALIC);

    return List.of(
        Component.literal(soulSurgeButton.getMultiplier() + "x"),
        status);
  }

  private void updateButtonVisibility() {
    soulSurgeButton.setVisibility(screen.mesoulcard$hasUpgradeInstalled(Registration.SOUL_CARD.get()));
  }
}
