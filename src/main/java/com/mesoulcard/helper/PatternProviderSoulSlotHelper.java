package com.mesoulcard.helper;

import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.Upgrades;
import appeng.core.localization.GuiText;
import com.mesoulcard.common.interfaces.IPatternProviderSoulSlotHost;
import com.mesoulcard.core.ModConstants;
import com.mesoulcard.core.Registration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

public final class PatternProviderSoulSlotHelper {
    public static final String SOUL_SLOT_NBT_KEY = "meSoulCardSoulUpgrade";

    private PatternProviderSoulSlotHelper() {}

    public static boolean hasUpgrade(Object target, ItemLike upgradeCard) {
        if (target instanceof IUpgradeableObject upgradeableObject
                && upgradeableObject.getUpgrades().isInstalled(upgradeCard)) {
            return true;
        }

        if (target instanceof IPatternProviderSoulSlotHost soulSlotHost) {
            return soulSlotHost.meSoulCard$getSoulUpgradeInventory().isInstalled(upgradeCard);
        }

        return false;
    }

    public static List<Component> getSoulSlotTooltip() {
        return List.of(
                GuiText.CompatibleUpgrades.text(),
                Registration.SOUL_CARD
                        .get()
                        .getDefaultInstance()
                        .getHoverName()
                        .copy()
                        .append(Component.literal(" (%s)".formatted(ModConstants.SOUL_CARD_PATTERN_PROVIDER_UPGRADE_LIMIT))));
    }

    public static List<Component> getRegularUpgradeTooltip(ItemLike upgradableItem) {
        ArrayList<Component> result = new ArrayList<>(Upgrades.getTooltipLinesForMachine(upgradableItem));
        String soulCardName = Registration.SOUL_CARD.get().getDefaultInstance().getHoverName().getString();
        result.removeIf(line -> line.getString().contains(soulCardName));
        result.addFirst(GuiText.CompatibleUpgrades.text());

        return result;
    }
}
