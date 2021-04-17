package com.direwolf20.mininggadgets.common.items;

import com.direwolf20.mininggadgets.client.OurKeys;
import com.direwolf20.mininggadgets.client.particles.playerparticle.PlayerParticleData;
import com.direwolf20.mininggadgets.client.screens.ModScreens;
import com.direwolf20.mininggadgets.common.Config;
import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.blocks.RenderBlock;
import com.direwolf20.mininggadgets.common.capabilities.CapabilityEnergyProvider;
import com.direwolf20.mininggadgets.common.items.gadget.MiningCollect;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.sounds.LaserLoopSound;
import com.direwolf20.mininggadgets.common.sounds.OurSounds;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import com.direwolf20.mininggadgets.common.util.MagicHelpers;
import com.direwolf20.mininggadgets.common.util.VectorHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.world.BlockEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MiningGadget extends Item {
    private int energyCapacity;
    private Random rand = new Random();
    private LaserLoopSound laserLoopSound;
    //private static int energyPerItem = 15;

    public MiningGadget() {
        super(new Item.Properties()
                .stacksTo(1)
                .tab(MiningGadgets.itemGroup)
                .setNoRepair());

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
        return stack.getCapability(CapabilityEnergy.ENERGY, null)
                .map(e -> 1D - (e.getEnergyStored() / (double) e.getMaxEnergyStored()))
                .orElse(0D);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY)
                .map(e -> MathHelper.hsvToRgb(Math.max(0.0F, (float) e.getEnergyStored() / (float) e.getMaxEnergyStored()) / 3.0F, 1.0F, 1.0F))
                .orElse(super.getRGBDurabilityForDisplay(stack));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        List<Upgrade> upgrades = UpgradeTools.getUpgrades(stack);
        Minecraft mc = Minecraft.getInstance();

        if (world == null || mc.player == null) {
            return;
        }

        boolean sneakPressed = Screen.hasShiftDown();

        if (!sneakPressed) {
            tooltip.add(new TranslationTextComponent("mininggadgets.tooltip.item.show_upgrades",
                    "shift")
                    .withStyle(TextFormatting.GRAY));
        } else {
            tooltip.add(new TranslationTextComponent("mininggadgets.tooltip.item.break_cost", getEnergyCost(stack)).withStyle(TextFormatting.RED));
            if (!(upgrades.isEmpty())) {
                tooltip.add(new TranslationTextComponent("mininggadgets.tooltip.item.upgrades").withStyle(TextFormatting.AQUA));
                for (Upgrade upgrade : upgrades) {
                    tooltip.add(new StringTextComponent(" - " +
                            I18n.get(upgrade.getLocal())
                    ).withStyle(TextFormatting.GRAY));
                }
            }
        }

        stack.getCapability(CapabilityEnergy.ENERGY, null)
                .ifPresent(energy -> {
                    TranslationTextComponent energyText = !sneakPressed
                            ? new TranslationTextComponent("mininggadgets.gadget.energy", MagicHelpers.tidyValue(energy.getEnergyStored()), MagicHelpers.tidyValue(energy.getMaxEnergyStored()))
                            : new TranslationTextComponent("mininggadgets.gadget.energy", String.format("%,d", energy.getEnergyStored()), String.format("%,d", energy.getMaxEnergyStored()));
                    tooltip.add(energyText.withStyle(TextFormatting.GREEN));
                });
    }

    @Override
    public void fillItemCategory(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        super.fillItemCategory(group, items);
        if (!allowdedIn(group))
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

        return energy.getEnergyStored() >= cost;
    }

    public static boolean canMineBlock(ItemStack tool, World world, PlayerEntity player, BlockPos pos, BlockState state) {
        if (!player.mayBuild() || !world.mayInteract(player, pos))
            return false;

        if (MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(world, pos, state, player)))
            return false;

        return canMine(tool);
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
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
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // Only perform the shift action
        if (player.isShiftKeyDown()) {
            if (!world.isClientSide) {
                MiningProperties.setCanMine(itemstack, true);
//                return ActionResult.resultPass(itemstack);
            }

            if (world.isClientSide) {
                if (OurKeys.shiftClickGuiBinding.getKey() == InputMappings.UNKNOWN) {
                    ModScreens.openGadgetSettingsScreen(itemstack);
                    return ActionResult.pass(itemstack);
                }
            }

            // INTENTIONALLY LEFT IN. I DON'T HAVE THE TIME TO FIX THIS ISSUE ATM
            // @todo: migrate keybinding setting onto gadget so I can set a tag on the item
            return ActionResult.pass(itemstack);
        }

        if (world.isClientSide) {
            float volume = MiningProperties.getVolume(itemstack);
            if (volume != 0.0f)
                if (itemstack.getHoverName().getString().toLowerCase(Locale.ROOT).contains("mongo"))
                    player.playSound(SoundEvents.STONE_HIT, volume * 0.5f, 1f);
                else
                    player.playSound(OurSounds.LASER_START.getSound(), volume * 0.5f, 1f);
            return new ActionResult<>(ActionResultType.PASS, itemstack);
        }

        if (!canMine(itemstack))
            return new ActionResult<>(ActionResultType.FAIL, itemstack);

        player.startUsingItem(hand);
        return new ActionResult<>(ActionResultType.PASS, itemstack);
    }

    public List<BlockPos> findSources(World world, List<BlockPos> coords) {
        List<BlockPos> sources = new ArrayList<>();
        for (BlockPos coord : coords) {
            for (Direction side : Direction.values()) {
                BlockPos sidePos = coord.relative(side);
                FluidState state = world.getFluidState(sidePos);
                if ((state.getType().isSame(Fluids.LAVA) || state.getType().isSame(Fluids.WATER)))
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
        Vector3d playerPos = player.position().add(0, player.getEyeHeight(), 0);
        Vector3d look = player.getLookAngle(); // or getLook(partialTicks)
        int range = MiningProperties.getBeamRange(stack);
        BlockRayTraceResult lookAt = VectorHelper.getLookingAt(player, RayTraceContext.FluidMode.NONE, range);
        Vector3d lookingAt = lookAt.getLocation();
        //The next 3 variables are directions on the screen relative to the players look direction. So right = to the right of the player, regardless of facing direction.
        Vector3d right = new Vector3d(-look.z, 0, look.x).normalize();
        Vector3d forward = look;
        Vector3d backward = look.multiply(-1, 1, -1);
        Vector3d down = right.cross(forward);

        //These are used to calculate where the particles are going. We want them going into the laser, so we move the destination right, down, and forward a bit.
        right = right.scale(0.65f);
        forward = forward.scale(0.85f);
        down = down.scale(-0.35);
        backward = backward.scale(0.05);

        //Take the player's eye position, and shift it to where the end of the laser is (Roughly)
        Vector3d laserPos = playerPos.add(right);
        laserPos = laserPos.add(forward);
        laserPos = laserPos.add(down);
        lookingAt = lookingAt.add(backward);
        PlayerParticleData data = PlayerParticleData.playerparticle("ice", sourcePos.getX() + randomTX, sourcePos.getY() + randomTY, sourcePos.getZ() + randomTZ, randomPartSize, 1f, 1f, 1f, 120, true);
        //Change the below laserPos to lookingAt to have it emit from the laser gun itself
        world.addParticle(data, laserPos.x, laserPos.y, laserPos.z, 0.025, 0.025f, 0.025);
    }

    private void spawnFireParticle(BlockPos sourcePos, ServerWorld world) {
        double partType = rand.nextDouble();
        if (partType < 0.75d) return;
        double randomTX = rand.nextDouble();
        double randomTY = rand.nextDouble();
        double randomTZ = rand.nextDouble();

        if (partType < 0.95d)
            world.sendParticles(ParticleTypes.FLAME, sourcePos.getX() + randomTX, sourcePos.getY() + randomTY, sourcePos.getZ() + randomTZ, 1, 0D, 0D, 0D, 0.0D);
        else
            world.sendParticles(ParticleTypes.SMOKE, sourcePos.getX() + randomTX, sourcePos.getY() + randomTY, sourcePos.getZ() + randomTZ, 1, 0D, 0D, 0D, 0.0D);
    }

    @OnlyIn(Dist.CLIENT)
    public void playLoopSound(LivingEntity player, ItemStack stack) {
        float volume = MiningProperties.getVolume(stack);
        PlayerEntity myplayer = Minecraft.getInstance().player;
        if (myplayer.equals(player)) {
            if (volume != 0.0f) {
                if (stack.getHoverName().getString().toLowerCase(Locale.ROOT).contains("mongo")) {
                    if (player.level.getGameTime() % 5 == 0)
                        if (rand.nextDouble() > 0.005d)
                            player.playSound(SoundEvents.STONE_HIT, volume * 0.5f, 1f);
                        else
                            player.playSound(SoundEvents.CREEPER_PRIMED, volume * 1f, 1f);
                }
                else {
                    if (laserLoopSound == null) {
                        laserLoopSound = new LaserLoopSound((PlayerEntity) player, volume);
                        Minecraft.getInstance().getSoundManager().play(laserLoopSound);
                    }
                }
            }
        }
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        //Server and Client side
        World world = player.level;
        if (world.isClientSide) {
            this.playLoopSound(player, stack);
        }

        if (!MiningProperties.getCanMine(stack))
            return;


        if (!world.isClientSide && stack.getHoverName().getString().toLowerCase(Locale.ROOT).contains("rgb")) {
            float beam2r = MiningProperties.getColor(stack, MiningProperties.COLOR_RED_INNER) / 255f;
            float beam2g = MiningProperties.getColor(stack, MiningProperties.COLOR_GREEN_INNER) / 255f;
            float beam2b = MiningProperties.getColor(stack, MiningProperties.COLOR_BLUE_INNER) / 255f;
            float r = MiningProperties.getColor(stack, MiningProperties.COLOR_RED) / 255f;
            float g = MiningProperties.getColor(stack, MiningProperties.COLOR_GREEN) / 255f;
            float b = MiningProperties.getColor(stack, MiningProperties.COLOR_BLUE) / 255f;
            if (beam2r < 1 && beam2g == 0)
                MiningProperties.setColor(stack, (short) (beam2r * 255f + Math.min(255-(beam2r * 255f), 5)), MiningProperties.COLOR_RED_INNER);
            else if (beam2b > 0 && beam2r == 1)
                MiningProperties.setColor(stack, (short) (beam2b * 255f - Math.min(Math.abs(0-(beam2b * 255f)), 5)), MiningProperties.COLOR_BLUE_INNER);
            else if (beam2g < 1 && beam2r == 1)
                MiningProperties.setColor(stack, (short) (beam2g * 255f + Math.min(255-(beam2g * 255f), 5)), MiningProperties.COLOR_GREEN_INNER);
            else if (beam2r > 0 && beam2g == 1)
                MiningProperties.setColor(stack, (short) (beam2r * 255f - Math.min(Math.abs(0-(beam2r * 255f)), 5)), MiningProperties.COLOR_RED_INNER);
            else if (beam2b < 1 && beam2g == 1)
                MiningProperties.setColor(stack, (short) (beam2b * 255f + Math.min(255-(beam2b * 255f), 5)), MiningProperties.COLOR_BLUE_INNER);
            else if (beam2g > 0 && beam2b == 1)
                MiningProperties.setColor(stack, (short) (beam2g * 255f - Math.min(Math.abs(0-(beam2g * 255f)), 5)), MiningProperties.COLOR_GREEN_INNER);

            if (r < 1 && g == 0)
                MiningProperties.setColor(stack, (short) (r * 255f + Math.min(255-(r * 255f), 5)), MiningProperties.COLOR_RED);
            else if (b > 0 && r == 1)
                MiningProperties.setColor(stack, (short) (b * 255f - Math.min(Math.abs(0-(b * 255f)), 5)), MiningProperties.COLOR_BLUE);
            else if (g < 1 && r == 1)
                MiningProperties.setColor(stack, (short) (g * 255f + Math.min(255-(g * 255f), 5)), MiningProperties.COLOR_GREEN);
            else if (r > 0 && g == 1)
                MiningProperties.setColor(stack, (short) (r * 255f - Math.min(Math.abs(0-(r * 255f)), 5)), MiningProperties.COLOR_RED);
            else if (b < 1 && g == 1)
                MiningProperties.setColor(stack, (short) (b * 255f + Math.min(255-(b * 255f), 5)), MiningProperties.COLOR_BLUE);
            else if (g > 0 && b == 1)
                MiningProperties.setColor(stack, (short) (g * 255f - Math.min(Math.abs(0-(g * 255f)), 5)), MiningProperties.COLOR_GREEN);

        }

        int range = MiningProperties.getBeamRange(stack);
        BlockRayTraceResult lookingAt = VectorHelper.getLookingAt((PlayerEntity) player, RayTraceContext.FluidMode.NONE, range);
        if (lookingAt == null || (world.getBlockState(VectorHelper.getLookingAt((PlayerEntity) player, stack, range).getBlockPos()) == Blocks.AIR.defaultBlockState()))
            return;

        List<BlockPos> coords = MiningCollect.collect((PlayerEntity) player, lookingAt, world, MiningProperties.getRange(stack));

        if (UpgradeTools.containsActiveUpgrade(stack, Upgrade.FREEZING)) {
            for (BlockPos sourcePos : findSources(player.level, coords)) {
                if (player instanceof PlayerEntity) {
                    int delay = MiningProperties.getFreezeDelay(stack);
                    if (delay == 0 || count % delay == 0)
                        spawnFreezeParticle((PlayerEntity) player, sourcePos, player.level, stack);
                }
            }
        }

        // Server Side
        if (!world.isClientSide) {
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
                    boolean placed = world.setBlockAndUpdate(coord, ModBlocks.RENDER_BLOCK.get().defaultBlockState());
                    RenderBlockTileEntity te = (RenderBlockTileEntity) world.getBlockEntity(coord);

                    if (!placed || te == null) {
                        // this can happen when another mod rejects the set block state (fixes #120)
                        return;
                    }

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
                    RenderBlockTileEntity te = (RenderBlockTileEntity) world.getBlockEntity(coord);
                    int durability = te.getDurability();
                    //System.out.println(durability);
                /*if (player.getHeldItemMainhand().getItem() instanceof MiningGadget && player.getHeldItemOffhand().getItem() instanceof MiningGadget)
                    durability = durability - 2;
                else*/
                    durability = durability - 1;
                    if (durability <= 0) {
                        stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> e.receiveEnergy(getEnergyCost(stack) * -1, false));
                        if (MiningProperties.getPrecisionMode(stack)) {
                            MiningProperties.setCanMine(stack, false);
                            player.stopUsingItem();
                        }
                    }
                    te.setDurability(durability, stack);
                    //}
                }
                if (player instanceof PlayerEntity && stack.getHoverName().getString().toLowerCase(Locale.ROOT).contains("wildfirev")) {
                    spawnFireParticle(coord, (ServerWorld) player.level);
                }
            }
            if (!(UpgradeTools.containsActiveUpgrade(stack, Upgrade.LIGHT_PLACER)))
                return;

            Direction side = lookingAt.getDirection();
            boolean vertical = side.getAxis().isVertical();
            Direction up = vertical ? player.getDirection() : Direction.UP;
            Direction right = vertical ? up.getClockWise() : side.getCounterClockWise();

            BlockPos pos;
            if (MiningProperties.getRange(stack) == 1)
                pos = lookingAt.getBlockPos().relative(side, 4);
            else
                pos = lookingAt.getBlockPos().relative(side).relative(right);

            if (world.getMaxLocalRawBrightness(pos) <= 7 && world.getBlockState(pos).getMaterial() == Material.AIR) {
                int energy = stack.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
                if (energy > Config.UPGRADECOST_LIGHT.get()) {
                    world.setBlockAndUpdate(pos, ModBlocks.MINERS_LIGHT.get().defaultBlockState());
                    stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> e.receiveEnergy((Config.UPGRADECOST_LIGHT.get() * -1), false));
                }
            }
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
        EffectInstance hasteEffect = player.getEffect(Effects.DIG_SPEED);
        if (hasteEffect != null) {
            int hasteLevel = hasteEffect.getAmplifier() + 1;
            toolSpeed = toolSpeed + (toolSpeed * ((hasteLevel * 20f) / 100));
        }

        EffectInstance miningFatigue = player.getEffect(Effects.DIG_SLOWDOWN);
        if (miningFatigue != null) {
            toolSpeed = toolSpeed / 3f;
        }

        World world = player.level;
        for (BlockPos coord : coords) {
            BlockState state = world.getBlockState(coord);
            float temphardness = state.getDestroySpeed(world, coord);
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
    public void releaseUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (worldIn.isClientSide) {
            if (laserLoopSound != null) {
                float volume = MiningProperties.getVolume(stack);
                if (volume != 0.0f && !laserLoopSound.isStopped()) {
                    entityLiving.playSound(OurSounds.LASER_END.getSound(), volume * 0.5f, 1f);
                }
                laserLoopSound = null;
            }
        }

        if (entityLiving instanceof PlayerEntity)
            entityLiving.stopUsingItem();

        if (!worldIn.isClientSide)
            MiningProperties.setCanMine(stack, true);
    }

    /*
        UTILS
    */
    public static ItemStack getGadget(PlayerEntity player) {
        ItemStack heldItem = player.getMainHandItem();
        if (!(heldItem.getItem() instanceof MiningGadget)) {
            heldItem = player.getOffhandItem();
            if (!(heldItem.getItem() instanceof MiningGadget)) {
                return ItemStack.EMPTY;
            }
        }
        return heldItem;
    }

    public static boolean isHolding(PlayerEntity entity) {
        return getGadget(entity).getItem() instanceof MiningGadget;
    }
}
