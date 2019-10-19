package com.direwolf20.mininggadgets.common.util;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

public class BlockOverlayRender {
    public static void render(BlockPos pos) {
        System.out.println(pos);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture();

        Tessellator t = Tessellator.getInstance();
        BufferBuilder bufferBuilder = t.getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        double maxX = pos.getX() + 1;
        double maxY = pos.getY() + 1;
        double maxZ = pos.getZ() + 1;

        float red = 1f;
        float green = 0.0f;
        float blue = 0.0f;
        float alpha = 0.33f;

        // Down
        bufferBuilder.pos(pos.getX(), pos.getY(), pos.getZ()).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, pos.getY(), pos.getZ()).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, pos.getY(), maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(pos.getX(), pos.getY(), maxZ).color(red, green, blue, alpha).endVertex();

        // Up
        bufferBuilder.pos(pos.getX(), maxY, pos.getZ()).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(pos.getX(), maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, pos.getZ()).color(red, green, blue, alpha).endVertex();

        // North
        bufferBuilder.pos(pos.getX(), pos.getY(), pos.getZ()).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(pos.getX(), maxY, pos.getZ()).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, pos.getZ()).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, pos.getY(), pos.getZ()).color(red, green, blue, alpha).endVertex();

        // South
        bufferBuilder.pos(pos.getX(), pos.getY(), maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, pos.getY(), maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(pos.getX(), maxY, maxZ).color(red, green, blue, alpha).endVertex();

        // East
        bufferBuilder.pos(maxX, pos.getY(), pos.getZ()).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, pos.getZ()).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, pos.getY(), maxZ).color(red, green, blue, alpha).endVertex();

        // West
        bufferBuilder.pos(pos.getX(), pos.getY(), pos.getZ()).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(pos.getX(), pos.getY(), maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(pos.getX(), maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(pos.getX(), maxY, pos.getZ()).color(red, green, blue, alpha).endVertex();
        t.draw();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture();
    }
}
