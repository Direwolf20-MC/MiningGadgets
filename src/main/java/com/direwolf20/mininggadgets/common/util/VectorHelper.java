package com.direwolf20.mininggadgets.common.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Todo: note: all of this can be replaced with the vanilla implementation. The vanilla implementation is using an
 *       obfuscated method so an actiontransformer will be needed to make it nice.
 */
public class VectorHelper {
    public static BlockRayTraceResult getLookingAt(PlayerEntity player, ItemStack tool, int range) {
        return getLookingAt(player, RayTraceContext.FluidMode.NONE, range);
    }

    public static BlockRayTraceResult getLookingAt(PlayerEntity player, RayTraceContext.FluidMode rayTraceFluid, int range) {
        World world = player.world;

        Vec3d look = player.getLookVec();
        Vec3d start = new Vec3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ());

        double rayTraceRange = range;
        Vec3d end = new Vec3d(player.getPosX() + look.x * rayTraceRange, player.getPosY() + player.getEyeHeight() + look.y * rayTraceRange, player.getPosZ() + look.z * rayTraceRange);
        RayTraceContext context = new RayTraceContext(start, end, RayTraceContext.BlockMode.COLLIDER, rayTraceFluid, player);
        BlockRayTraceResult result = world.rayTraceBlocks(context);
        return result;
    }

    public static BlockPos getPosLookingAt(PlayerEntity player, ItemStack tool, int range) {
        return VectorHelper.getLookingAt(player, tool, range).getPos();
    }
}
