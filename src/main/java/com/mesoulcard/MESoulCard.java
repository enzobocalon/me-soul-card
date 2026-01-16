package com.mesoulcard;

import com.mesoulcard.common.SoulAccelerationManager;
import com.mesoulcard.core.Registration;
import com.mesoulcard.network.PacketHandler;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@Mod(MESoulCard.MOD_ID)
public class MESoulCard {
    public static final String MOD_ID = "mesoulcard";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final boolean ENABLE_DEBUG_LOGS = false;

    public MESoulCard(IEventBus modEventBus, ModContainer modContainer) {
        Registration.init(modEventBus);
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);

        PacketHandler.init(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        Registration.registerUpgrades();
        Registration.registerServices();
    }

    public static ResourceLocation makeId(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        // Clear all acceleration locks when server stops
        SoulAccelerationManager.clearAll();
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        // Clear locks for the unloading chunk to prevent stale cache entries
        if (!event.getLevel().isClientSide()) {
            SoulAccelerationManager.clearChunk(event.getChunk().getPos());
        }
    }
}
