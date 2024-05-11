package com.direwolf20.mininggadgets.setup;

import com.direwolf20.mininggadgets.client.ClientEvents;
import com.direwolf20.mininggadgets.client.renderer.ModificationTableTER;
import com.direwolf20.mininggadgets.client.renderer.RenderBlockTER;
import com.direwolf20.mininggadgets.client.screens.FilterScreen;
import com.direwolf20.mininggadgets.client.screens.ModificationTableScreen;
import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;

@EventBusSubscriber(modid = MiningGadgets.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    public static void init(final FMLClientSetupEvent event) {
        //registerRenderers(event);

        //Register our Render Events Class
        NeoForge.EVENT_BUS.register(ClientEvents.class);
    }

    /**
     * Called from some Client Dist runner in the main class
     */
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(Registration.MODIFICATIONTABLE_CONTAINER.get(), ModificationTableScreen::new);
        event.register(Registration.FILTER_CONTAINER.get(), FilterScreen::new);
    }

    /**
     * Client Registry for renders
     */
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(Registration.RENDERBLOCK_TILE.get(), RenderBlockTER::new);
        event.registerBlockEntityRenderer(Registration.MODIFICATIONTABLE_TILE.get(), ModificationTableTER::new);
    }
}
