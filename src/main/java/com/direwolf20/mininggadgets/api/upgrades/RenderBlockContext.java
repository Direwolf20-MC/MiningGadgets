package com.direwolf20.mininggadgets.api.upgrades;

import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record RenderBlockContext(
        RenderBlockTileEntity entity,
        BlockPos pos,
        Level level,
        ItemStack gadget
) {}
