package com.direwolf20.mininggadgets.api.upgrades;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public record GadgetUseContext(
   Level level,
   BlockState state,
   BlockPos pos,
   ItemStack gadget,
   Player player,
   boolean isClientSide
) {}
