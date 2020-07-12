package com.direwolf20.mininggadgets.common.blocks;

import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

//import net.minecraft.util.BlockRenderLayer;

public class RenderBlock extends Block {
    public RenderBlock() {
        super(
                Properties.create(Material.IRON)
                        .hardnessAndResistance(2.0f)
                        .notSolid()
                        .noDrops()
                        .setOpaque((a, b, c) -> false) // @mcp: setOpaque seems to replace isNormalBlock
        );
    }


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new RenderBlockTileEntity();
    }

    /**
     * @param state blockState
     * @return Render Type
     * @deprecated call via {@link BlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
     */
    @Override
    @SuppressWarnings("deprecation")
    public BlockRenderType getRenderType(BlockState state) {
        // We still make effect blocks invisible because all effects (scaling block, transparent box) are dynamic so they has to be in the TER
        return BlockRenderType.INVISIBLE;
    }

    /**
     * @deprecated call via {@link BlockState#getPushReaction()} whenever possible. Implementing/overriding is fine.
     */
    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public int getOpacity(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 1.0f;
    }

    @Override
    public boolean isSideInvisible(BlockState p_200122_1_, BlockState p_200122_2_, Direction p_200122_3_) {
        return true;
    }
}
