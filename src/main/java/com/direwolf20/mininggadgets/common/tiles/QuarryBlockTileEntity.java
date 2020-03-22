package com.direwolf20.mininggadgets.common.tiles;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

import static com.direwolf20.mininggadgets.common.blocks.ModBlocks.QUARRY_TILE;

public class QuarryBlockTileEntity extends TileEntity implements ITickableTileEntity {

    public QuarryBlockTileEntity() {
        super(QUARRY_TILE.get());
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        return super.write(tag);
    }

    public boolean isPowered() {
        return world.getStrongPower(this.pos) > 0;
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
          
        }
    }
}
