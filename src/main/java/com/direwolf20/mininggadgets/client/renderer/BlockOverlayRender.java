package com.direwolf20.mininggadgets.client.renderer;

import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.items.gadget.MiningCollect;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.util.VectorHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import java.awt.*;
import java.util.List;

public class BlockOverlayRender {

    public static void render(RenderWorldLastEvent event, ItemStack item) {
        final Minecraft mc = Minecraft.getInstance();

        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();

        int range = MiningProperties.getBeamRange(item);
        BlockRayTraceResult lookingAt = VectorHelper.getLookingAt(mc.player, RayTraceContext.FluidMode.NONE, range);
        if (mc.world.getBlockState(VectorHelper.getLookingAt(mc.player, item, range).getPos()) == Blocks.AIR.getDefaultState()) {
            return;
        }

        List<BlockPos> coords = MiningCollect.collect(mc.player, lookingAt, mc.world, MiningProperties.getRange(item));
        Vector3d view = mc.gameRenderer.getActiveRenderInfo().getProjectedView();

        MatrixStack matrix = event.getMatrixStack();
        matrix.push();
        matrix.translate(-view.getX(), -view.getY(), -view.getZ());

        IVertexBuilder builder;
        builder = buffer.getBuffer(MyRenderType.BlockOverlay);
        coords.forEach(e -> {
            if (mc.world.getBlockState(e).getBlock() != ModBlocks.RENDER_BLOCK.get()) {

                matrix.push();
                matrix.translate(e.getX(), e.getY(), e.getZ());
                matrix.translate(-0.005f, -0.005f, -0.005f);
                matrix.scale(1.01f, 1.01f, 1.01f);
                matrix.rotate(Vector3f.YP.rotationDegrees(-90.0F));

                Matrix4f positionMatrix = matrix.getLast().getMatrix();
                BlockOverlayRender.render(positionMatrix, builder, e, Color.GREEN);
                matrix.pop();
            }
        });
        matrix.pop();
        RenderSystem.disableDepthTest();
        buffer.finish(MyRenderType.BlockOverlay);
    }

    public static void render(Matrix4f matrix, IVertexBuilder builder, BlockPos pos, Color color) {
        float red = color.getRed() / 255f, green = color.getGreen() / 255f, blue = color.getBlue() / 255f, alpha = .125f;

        float startX = 0, startY = 0, startZ = -1, endX = 1, endY = 1, endZ = 0;

        //down
        builder.pos(matrix, startX, startY, startZ).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, endX, startY, startZ).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, endX, startY, endZ).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, startX, startY, endZ).color(red, green, blue, alpha).endVertex();

        //up
        builder.pos(matrix, startX, endY, startZ).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, startX, endY, endZ).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, endX, endY, endZ).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, endX, endY, startZ).color(red, green, blue, alpha).endVertex();

        //east
        builder.pos(matrix, startX, startY, startZ).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, startX, endY, startZ).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, endX, endY, startZ).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, endX, startY, startZ).color(red, green, blue, alpha).endVertex();

        //west
        builder.pos(matrix, startX, startY, endZ).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, endX, startY, endZ).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, endX, endY, endZ).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, startX, endY, endZ).color(red, green, blue, alpha).endVertex();

        //south
        builder.pos(matrix, endX, startY, startZ).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, endX, endY, startZ).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, endX, endY, endZ).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, endX, startY, endZ).color(red, green, blue, alpha).endVertex();

        //north
        builder.pos(matrix, startX, startY, startZ).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, startX, startY, endZ).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, startX, endY, endZ).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, startX, endY, startZ).color(red, green, blue, alpha).endVertex();
    }
}
