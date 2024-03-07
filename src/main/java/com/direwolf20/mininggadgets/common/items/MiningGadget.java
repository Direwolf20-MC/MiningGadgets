package com.direwolf20.mininggadgets.common.items;

import com.direwolf20.mininggadgets.client.OurKeys;
import com.direwolf20.mininggadgets.client.particles.playerparticle.PlayerParticleData;
import com.direwolf20.mininggadgets.client.screens.ModScreens;
import com.direwolf20.mininggadgets.common.blocks.RenderBlock;
import com.direwolf20.mininggadgets.common.items.gadget.MiningCollect;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.sounds.LaserLoopSound;
import com.direwolf20.mininggadgets.common.sounds.OurSounds;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import com.direwolf20.mininggadgets.common.util.MagicHelpers;
import com.direwolf20.mininggadgets.common.util.VectorHelper;
import com.direwolf20.mininggadgets.setup.Config;
import com.direwolf20.mininggadgets.setup.Registration;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MiningGadget extends Item {
    private final Random rand = new Random();
    private LaserLoopSound laserLoopSound;
    //private static int energyPerItem = 15;

    public MiningGadget() {
        super(new Item.Properties()
                .stacksTo(1)
                .setNoRepair());

    }

    public int getEnergyMax() {
        return Config.MININGGADGET_MAXPOWER.get();
    }

    //TODO Add an override for onCreated and initialize all NBT Tags in it

    @Override
    public void verifyTagAfterLoad(@NotNull CompoundTag tag) {
        if (UpgradeTools.containsUpgrades(tag)) {
            UpgradeTools.walkUpgradesOnTag(tag, (CompoundTag upgradeTag, String upgradeName) -> {
                if (upgradeName.equalsIgnoreCase("three_by_three")) {
                    return Upgrade.SIZE_1.getName();
                }
                return null;
            });
        }
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return this.getEnergyMax();
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        if (MiningProperties.getBatteryTier(stack) == Upgrade.BATTERY_CREATIVE.getTier())
            return false;

        var energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy == null) {
            return false;
        }
        return (energy.getEnergyStored() < energy.getMaxEnergyStored());
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (MiningProperties.getBatteryTier(stack) == Upgrade.BATTERY_CREATIVE.getTier())
            return 13;

        var energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy == null) {
            return 13;
        }

        return Math.min(13 * energy.getEnergyStored() / energy.getMaxEnergyStored(), 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        if (MiningProperties.getBatteryTier(stack) == Upgrade.BATTERY_CREATIVE.getTier())
            return Mth.color(0, 1, 0);

        var energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy == null) {
            super.getBarColor(stack);
        }
        return Mth.hsvToRgb(Math.max(0.0F, (float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored()) / 3.0F, 1.0F, 1.0F);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        List<Upgrade> upgrades = UpgradeTools.getUpgrades(stack);
        Minecraft mc = Minecraft.getInstance();

        if (world == null || mc.player == null) {
            return;
        }

        boolean sneakPressed = Screen.hasShiftDown();

        if (!sneakPressed) {
            tooltip.add(Component.translatable("mininggadgets.tooltip.item.show_upgrades",
                    "shift")
                    .withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("mininggadgets.tooltip.item.break_cost", getEnergyCost(stack)).withStyle(ChatFormatting.RED));
            if (!(upgrades.isEmpty())) {
                tooltip.add(Component.translatable("mininggadgets.tooltip.item.upgrades").withStyle(ChatFormatting.AQUA));
                for (Upgrade upgrade : upgrades) {
                    tooltip.add(Component.literal(" - " +
                            I18n.get(upgrade.getLocal())
                    ).withStyle(ChatFormatting.GRAY));
                }
            }
        }

        var energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy == null) {
            return;
        }

        MutableComponent energyText = !sneakPressed
                ? Component.translatable("mininggadgets.gadget.energy", MagicHelpers.tidyValue(energy.getEnergyStored()), MagicHelpers.tidyValue(energy.getMaxEnergyStored()))
                : Component.translatable("mininggadgets.gadget.energy", String.format("%,d", energy.getEnergyStored()), String.format("%,d", energy.getMaxEnergyStored()));
        tooltip.add(energyText.withStyle(ChatFormatting.GREEN));

    }

    // TODO: Use event
