package com.direwolf20.mininggadgets.common.items;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.blocks.RenderBlock;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import com.direwolf20.mininggadgets.common.util.VectorHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;

public class MiningGadget extends Item {
    public MiningGadget() {
        super(new Item.Properties().maxStackSize(1).group(MiningGadgets.setup.itemGroup));
        setRegistryName("mininggadget");
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
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
        player.setActiveHand(hand);
        System.out.println("Using Item Start");
        return new ActionResult<>(ActionResultType.PASS, itemstack);
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        World world = player.world;
        //public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (world.isRemote) {
            return;
        }
        BlockRayTraceResult lookingAt = VectorHelper.getLookingAt((PlayerEntity) player, RayTraceContext.FluidMode.NONE);
        if (lookingAt == null || (world.getBlockState(VectorHelper.getLookingAt((PlayerEntity) player, stack).getPos()) == Blocks.AIR.getDefaultState()))
            return;
        BlockPos pos = lookingAt.getPos();
        BlockState state = world.getBlockState(pos);

        if (!(state.getBlock() instanceof RenderBlock)) {
            world.setBlockState(lookingAt.getPos(), ModBlocks.RENDERBLOCK.getDefaultState());
            RenderBlockTileEntity te = (RenderBlockTileEntity) world.getTileEntity(pos);
            te.setRenderBlock(state);
            te.setDurability(40);
            te.setOriginalDurability(40);
        } else {
            RenderBlockTileEntity te = (RenderBlockTileEntity) world.getTileEntity(pos);
            te.setDurability(te.getDurability() - 1);
        }

        return;
    }

    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        System.out.println("Stopped Using");
    }
}
