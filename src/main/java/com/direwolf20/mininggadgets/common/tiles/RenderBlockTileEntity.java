package com.direwolf20.mininggadgets.common.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

import static com.direwolf20.mininggadgets.common.blocks.ModBlocks.RENDERBLOCK_TILE;

public class RenderBlockTileEntity extends TileEntity implements ITickableTileEntity {
    /**
     * Even though this is called "rendered", is will be used for replacement under normal conditions.
     */
    private BlockState renderBlock;
    private int priorDurability = 9999;
    private int durability;

    private int originalDurability;
    private int ticks = 0;

    public RenderBlockTileEntity() {
        super(RENDERBLOCK_TILE);
    }

    public void setRenderBlock(BlockState state) {
        renderBlock = state;
    }

    public BlockState getRenderBlock() {
        return renderBlock;
    }

    public void setDurability(int dur) {
        durability = dur;
    }

    public int getDurability() {
        return durability;
    }

    public int getOriginalDurability() {
        return originalDurability;
    }

    public void setOriginalDurability(int originalDurability) {
        this.originalDurability = originalDurability;
    }


    @Override
    public void tick() {
        if (world.isRemote) {
            return;
        }
        if (priorDurability == 9999) {
            priorDurability = durability;
        }
        //if (ticks == 20) {
        System.out.println(durability);
        if (priorDurability == durability) {
            if (durability > originalDurability) {
                world.setBlockState(this.pos, renderBlock);
            } else {
                durability++;
                priorDurability = durability;
            }
        } else {
            priorDurability = durability;
        }
        ticks = 0;
        //}
        //else ticks++;
        if (durability <= 0) {
            world.setBlockState(this.pos, Blocks.AIR.getDefaultState());
        }
    }
}
