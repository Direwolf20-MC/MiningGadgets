package com.direwolf20.mininggadgets.common.collectors;

import net.minecraft.core.BlockPos;

import java.util.List;

// Borrowed from FTB Ulitmine by LatvianModder
// original code found here https://github.com/FTBTeam/FTB-Ultimine/blob/c8169d1f703beeb11a2dfb41be2e5b10114c207e/common/src/main/java/dev/ftb/mods/ftbultimine/shape/Shape.java
/**
 * @author LatvianModder
 */
public interface Shape {
    String getName();

    List<BlockPos> getBlocks(ShapeContext context);

    default BlockMatcher getTagMatcher() {
        return BlockMatcher.TAGS_MATCH_SHAPED;
    }
}
