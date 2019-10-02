package com.direwolf20.mininggadgets.common.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

import static com.direwolf20.mininggadgets.common.blocks.ModBlocks.RENDERBLOCK_TILE;

public class RenderBlockTileEntity extends TileEntity implements ITickableTileEntity {
    /**
     * Even though this is called "rendered", is will be used for replacement under normal conditions.
     */
    private BlockState renderedBlock;
    public int durability;
    private int ticks = 0;

    public RenderBlockTileEntity() {
        super(RENDERBLOCK_TILE);
    }

    @Override
    public void tick() {
        if (world.isRemote) {
            return;
        }
        if (ticks == 20) {
            System.out.println(durability);
            ticks = 0;
        }
        else ticks++;
    }
}
