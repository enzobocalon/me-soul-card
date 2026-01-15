package com.mesoulcard.core;

import appeng.api.upgrades.Upgrades;
import appeng.items.materials.UpgradeCardItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;

public class AAELoader {
    public static void registerUpgradesInAAEParts(DeferredItem<UpgradeCardItem> soulCard) {
        Upgrades.add(soulCard, AAEItems.ADV_PATTERN_PROVIDER, 1);
        Upgrades.add(soulCard, AAEItems.SMALL_ADV_PATTERN_PROVIDER, 1);
        Upgrades.add(soulCard, AAEItems.IMPORT_EXPORT_BUS, 1);
    }
}
