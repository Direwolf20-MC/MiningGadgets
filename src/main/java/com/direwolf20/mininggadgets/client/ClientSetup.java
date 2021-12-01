package com.direwolf20.mininggadgets.client;

import com.direwolf20.mininggadgets.client.renderer.ModificationTableTER;
import com.direwolf20.mininggadgets.client.renderer.RenderBlockTER;
import com.direwolf20.mininggadgets.client.screens.FilterScreen;
import com.direwolf20.mininggadgets.client.screens.ModificationTableScreen;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.containers.ModContainers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

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
        MenuScreens.register(ModContainers.MODIFICATIONTABLE_CONTAINER.get(), ModificationTableScreen::new);
        MenuScreens.register(ModContainers.FILTER_CONTAINER.get(), FilterScreen::new);
    }

    /**
     * Client Registry for renders
     */
    private static void registerRenderers() {
        BlockEntityRenderers.register(ModBlocks.RENDERBLOCK_TILE.get(), RenderBlockTER::new);
        BlockEntityRenderers.register(ModBlocks.MODIFICATIONTABLE_TILE.get(), ModificationTableTER::new);
    }
}
