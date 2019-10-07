package com.direwolf20.mininggadgets.client.renderer;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.util.VectorHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;

import static org.lwjgl.opengl.GL11.GL_QUADS;

public class RenderMiningLaser {

    public static void renderLaser(PlayerEntity player, float ticks) {
        //Vec3d pos =new Vec3d(TileEntityRendererDispatcher.staticPlayerX, TileEntityRendererDispatcher.staticPlayerY, TileEntityRendererDispatcher.staticPlayerZ);
        //Vec3d look = player.getLook(ticks);
        BlockRayTraceResult lookingAt = VectorHelper.getLookingAt(player, RayTraceContext.FluidMode.NONE);
        Vec3d pos = player.getEyePosition(ticks);
        Vec3d blockpos = lookingAt.getHitVec();

        renderBeam(pos.getX(), pos.getY(), pos.getZ(), blockpos.getX(), blockpos.getY(), blockpos.getZ(), new Vec3d(-0.1, -0.1, 0.15), 1f, 0f, 0f, 0.01f, player);
        //renderBeam(1, 72, -6, blockpos.getX(), blockpos.getY(), blockpos.getZ(), new Vec3d(-0.1, -0.1, 0.15), 1f, 0f, 0f, 0.01f, player);
    }

    public static void renderBeam(double startX, double startY, double startZ, double endX, double endY, double endZ, Vec3d offset, float r, float g, float b, float thickness, PlayerEntity player) {
        //BlockPos playerPos = player.getPosition();
        Vec3d playerPos = new Vec3d(TileEntityRendererDispatcher.staticPlayerX, TileEntityRendererDispatcher.staticPlayerY, TileEntityRendererDispatcher.staticPlayerZ);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        /*GlStateManager.pushMatrix();

        GlStateManager.translated(-playerPos.getX(), -playerPos.getY(), -playerPos.getZ());//The render starts at the player, so we subtract the player coords and move the render to 0,0,0

        GlStateManager.disableLighting();
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        GlStateManager.lineWidth(2.0F);
        bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(startX+0.1, startY-0.1, startZ-0.15).color(r,g,b, 1.0F).endVertex();
        bufferbuilder.pos(endX, endY, endZ).color(r, g, b, 1.0F).endVertex();
        tessellator.draw();
        GlStateManager.lineWidth(1.0F);
        GlStateManager.lineWidth(1.0F);
        GlStateManager.enableLighting();
        GlStateManager.enableTexture();
        GlStateManager.enableDepthTest();
        GlStateManager.depthMask(true);

        GlStateManager.popMatrix();*/
        Vec3d from = new Vec3d(startX, startY, startZ);
        Vec3d to = new Vec3d(endX, endY, endZ);
        double distance = from.subtract(to).length();
        double v = -player.world.getGameTime() * 0.2;
        //Vec3d playerPos = player.getPositionVec();

        GlStateManager.color3f(r, g, b);
        GlStateManager.pushMatrix();
        GlStateManager.translated(-playerPos.getX(), -playerPos.getY(), -playerPos.getZ());
        GlStateManager.translated(from.x, from.y, from.z);
        GlStateManager.rotatef(-player.getRotationYawHead(), 0, 1, 0);
        GlStateManager.rotatef(player.rotationPitch, 1, 0, 0);
        //GlStateManager.translated(offset.x, offset.y, offset.z);
        BufferBuilder wr = Tessellator.getInstance().getBuffer();

        ItemStack heldItem = player.getHeldItemMainhand();


        if (heldItem.getItem() instanceof MiningGadget) {
            double startYOffset = -.25;
            double startXOffset = -0.35;
            wr.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            /*wr.pos(thickness+startXOffset, startYOffset, 0).tex(0, v).endVertex();
            wr.pos(thickness, 0, distance).tex(0, v + distance * 1.5).endVertex();
            wr.pos(-thickness, 0, distance).tex(1, v + distance * 1.5).endVertex();
            wr.pos(-thickness+startXOffset, startYOffset, 0).tex(1, v).endVertex();*/

            wr.pos(startXOffset, thickness + startYOffset, 0).tex(0, v).endVertex();
            wr.pos(0, thickness, distance).tex(0, v + distance * 1.5).endVertex();
            wr.pos(0, -thickness, distance).tex(1, v + distance * 1.5).endVertex();
            wr.pos(startXOffset, -thickness + startYOffset, 0).tex(1, v).endVertex();
            Tessellator.getInstance().draw();
        }
        heldItem = player.getHeldItemOffhand();
        if (heldItem.getItem() instanceof MiningGadget) {
            double startYOffset = -0.25;
            double startXOffset = 0.35;
            wr.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            /*wr.pos(thickness, startYOffset, 0).tex(0, v).endVertex();
            wr.pos(thickness, 0, distance).tex(0, v + distance * 1.5).endVertex();
            wr.pos(-thickness, 0, distance).tex(1, v + distance * 1.5).endVertex();
            wr.pos(-thickness, startYOffset, 0).tex(1, v).endVertex();*/
            wr.pos(startXOffset, -thickness + startYOffset, 0).tex(1, v).endVertex();
            wr.pos(0, -thickness, distance).tex(1, v + distance * 1.5).endVertex();
            wr.pos(0, thickness, distance).tex(0, v + distance * 1.5).endVertex();
            wr.pos(startXOffset, thickness + startYOffset, 0).tex(0, v).endVertex();
            Tessellator.getInstance().draw();
        }

        GlStateManager.popMatrix();
    }

}
