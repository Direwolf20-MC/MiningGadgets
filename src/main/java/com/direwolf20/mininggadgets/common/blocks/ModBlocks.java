package com.direwolf20.mininggadgets.common.blocks;

import com.direwolf20.mininggadgets.Setup;
import com.direwolf20.mininggadgets.client.renderer.RenderBlockTER;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(bus= EventBusSubscriber.Bus.MOD, modid = Setup.MOD_ID)
@ObjectHolder(Setup.MOD_ID)
public class ModBlocks {
    // Blocks
    @ObjectHolder("renderblock")
    public static RenderBlock RENDERBLOCK;

    @ObjectHolder("minerslight")
    public static MinersLight MINERSLIGHT;

    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
        blockRegistryEvent.getRegistry().registerAll(
                new RenderBlock(),
                new MinersLight()
        );
    }

    // Tiles
    @ObjectHolder("renderblock")
    public static TileEntityType<RenderBlockTileEntity> RENDERBLOCK_TILE;

    /**
     * TileEntity Registers
     */
    @SubscribeEvent
    public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().registerAll(
                TileEntityType.Builder.create(RenderBlockTileEntity::new, ModBlocks.RENDERBLOCK).build(null).setRegistryName("renderblock")
        );
    }

    /**
     * Client Registry for renders
     */
    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(event -> {
            ClientRegistry.bindTileEntitySpecialRenderer(RenderBlockTileEntity.class, new RenderBlockTER());
        });
    }
}
