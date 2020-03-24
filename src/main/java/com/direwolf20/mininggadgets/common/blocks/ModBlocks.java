package com.direwolf20.mininggadgets.common.blocks;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.tiles.ModificationTableTileEntity;
import com.direwolf20.mininggadgets.common.tiles.QuarryBlockTileEntity;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
    /**
     * Deferred Registers for the our Main class to load.
     */
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MiningGadgets.MOD_ID);
    public static final DeferredRegister<TileEntityType<?>> TILES_ENTITIES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, MiningGadgets.MOD_ID);

    /**
     * Register our blocks to the above registers to be loaded when the mod is initialized
     */
    public static final RegistryObject<Block> RENDER_BLOCK = BLOCKS.register("renderblock", RenderBlock::new);
    public static final RegistryObject<Block> MINERS_LIGHT = BLOCKS.register("minerslight", MinersLight::new);
    public static final RegistryObject<Block> MODIFICATION_TABLE = BLOCKS.register("modificationtable", ModificationTable::new);
    public static final RegistryObject<Block> QUARRY = BLOCKS.register("quarry", QuarryBlock::new);
    public static final RegistryObject<Block> MARKER_BLOCK = BLOCKS.register("marker", MarkerBlock::new);

    /**
     * TileEntity Registers to the above deferred registers to be loaded in from the mods main class.
     */
    public static final RegistryObject<TileEntityType<RenderBlockTileEntity>> RENDERBLOCK_TILE =
            TILES_ENTITIES.register("renderblock", () -> TileEntityType.Builder.create(RenderBlockTileEntity::new, ModBlocks.RENDER_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<ModificationTableTileEntity>> MODIFICATIONTABLE_TILE =
            TILES_ENTITIES.register("modificationtable", () -> TileEntityType.Builder.create(ModificationTableTileEntity::new, ModBlocks.MODIFICATION_TABLE.get()).build(null));

    public static final RegistryObject<TileEntityType<QuarryBlockTileEntity>> QUARRY_TILE =
            TILES_ENTITIES.register("quarry", () -> TileEntityType.Builder.create(QuarryBlockTileEntity::new, ModBlocks.QUARRY.get()).build(null));
}
