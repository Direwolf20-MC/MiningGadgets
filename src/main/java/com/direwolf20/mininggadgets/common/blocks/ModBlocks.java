package com.direwolf20.mininggadgets.common.blocks;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.Setup;
import com.direwolf20.mininggadgets.client.renderer.RenderBlockTER;
import com.direwolf20.mininggadgets.common.containers.ModificationTableContainer;
import com.direwolf20.mininggadgets.common.tiles.ModificationTableTileEntity;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(bus= EventBusSubscriber.Bus.MOD, modid = Setup.MOD_ID)
@ObjectHolder(Setup.MOD_ID)
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

    //Containers
    @ObjectHolder("mininggadgets:modificationtable")
    public static ContainerType<ModificationTableContainer> MODIFICATIONTABLE_CONTAINER;

    /**
     * Container registry
     */
    @SubscribeEvent
    public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            return new ModificationTableContainer(windowId, MiningGadgets.proxy.getClientWorld(), pos, inv, MiningGadgets.proxy.getClientPlayer());
        }).setRegistryName("modificationtable"));
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
