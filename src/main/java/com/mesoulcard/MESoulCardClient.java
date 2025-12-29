package com.mesoulcard;

import com.mesoulcard.core.Registration;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = MESoulCard.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MESoulCard.MOD_ID, value = Dist.CLIENT)
public class MESoulCardClient {
    public MESoulCardClient(IEventBus modEventBus, ModContainer container) { }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {}
}
