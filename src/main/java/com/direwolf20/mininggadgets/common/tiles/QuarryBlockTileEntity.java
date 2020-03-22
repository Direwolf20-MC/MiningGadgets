package com.direwolf20.mininggadgets.common.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

import static com.direwolf20.mininggadgets.common.blocks.ModBlocks.QUARRY_TILE;

public class QuarryBlockTileEntity extends TileEntity implements ITickableTileEntity {

    public ArrayList<BlockPos> adjacentStorage;
    boolean needScanAdjacent;

    public QuarryBlockTileEntity() {
        super(QUARRY_TILE.get());
        needScanAdjacent = true;
    }

    public void addToAdjacentStorage(BlockPos pos) {
        adjacentStorage.add(pos);
    }

    public void removeFromAdjacentStorage(BlockPos pos) {
        adjacentStorage.remove(pos);
    }

    public void scanAdjacentStorage() {
        adjacentStorage = new ArrayList<BlockPos>();
        for (Direction direction : Direction.values()) {
            BlockPos adjacentPos = this.pos.offset(direction);
            BlockState adjacentState = world.getBlockState(adjacentPos);
            if (adjacentState.getBlock().equals(Blocks.CHEST)) { //TODO Look for any inventory
                adjacentStorage.add(this.pos.offset(direction));
            }
        }
        needScanAdjacent = false;
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
        if (needScanAdjacent) scanAdjacentStorage();
        if (!world.isRemote) {
            System.out.println(adjacentStorage);
        }
    }
}
