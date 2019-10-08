package com.direwolf20.mininggadgets.common.items;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.blocks.RenderBlock;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import com.direwolf20.mininggadgets.common.util.MiscTools;
import com.direwolf20.mininggadgets.common.util.VectorHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MiningGadget extends Item {
    public MiningGadget() {
        super(new Item.Properties().maxStackSize(1).group(MiningGadgets.setup.itemGroup));
        setRegistryName("mininggadget");
    }

    private static void setToolRange(ItemStack tool, int range) {
        CompoundNBT tagCompound = MiscTools.getOrNewTag(tool);
        tagCompound.putInt("range", range);
    }

    public static int getToolRange(ItemStack tool) {
        CompoundNBT tagCompound = MiscTools.getOrNewTag(tool);
        int range = tagCompound.getInt("range");
        if (range == 0) {
            setToolRange(tool, 1);
            return 1;
        }
        return tagCompound.getInt("range");
    }

    public static void changeRange(ItemStack tool) {
        if (getToolRange(tool) == 1)
            setToolRange(tool, 3);
        else
            setToolRange(tool, 1);

    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            changeRange(itemstack);
            //player.sendStatusMessage(new StringTextComponent(TextFormatting.AQUA + new TranslationTextComponent(prefix, new TranslationTextComponent(prefix + (shouldPlaceAtop(stack) ? ".atop" : ".inside"))).getUnformattedComponentText()), true);
            player.sendStatusMessage(new StringTextComponent(TextFormatting.AQUA + "Range Change: " + getToolRange(itemstack) + "x" + getToolRange(itemstack)), true);
            return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
        } else {
            player.setActiveHand(hand);
            return new ActionResult<>(ActionResultType.PASS, itemstack);
        }

    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        World world = player.world;
        BlockRayTraceResult lookingAt = VectorHelper.getLookingAt((PlayerEntity) player, RayTraceContext.FluidMode.NONE);
        if (lookingAt == null || (world.getBlockState(VectorHelper.getLookingAt((PlayerEntity) player, stack).getPos()) == Blocks.AIR.getDefaultState()))
            return;
        BlockPos pos = lookingAt.getPos();
        List<BlockPos> coords = getMinableBlocks(stack, lookingAt, (PlayerEntity) player);
        float hardness = getHardness(coords, (PlayerEntity) player);
        hardness = hardness * getToolRange(stack);
        for (BlockPos coord : coords) {
            BlockState state = world.getBlockState(coord);
            if (!(state.getBlock() instanceof RenderBlock)) {
                if (!world.isRemote) {
                    world.setBlockState(coord, ModBlocks.RENDERBLOCK.getDefaultState());
                    RenderBlockTileEntity te = (RenderBlockTileEntity) world.getTileEntity(coord);
                    te.setRenderBlock(state);
                    te.setDurability((int) hardness - 1);
                    te.setOriginalDurability((int) hardness);
                    te.setPlayer((PlayerEntity) player);
                }
            } else {
                //if (!world.isRemote) {
                RenderBlockTileEntity te = (RenderBlockTileEntity) world.getTileEntity(coord);
                if (player.getHeldItemMainhand().getItem() instanceof MiningGadget && player.getHeldItemOffhand().getItem() instanceof MiningGadget)
                    te.setDurability(te.getDurability() - 2);
                else
                    te.setDurability(te.getDurability() - 1);
                //}
            }
        }

        return;
    }

    private static float getHardness(List<BlockPos> coords, PlayerEntity player) {
        float hardness = 0;
        World world = player.getEntityWorld();
        for (BlockPos coord : coords) {
            BlockState state = world.getBlockState(coord);
            float temphardness = state.getBlockHardness(world, coord);
            if (state.getMaterial() == Material.EARTH) temphardness = temphardness * 4;
            hardness += temphardness;
        }
        return ((hardness / coords.size()) * 3);
    }

    public static List<BlockPos> getMinableBlocks(ItemStack stack, BlockRayTraceResult lookingAt, PlayerEntity player) {
        List<BlockPos> coordinates = new ArrayList<BlockPos>();

        if (getToolRange(stack) == 1) {
            coordinates.add(lookingAt.getPos());
            return coordinates;
        }

        Direction side = lookingAt.getFace();
        boolean vertical = side.getAxis().isVertical();
        Direction up = vertical ? player.getHorizontalFacing() : Direction.UP;
        Direction down = up.getOpposite();
        Direction right = vertical ? up.rotateY() : side.rotateYCCW();
        Direction left = right.getOpposite();
        World world = player.world;

        addCoord(coordinates, lookingAt.getPos().offset(up).offset(left), world);
        addCoord(coordinates, lookingAt.getPos().offset(up), world);
        addCoord(coordinates, lookingAt.getPos().offset(up).offset(right), world);
        addCoord(coordinates, lookingAt.getPos().offset(left), world);
        addCoord(coordinates, lookingAt.getPos(), world);
        addCoord(coordinates, lookingAt.getPos().offset(right), world);
        addCoord(coordinates, lookingAt.getPos().offset(down).offset(left), world);
        addCoord(coordinates, lookingAt.getPos().offset(down), world);
        addCoord(coordinates, lookingAt.getPos().offset(down).offset(right), world);

        return coordinates;
    }

    private static void addCoord(List<BlockPos> coordinates, BlockPos coord, World world) {
        BlockState state = world.getBlockState(coord);
        if (state.getMaterial() != Material.AIR) {
            coordinates.add(coord);
        }
    }

    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {

    }
}
