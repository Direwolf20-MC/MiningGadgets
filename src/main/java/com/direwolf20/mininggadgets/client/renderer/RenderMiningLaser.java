package com.direwolf20.mininggadgets.client.renderer;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.common.gadget.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.gadget.upgrade.UpgradeTools;
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
import org.lwjgl.opengl.GL14;
import org.lwjgl.system.MathUtil;

import static org.lwjgl.opengl.GL11.*;

public class RenderMiningLaser {

    public final static ResourceLocation laserBeam = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/misc/laser.png");
    public final static ResourceLocation laserBeam2 = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/misc/laser2.png");
    public final static ResourceLocation laserBeamGlow = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/misc/laser_glow.png");

    public static void renderLaser(PlayerEntity player, float ticks) {
        ItemStack stack = player.getHeldItemMainhand();
        if (!(stack.getItem() instanceof MiningGadget))
            stack = player.getHeldItemOffhand();
        int range = MiningGadget.getBeamRange(stack);
        BlockRayTraceResult lookingAt = VectorHelper.getLookingAt(player, RayTraceContext.FluidMode.NONE, range);
        Vec3d playerPos = player.getEyePosition(ticks);
        Vec3d lookBlockPos = lookingAt.getHitVec();

        renderBeam(playerPos, lookBlockPos, 0, 0, 0, 1f, 0f, 0f, 0.02f, player, ticks);
    }

