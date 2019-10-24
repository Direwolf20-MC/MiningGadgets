package com.direwolf20.mininggadgets.common.items;

import com.direwolf20.mininggadgets.Config;
import com.direwolf20.mininggadgets.Setup;
import com.direwolf20.mininggadgets.client.particles.playerparticle.PlayerParticleData;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.blocks.RenderBlock;
import com.direwolf20.mininggadgets.common.capabilities.CapabilityEnergyProvider;
import com.direwolf20.mininggadgets.common.gadget.MiningCollect;
import com.direwolf20.mininggadgets.common.gadget.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.gadget.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import com.direwolf20.mininggadgets.common.util.MiscTools;
import com.direwolf20.mininggadgets.common.util.VectorHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MiningGadget extends Item {
    private int energyCapacity;
    private Random rand = new Random();
    //private static int energyPerItem = 15;

    public MiningGadget() {
        super(new Item.Properties().maxStackSize(1).group(Setup.getItemGroup()));
        setRegistryName("mininggadget");

        this.energyCapacity = Config.MININGGADGET_MAXPOWER.get();
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return this.energyCapacity;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);
        return (energy.getEnergyStored() < energy.getMaxEnergyStored());
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapabilityEnergyProvider(stack, Config.MININGGADGET_MAXPOWER.get());
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);
        if (energy == null)
            return 0;

        return 1D - (energy.getEnergyStored() / (double) energy.getMaxEnergyStored());
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);
        if (energy == null)
            return super.getRGBDurabilityForDisplay(stack);

        return MathHelper.hsvToRGB(Math.max(0.0F, (float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored()) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        List<Upgrade> upgrades = UpgradeTools.getUpgrades(stack);
        if (!(upgrades.isEmpty())) {
            for (Upgrade upgrade : upgrades) {
                tooltip.add(new StringTextComponent(
                        I18n.format(String.format("item.mininggadgets.upgrade_%s", upgrade.getName()))
                ));
            }
        }

        stack.getCapability(CapabilityEnergy.ENERGY, null)
                .ifPresent(energy -> tooltip.add(new TranslationTextComponent("mininggadgets.item.energy", MiscTools.tidyValue(energy.getEnergyStored()), MiscTools.tidyValue(energy.getMaxEnergyStored()))));
    }

    public static void setToolRange(ItemStack tool, int range) {
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

    public static void setLastBreak(ItemStack tool, long lastBreak) {
        CompoundNBT tagCompound = MiscTools.getOrNewTag(tool);
        tagCompound.putLong("lastBreak", lastBreak);
    }

    public static long getLastBreak(ItemStack tool) {
        CompoundNBT tagCompound = MiscTools.getOrNewTag(tool);
        return tagCompound.getLong("lastBreak");
    }

    public static boolean canMine(ItemStack tool, World world) {
        long lastBreak = getLastBreak(tool);
        //if ((world.getGameTime() - lastBreak) < 2) return false;

        IEnergyStorage energy = tool.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);
        int cost = getEnergyCost(tool);
        if (getToolRange(tool) == 3) cost = cost * 9;
        if (energy.getEnergyStored() <= cost)
            return false;


        return true;
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
        if (world.isRemote)
            return new ActionResult<>(ActionResultType.PASS, itemstack);

        // Only perform the shift action
        if (player.isSneaking())
            return this.onItemShiftRightClick(world, player, hand, itemstack);

        if (!canMine(itemstack, world))
            return new ActionResult<>(ActionResultType.FAIL, itemstack);

        player.setActiveHand(hand);
        return new ActionResult<>(ActionResultType.PASS, itemstack);
    }

    public ActionResult<ItemStack> onItemShiftRightClick(World world, PlayerEntity player, Hand hand, ItemStack itemstack) {

        // Debug code for free energy
        itemstack.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> e.receiveEnergy(1500000000, false));
        if (UpgradeTools.containsUpgrade(itemstack, Upgrade.THREE_BY_THREE)) {
            changeRange(itemstack);
            player.sendStatusMessage(new StringTextComponent(TextFormatting.AQUA + new TranslationTextComponent("mininggadgets.mininggadget.range_change", getToolRange(itemstack)).getUnformattedComponentText()), true);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
    }

    public List<BlockPos> findSources(World world, List<BlockPos> coords) {
        List<BlockPos> sources = new ArrayList<>();
        for (BlockPos coord : coords) {
            for (Direction side : Direction.values()) {
                BlockPos sidePos = coord.offset(side);
                IFluidState state = world.getFluidState(sidePos);
                if ((state.getFluid().isEquivalentTo(Fluids.LAVA) || state.getFluid().isEquivalentTo(Fluids.WATER)) && state.getFluid().isSource(state))
                    if (!sources.contains(sidePos))
                        sources.add(sidePos);
            }
        }
        return sources;
    }

    private void spawnFreezeParticle(PlayerEntity player, BlockPos sourcePos, World world) {
        float randomPartSize = 0.05f + (0.125f - 0.05f) * rand.nextFloat();
        double randomTX = rand.nextDouble();
        double randomTY = rand.nextDouble();
        double randomTZ = rand.nextDouble();
        double alpha = -0.5f + (1.0f - 0.5f) * rand.nextDouble(); //rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        Vec3d playerPos = player.getPositionVec().add(0, player.getEyeHeight(), 0);
        Vec3d look = player.getLookVec(); // or getLook(partialTicks)
        BlockRayTraceResult lookAt = VectorHelper.getLookingAt(player, RayTraceContext.FluidMode.NONE);
        Vec3d lookingAt = lookAt.getHitVec();
        //The next 3 variables are directions on the screen relative to the players look direction. So right = to the right of the player, regardless of facing direction.
        Vec3d right = new Vec3d(-look.z, 0, look.x).normalize();
        Vec3d forward = look;
        Vec3d backward = look.mul(-1, 1, -1);
        Vec3d down = right.crossProduct(forward);

        //These are used to calculate where the particles are going. We want them going into the laser, so we move the destination right, down, and forward a bit.
        right = right.scale(0.65f);
        forward = forward.scale(0.85f);
        down = down.scale(-0.35);
        backward = backward.scale(0.05);

        //Take the player's eye position, and shift it to where the end of the laser is (Roughly)
        Vec3d laserPos = playerPos.add(right);
        laserPos = laserPos.add(forward);
        laserPos = laserPos.add(down);
        lookingAt = lookingAt.add(backward);
        PlayerParticleData data = PlayerParticleData.playerparticle("ice", sourcePos.getX() + randomTX, sourcePos.getY() + randomTY, sourcePos.getZ() + randomTZ, randomPartSize, 1f, 1f, 1f, 120, true);
        //Change the below laserPos to lookingAt to have it emit from the laser gun itself
        world.addParticle(data, laserPos.x, laserPos.y, laserPos.z, 0.025, 0.025f, 0.025);
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        //Server and Client side
        World world = player.world;
        BlockRayTraceResult lookingAt = VectorHelper.getLookingAt((PlayerEntity) player, RayTraceContext.FluidMode.NONE);
        if (lookingAt == null || (world.getBlockState(VectorHelper.getLookingAt((PlayerEntity) player, stack).getPos()) == Blocks.AIR.getDefaultState()))
            return;
        List<BlockPos> coords = MiningCollect.collect((PlayerEntity) player, lookingAt, world, getToolRange(stack));

        if (UpgradeTools.containsUpgrade(stack, Upgrade.FREEZING)) {
            for (BlockPos sourcePos : findSources(player.world, coords)) {
                if (player instanceof PlayerEntity)
                    spawnFreezeParticle((PlayerEntity) player, sourcePos, player.world);
            }
        }
        //Server Side
        if (!world.isRemote) {
            // As all upgrade types with tiers contain the same name, we can check for a single
            // type in the enum and produce a result that we can then pull the tier from
            int efficiency = 0;
            if (UpgradeTools.getUpgradeFromGadget((stack), Upgrade.EFFICIENCY_1).isPresent())
                efficiency = UpgradeTools.getUpgradeFromGadget((stack), Upgrade.EFFICIENCY_1).get().getTier();

            float hardness = getHardness(coords, (PlayerEntity) player, efficiency);
            hardness = hardness * getToolRange(stack) * 1;
            hardness = (float) Math.floor(hardness);
            if (hardness == 0) hardness = 1;
            for (BlockPos coord : coords) {
                BlockState state = world.getBlockState(coord);
                if (!(state.getBlock() instanceof RenderBlock)) {
                    //if (!world.isRemote) {
                    if (!canMine(stack, world)) {
                        return;
                    }
                    List<Upgrade> gadgetUpgrades = UpgradeTools.getUpgrades(stack);
                    world.setBlockState(coord, ModBlocks.RENDERBLOCK.getDefaultState());
                    RenderBlockTileEntity te = (RenderBlockTileEntity) world.getTileEntity(coord);
                    te.setRenderBlock(state);
                    te.setGadgetUpgrades(gadgetUpgrades);
                    te.setPriorDurability((int) hardness + 1);
                    te.setOriginalDurability((int) hardness + 1);
                    te.setDurability((int) hardness, stack);
                    te.setPlayer((PlayerEntity) player);

                    //}
                } else {
                    //if (!world.isRemote) {
                    RenderBlockTileEntity te = (RenderBlockTileEntity) world.getTileEntity(coord);
                    int durability = te.getDurability();
                    //System.out.println(durability);
                /*if (player.getHeldItemMainhand().getItem() instanceof MiningGadget && player.getHeldItemOffhand().getItem() instanceof MiningGadget)
                    durability = durability - 2;
                else*/
                    durability = durability - 1;
                    if (durability <= 0) {
                        setLastBreak(stack, world.getGameTime());
                        player.resetActiveHand();
                        stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> e.receiveEnergy(getEnergyCost(stack) * -1, false));
                    }
                    te.setDurability(durability, stack);
                    //}
                }
            }
            if (!(UpgradeTools.containsUpgrade(stack, Upgrade.LIGHT_PLACER)))
                return;

            Direction side = lookingAt.getFace();
            boolean vertical = side.getAxis().isVertical();
            Direction up = vertical ? player.getHorizontalFacing() : Direction.UP;
            Direction right = vertical ? up.rotateY() : side.rotateYCCW();

            BlockPos pos;
            if (getToolRange(stack) == 1)
                pos = lookingAt.getPos().offset(side, 4);
            else
                pos = lookingAt.getPos().offset(side).offset(right);

            if (world.getLight(pos) <= 7 && world.getBlockState(pos).getMaterial() == Material.AIR) {
                world.setBlockState(pos, ModBlocks.MINERSLIGHT.getDefaultState());
                stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> e.receiveEnergy(-100, false));
            }

        }
    }

    public static int getEnergyCost(ItemStack stack) {
        int cost = Config.MININGGADGET_BASECOST.get();
        List<Upgrade> upgrades = UpgradeTools.getUpgrades(stack);
        if (upgrades.isEmpty())
            return cost;

        return cost + upgrades.stream().mapToInt(Upgrade::getCostPerBlock).sum();
    }

    private static float getHardness(List<BlockPos> coords, PlayerEntity player, int efficiency) {
        float hardness = 0;
        float toolSpeed = 8;
        if (efficiency > 0) {
            toolSpeed = toolSpeed + ((efficiency * efficiency + 1));
        }
        EffectInstance hasteEffect = player.getActivePotionEffect(Effects.HASTE);
        if (hasteEffect != null) {
            int hasteLevel = hasteEffect.getAmplifier() + 1;
            toolSpeed = toolSpeed + (toolSpeed * ((hasteLevel * 20f) / 100));
        }
        World world = player.getEntityWorld();
        for (BlockPos coord : coords) {
            BlockState state = world.getBlockState(coord);
            float temphardness = state.getBlockHardness(world, coord);
            //if (state.getMaterial() == Material.EARTH) temphardness = temphardness * 4;
            hardness += (temphardness * 30) / toolSpeed;
        }
        return ((hardness / coords.size()));
    }

    public static void applyUpgrade(ItemStack tool, UpgradeCard upgradeCard) {
        if (UpgradeTools.containsUpgrade(tool, upgradeCard.getUpgrade()))
            return;

        UpgradeTools.setUpgrade(tool, upgradeCard);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity) {
            entityLiving.resetActiveHand();
        }

        /*if (!(worldIn.isRemote)) {
            BlockRayTraceResult lookingAt = VectorHelper.getLookingAt((PlayerEntity) entityLiving, RayTraceContext.FluidMode.NONE);
            if (lookingAt == null || (worldIn.getBlockState(VectorHelper.getLookingAt((PlayerEntity) entityLiving, stack).getPos()) == Blocks.AIR.getDefaultState()))
                return;

            List<BlockPos> coords = getMinableBlocks(stack, lookingAt, (PlayerEntity) entityLiving);
            for (BlockPos coord : coords) {
                TileEntity te = worldIn.getTileEntity(coord);
                if (te instanceof RenderBlockTileEntity)
                    ((RenderBlockTileEntity) te).markDirtyClient();
            }
        }*/
    }
}
