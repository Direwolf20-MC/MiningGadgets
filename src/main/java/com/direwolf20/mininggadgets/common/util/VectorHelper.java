package com.direwolf20.mininggadgets.common.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class VectorHelper {
    public static BlockRayTraceResult getLookingAt(PlayerEntity player, ItemStack tool) {
        return getLookingAt(player, RayTraceContext.FluidMode.NONE);
    }

    public static BlockRayTraceResult getLookingAt(PlayerEntity player, RayTraceContext.FluidMode rayTraceFluid) {
        World world = player.world;

        Vec3d look = player.getLookVec();
        Vec3d start = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);

        double rayTraceRange = 15;
        Vec3d end = new Vec3d(player.posX + look.x * rayTraceRange, player.posY + player.getEyeHeight() + look.y * rayTraceRange, player.posZ + look.z * rayTraceRange);
        RayTraceContext context = new RayTraceContext(start, end, RayTraceContext.BlockMode.OUTLINE, rayTraceFluid, player);
        BlockRayTraceResult result = world.rayTraceBlocks(context);
        return result;
    }

    public static BlockPos getPosLookingAt(PlayerEntity player, ItemStack tool) {
        return VectorHelper.getLookingAt(player, tool).getPos();
    }
}
