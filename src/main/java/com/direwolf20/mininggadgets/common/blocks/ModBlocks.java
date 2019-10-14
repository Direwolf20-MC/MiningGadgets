package com.direwolf20.mininggadgets.common.blocks;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.client.renderer.RenderBlockTER;
import com.direwolf20.mininggadgets.common.tiles.ModificationTableTileEntity;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(bus= EventBusSubscriber.Bus.MOD, modid = MiningGadgets.MOD_ID)
@ObjectHolder(MiningGadgets.MOD_ID)
public class ModBlocks {
    // Blocks
    @ObjectHolder("renderblock")
    public static RenderBlock RENDERBLOCK;

    @ObjectHolder("minerslight")
    public static MinersLight MINERSLIGHT;

    @ObjectHolder("modificationtable")
    public static ModificationTable MODIFICATIONTABLE;

    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
        blockRegistryEvent.getRegistry().registerAll(
                new RenderBlock(),
                new MinersLight(),
                new ModificationTable()
        );
    }

    // Tiles
    @ObjectHolder("renderblock")
    public static TileEntityType<RenderBlockTileEntity> RENDERBLOCK_TILE;

    @ObjectHolder("modificationtable")
    public static TileEntityType<ModificationTableTileEntity> MODIFICATIONTABLE_TILE;

    /**
     * TileEntity Registers
     */
    @SubscribeEvent
    public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().registerAll(
                TileEntityType.Builder.create(RenderBlockTileEntity::new, ModBlocks.RENDERBLOCK).build(null).setRegistryName("renderblock"),
                TileEntityType.Builder.create(ModificationTableTileEntity::new, ModBlocks.MODIFICATIONTABLE).build(null).setRegistryName("modificationtable")
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
