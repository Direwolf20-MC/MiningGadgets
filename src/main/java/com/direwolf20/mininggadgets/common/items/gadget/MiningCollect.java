package com.direwolf20.mininggadgets.common.items.gadget;

import com.direwolf20.mininggadgets.common.blocks.MinersLight;
import com.direwolf20.mininggadgets.common.blocks.RenderBlock;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles collecting the blocks for the mining action.
 */
public class MiningCollect {
    public static List<BlockPos> collect(Player player, BlockHitResult startBlock, Level world, int range, MiningProperties.SizeMode sizeMode) {
        List<BlockPos> coordinates = new ArrayList<>();
        BlockPos startPos = startBlock.getBlockPos();

        if (range == 1) {
            if( !isValid(player, startBlock.getBlockPos(), world) )
                return coordinates;

            coordinates.add(startBlock.getBlockPos());
            return coordinates;
        }

        Direction side = startBlock.getDirection();
        boolean vertical = side.getAxis().isVertical();
        Direction up = vertical ? player.getDirection() : Direction.UP;
        Direction down = up.getOpposite();
        Direction right = vertical ? up.getClockWise() : side.getCounterClockWise();
        Direction left = right.getOpposite();

        int midRange = ((range - 1) / 2);
        int upRange = midRange;
        int downRange = midRange;

        if (!vertical && range > 3) {

            if (sizeMode == MiningProperties.SizeMode.AUTO) {
                double myYPos = player.position().get(Direction.UP.getAxis());
                double hitBlockPos = startBlock.getBlockPos().get(Direction.UP.getAxis());

                if (Math.abs(myYPos - hitBlockPos) < 2) {
                    downRange = 1;
                    upRange = range - 2;
                }
            } else if (sizeMode == MiningProperties.SizeMode.PATHWAY) {
                downRange = 1;
                upRange = range - 2;
            }
        }

        BlockPos topLeft = startPos.relative(up, upRange).relative(left, midRange);
        BlockPos bottomRight = startPos.relative(down, downRange).relative(right, midRange);

        return BlockPos
                .betweenClosedStream(topLeft, bottomRight)
                .map(BlockPos::immutable)
                .filter(e -> isValid(player, e, world))
                .collect(Collectors.toList());
    }

    private static boolean isValid(Player player, BlockPos pos, Level world) {
        BlockState state = world.getBlockState(pos);

        // Already checked the contained block. And declaring it invalid would prevent the contained block from being broken
        if(state.getBlock() instanceof RenderBlock)
            return true;

        // Reject fluids and air (supports waterlogged blocks too)
        if ((!state.getFluidState().isEmpty() && !state.hasProperty(BlockStateProperties.WATERLOGGED)) || world.isEmptyBlock(pos))
            return false;

        // Rejects any blocks with a hardness less than 0
        if (state.getDestroySpeed(world, pos) < 0)
            return false;

        // No TE's
        BlockEntity te = world.getBlockEntity(pos);
        if (te != null && !(te instanceof RenderBlockTileEntity))
            return false;

        // Ignore Doors because they are special snowflakes
        if (state.getBlock() instanceof DoorBlock)
            return false;

        //Ignore our Miners Light Block
        return !(state.getBlock() instanceof MinersLight);
    }
}
