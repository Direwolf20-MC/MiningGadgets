package com.direwolf20.mininggadgets.common.util;

import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.gadget.MiningCollect;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

public class BlockOverlayRender {

    public static void render(ItemStack item) {
        final Minecraft mc = Minecraft.getInstance();

        BlockRayTraceResult lookingAt = VectorHelper.getLookingAt(mc.player, RayTraceContext.FluidMode.NONE);
        if (mc.world.getBlockState(VectorHelper.getLookingAt(mc.player, item).getPos()) == Blocks.AIR.getDefaultState()) {
            return;
        }

        List<BlockPos> coords = MiningCollect.collect(mc.player, lookingAt, mc.world, MiningGadget.getToolRange(item));

        Vec3d playerPos = new Vec3d(TileEntityRendererDispatcher.staticPlayerX, TileEntityRendererDispatcher.staticPlayerY, TileEntityRendererDispatcher.staticPlayerZ);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture();

        GlStateManager.translated(-playerPos.getX(), -playerPos.getY(), -playerPos.getZ());
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        coords.forEach(e -> {
            if (mc.world.getBlockState(e).getBlock() != ModBlocks.RENDERBLOCK) {
                GlStateManager.pushMatrix();
                GlStateManager.translatef(e.getX(), e.getY(), e.getZ());
                GlStateManager.translatef(-0.0005f, -0.0005f, -0.0005f);
                GlStateManager.scalef(1.001f, 1.001f, 1.001f);
                GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                BlockOverlayRender.render(e, tessellator, buffer, Color.GREEN);
                GlStateManager.popMatrix();
            }
        });

        GlStateManager.disableBlend();
        GlStateManager.enableTexture();
        GlStateManager.popMatrix();
    }

    public static void render(BlockPos pos, Tessellator tessellator, BufferBuilder buffer, Color color) {
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        double maxX = pos.getX() + 1, maxY = pos.getY() + 1, maxZ = pos.getZ() + 1;
        float red = color.getRed() / 255f, green = color.getGreen() / 255f, blue = color.getBlue() / 255f, alpha = .125f;

        double startX = 0, startY = 0, startZ = -1, endX = 1, endY = 1, endZ = 0;

        buffer.pos(startX, startY, startZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(endX, startY, startZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(endX, startY, endZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(startX, startY, endZ).color(red, green, blue, alpha).endVertex();

        //up
        buffer.pos(startX, endY, startZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(startX, endY, endZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(endX, endY, endZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(endX, endY, startZ).color(red, green, blue, alpha).endVertex();

        //east
        buffer.pos(startX, startY, startZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(startX, endY, startZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(endX, endY, startZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(endX, startY, startZ).color(red, green, blue, alpha).endVertex();

        //west
        buffer.pos(startX, startY, endZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(endX, startY, endZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(endX, endY, endZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(startX, endY, endZ).color(red, green, blue, alpha).endVertex();

        //south
        buffer.pos(endX, startY, startZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(endX, endY, startZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(endX, endY, endZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(endX, startY, endZ).color(red, green, blue, alpha).endVertex();

        //north
        buffer.pos(startX, startY, startZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(startX, startY, endZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(startX, endY, endZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(startX, endY, startZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
    }
}
