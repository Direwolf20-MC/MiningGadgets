package com.direwolf20.mininggadgets.common.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

/**
 * @implNote The main reason behind this is so we have control over the RayTraceContext,
 *           this means that we can use COLLIDER so it traces through non-collidable objects
 */
public class VectorHelper {
    public static BlockHitResult getLookingAt(Player player, ItemStack tool, int range) {
        return getLookingAt(player, ClipContext.Fluid.NONE, range);
    }

    public static BlockHitResult getLookingAt(Player player, ClipContext.Fluid rayTraceFluid, int range) {
        Level world = player.level;

        Vec3 look = player.getLookAngle();
        Vec3 start = new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());

        Vec3 end = new Vec3(player.getX() + look.x * (double) range, player.getY() + player.getEyeHeight() + look.y * (double) range, player.getZ() + look.z * (double) range);
        ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, rayTraceFluid, player);
        return world.clip(context);
    }
}
