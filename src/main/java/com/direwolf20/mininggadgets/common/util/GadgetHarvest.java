package com.direwolf20.mininggadgets.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

/**
 * Helpers for what happens to a block's drops once the gadget has mined it.
 */
public final class GadgetHarvest {
    private GadgetHarvest() {}

    /**
     * Magnet pickup done the vanilla way: spawn the item and let the player touch it straight away. Unlike adding it to
     * the inventory directly, this fires the item pickup events (Pre and Post, used by quest/stat mods), plays the
     * pickup animation and sound, and sends the take-item packet that pickup notification mods listen for. Whatever does
     * not fit stays on the ground as a normal item entity.
     */
    public static void pickUp(ServerLevel level, BlockPos pos, ItemStack drop, Player player) {
        if (player.isRemoved()) {
            Block.popResource(level, pos, drop);
            return;
        }
        ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop, 0, 0, 0);
        entity.setNoPickUpDelay();
        if (level.addFreshEntity(entity) && entity.isAlive()) {
            entity.playerTouch(player);
        }
    }

    /**
     * Magnet XP done the vanilla way: experience orbs at the player's feet, collected on the next player tick. This keeps
     * the XP pickup event, sound, Mending repair and pickup notifications that giving the points directly skipped.
     */
    public static void pickUpXp(ServerLevel level, BlockPos pos, int exp, Player player) {
        if (exp <= 0) {
            return;
        }
        ExperienceOrb.award(level, player.isRemoved() ? Vec3.atCenterOf(pos) : player.position(), exp);
    }
}
