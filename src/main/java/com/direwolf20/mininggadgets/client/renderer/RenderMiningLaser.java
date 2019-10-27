package com.direwolf20.mininggadgets.client.renderer;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.util.VectorHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;

import static org.lwjgl.opengl.GL11.GL_QUADS;

public class RenderMiningLaser {

    public static final ResourceLocation laserBeam = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/misc/laser.png");

    public static void renderLaser(PlayerEntity player, float ticks) {
        BlockRayTraceResult lookingAt = VectorHelper.getLookingAt(player, RayTraceContext.FluidMode.NONE);
        Vec3d playerPos = player.getEyePosition(ticks);
        Vec3d lookBlockPos = lookingAt.getHitVec();

        renderBeam(playerPos, lookBlockPos, 0, 0, 0, 1f, 0f, 0f, 0.02f, player, ticks);
    }

    public static void renderBeam(Vec3d from, Vec3d to, double xOffset, double yOffset, double zOffset, float r, float g, float b, float thickness, PlayerEntity player, float ticks) {
        Vec3d playerPos = new Vec3d(TileEntityRendererDispatcher.staticPlayerX, TileEntityRendererDispatcher.staticPlayerY, TileEntityRendererDispatcher.staticPlayerZ);

        double distance = from.subtract(to).length();
        double v = -player.world.getGameTime() * 0.2;

        Minecraft.getInstance().getTextureManager().bindTexture(laserBeam);

        GlStateManager.pushMatrix();
        GlStateManager.enableColorMaterial();
        GlStateManager.color3f(r, g, b);

        GlStateManager.enableTexture();
        GlStateManager.translated(-playerPos.getX(), -playerPos.getY(), -playerPos.getZ());
        GlStateManager.translated(from.x, from.y, from.z);
        GlStateManager.rotatef(MathHelper.lerp(ticks, -player.rotationYaw, -player.prevRotationYaw), 0, 1, 0);
        GlStateManager.rotatef(MathHelper.lerp(ticks, player.rotationPitch, player.prevRotationPitch), 1, 0, 0);
        BufferBuilder wr = Tessellator.getInstance().getBuffer();

        ItemStack heldItem = player.getHeldItemMainhand();

        if (heldItem.getItem() instanceof MiningGadget) {
            double startXOffset = -0.35;
            double startYOffset = -.135;
            double startZOffset = 0.4;
            wr.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            wr.pos(startXOffset, -thickness + startYOffset, startZOffset).tex(1, v).endVertex();
            wr.pos(xOffset, -thickness + yOffset, distance + zOffset).tex(1, v + distance * 1.5).endVertex();
            wr.pos(xOffset, thickness + yOffset, distance + zOffset).tex(0, v + distance * 1.5).endVertex();
            wr.pos(startXOffset, thickness + startYOffset, startZOffset).tex(0, v).endVertex();

            wr.pos(startXOffset, thickness + startYOffset, startZOffset).tex(0, v).endVertex();
            wr.pos(xOffset, thickness + yOffset, distance + zOffset).tex(0, v + distance * 1.5).endVertex();
            wr.pos(xOffset, -thickness + yOffset, distance + zOffset).tex(1, v + distance * 1.5).endVertex();
            wr.pos(startXOffset, -thickness + startYOffset, startZOffset).tex(1, v).endVertex();
            Tessellator.getInstance().draw();
        }
        heldItem = player.getHeldItemOffhand();
        if (heldItem.getItem() instanceof MiningGadget) {
            double startXOffset = 0.35;
            double startYOffset = -.165;
            double startZOffset = 0.5;
            wr.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            wr.pos(startXOffset, thickness + startYOffset, startZOffset).tex(0, v).endVertex();
            wr.pos(xOffset, thickness + yOffset, distance + zOffset).tex(0, v + distance * 1.5).endVertex();
            wr.pos(xOffset, -thickness + yOffset, distance + zOffset).tex(1, v + distance * 1.5).endVertex();
            wr.pos(startXOffset, -thickness + startYOffset, startZOffset).tex(1, v).endVertex();

            wr.pos(startXOffset, -thickness + startYOffset, startZOffset).tex(1, v).endVertex();
            wr.pos(xOffset, -thickness + yOffset, distance + zOffset).tex(1, v + distance * 1.5).endVertex();
            wr.pos(xOffset, thickness + yOffset, distance + zOffset).tex(0, v + distance * 1.5).endVertex();
            wr.pos(startXOffset, thickness + startYOffset, startZOffset).tex(0, v).endVertex();
            Tessellator.getInstance().draw();
        }
        //GlStateManager.enableTexture();
        GlStateManager.popMatrix();
    }

}
