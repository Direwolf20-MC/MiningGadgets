package com.direwolf20.mininggadgets.common.items.gadget;

import com.direwolf20.mininggadgets.common.blocks.MinersLight;
import com.direwolf20.mininggadgets.common.blocks.RenderBlock;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles collecting the blocks for the mining action.
 */
public class MiningCollect {
    public static List<BlockPos> collect(Player player, BlockHitResult startBlock, Level world, int range) {
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

        coordinates.add(startPos.relative(up).relative(left));
        coordinates.add(startPos.relative(up));
        coordinates.add(startPos.relative(up).relative(right));
        coordinates.add(startPos.relative(left));
        coordinates.add(startPos);
        coordinates.add(startPos.relative(right));
        coordinates.add(startPos.relative(down).relative(left));
        coordinates.add(startPos.relative(down));
        coordinates.add(startPos.relative(down).relative(right));

        return coordinates.stream().filter(e -> isValid(player, e, world)).collect(Collectors.toList());
    }

    private static boolean isValid(Player player, BlockPos pos, Level world) {
        BlockState state = world.getBlockState(pos);

        // Already checked the contained block. And declaring it invalid would prevent the contained block from being broken
        if(state.getBlock() instanceof RenderBlock)
            return true;

        // TODO: 12/07/2020 Reimplement when we find a replacement (1.16 port phase)
        // Reject, if the dimension blocks the player from mining the block
//        if(!world.canMineBlock(player, pos))
//           return false;

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
