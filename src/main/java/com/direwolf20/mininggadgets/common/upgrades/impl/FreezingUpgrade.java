package com.direwolf20.mininggadgets.common.upgrades.impl;

import com.direwolf20.mininggadgets.api.upgrades.*;
import com.direwolf20.mininggadgets.client.particles.playerparticle.PlayerParticleData;
import com.direwolf20.mininggadgets.common.Config;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.util.VectorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class FreezingUpgrade extends MinerUpgrade implements GadgetHooks, RenderBlockHooks {
    private Supplier<UpgradeItem> item;

    public FreezingUpgrade(ResourceLocation id, Supplier<UpgradeItem> item) {
        super(id);
        this.item = item;
    }

    @Override
    public UpgradeItem item() {
        return item.get();
    }

    @Override
    public int costPerOperation() {
        return 0;
    }

    /**
     * @param count The amount of time in tick the item has been used for continuously
     */
    @Override
    public void usingTick(GadgetUseContext context, List<BlockPos> targetLocations, int count) {
        Player player = context.player();
        Level level = context.level();
        for (BlockPos sourcePos : findSources(level, targetLocations)) {
            int delay = MiningProperties.getFreezeDelay(context.gadget());
            if (delay == 0 || count % delay == 0)
                spawnFreezeParticle(player, sourcePos, level, context.gadget());
        }
    }

    @Override
    public void afterBlockRemoved(RenderBlockContext context) {
        ItemStack gadget = context.gadget();
        int freezeCost = Config.UPGRADECOST_FREEZE.get() * -1;
        int energy = gadget.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);

        if (energy == 0) {
            return;
        }

        Level level = context.level();
        for (Direction side : Direction.values()) {
            BlockPos sidePos = context.pos().relative(side);
            BlockState blockState = level.getBlockState(sidePos);
            FluidState state = blockState.getFluidState();

            if (blockState.is(Blocks.LAVA) && state.getType().isSame(Fluids.LAVA) && state.getType().isSource(state)) {
                energy -= this.replaceBlockWithAlternative(level, sidePos, Blocks.OBSIDIAN.defaultBlockState(), gadget, freezeCost, energy);
            } else if (blockState.is(Blocks.WATER) && state.getType().isSame(Fluids.WATER) && state.getType().isSource(state)) {
                energy -= this.replaceBlockWithAlternative(level, sidePos, Blocks.PACKED_ICE.defaultBlockState(), gadget, freezeCost, energy);
            } else if ((blockState.is(Blocks.WATER) || blockState.is(Blocks.LAVA)) && (state.getType().isSame(Fluids.WATER) || state.getType().isSame(Fluids.LAVA)) && !state.getType().isSource(state)) {
                energy -= this.replaceBlockWithAlternative(level, sidePos, Blocks.COBBLESTONE.defaultBlockState(), gadget, freezeCost, energy);
            }
        }
    }

    // TODO: modify the energy of the gadget directly
    private int replaceBlockWithAlternative(Level world, BlockPos pos, BlockState state, ItemStack stack, int costOfOperation, int remainingEnergy) {
        if (remainingEnergy < costOfOperation) {
            return 0;
        }

        stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> e.receiveEnergy(costOfOperation, false));

        // If the block is just water logged, remove the fluid
        BlockState blockState = world.getBlockState(pos);
        if (blockState.hasProperty(BlockStateProperties.WATERLOGGED) && blockState.getValue(BlockStateProperties.WATERLOGGED) && world.getBlockEntity(pos) == null) {
            world.setBlockAndUpdate(pos, blockState.setValue(BlockStateProperties.WATERLOGGED, false));
            return costOfOperation;
        }

        world.setBlockAndUpdate(pos, state);
        return costOfOperation;
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
        Random rand = world.random;

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
}
