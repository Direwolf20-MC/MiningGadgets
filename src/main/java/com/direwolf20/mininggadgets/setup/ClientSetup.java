package com.direwolf20.mininggadgets.setup;

import com.direwolf20.mininggadgets.client.ClientEvents;
import com.direwolf20.mininggadgets.client.renderer.ModificationTableTER;
import com.direwolf20.mininggadgets.client.renderer.RenderBlockTER;
import com.direwolf20.mininggadgets.client.screens.FilterScreen;
import com.direwolf20.mininggadgets.client.screens.ModificationTableScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ClientSetup {
    public static void init(final FMLClientSetupEvent event) {
        //registerRenderers(event);
        registerContainerScreens(event);

        //Register our Render Events Class
        NeoForge.EVENT_BUS.register(ClientEvents.class);
    }

    /**
     * Called from some Client Dist runner in the main class
     */
    private static void registerContainerScreens(final FMLClientSetupEvent event) {
        //Screens
        event.enqueueWork(() -> {
            MenuScreens.register(Registration.MODIFICATIONTABLE_CONTAINER.get(), ModificationTableScreen::new);
            MenuScreens.register(Registration.FILTER_CONTAINER.get(), FilterScreen::new);
        });
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
