package com.direwolf20.mininggadgets.common.blocks;

import com.direwolf20.mininggadgets.common.tiles.QuarryBlockTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class QuarryBlock extends Block {
    public QuarryBlock() {
        super(
                Properties.create(Material.IRON).hardnessAndResistance(2.0f)
        );
    }
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new QuarryBlockTileEntity();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);

        TileEntity tile = worldIn.getTileEntity(pos);
        if( tile == null )
            return;

        QuarryBlockTileEntity te = (QuarryBlockTileEntity) tile;
        te.scanAdjacentStorage();
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if( tile == null || player.getHeldItem(handIn) != ItemStack.EMPTY )
            return ActionResultType.FAIL;

        QuarryBlockTileEntity te = (QuarryBlockTileEntity) tile;
        if( !te.hasValidArea() ) {
            // todo: lang file
            player.sendMessage(new StringTextComponent("No valid area defined, use markers to define this blocks start and end locations"));
            return ActionResultType.FAIL;
        }

        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }
}
