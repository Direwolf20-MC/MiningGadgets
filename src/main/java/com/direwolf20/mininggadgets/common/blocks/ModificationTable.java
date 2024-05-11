package com.direwolf20.mininggadgets.common.blocks;

import com.direwolf20.mininggadgets.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class ModificationTable extends Block implements EntityBlock {
    public static DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public ModificationTable() {
        super(Properties.of().strength(2.0f));

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Shape.getFromFacing(state.getValue(FACING));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return Registration.MODIFICATIONTABLE_TILE.get().create(p_153215_, p_153216_);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity tileEntity = level.getBlockEntity(blockPos);
            if (tileEntity instanceof MenuProvider) {
                player.openMenu((MenuProvider) tileEntity, tileEntity.getBlockPos());
            } else {
                throw new IllegalStateException("Our named container provider is missing!");
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.getBlock() != this) {
            BlockEntity tile = worldIn.getBlockEntity(pos);
            if (tile != null) {
                var cap = tile.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, tile.getBlockPos(), tile.getBlockState(), tile, null);
                if (cap == null) return;
                for (int i = 0; i < cap.getSlots(); ++i) {
                    Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), cap.getStackInSlot(i));
                }
                super.onRemove(state, worldIn, pos, newState, isMoving);
            }
        }
    }

    private enum Shape {
        NORTH(Stream.of(Block.box(2, 11, 12, 14, 16, 16), Block.box(0, 0, 0, 16, 10, 16), Block.box(1, 10, 1, 15, 11, 9), Block.box(0, 10, 11, 16, 11, 16), Block.box(0, 11, 0, 16, 12, 10), Block.box(13, 12, 2, 14, 13, 8)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()),
        EAST(Stream.of(Block.box(0, 11, 2, 4, 16, 14), Block.box(0, 0, 0, 16, 10, 16), Block.box(7, 10, 1, 15, 11, 15), Block.box(0, 10, 0, 5, 11, 16), Block.box(6, 11, 0, 16, 12, 16), Block.box(8, 12, 13, 14, 13, 14)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()),
        SOUTH(Stream.of(Block.box(2, 11, 0, 14, 16, 4), Block.box(0, 0, 0, 16, 10, 16), Block.box(1, 10, 7, 15, 11, 15), Block.box(0, 10, 0, 16, 11, 5), Block.box(0, 11, 6, 16, 12, 16), Block.box(2, 12, 8, 3, 13, 14)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()),
        WEST(Stream.of(Block.box(12, 11, 2, 16, 16, 14), Block.box(0, 0, 0, 16, 10, 16), Block.box(1, 10, 1, 9, 11, 15), Block.box(11, 10, 0, 16, 11, 16), Block.box(0, 11, 0, 10, 12, 16), Block.box(2, 12, 2, 8, 13, 3)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get());

        private final VoxelShape shape;

        Shape(VoxelShape shape) {
            this.shape = shape;
        }

        public static VoxelShape getFromFacing(Direction facing) {
            return switch (facing) {
                case NORTH -> NORTH.shape;
                case EAST -> EAST.shape;
                case SOUTH -> SOUTH.shape;
                case WEST -> WEST.shape;
                default -> throw new IllegalStateException("Invalid facing");
            };
        }
    }
}
