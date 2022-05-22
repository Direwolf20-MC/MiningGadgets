package com.direwolf20.mininggadgets.common.collectors;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

// Provided by LatvianModder with express permission, original code found here https://github.com/FTBTeam/FTB-Ultimine/blob/ea9fc0c812e66c7852e05f6e63ce077bcd48594f/common/src/main/java/com/feed_the_beast/mods/ftbultimine/shape/ShapelessShape.java
public class ShapelessWalker {
    public static final BlockPos[] NEIGHBOR_POSITIONS = new BlockPos[26];

    static {
        NEIGHBOR_POSITIONS[0] = new BlockPos(1, 0, 0);
        NEIGHBOR_POSITIONS[1] = new BlockPos(-1, 0, 0);
        NEIGHBOR_POSITIONS[2] = new BlockPos(0, 0, 1);
        NEIGHBOR_POSITIONS[3] = new BlockPos(0, 0, -1);
        NEIGHBOR_POSITIONS[4] = new BlockPos(0, 1, 0);
        NEIGHBOR_POSITIONS[5] = new BlockPos(0, -1, 0);

        NEIGHBOR_POSITIONS[6] = new BlockPos(1, 0, 1);
        NEIGHBOR_POSITIONS[7] = new BlockPos(1, 0, -1);
        NEIGHBOR_POSITIONS[8] = new BlockPos(-1, 0, 1);
        NEIGHBOR_POSITIONS[9] = new BlockPos(-1, 0, -1);

        NEIGHBOR_POSITIONS[10] = new BlockPos(1, 1, 0);
        NEIGHBOR_POSITIONS[11] = new BlockPos(-1, 1, 0);
        NEIGHBOR_POSITIONS[12] = new BlockPos(0, 1, 1);
        NEIGHBOR_POSITIONS[13] = new BlockPos(0, 1, -1);

        NEIGHBOR_POSITIONS[14] = new BlockPos(1, -1, 0);
        NEIGHBOR_POSITIONS[15] = new BlockPos(-1, -1, 0);
        NEIGHBOR_POSITIONS[16] = new BlockPos(0, -1, 1);
        NEIGHBOR_POSITIONS[17] = new BlockPos(0, -1, -1);

        NEIGHBOR_POSITIONS[18] = new BlockPos(1, 1, 1);
        NEIGHBOR_POSITIONS[19] = new BlockPos(1, 1, -1);
        NEIGHBOR_POSITIONS[20] = new BlockPos(-1, 1, 1);
        NEIGHBOR_POSITIONS[21] = new BlockPos(-1, 1, -1);

        NEIGHBOR_POSITIONS[22] = new BlockPos(1, -1, 1);
        NEIGHBOR_POSITIONS[23] = new BlockPos(1, -1, -1);
        NEIGHBOR_POSITIONS[24] = new BlockPos(-1, -1, 1);
        NEIGHBOR_POSITIONS[25] = new BlockPos(-1, -1, -1);
    }

    public static Set<BlockPos> walk(Level level, BlockPos pos, BlockState findState) {
        HashSet<BlockPos> known = new HashSet<>();
        Deque<BlockPos> openSet = new ArrayDeque<>();
        Set<BlockPos> traversed = new HashSet<>();

        openSet.add(pos);
        traversed.add(pos);

        while (!openSet.isEmpty()) {
            BlockPos ptr = openSet.pop();

            BlockState blockState = level.getBlockState(ptr);
            if ((ptr == pos || blockState.is(findState.getBlock())) && known.add(ptr)) {
                if (known.size() >= 50) {
                    return known;
                }

                for (BlockPos side : NEIGHBOR_POSITIONS) {
                    BlockPos offset = ptr.offset(side);

                    if (traversed.add(offset)) {
                        openSet.add(offset);
                    }
                }
            }
        }

        return known;
    }
}
