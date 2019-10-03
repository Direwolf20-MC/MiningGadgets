package com.direwolf20.mininggadgets.common.blocks;

import com.direwolf20.mininggadgets.client.renderer.RenderBlockTER;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlocks {
    @ObjectHolder("mininggadgets:renderblock")
    public static RenderBlock RENDERBLOCK;

    @ObjectHolder("mininggadgets:renderblock")
    public static TileEntityType<RenderBlockTileEntity> RENDERBLOCK_TILE;

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(event -> {
            ClientRegistry.bindTileEntitySpecialRenderer(RenderBlockTileEntity.class, new RenderBlockTER());
        });
    }
}
