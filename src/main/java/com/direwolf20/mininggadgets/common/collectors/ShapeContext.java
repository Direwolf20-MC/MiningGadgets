package com.direwolf20.mininggadgets.common.collectors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

// Borrowed from FTB Ulitmine by LatvianModder
// original code found here https://github.com/FTBTeam/FTB-Ultimine/blob/c8169d1f703beeb11a2dfb41be2e5b10114c207e/common/src/main/java/dev/ftb/mods/ftbultimine/shape/ShapeContext.java
/**
 * @author LatvianModder
 */
public record ShapeContext(Level level, BlockPos pos, Direction face, BlockState original, BlockMatcher matcher, int maxBlocks) {
    public boolean check(BlockState state) {
        return matcher.actualCheck(original, state);
    }

    public BlockState block(BlockPos pos) {
        return level.getBlockState(pos);
    }

    public boolean check(BlockPos pos) {
        return check(block(pos));
    }
}