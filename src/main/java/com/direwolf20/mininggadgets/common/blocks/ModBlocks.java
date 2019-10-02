package com.direwolf20.mininggadgets.common.blocks;

import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlocks {
    @ObjectHolder("mininggadgets:renderblock")
    public static RenderBlock RENDERBLOCK;

    @ObjectHolder("mininggadgets:renderblock")
    public static TileEntityType<RenderBlockTileEntity> RENDERBLOCK_TILE;
}
