package com.direwolf20.mininggadgets.common.util;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class BlockOverlayRender {
    public static void render(BlockPos pos, Tessellator tessellator, BufferBuilder buffer, Color color) {
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        double maxX = pos.getX() + 1, maxY = pos.getY() + 1, maxZ = pos.getZ() + 1;
        float red = color.getRed() / 255f, green = color.getGreen()/ 255f, blue = color.getBlue()/ 255f, alpha = .2f;

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
