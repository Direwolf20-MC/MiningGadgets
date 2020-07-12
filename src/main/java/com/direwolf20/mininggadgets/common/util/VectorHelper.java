package com.direwolf20.mininggadgets.common.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

/**
 * @implNote The main reason behind this is so we have control over the RayTraceContext,
 *           this means that we can use COLLIDER so it traces through non-collidable objects
 */
public class VectorHelper {
    public static BlockRayTraceResult getLookingAt(PlayerEntity player, ItemStack tool, int range) {
        return getLookingAt(player, RayTraceContext.FluidMode.NONE, range);
    }

    public static BlockRayTraceResult getLookingAt(PlayerEntity player, RayTraceContext.FluidMode rayTraceFluid, int range) {
        World world = player.world;

        Vector3d look = player.getLookVec();
        Vector3d start = new Vector3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ());

        Vector3d end = new Vector3d(player.getPosX() + look.x * (double) range, player.getPosY() + player.getEyeHeight() + look.y * (double) range, player.getPosZ() + look.z * (double) range);
        RayTraceContext context = new RayTraceContext(start, end, RayTraceContext.BlockMode.COLLIDER, rayTraceFluid, player);
        return world.rayTraceBlocks(context);
    }
}
