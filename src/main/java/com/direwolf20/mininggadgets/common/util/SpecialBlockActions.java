package com.direwolf20.mininggadgets.common.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.HashMap;

/**
 * This class handles the registration of custom block actions (when you break a block), I'm sure more mods will
 * end up having edge cases like this so for now I'm going to put Ice in it and see what else pops up. If it's
 * only ice then I'll remove this class :+1:
 *
 * @implNote The consumer is likely a bit much haha.
 */
public class SpecialBlockActions {
    private static final HashMap<Block, TriConsumer<World, BlockPos, BlockState>> register = new HashMap<>();

    static {
        register.put(Blocks.ICE, (world, pos, state) -> {
            Material material = world.getBlockState(pos.down()).getMaterial();
            if (material.blocksMovement() || material.isLiquid())
                world.setBlockState(pos, Blocks.WATER.getDefaultState());
        });
    }

    public static HashMap<Block, TriConsumer<World, BlockPos, BlockState>> getRegister() {
        return register;
    }
}
