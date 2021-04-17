package com.direwolf20.mininggadgets.common.blocks;

import com.direwolf20.mininggadgets.common.tiles.ModificationTableTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock.Properties;

import java.util.stream.Stream;

public class ModificationTable extends Block {
    public static DirectionProperty FACING = HorizontalBlock.FACING;

    private static final VoxelShape SHAPE_N = Stream.of(
            Block.box(2, 11, 12, 14, 16, 16),
            Block.box(0, 0, 0, 16, 10, 16),
            Block.box(1, 10, 1, 15, 11, 9),
            Block.box(0, 10, 11, 16, 11, 16),
            Block.box(0, 11, 0, 16, 12, 10),
            Block.box(13, 12, 2, 14, 13, 8)
    ).reduce((v1, v2) -> {return VoxelShapes.join(v1, v2, IBooleanFunction.OR);}).get();

    private static final VoxelShape SHAPE_E = Stream.of(
            Block.box(0, 11, 2, 4, 16, 14),
            Block.box(0, 0, 0, 16, 10, 16),
            Block.box(7, 10, 1, 15, 11, 15),
            Block.box(0, 10, 0, 5, 11, 16),
            Block.box(6, 11, 0, 16, 12, 16),
            Block.box(8, 12, 13, 14, 13, 14)
    ).reduce((v1, v2) -> {return VoxelShapes.join(v1, v2, IBooleanFunction.OR);}).get();

    private static final VoxelShape SHAPE_S = Stream.of(
            Block.box(2, 11, 0, 14, 16, 4),
            Block.box(0, 0, 0, 16, 10, 16),
            Block.box(1, 10, 7, 15, 11, 15),
            Block.box(0, 10, 0, 16, 11, 5),
            Block.box(0, 11, 6, 16, 12, 16),
            Block.box(2, 12, 8, 3, 13, 14)
    ).reduce((v1, v2) -> {return VoxelShapes.join(v1, v2, IBooleanFunction.OR);}).get();

    private static final VoxelShape SHAPE_W = Stream.of(
            Block.box(12, 11, 2, 16, 16, 14),
            Block.box(0, 0, 0, 16, 10, 16),
            Block.box(1, 10, 1, 9, 11, 15),
            Block.box(11, 10, 0, 16, 11, 16),
            Block.box(0, 11, 0, 10, 12, 16),
            Block.box(2, 12, 2, 8, 13, 3)
    ).reduce((v1, v2) -> {return VoxelShapes.join(v1, v2, IBooleanFunction.OR);}).get();

    public ModificationTable() {
        super(
                Properties.of(Material.METAL).strength(2.0f).harvestTool(ToolType.PICKAXE)
        );

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
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

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ModificationTableTileEntity();
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        if (!world.isClientSide) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof INamedContainerProvider) {
                NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getBlockPos());
            } else {
                throw new IllegalStateException("Our named container provider is missing!");
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.getBlock() != this) {
            TileEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity != null) {
                LazyOptional<IItemHandler> cap = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
                cap.ifPresent(handler -> {
                    for(int i = 0; i < handler.getSlots(); ++i) {
                        InventoryHelper.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
                    }
                });
            }
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }
}
