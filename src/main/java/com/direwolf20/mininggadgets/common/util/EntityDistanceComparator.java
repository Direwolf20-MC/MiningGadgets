package com.direwolf20.mininggadgets.common.util;

import net.minecraft.core.BlockPos;

import java.util.Comparator;

// Borrowed from FTB Ulitmine by LatvianModder
// original code found here https://github.com/FTBTeam/FTB-Ultimine/blob/c8169d1f703beeb11a2dfb41be2e5b10114c207e/common/src/main/java/dev/ftb/mods/ftbultimine/EntityDistanceComparator.java

/**
 * @author LatvianModder
 */
public class EntityDistanceComparator implements Comparator<BlockPos> {
    private final int x, y, z;

    public EntityDistanceComparator(BlockPos p) {
        x = p.getX();
        y = p.getY();
        z = p.getZ();
    }

    private int distSq(BlockPos p) {
        int dx = (p.getX() - x);
        int dy = (p.getY() - y);
        int dz = (p.getZ() - z);
        return dx * dx + dy * dy + dz * dz;
    }

    @Override
    public int compare(BlockPos o1, BlockPos o2) {
        return distSq(o1) - distSq(o2);
    }
}