package com.direwolf20.mininggadgets.common.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
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
    private static final HashMap<Block, TriConsumer<Level, BlockPos, BlockState>> register = new HashMap<>();

    static {
        register.put(Blocks.ICE, (world, pos, state) -> {
            Material material = world.getBlockState(pos.below()).getMaterial();
            if (material.blocksMotion() || material.isLiquid())
                world.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
        });
    }

    public static HashMap<Block, TriConsumer<Level, BlockPos, BlockState>> getRegister() {
        return register;
    }
}
