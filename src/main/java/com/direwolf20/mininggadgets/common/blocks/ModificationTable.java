package com.direwolf20.mininggadgets.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

import java.util.stream.Stream;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

public class ModificationTable extends Block implements EntityBlock {
    public static DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private static final VoxelShape SHAPE_N = Stream.of(
            Block.box(2, 11, 12, 14, 16, 16),
            Block.box(0, 0, 0, 16, 10, 16),
            Block.box(1, 10, 1, 15, 11, 9),
            Block.box(0, 10, 11, 16, 11, 16),
            Block.box(0, 11, 0, 16, 12, 10),
            Block.box(13, 12, 2, 14, 13, 8)
    ).reduce((v1, v2) -> {return Shapes.join(v1, v2, BooleanOp.OR);}).get();

    private static final VoxelShape SHAPE_E = Stream.of(
            Block.box(0, 11, 2, 4, 16, 14),
            Block.box(0, 0, 0, 16, 10, 16),
            Block.box(7, 10, 1, 15, 11, 15),
            Block.box(0, 10, 0, 5, 11, 16),
            Block.box(6, 11, 0, 16, 12, 16),
            Block.box(8, 12, 13, 14, 13, 14)
    ).reduce((v1, v2) -> {return Shapes.join(v1, v2, BooleanOp.OR);}).get();

    private static final VoxelShape SHAPE_S = Stream.of(
            Block.box(2, 11, 0, 14, 16, 4),
            Block.box(0, 0, 0, 16, 10, 16),
            Block.box(1, 10, 7, 15, 11, 15),
            Block.box(0, 10, 0, 16, 11, 5),
            Block.box(0, 11, 6, 16, 12, 16),
            Block.box(2, 12, 8, 3, 13, 14)
    ).reduce((v1, v2) -> {return Shapes.join(v1, v2, BooleanOp.OR);}).get();

    private static final VoxelShape SHAPE_W = Stream.of(
            Block.box(12, 11, 2, 16, 16, 14),
            Block.box(0, 0, 0, 16, 10, 16),
            Block.box(1, 10, 1, 9, 11, 15),
            Block.box(11, 10, 0, 16, 11, 16),
            Block.box(0, 11, 0, 10, 12, 16),
            Block.box(2, 12, 2, 8, 13, 3)
    ).reduce((v1, v2) -> {return Shapes.join(v1, v2, BooleanOp.OR);}).get();

    public ModificationTable() {
        super(
                Properties.of(Material.METAL).strength(2.0f)
        );

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        switch (state.getValue(FACING)) {
            case NORTH:
                return SHAPE_N;
            case EAST:
                return SHAPE_E;
            case SOUTH:
                return SHAPE_S;
            case WEST:
                return SHAPE_W;
            default:
                throw new IllegalStateException("Invalid State");
        }
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
        return ModBlocks.MODIFICATIONTABLE_TILE.get().create(p_153215_, p_153216_);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (!world.isClientSide) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof MenuProvider) {
                NetworkHooks.openGui((ServerPlayer) player, (MenuProvider) tileEntity, tileEntity.getBlockPos());
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
            BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity != null) {
                LazyOptional<IItemHandler> cap = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
                cap.ifPresent(handler -> {
                    for(int i = 0; i < handler.getSlots(); ++i) {
                        Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
                    }
                });
            }
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }
}
