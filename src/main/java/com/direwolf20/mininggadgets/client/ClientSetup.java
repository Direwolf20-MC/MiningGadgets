package com.direwolf20.mininggadgets.client;

import com.direwolf20.mininggadgets.client.renderer.RenderBlockTER;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Only put client code here plz.
 */
public class ClientSetup {

    /**
     * Client Registry for renders
     */
    public static void registerRenderers() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(event ->
                ClientRegistry.bindTileEntitySpecialRenderer(RenderBlockTileEntity.class, new RenderBlockTER()));
    }
}
