package com.direwolf20.mininggadgets.common.items;

import com.direwolf20.mininggadgets.Config;
import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.client.particles.playerparticle.PlayerParticleData;
import com.direwolf20.mininggadgets.client.screens.ModScreens;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.blocks.RenderBlock;
import com.direwolf20.mininggadgets.common.capabilities.CapabilityEnergyProvider;
import com.direwolf20.mininggadgets.common.gadget.MiningCollect;
import com.direwolf20.mininggadgets.common.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.gadget.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.gadget.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.sounds.LaserLoopSound;
import com.direwolf20.mininggadgets.common.sounds.OurSounds;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import com.direwolf20.mininggadgets.common.util.MiscTools;
import com.direwolf20.mininggadgets.common.util.VectorHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.world.BlockEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MiningGadget extends Item {
    private int energyCapacity;
    private Random rand = new Random();
    private LaserLoopSound laserLoopSound;
    //private static int energyPerItem = 15;

    public MiningGadget() {
        super(new Item.Properties().maxStackSize(1).group(MiningGadgets.itemGroup));
        this.energyCapacity = Config.MININGGADGET_MAXPOWER.get();
    }

    //TODO Add an override for onCreated and initialize all NBT Tags in it

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
        Minecraft mc = Minecraft.getInstance();

        if (!InputMappings.isKeyDown(mc.getMainWindow().getHandle(), mc.gameSettings.keyBindSneak.getKey().getKeyCode())) {
            tooltip.add(new TranslationTextComponent("mininggadgets.tooltip.item.show_upgrades",
                    mc.gameSettings.keyBindSneak.getLocalizedName().toLowerCase())
                    .applyTextStyle(TextFormatting.GRAY));
        } else {
            tooltip.add(new TranslationTextComponent("mininggadgets.tooltip.item.break_cost", getEnergyCost(stack)).applyTextStyle(TextFormatting.RED));
            tooltip.add(new TranslationTextComponent("mininggadgets.tooltip.item.upgrades").applyTextStyle(TextFormatting.AQUA));
            if (!(upgrades.isEmpty())) {
                for (Upgrade upgrade : upgrades) {
                    tooltip.add(new StringTextComponent(" - " +
                            I18n.format(upgrade.getLocal())
                    ).applyTextStyle(TextFormatting.GRAY));
                }
            }
        }

        stack.getCapability(CapabilityEnergy.ENERGY, null)
                .ifPresent(energy -> tooltip.add(
                        new TranslationTextComponent("mininggadgets.gadget.energy",
                                MiscTools.tidyValue(energy.getEnergyStored()),
                                MiscTools.tidyValue(energy.getMaxEnergyStored())).applyTextStyles(TextFormatting.GREEN)));
    }

    @Override
    public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        super.fillItemGroup(group, items);
        if (!isInGroup(group))
            return;

        ItemStack charged = new ItemStack(this);
        charged.getOrCreateTag().putDouble("energy", Config.MININGGADGET_MAXPOWER.get());
        items.add(charged);
    }

    public static void changeRange(ItemStack tool) {
        if (MiningProperties.getRange(tool) == 1)
            MiningProperties.setRange(tool, 3);
        else
            MiningProperties.setRange(tool, 1);
    }

    public static boolean canMine(ItemStack tool) {
        IEnergyStorage energy = tool.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);
        int cost = getEnergyCost(tool);

        if (MiningProperties.getRange(tool) == 3)
            cost = cost * 9;

        return energy.getEnergyStored() > cost;
    }

    public static boolean canMineBlock(ItemStack tool, World world, PlayerEntity player, BlockPos pos, BlockState state) {
        if (!player.isAllowEdit() || !world.isBlockModifiable(player, pos))
            return false;

        BlockEvent.BreakEvent e = new BlockEvent.BreakEvent(world, pos, state, player);
        if (e.isCanceled())
            return false;

        return canMine(tool);
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
    public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
        return true;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);

        // Only perform the shift action
        if (player.isShiftKeyDown())
            return this.onItemShiftRightClick(world, player, hand, itemstack);

        if (world.isRemote) {
            float volume = MiningProperties.getVolume(itemstack);
            if (volume != 0.0f)
                player.playSound(OurSounds.LASER_START.getSound(), volume, 1f);
            return new ActionResult<>(ActionResultType.PASS, itemstack);
        }

        if (!canMine(itemstack))
            return new ActionResult<>(ActionResultType.FAIL, itemstack);

        player.setActiveHand(hand);
        return new ActionResult<>(ActionResultType.PASS, itemstack);
    }

    private ActionResult<ItemStack> onItemShiftRightClick(World world, PlayerEntity player, Hand hand, ItemStack itemstack) {
        // Debug code for free energy
        //itemstack.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> e.receiveEnergy(1500000000, false));

//        if (UpgradeTools.containsUpgrade(itemstack, Upgrade.THREE_BY_THREE)) {
//            changeRange(itemstack);
//            player.sendStatusMessage(new StringTextComponent(TextFormatting.AQUA + new TranslationTextComponent("mininggadgets.gadget.range_change", MiningProperties.getRange(itemstack)).getUnformattedComponentText()), true);
//        }
        if (!world.isRemote)
            MiningProperties.setCanMine(itemstack, true);
        if (world.isRemote)
            ModScreens.openGadgetSettingsScreen(itemstack);

        return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
    }

    public List<BlockPos> findSources(World world, List<BlockPos> coords) {
        List<BlockPos> sources = new ArrayList<>();
        for (BlockPos coord : coords) {
            for (Direction side : Direction.values()) {
                BlockPos sidePos = coord.offset(side);
                IFluidState state = world.getFluidState(sidePos);
                if ((state.getFluid().isEquivalentTo(Fluids.LAVA) || state.getFluid().isEquivalentTo(Fluids.WATER)))
                    if (!sources.contains(sidePos))
                        sources.add(sidePos);
            }
        }
        return sources;
    }

    private void spawnFreezeParticle(PlayerEntity player, BlockPos sourcePos, World world, ItemStack stack) {
        float randomPartSize = 0.05f + (0.125f - 0.05f) * rand.nextFloat();
        double randomTX = rand.nextDouble();
        double randomTY = rand.nextDouble();
        double randomTZ = rand.nextDouble();
        double alpha = -0.5f + (1.0f - 0.5f) * rand.nextDouble(); //rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        Vec3d playerPos = player.getPositionVec().add(0, player.getEyeHeight(), 0);
        Vec3d look = player.getLookVec(); // or getLook(partialTicks)
        int range = MiningProperties.getBeamRange(stack);
        BlockRayTraceResult lookAt = VectorHelper.getLookingAt(player, RayTraceContext.FluidMode.NONE, range);
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

    @OnlyIn(Dist.CLIENT)
    public void playLoopSound(LivingEntity player, ItemStack stack) {
        float volume = MiningProperties.getVolume(stack);
        PlayerEntity myplayer = Minecraft.getInstance().player;
        if (myplayer.equals((PlayerEntity) player)) {
            if (volume != 0.0f) {
                if (laserLoopSound == null) {
                    laserLoopSound = new LaserLoopSound((PlayerEntity) player, volume);
                    Minecraft.getInstance().getSoundHandler().play(laserLoopSound);
                }
            }
        }

    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        //Server and Client side
        World world = player.world;
        if (world.isRemote) {
            playLoopSound(player, stack);
        }

        if (!MiningProperties.getCanMine(stack))
            return;


        int range = MiningProperties.getBeamRange(stack);
        BlockRayTraceResult lookingAt = VectorHelper.getLookingAt((PlayerEntity) player, RayTraceContext.FluidMode.NONE, range);
        if (lookingAt == null || (world.getBlockState(VectorHelper.getLookingAt((PlayerEntity) player, stack, range).getPos()) == Blocks.AIR.getDefaultState()))
            return;

        List<BlockPos> coords = MiningCollect.collect((PlayerEntity) player, lookingAt, world, MiningProperties.getRange(stack));

        if (UpgradeTools.containsActiveUpgrade(stack, Upgrade.FREEZING)) {
            for (BlockPos sourcePos : findSources(player.world, coords)) {
                if (player instanceof PlayerEntity)
                    spawnFreezeParticle((PlayerEntity) player, sourcePos, player.world, stack);
            }
        }
        //Server Side
        if (!world.isRemote) {
            // As all upgrade types with tiers contain the same name, we can check for a single
            // type in the enum and produce a result that we can then pull the tier from
            int efficiency = 0;
            if (UpgradeTools.containsActiveUpgrade((stack), Upgrade.EFFICIENCY_1))
                efficiency = UpgradeTools.getUpgradeFromGadget((stack), Upgrade.EFFICIENCY_1).get().getTier();

            float hardness = getHardness(coords, (PlayerEntity) player, efficiency);
            hardness = hardness * MiningProperties.getRange(stack) * 1;
            hardness = (float) Math.floor(hardness);
            if (hardness == 0) hardness = 1;
            for (BlockPos coord : coords) {
                BlockState state = world.getBlockState(coord);
                if (!(state.getBlock() instanceof RenderBlock)) {
                    //if (!world.isRemote) {
                    if (!canMineBlock(stack, world, (PlayerEntity) player, coord, state)) {
                        return;
                    }
                    List<Upgrade> gadgetUpgrades = UpgradeTools.getUpgrades(stack);
                    world.setBlockState(coord, ModBlocks.RENDER_BLOCK.get().getDefaultState());
                    RenderBlockTileEntity te = (RenderBlockTileEntity) world.getTileEntity(coord);
                    te.setRenderBlock(state);
                    te.setBreakType(MiningProperties.getBreakType(stack));
                    te.setGadgetUpgrades(gadgetUpgrades);
                    te.setGadgetFilters(MiningProperties.getFiltersAsList(stack));
                    te.setGadgetIsWhitelist(MiningProperties.getWhiteList(stack));
                    te.setPriorDurability((int) hardness + 1);
                    te.setOriginalDurability((int) hardness + 1);
                    te.setDurability((int) hardness, stack);
                    te.setPlayer((PlayerEntity) player);
                    te.setBlockAllowed();
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
                        if (!UpgradeTools.containsUpgrade(stack, Upgrade.HEATSINK)) {
                            //player.resetActiveHand();
                        }
                        stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> e.receiveEnergy(getEnergyCost(stack) * -1, false));
                        if (MiningProperties.getPrecisionMode(stack)) {
                            MiningProperties.setCanMine(stack, false);
                            player.resetActiveHand();
                        }
                    }
                    te.setDurability(durability, stack);
                    //}
                }
            }
            if (!(UpgradeTools.containsActiveUpgrade(stack, Upgrade.LIGHT_PLACER)))
                return;

            Direction side = lookingAt.getFace();
            boolean vertical = side.getAxis().isVertical();
            Direction up = vertical ? player.getHorizontalFacing() : Direction.UP;
            Direction right = vertical ? up.rotateY() : side.rotateYCCW();

            BlockPos pos;
            if (MiningProperties.getRange(stack) == 1)
                pos = lookingAt.getPos().offset(side, 4);
            else
                pos = lookingAt.getPos().offset(side).offset(right);

            if (world.getLight(pos) <= 7 && world.getBlockState(pos).getMaterial() == Material.AIR) {
                world.setBlockState(pos, ModBlocks.MINERS_LIGHT.get().getDefaultState());
                stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> e.receiveEnergy((Config.UPGRADECOST_LIGHT.get() * -1), false));
            }
        } else {

        }
    }

    public static int getEnergyCost(ItemStack stack) {
        int cost = Config.MININGGADGET_BASECOST.get();
        List<Upgrade> upgrades = UpgradeTools.getActiveUpgrades(stack);
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
        if (worldIn.isRemote) {
            if (laserLoopSound != null) {
                float volume = MiningProperties.getVolume(stack);
                if (volume != 0.0f && !laserLoopSound.isDonePlaying()) {
                    entityLiving.playSound(OurSounds.LASER_END.getSound(), volume, 1f);
                }
                laserLoopSound = null;
            }
        }

        if (entityLiving instanceof PlayerEntity) {
            entityLiving.resetActiveHand();
        }
        if (!worldIn.isRemote)
            MiningProperties.setCanMine(stack, true);
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

    /*
        UTILS
    */
    public static ItemStack getGadget(PlayerEntity player) {
        ItemStack heldItem = player.getHeldItemMainhand();
        if (!(heldItem.getItem() instanceof MiningGadget)) {
            heldItem = player.getHeldItemOffhand();
            if (!(heldItem.getItem() instanceof MiningGadget)) {
                return ItemStack.EMPTY;
            }
        }
        return heldItem;
    }
}
