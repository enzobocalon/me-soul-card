package com.mesoulcard.core;

import appeng.api.upgrades.Upgrades;
import appeng.items.materials.UpgradeCardItem;
import com.glodblock.github.extendedae.common.EAESingletons;
import net.neoforged.neoforge.registries.DeferredItem;

public class EAELoader {

    public static void registerUpgradesInEAEParts(DeferredItem<UpgradeCardItem> soulCard) {
        Upgrades.add(soulCard, EAESingletons.EX_PATTERN_PROVIDER_PART, 1);
        Upgrades.add(soulCard, EAESingletons.EX_EXPORT_BUS, 1);
        Upgrades.add(soulCard, EAESingletons.EX_IMPORT_BUS, 1);
    }
}
