package com.direwolf20.mininggadgets.common.items.gadget;

import com.direwolf20.mininggadgets.common.blocks.MinersLight;
import com.direwolf20.mininggadgets.common.blocks.RenderBlock;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.Dimension;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles collecting the blocks for the mining action.
 *
 * @implNote Currently done using static reference but having it as a dynamic class
 * may work in the future as more methods and functionality is added.
 *
 * @since 1.0.5
 */
public class MiningCollect {
    public static List<BlockPos> collect(PlayerEntity player, BlockRayTraceResult startBlock, World world, int range) {
        List<BlockPos> coordinates = new ArrayList<>();
        BlockPos startPos = startBlock.getPos();

        if (range == 1) {
            if( !isValid(player, startBlock.getPos(), world) )
                return coordinates;

            coordinates.add(startBlock.getPos());
            return coordinates;
        }

        Direction side = startBlock.getFace();
        boolean vertical = side.getAxis().isVertical();
        Direction up = vertical ? player.getHorizontalFacing() : Direction.UP;
        Direction down = up.getOpposite();
        Direction right = vertical ? up.rotateY() : side.rotateYCCW();
        Direction left = right.getOpposite();

        coordinates.add(startPos.offset(up).offset(left));
        coordinates.add(startPos.offset(up));
        coordinates.add(startPos.offset(up).offset(right));
        coordinates.add(startPos.offset(left));
        coordinates.add(startPos);
        coordinates.add(startPos.offset(right));
        coordinates.add(startPos.offset(down).offset(left));
        coordinates.add(startPos.offset(down));
        coordinates.add(startPos.offset(down).offset(right));

        return coordinates.stream().filter(e -> isValid(player, e, world)).collect(Collectors.toList());
    }

    private static boolean isValid(PlayerEntity player, BlockPos pos, World world) {
        BlockState state = world.getBlockState(pos);

        // Already checked the contained block. And declaring it invalid would prevent the contained block from being broken
        if(state.getBlock() instanceof RenderBlock)
            return true;

        // TODO: 12/07/2020 Reimplement when we find a replacement (1.16 port phase)
        // Reject, if the dimension blocks the player from mining the block
//        if(!world.canMineBlock(player, pos))
//           return false;

        // Reject fluids and air (supports waterlogged blocks too)
        if ((!state.getFluidState().isEmpty() && !state.hasProperty(BlockStateProperties.WATERLOGGED)) || world.isAirBlock(pos))
            return false;

        // Rejects any blocks with a hardness less than 0
        if (state.getBlockHardness(world, pos) < 0)
            return false;

        // No TE's
        TileEntity te = world.getTileEntity(pos);
        if (te != null && !(te instanceof RenderBlockTileEntity))
            return false;

        // Ignore Doors because they are special snowflakes
        if (state.getBlock() instanceof DoorBlock)
            return false;

        //Ignore our Miners Light Block
        return !(state.getBlock() instanceof MinersLight);
    }
}
