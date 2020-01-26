package com.direwolf20.mininggadgets.client;

import com.direwolf20.mininggadgets.client.renderer.RenderBlockTER;
import com.direwolf20.mininggadgets.client.screens.FilterScreen;
import com.direwolf20.mininggadgets.client.screens.ModificationTableScreen;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.containers.ModContainers;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Only put client code here plz.
 */
public final class ClientSetup {
    public static void setup() {
        registerRenderers();
        registerContainerScreens();
    }

    /**
     * Called from some Client Dist runner in the main class
     */
    private static void registerContainerScreens() {
        ScreenManager.registerFactory(ModContainers.MODIFICATIONTABLE_CONTAINER.get(), ModificationTableScreen::new);
        ScreenManager.registerFactory(ModContainers.FILTER_CONTAINER.get(), FilterScreen::new);
    }

    /**
     * Client Registry for renders
     */
    private static void registerRenderers() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(event ->
                ClientRegistry.bindTileEntityRenderer(ModBlocks.RENDERBLOCK_TILE.get(), RenderBlockTER::new));
    }
}
