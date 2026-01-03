package com.mesoulcard.core;

import appeng.api.networking.GridServices;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEParts;
import appeng.items.materials.UpgradeCardItem;
import com.mesoulcard.MESoulCard;
import com.mesoulcard.common.SoulService;
import com.mesoulcard.items.SoulCard;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.glodblock.github.extendedae.common.EAESingletons;

public class Registration {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MESoulCard.MOD_ID);

    public static final DeferredItem<UpgradeCardItem> SOUL_CARD = ITEMS.registerItem("soul_card", SoulCard::new, new Item.Properties());;
    public static void init(IEventBus eventBus) {
        ITEMS.register(eventBus);
        eventBus.addListener(Registration::addCreative);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(SOUL_CARD);
        }
    }

    public static void registerUpgrades() {
        Upgrades.add(SOUL_CARD, AEBlocks.INTERFACE, 1);
        Upgrades.add(SOUL_CARD, AEParts.PATTERN_PROVIDER, 1);
        Upgrades.add(SOUL_CARD, AEParts.EXPORT_BUS, 1);
        Upgrades.add(SOUL_CARD, AEParts.IMPORT_BUS, 1);
        Upgrades.add(SOUL_CARD, AEParts.STORAGE_BUS, 1);
        Upgrades.add(SOUL_CARD, EAESingletons.EX_PATTERN_PROVIDER_PART, 1);
    }

    public static void registerServices() {
        GridServices.register(SoulService.class, SoulService.class);
    }

}
