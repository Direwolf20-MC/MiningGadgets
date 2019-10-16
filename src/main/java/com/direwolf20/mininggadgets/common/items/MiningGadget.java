package com.direwolf20.mininggadgets.common.items;

import com.direwolf20.mininggadgets.Config;
import com.direwolf20.mininggadgets.Setup;
import com.direwolf20.mininggadgets.common.blocks.MinersLight;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.blocks.RenderBlock;
import com.direwolf20.mininggadgets.common.capabilities.CapabilityEnergyProvider;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MiningGadget extends Item {
    private int energyCapacity;
    private static int energyPerItem = 15;

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
        return true;
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
                .ifPresent(energy -> tooltip.add(
                        new TranslationTextComponent("mininggadgets.item.energy", energy.getEnergyStored(), energy.getMaxEnergyStored())
                        )
                );
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
        IEnergyStorage energy = tool.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);
        if (energy.getEnergyStored() <= energyPerItem)
            return false;

        long lastBreak = getLastBreak(tool);
        if ((world.getGameTime() - lastBreak) < 2) return false;
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
        if( player.isSneaking() )
            return this.onItemShiftRightClick(world, player, hand, itemstack);

        if (!canMine(itemstack, world))
            return new ActionResult<>(ActionResultType.FAIL, itemstack);

        player.setActiveHand(hand);
        return new ActionResult<>(ActionResultType.PASS, itemstack);
    }

    public ActionResult<ItemStack> onItemShiftRightClick(World world, PlayerEntity player, Hand hand, ItemStack itemstack) {
        // Debug code for free energy
        itemstack.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> e.receiveEnergy(100000, false));
        if (UpgradeTools.containsUpgrade(itemstack, Upgrade.THREE_BY_THREE)) {
            changeRange(itemstack);
            DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> player.sendStatusMessage(new StringTextComponent(TextFormatting.AQUA + I18n.format("mininggadgets.mininggadget.range_change", getToolRange(itemstack))), true));
        }
        return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        World world = player.world;
        BlockRayTraceResult lookingAt = VectorHelper.getLookingAt((PlayerEntity) player, RayTraceContext.FluidMode.NONE);
        if (lookingAt == null || (world.getBlockState(VectorHelper.getLookingAt((PlayerEntity) player, stack).getPos()) == Blocks.AIR.getDefaultState()))
            return;
        List<BlockPos> coords = getMinableBlocks(stack, lookingAt, (PlayerEntity) player);

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
                if (!world.isRemote) {
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
                    te.setDurability((int) hardness);
                    te.setPlayer((PlayerEntity) player);

                }
            } else {
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
                    stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> e.receiveEnergy(getEnergyCost() * -1, false));
                }
                te.setDurability(durability);
            }
        }
        if (!world.isRemote) {
            if (!(UpgradeTools.containsUpgrade(stack, Upgrade.LIGHT_PLACER)))
                return;

            Direction side = lookingAt.getFace();
            boolean vertical = side.getAxis().isVertical();
            Direction up = vertical ? player.getHorizontalFacing() : Direction.UP;
            Direction right = vertical ? up.rotateY() : side.rotateYCCW();

            BlockPos pos;
            if( getToolRange(stack) == 1 )
                pos = lookingAt.getPos().offset(side, 4);
            else
                pos = lookingAt.getPos().offset(side).offset(right);

            if (world.getLight(pos) <= 7 && world.getBlockState(pos).getMaterial() == Material.AIR)
                world.setBlockState(pos, ModBlocks.MINERSLIGHT.getDefaultState());
        }
    }

    public int getEnergyCost() {
        int cost = 200;

        return cost;
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

    public static List<BlockPos> getMinableBlocks(ItemStack stack, BlockRayTraceResult lookingAt, PlayerEntity player) {
        List<BlockPos> coordinates = new ArrayList<>();
        World world = player.world;

        if (getToolRange(stack) == 1) {
            addCoord(coordinates, lookingAt.getPos(), world);
            return coordinates;
        }

        Direction side = lookingAt.getFace();
        boolean vertical = side.getAxis().isVertical();
        Direction up = vertical ? player.getHorizontalFacing() : Direction.UP;
        Direction down = up.getOpposite();
        Direction right = vertical ? up.rotateY() : side.rotateYCCW();
        Direction left = right.getOpposite();

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

        // Reject fluids and air
        if (!state.getFluidState().isEmpty() || world.isAirBlock(coord))
            return;

        // Rejects any blocks with a hardness less than 0
        if (state.getBlockHardness(world, coord) < 0)
            return;

        // No TE's
        TileEntity te = world.getTileEntity(coord);
        if (te != null && !(te instanceof RenderBlockTileEntity))
            return;

        //Ignore our Miners Light Block
        if (state.getBlock() instanceof MinersLight)
            return;

        coordinates.add(coord);
    }

    public static void applyUpgrade(ItemStack tool, UpgradeCard upgradeCard) {
        if(UpgradeTools.containsUpgrade(tool, upgradeCard.getUpgrade()) )
            return;

        UpgradeTools.setUpgrade(tool, upgradeCard);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity) {
            entityLiving.resetActiveHand();
        }

        if (!(worldIn.isRemote)) {
            BlockRayTraceResult lookingAt = VectorHelper.getLookingAt((PlayerEntity) entityLiving, RayTraceContext.FluidMode.NONE);
            if (lookingAt == null || (worldIn.getBlockState(VectorHelper.getLookingAt((PlayerEntity) entityLiving, stack).getPos()) == Blocks.AIR.getDefaultState()))
                return;

            List<BlockPos> coords = getMinableBlocks(stack, lookingAt, (PlayerEntity) entityLiving);
            for (BlockPos coord : coords) {
                TileEntity te = worldIn.getTileEntity(coord);
                if (te instanceof RenderBlockTileEntity)
                    ((RenderBlockTileEntity) te).markDirtyClient();
            }
        }
    }
}
