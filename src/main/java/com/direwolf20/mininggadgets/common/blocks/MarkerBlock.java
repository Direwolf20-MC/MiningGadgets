package com.direwolf20.mininggadgets.common.blocks;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.tiles.QuarryBlockTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class MarkerBlock extends Block {
    public MarkerBlock() {
        super(Properties.create(Material.MISCELLANEOUS).harvestTool(ToolType.PICKAXE).hardnessAndResistance(.5f));
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        System.out.println("Create Tile");
        return null;
    }

    @Override
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
        System.out.println("Destroyed");
        super.onPlayerDestroy(worldIn, pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        System.out.println("Added");
        super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
    }

    public static class MarkerBlockItem extends BlockItem {
        public static final String LINK_KEY = "link";

        public MarkerBlockItem() {
            super(ModBlocks.MARKER_BLOCK.get(), new Properties().group(MiningGadgets.itemGroup).setNoRepair());
        }

        @Override
        public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
            ItemStack item = playerIn.getHeldItem(handIn);
            if( playerIn.isShiftKeyDown() && item.getItem() instanceof MarkerBlockItem ) {
                CompoundNBT nbt = item.getOrCreateTag();
                if( !nbt.contains(LINK_KEY) )
                    return ActionResult.resultPass(item);

                // Remove the link
                nbt.remove(LINK_KEY);
                return ActionResult.resultPass(item);
            }

            return super.onItemRightClick(worldIn, playerIn, handIn);
        }

        @Override
        public ActionResultType onItemUse(ItemUseContext context) {
            // Do we have a block pos linked?
            CompoundNBT nbt = context.getItem().getOrCreateTag();
            boolean hasLink = nbt.contains(LINK_KEY);

            // Create a link!
            BlockState state = context.getWorld().getBlockState(context.getPos());
            if( !hasLink ) {
                if( !(state.getBlock() instanceof QuarryBlock) ) {
                    TileEntity tile = context.getWorld().getTileEntity(context.getPos());
                    if( tile != null ) {
                        QuarryBlockTileEntity te = (QuarryBlockTileEntity) tile;
                        if( te.hasValidArea() )
                            context.getPlayer().sendMessage(new StringTextComponent("This quarry has already been setup, remove it's markers before trying to set them back up"));
                    }
                    else
                        context.getPlayer().sendMessage(new StringTextComponent("The item isn't linked! You must first right click on the quarry to define a link"));

                    return ActionResultType.FAIL;
                }

                nbt.put(LINK_KEY, NBTUtil.writeBlockPos(context.getPos()));
                System.out.println("Start pos set at: "+ context.getPos());
                return ActionResultType.FAIL;
            }

            // Now we have a start we can define and end by notifying the linked tile entity of the second pos
            BlockPos pos = NBTUtil.readBlockPos(nbt.getCompound(LINK_KEY));
            TileEntity tile = context.getWorld().getTileEntity(pos);
            if(tile == null) {
                context.getPlayer().sendMessage(new StringTextComponent("The item isn't linked! Someone must have broken the quarry"));
                return ActionResultType.FAIL;
            }

            // Check if it failed to place before setting up the TE
            ActionResultType type = super.onItemUse(context);
            if( !type.isSuccess() )
                return type;

            QuarryBlockTileEntity te = (QuarryBlockTileEntity) tile;
            if( te.getStartPos() == BlockPos.ZERO )
                te.setStartPos(context.getPos());
            else
                te.setEndPos(context.getPos());

            return super.onItemUse(context);
        }
    }
}