    public static void drawLaser(Vec3d from, Vec3d to, double xOffset, double yOffset, double zOffset, float r, float g, float b, float thickness, PlayerEntity player, float ticks, int src, int dest) {
        ItemStack stack = player.getHeldItemMainhand();
        if (!(stack.getItem() instanceof MiningGadget))
            stack = player.getHeldItemOffhand();

        Vec3d playerPos = new Vec3d(TileEntityRendererDispatcher.staticPlayerX, TileEntityRendererDispatcher.staticPlayerY, TileEntityRendererDispatcher.staticPlayerZ);
        double distance = from.subtract(to).length();
        double v = -player.world.getGameTime();
        if (UpgradeTools.containsUpgrade(stack, Upgrade.EFFICIENCY_1)) {
            double efficiency = UpgradeTools.getUpgradeFromGadget(stack, Upgrade.EFFICIENCY_1).get().getTier() / 5;
            double speedModifier = MathHelper.lerp(efficiency, 0.02, 0.05);
            v = v * speedModifier;
        } else {
            v = v * 0.02;
        }
        BufferBuilder wr = Tessellator.getInstance().getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.disableDepthTest();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(src, dest);
        GlStateManager.color3f(r, g, b);
        GlStateManager.disableCull();  //This makes it so multiplayer doesn't matter which side the player is standing on to see someone elses laser
        GlStateManager.enableTexture();
        GlStateManager.translated(-playerPos.getX(), -playerPos.getY(), -playerPos.getZ());
        GlStateManager.translated(from.x, from.y, from.z);
        GlStateManager.rotatef(MathHelper.lerp(ticks, -player.rotationYaw, -player.prevRotationYaw), 0, 1, 0);
        GlStateManager.rotatef(MathHelper.lerp(ticks, player.rotationPitch, player.prevRotationPitch), 1, 0, 0);
        ItemStack heldItem = player.getHeldItemMainhand();

        if (heldItem.getItem() instanceof MiningGadget) {
            double startXOffset = -0.35;
            double startYOffset = -.135;
            double startZOffset = 0.5;
            wr.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            wr.pos(startXOffset, -thickness + startYOffset, startZOffset).tex(1, v).endVertex();
            wr.pos(xOffset, -thickness + yOffset, distance + zOffset).tex(1, v + distance * 1.5).endVertex();
            wr.pos(xOffset, thickness + yOffset, distance + zOffset).tex(0, v + distance * 1.5).endVertex();
            wr.pos(startXOffset, thickness + startYOffset, startZOffset).tex(0, v).endVertex();
            //In theory no longer needed but leaving here for a version or 2 just in case....
            /*wr.pos(startXOffset, thickness + startYOffset, startZOffset).tex(0, 1).endVertex();
            wr.pos(xOffset, thickness + yOffset, distance + zOffset).tex(0, 1).endVertex();
            wr.pos(xOffset, -thickness + yOffset, distance + zOffset).tex(1, 0).endVertex();
            wr.pos(startXOffset, -thickness + startYOffset, startZOffset).tex(1, 0).endVertex();*/
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
            //In theory no longer needed but leaving here for a version or 2 just in case....
            /*wr.pos(startXOffset, -thickness + startYOffset, startZOffset).tex(1, v).endVertex();
            wr.pos(xOffset, -thickness + yOffset, distance + zOffset).tex(1, v + distance * 1.5).endVertex();
            wr.pos(xOffset, thickness + yOffset, distance + zOffset).tex(0, v + distance * 1.5).endVertex();
            wr.pos(startXOffset, thickness + startYOffset, startZOffset).tex(0, v).endVertex();*/
            Tessellator.getInstance().draw();
        }
        GlStateManager.enableDepthTest();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    public static void drawLaserAdditively(Vec3d from, Vec3d to, double xOffset, double yOffset, double zOffset, float r, float g, float b, float thickness, PlayerEntity player, float ticks, int src, int dest) {
        ItemStack stack = player.getHeldItemMainhand();
        if (!(stack.getItem() instanceof MiningGadget))
            stack = player.getHeldItemOffhand();

        Vec3d playerPos = new Vec3d(TileEntityRendererDispatcher.staticPlayerX, TileEntityRendererDispatcher.staticPlayerY, TileEntityRendererDispatcher.staticPlayerZ);
        double distance = from.subtract(to).length();
        long gameTime = player.world.getGameTime();
        double v = -gameTime;
        if (UpgradeTools.containsUpgrade(stack, Upgrade.EFFICIENCY_1)) {
            double efficiency = UpgradeTools.getUpgradeFromGadget(stack, Upgrade.EFFICIENCY_1).get().getTier() / 5;
            double speedModifier = MathHelper.lerp(efficiency, 0.02, 0.05);
            v = v * speedModifier;
        } else {
            v = v * 0.02;
        }
        BufferBuilder wr = Tessellator.getInstance().getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.disableDepthTest();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color4f(r, g, b, 0.7f);
        GlStateManager.disableCull();  //This makes it so multiplayer doesn't matter which side the player is standing on to see someone elses laser
        GlStateManager.enableTexture();
        GlStateManager.translated(-playerPos.getX(), -playerPos.getY(), -playerPos.getZ());
        GlStateManager.translated(from.x, from.y, from.z);
        //GlStateManager.scaled(5f, 5f, 5f);
        GlStateManager.rotatef(MathHelper.lerp(ticks, -player.rotationYaw, -player.prevRotationYaw), 0, 1, 0);
        GlStateManager.rotatef(MathHelper.lerp(ticks, player.rotationPitch, player.prevRotationPitch), 1, 0, 0);
        thickness *= 0.9f + 0.1f * MathHelper.sin(gameTime *0.99f) * MathHelper.sin(gameTime *0.3f) * MathHelper.sin(gameTime*0.1f);
        ItemStack heldItem = player.getHeldItemMainhand();

        if (heldItem.getItem() instanceof MiningGadget) {
            double startXOffset = -0.35;
            double startYOffset = -.135;
            double startZOffset = 0.5;
            wr.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            wr.pos(startXOffset, -thickness + startYOffset, startZOffset).tex(1, 0.5).endVertex();
            wr.pos(xOffset, -thickness + yOffset, distance + zOffset).tex(1, 1).endVertex();
            wr.pos(xOffset, thickness + yOffset, distance + zOffset).tex(0, 1).endVertex();
            wr.pos(startXOffset, thickness + startYOffset, startZOffset).tex(0, 0.5).endVertex();
            //In theory no longer needed but leaving here for a version or 2 just in case....
            /*wr.pos(startXOffset, thickness + startYOffset, startZOffset).tex(0, 1).endVertex();
            wr.pos(xOffset, thickness + yOffset, distance + zOffset).tex(0, 1).endVertex();
            wr.pos(xOffset, -thickness + yOffset, distance + zOffset).tex(1, 0).endVertex();
            wr.pos(startXOffset, -thickness + startYOffset, startZOffset).tex(1, 0).endVertex();*/
            Tessellator.getInstance().draw();
        }
        heldItem = player.getHeldItemOffhand();
        if (heldItem.getItem() instanceof MiningGadget) {
            double startXOffset = 0.35;
            double startYOffset = -.165;
            double startZOffset = 0.5;
            wr.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            wr.pos(startXOffset, thickness + startYOffset, startZOffset).tex(0, 0.5).endVertex();
            wr.pos(xOffset, thickness + yOffset, distance + zOffset).tex(0, 1).endVertex();
            wr.pos(xOffset, -thickness + yOffset, distance + zOffset).tex(1, 1).endVertex();
            wr.pos(startXOffset, -thickness + startYOffset, startZOffset).tex(1, 0.5).endVertex();
            //In theory no longer needed but leaving here for a version or 2 just in case....
            /*wr.pos(startXOffset, -thickness + startYOffset, startZOffset).tex(1, v).endVertex();
            wr.pos(xOffset, -thickness + yOffset, distance + zOffset).tex(1, v + distance * 1.5).endVertex();
            wr.pos(xOffset, thickness + yOffset, distance + zOffset).tex(0, v + distance * 1.5).endVertex();
            wr.pos(startXOffset, thickness + startYOffset, startZOffset).tex(0, v).endVertex();*/
            Tessellator.getInstance().draw();
        }
        GlStateManager.enableCull();
        GlStateManager.blendFunc(src, dest);
        GlStateManager.disableBlend();
        GlStateManager.enableDepthTest();
        GlStateManager.popMatrix();
    }

    public static void renderBeam(Vec3d from, Vec3d to, double xOffset, double yOffset, double zOffset, float r, float g, float b, float thickness, PlayerEntity player, float ticks) {
        Minecraft.getInstance().getTextureManager().bindTexture(laserBeamGlow);
        drawLaserAdditively(from, to, xOffset, yOffset, zOffset, r, g, b, thickness * 3.5f, player, ticks, GL14.GL_SRC_ALPHA, GL14.GL_ONE_MINUS_SRC_ALPHA);
        Minecraft.getInstance().getTextureManager().bindTexture(laserBeam2);
        drawLaser(from, to, xOffset, yOffset, zOffset, r, g, b, thickness, player, ticks, GL14.GL_SRC_ALPHA, GL14.GL_ONE_MINUS_SRC_ALPHA);
        Minecraft.getInstance().getTextureManager().bindTexture(laserBeam);
        drawLaser(from, to, xOffset, yOffset, zOffset, 1, 1, 1, thickness / 2, player, ticks, GL14.GL_SRC_ALPHA, GL14.GL_ONE_MINUS_SRC_ALPHA);
    }

}