//    @Override
//    public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
//        super.fillItemCategory(group, items);
//        if (!allowedIn(group))
//            return;
//
//        ItemStack charged = new ItemStack(this);
//        charged.getOrCreateTag().putDouble("energy", Config.MININGGADGET_MAXPOWER.get());
//        items.add(charged);
//    }

    public static void changeRange(ItemStack tool) {
        int maxRange = MiningProperties.getMaxMiningRange(tool);
        if (maxRange == 1) {
            MiningProperties.setRange(tool, 1);
            return;
        }

        int range = MiningProperties.getRange(tool);
        if (range == maxRange) // If we're at max range (set by upgrade), then we toggle back to 1x1
            MiningProperties.setRange(tool, 1);
        else
            MiningProperties.setRange(tool, range + 2); // 1 -> 3 -> 5 -> 7 -> 9 -> 11 -> etc
    }

    public static boolean canMine(ItemStack tool) {
        if (MiningProperties.getBatteryTier(tool) == Upgrade.BATTERY_CREATIVE.getTier())
            return true;

        var energy = tool.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy == null) {
            return false;
        }
        int cost = getEnergyCost(tool);

        var range = MiningProperties.getRange(tool);

        if (range > 1)
            cost = cost * (range * range);

        return energy.getEnergyStored() >= cost;
    }

    public static boolean canMineBlock(ItemStack tool, Level world, Player player, BlockPos pos, BlockState state) {
        if (!player.mayBuild() || !world.mayInteract(player, pos))
            return false;

        if (NeoForge.EVENT_BUS.post(new BlockEvent.BreakEvent(world, pos, state, player)).isCanceled())
            return false;

        return canMine(tool);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
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
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // Only perform the shift action
        if (player.isShiftKeyDown()) {
            if (!world.isClientSide) {
                MiningProperties.setCanMine(itemstack, true);
//                return ActionResult.resultPass(itemstack);
            }

            if (world.isClientSide) {
                if (OurKeys.shiftClickGuiBinding.getKey() == InputConstants.UNKNOWN) {
                    ModScreens.openGadgetSettingsScreen(itemstack);
                    return InteractionResultHolder.pass(itemstack);
                }
            }

            // INTENTIONALLY LEFT IN. I DON'T HAVE THE TIME TO FIX THIS ISSUE ATM
            // @todo: migrate keybinding setting onto gadget so I can set a tag on the item
            return InteractionResultHolder.pass(itemstack);
        }

        if (world.isClientSide) {
            float volume = MiningProperties.getVolume(itemstack);
            if (volume != 0.0f)
                if (itemstack.getHoverName().getString().toLowerCase(Locale.ROOT).contains("mongo"))
                    player.playSound(SoundEvents.STONE_HIT, volume * 0.5f, 1f);
                else
                    player.playSound(OurSounds.LASER_START.get(), volume * 0.5f, 1f);
            return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
        }

        if (!canMine(itemstack))
            return new InteractionResultHolder<>(InteractionResult.FAIL, itemstack);

        player.startUsingItem(hand);
        return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
    }

    public List<BlockPos> findSources(Level world, List<BlockPos> coords) {
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

    private void spawnFreezeParticle(Player player, BlockPos sourcePos, Level world, ItemStack stack) {
        float randomPartSize = 0.05f + (0.125f - 0.05f) * rand.nextFloat();
        double randomTX = rand.nextDouble();
        double randomTY = rand.nextDouble();
        double randomTZ = rand.nextDouble();
        double alpha = -0.5f + (1.0f - 0.5f) * rand.nextDouble(); //rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        Vec3 playerPos = player.position().add(0, player.getEyeHeight(), 0);
        Vec3 look = player.getLookAngle(); // or getLook(partialTicks)
        int range = MiningProperties.getBeamRange(stack);
        BlockHitResult lookAt = VectorHelper.getLookingAt(player, ClipContext.Fluid.NONE, range);
        Vec3 lookingAt = lookAt.getLocation();
        //The next 3 variables are directions on the screen relative to the players look direction. So right = to the right of the player, regardless of facing direction.
        Vec3 right = new Vec3(-look.z, 0, look.x).normalize();
        Vec3 forward = look;
        Vec3 backward = look.multiply(-1, 1, -1);
        Vec3 down = right.cross(forward);

        //These are used to calculate where the particles are going. We want them going into the laser, so we move the destination right, down, and forward a bit.
        right = right.scale(0.65f);
        forward = forward.scale(0.85f);
        down = down.scale(-0.35);
        backward = backward.scale(0.05);

        //Take the player's eye position, and shift it to where the end of the laser is (Roughly)
        Vec3 laserPos = playerPos.add(right);
        laserPos = laserPos.add(forward);
        laserPos = laserPos.add(down);
        lookingAt = lookingAt.add(backward);
        PlayerParticleData data = PlayerParticleData.playerparticle("ice", sourcePos.getX() + randomTX, sourcePos.getY() + randomTY, sourcePos.getZ() + randomTZ, randomPartSize, 1f, 1f, 1f, 120, true);
        //Change the below laserPos to lookingAt to have it emit from the laser gun itself
        world.addParticle(data, laserPos.x, laserPos.y, laserPos.z, 0.025, 0.025f, 0.025);
    }

    private void spawnFireParticle(BlockPos sourcePos, ServerLevel world) {
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
        Player myplayer = Minecraft.getInstance().player;
        if (myplayer.equals(player)) {
            if (volume != 0.0f) {
                if (stack.getHoverName().getString().toLowerCase(Locale.ROOT).contains("mongo")) {
                    if (player.level().getGameTime() % 5 == 0)
                        if (rand.nextDouble() > 0.005d)
                            player.playSound(SoundEvents.STONE_HIT, volume * 0.5f, 1f);
                        else
                            player.playSound(SoundEvents.CREEPER_PRIMED, volume * 1f, 1f);
                }
                else {
                    if (laserLoopSound == null) {
                        laserLoopSound = new LaserLoopSound((Player) player, volume, player.level().random);
                        Minecraft.getInstance().getSoundManager().play(laserLoopSound);
                    }
                }
            }
        }
    }

    @Override
    public void onUseTick(Level world, LivingEntity livingEntity, ItemStack stack, int count)
    {
        if(!(livingEntity instanceof Player)) return;

        Player player = (Player) livingEntity;
        //Server and Client side
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
        BlockHitResult lookingAt = VectorHelper.getLookingAt((Player) player, ClipContext.Fluid.NONE, range);
        if (world.getBlockState(VectorHelper.getLookingAt((Player) player, stack, range).getBlockPos()) == Blocks.AIR.defaultBlockState())
            return;

        List<BlockPos> coords = MiningCollect.collect((Player) player, lookingAt, world, MiningProperties.getRange(stack), MiningProperties.getSizeMode(stack));

        if (UpgradeTools.containsActiveUpgrade(stack, Upgrade.FREEZING)) {
            for (BlockPos sourcePos : findSources(player.level(), coords)) {
                int delay = MiningProperties.getFreezeDelay(stack);
                if (delay == 0 || count % delay == 0)
                    spawnFreezeParticle((Player) player, sourcePos, player.level(), stack);
            }
        }

        // Server Side
        if (!world.isClientSide) {
            var cap = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (cap == null) return;
            // As all upgrade types with tiers contain the same name, we can check for a single
            // type in the enum and produce a result that we can then pull the tier from
            int efficiency = 0;
            if (UpgradeTools.containsActiveUpgrade((stack), Upgrade.EFFICIENCY_1))
                efficiency = UpgradeTools.getUpgradeFromGadget((stack), Upgrade.EFFICIENCY_1).get().getTier();

            float hardness = getHardness(coords, (Player) player, efficiency);
            hardness = hardness * MiningProperties.getRange(stack) * 1;
            hardness = (float) Math.floor(hardness);
            if (hardness == 0) hardness = 1;
            for (BlockPos coord : coords) {
                BlockState state = world.getBlockState(coord);
                if (!(state.getBlock() instanceof RenderBlock)) {
                    //if (!world.isRemote) {
                    if (!canMineBlock(stack, world, (Player) player, coord, state)) {
                        return;
                    }
                    List<Upgrade> gadgetUpgrades = UpgradeTools.getUpgrades(stack);
                    boolean placed = world.setBlockAndUpdate(coord, Registration.RENDER_BLOCK.get().defaultBlockState());
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
                    te.setPlayer((Player) player);
                    te.setBlockAllowed();
                    //}
                } else {
                    //if (!world.isRemote) {
                    RenderBlockTileEntity te = (RenderBlockTileEntity) world.getBlockEntity(coord);
                    if (te != null) {
                        int durability = te.getDurability();
                /*if (player.getHeldItemMainhand().getItem() instanceof MiningGadget && player.getHeldItemOffhand().getItem() instanceof MiningGadget)
                    durability = durability - 2;
                else*/
                        durability = durability - 1;
                        if (durability <= 0) {
                            cap.receiveEnergy(getEnergyCost(stack) * -1, false);
                            if (MiningProperties.getPrecisionMode(stack)) {
                                MiningProperties.setCanMine(stack, false);
                                player.stopUsingItem();
                            }
                        }
                        te.setDurability(durability, stack);
                    }
                    //}
                }
                if (stack.getHoverName().getString().toLowerCase(Locale.ROOT).contains("wildfirev")) {
                    spawnFireParticle(coord, (ServerLevel) player.level());
                }
            }
            if (!(UpgradeTools.containsActiveUpgrade(stack, Upgrade.LIGHT_PLACER)))
                return;

            Direction side = lookingAt.getDirection();
            boolean vertical = side.getAxis().isVertical();
            Direction up = vertical ? player.getDirection() : Direction.UP;
            Direction right = vertical ? up.getClockWise() : side.getCounterClockWise();

            int rightAmt = MiningProperties.getRange(stack) / 2;
            BlockPos pos = lookingAt.getBlockPos().relative(side).relative(right, rightAmt);

            if (world.getMaxLocalRawBrightness(pos) <= 7 && world.getBlockState(pos).isAir()) {
                int energy = cap.getEnergyStored();
                if (energy > Config.UPGRADECOST_LIGHT.get()) {
                    world.setBlockAndUpdate(pos, Registration.MINERS_LIGHT.get().defaultBlockState());
                    cap.receiveEnergy((Config.UPGRADECOST_LIGHT.get() * -1), false);
                }

            }
        }
    }

    public static int getEnergyCost(ItemStack stack) {
        if (MiningProperties.getBatteryTier(stack) == Upgrade.BATTERY_CREATIVE.getTier())
            return 0;

        int cost = Config.MININGGADGET_BASECOST.get();
        List<Upgrade> upgrades = UpgradeTools.getActiveUpgrades(stack);
        if (upgrades.isEmpty())
            return cost;

        return cost + upgrades.stream().mapToInt(Upgrade::getCostPerBlock).sum();
    }

    private static float getHardness(List<BlockPos> coords, Player player, int efficiency) {
        float hardness = 0;
        float toolSpeed = 8;
        if (efficiency > 0) {
            toolSpeed = toolSpeed + ((efficiency * efficiency + 1));
        }

        MobEffectInstance hasteEffect = player.getEffect(MobEffects.DIG_SPEED);
        if (hasteEffect != null) {
            int hasteLevel = hasteEffect.getAmplifier() + 1;
            toolSpeed = toolSpeed + (toolSpeed * ((hasteLevel * 20f) / 100));
        }

        MobEffectInstance miningFatigue = player.getEffect(MobEffects.DIG_SLOWDOWN);
        if (miningFatigue != null) {
            toolSpeed = toolSpeed / 3f;
        }

        Level world = player.level();
        for (BlockPos coord : coords) {
            BlockState state = world.getBlockState(coord);
            float temphardness = state.getDestroySpeed(world, coord);

            if (state.getBlock() instanceof RenderBlock) {
                RenderBlockTileEntity blockEntity = (RenderBlockTileEntity) world.getBlockEntity(coord);
                if (blockEntity != null && blockEntity.getRenderBlock() != null) {
                    temphardness = blockEntity.getRenderBlock().getDestroySpeed(world, coord);
                }
            }


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
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if (worldIn.isClientSide) {
            if (laserLoopSound != null) {
                float volume = MiningProperties.getVolume(stack);
                if (volume != 0.0f && !laserLoopSound.isStopped()) {
                    entityLiving.playSound(OurSounds.LASER_END.get(), volume * 0.5f, 1f);
                }
                laserLoopSound = null;
            }
        }

        if (entityLiving instanceof Player)
            entityLiving.stopUsingItem();

        if (!worldIn.isClientSide)
            MiningProperties.setCanMine(stack, true);
    }

    /*
        UTILS
    */
    public static ItemStack getGadget(Player player) {
        ItemStack heldItem = player.getMainHandItem();
        if (!(heldItem.getItem() instanceof MiningGadget)) {
            heldItem = player.getOffhandItem();
            if (!(heldItem.getItem() instanceof MiningGadget)) {
                return ItemStack.EMPTY;
            }
        }
        return heldItem;
    }

    public static boolean isHolding(Player entity) {
        return getGadget(entity).getItem() instanceof MiningGadget;
    }
}
