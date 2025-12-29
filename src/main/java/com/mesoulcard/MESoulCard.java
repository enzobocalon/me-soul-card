package com.mesoulcard;

import com.mesoulcard.core.Registration;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(MESoulCard.MOD_ID)
public class MESoulCard {
    public static final String MOD_ID = "mesoulcard";
    public static final Logger LOGGER = LogUtils.getLogger();
    public MESoulCard(IEventBus modEventBus, ModContainer modContainer) {
        Registration.init(modEventBus);
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        Registration.registerUpgrades();
        Registration.registerServices();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}
}
