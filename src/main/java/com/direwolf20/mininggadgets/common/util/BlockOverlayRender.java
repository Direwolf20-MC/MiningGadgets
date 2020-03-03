package com.direwolf20.mininggadgets.common.util;

import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.gadget.MiningCollect;
import com.direwolf20.mininggadgets.common.gadget.MiningProperties;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

public class BlockOverlayRender {

    public static void render(RenderWorldLastEvent event, ItemStack item) {
        final Minecraft mc = Minecraft.getInstance();

        int range = MiningProperties.getBeamRange(item);
        BlockRayTraceResult lookingAt = VectorHelper.getLookingAt(mc.player, RayTraceContext.FluidMode.NONE, range);
        if (mc.world.getBlockState(VectorHelper.getLookingAt(mc.player, item, range).getPos()) == Blocks.AIR.getDefaultState()) {
            return;
        }

        List<BlockPos> coords = MiningCollect.collect(mc.player, lookingAt, mc.world, MiningProperties.getRange(item));
        Vec3d view = mc.gameRenderer.getActiveRenderInfo().getProjectedView();

        MatrixStack stack = event.getMatrixStack();
        stack.push();
        stack.translate(-view.getX(), -view.getY(), -view.getZ());

        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(stack.getLast().getMatrix());

        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        coords.forEach(e -> {
            if (mc.world.getBlockState(e).getBlock() != ModBlocks.RENDER_BLOCK.get()) {
                RenderSystem.pushMatrix();

                RenderSystem.translatef(e.getX(), e.getY(), e.getZ());
                RenderSystem.translatef(-0.005f, -0.005f, -0.005f);
                RenderSystem.scalef(1.01f, 1.01f, 1.01f);
                RenderSystem.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
//              Removed as we'd have to build the tool and check all the upgrades etc on a single tick. Todo: cache most of it?
//                if ( UpgradeTools.containsActiveUpgrade(item, Upgrade.VOID_JUNK) ) {
//
//                    BlockOverlayRender.render(e, tessellator, buffer, Color.RED);
//                }
//                else
                BlockOverlayRender.render(e, tessellator, buffer, Color.GREEN);
                RenderSystem.popMatrix();
            }
        });

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
        stack.pop();
    }

    public static void render(BlockPos pos, Tessellator tessellator, BufferBuilder buffer, Color color) {
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

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
