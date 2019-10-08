package com.direwolf20.mininggadgets.common.blocks;

import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.storage.loot.LootContext;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class RenderBlock extends Block {
    public RenderBlock() {
        super(Properties.create(Material.IRON)
                .hardnessAndResistance(2.0f)
        );
        setRegistryName("renderblock");
    }

    /*@Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof RenderBlockTileEntity) {
                //((RenderBlockTileEntity) tileEntity).durability++;
            }
        }
        return true;
    }*/

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
     * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
     * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
     */
    @Override
    public BlockRenderLayer getRenderLayer() {
        // Since the effect block has no model rendering at all, which means we don't need blending, simply cutout is fine
        return BlockRenderLayer.CUTOUT;
    }

    /**
     * This gets a complete list of items dropped from this block.
     *
     * @param state Current state
     */
    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder loot) {
        return new ArrayList<>();
    }

    /**
     * @deprecated call via {@link BlockState#getPushReaction()} whenever possible. Implementing/overriding is fine.
     */
    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }
}
