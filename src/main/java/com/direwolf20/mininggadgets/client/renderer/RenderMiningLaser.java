package com.direwolf20.mininggadgets.client.renderer;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.common.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.gadget.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.gadget.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import static org.lwjgl.opengl.GL11.*;

public class RenderMiningLaser {

    private final static ResourceLocation laserBeam = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/misc/laser.png");
    private final static ResourceLocation laserBeam2 = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/misc/laser2.png");
    private final static ResourceLocation laserBeamGlow = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/misc/laser_glow.png");

    public static void renderLaser(RenderWorldLastEvent event, PlayerEntity player, float ticks) {
        ItemStack stack = MiningGadget.getGadget(player);

        if (!MiningProperties.getCanMine(stack))
            return;

        int range = MiningProperties.getBeamRange(stack);

        Vec3d playerPos = player.getEyePosition(ticks);
        RayTraceResult trace = player.pick(range, 0.0F, false);

        // parse data from item
        float speedModifier = getSpeedModifier(stack);

        drawLasers(event, playerPos, trace, 0, 0, 0, MiningProperties.getColor(stack, MiningProperties.COLOR_RED) / 255f, MiningProperties.getColor(stack, MiningProperties.COLOR_GREEN) / 255f, MiningProperties.getColor(stack, MiningProperties.COLOR_BLUE) / 255f, 0.02f, player, ticks, speedModifier);
    }

    private static float getSpeedModifier(ItemStack stack) {
        if (UpgradeTools.containsUpgrade(stack, Upgrade.EFFICIENCY_1)) {
            double efficiency = UpgradeTools.getUpgradeFromGadget(stack, Upgrade.EFFICIENCY_1).get().getTier() / 5f;
            double speedModifier = MathHelper.lerp(efficiency, 0.02, 0.05);
            return (float) -speedModifier;
        } else {
            return -0.02f;
        }
    }

    private static void drawLasers(RenderWorldLastEvent event, Vec3d from, RayTraceResult trace, double xOffset, double yOffset, double zOffset, float r, float g, float b, float thickness, PlayerEntity player, float ticks, float speedModifier) {
        Hand activeHand;
        if (player.getHeldItemMainhand().getItem() instanceof MiningGadget) {
            activeHand = Hand.MAIN_HAND;
        } else if (player.getHeldItemOffhand().getItem() instanceof MiningGadget) {
            activeHand = Hand.OFF_HAND;
        } else {
            return;
        }

        ItemStack stack = player.getHeldItem(activeHand);

        double distance = from.subtract(trace.getHitVec()).length();
        long gameTime = player.world.getGameTime();
        double v = gameTime * speedModifier;
        float additiveThickness = (thickness * 3.5f) * calculateLaserFlickerModifier(gameTime);
        BufferBuilder wr = Tessellator.getInstance().getBuffer();

        Vec3d view = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();

        MatrixStack matrix = event.getMatrixStack();
        matrix.translate(view.getX(), view.getY(), view.getZ());
        if( trace.getType() == RayTraceResult.Type.MISS )
            matrix.translate(-from.x, -from.y, -from.z);

        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrix.getLast().getMatrix());

        RenderSystem.enableColorMaterial();
        // This makes it so we don't clip into the world, we're effectively drawing on it
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        //This makes it so multiplayer doesn't matter which side the player is standing on to see someone elses laser
        RenderSystem.disableCull();
        RenderSystem.enableTexture();

        RenderSystem.rotatef(MathHelper.lerp(ticks, -player.rotationYaw, -player.prevRotationYaw), 0, 1, 0);
        RenderSystem.rotatef(MathHelper.lerp(ticks, player.rotationPitch, player.prevRotationPitch), 1, 0, 0);

        // additive laser beam
        RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(r, g, b, 0.7f);
        Minecraft.getInstance().getTextureManager().bindTexture(laserBeamGlow);
        drawBeam(xOffset, yOffset, zOffset, additiveThickness, activeHand, distance, wr, 0.5, 1, ticks);

        // main laser, colored part
        RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(r, g, b, 1.0f);
        Minecraft.getInstance().getTextureManager().bindTexture(laserBeam2);
        drawBeam(xOffset, yOffset, zOffset, thickness, activeHand, distance, wr, v, v + distance * 1.5, ticks);
        // white core
        RenderSystem.color4f(MiningProperties.getColor(stack, MiningProperties.COLOR_RED_INNER) / 255f, MiningProperties.getColor(stack, MiningProperties.COLOR_GREEN_INNER) / 255f, MiningProperties.getColor(stack, MiningProperties.COLOR_BLUE_INNER) / 255f, 1.0f);
        Minecraft.getInstance().getTextureManager().bindTexture(laserBeam);
        drawBeam(xOffset, yOffset, zOffset, thickness / 2, activeHand, distance, wr, v, v + distance * 1.5, ticks);

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.popMatrix();
    }

    private static float calculateLaserFlickerModifier(long gameTime) {
        return 0.9f + 0.1f * MathHelper.sin(gameTime * 0.99f) * MathHelper.sin(gameTime * 0.3f) * MathHelper.sin(gameTime * 0.1f);
    }

    private static void drawBeam(double xOffset, double yOffset, double zOffset, float thickness, Hand hand, double distance, BufferBuilder wr, double v1, double v2, float ticks) {
        ClientPlayerEntity player = Minecraft.getInstance().player;

        float startXOffset = -0.25f;
        float startYOffset = -.115f;
        float startZOffset = 0.65f + (1 - player.getFovModifier());

        float f = (MathHelper.lerp(ticks, player.prevRotationPitch, player.rotationPitch) - MathHelper.lerp(ticks, player.prevRenderArmPitch, player.renderArmPitch));
        float f1 = (MathHelper.lerp(ticks, player.prevRotationYaw, player.rotationYaw) - MathHelper.lerp(ticks, player.prevRenderArmYaw, player.renderArmYaw));
        startXOffset = startXOffset + (f1 / 1000);
        startYOffset = startYOffset + (f / 1000);

        // Support for hand sides remembering to take into account of Skin options
        if( Minecraft.getInstance().gameSettings.mainHand != HandSide.RIGHT )
            hand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;

        wr.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        if (hand == Hand.MAIN_HAND) {
            wr.pos(startXOffset, -thickness + startYOffset, startZOffset).tex(1, (float) v1).endVertex();
            wr.pos(xOffset, -thickness + yOffset, distance + zOffset).tex(1, (float) v2).endVertex();
            wr.pos(xOffset, thickness + yOffset, distance + zOffset).tex(0, (float) v2).endVertex();
            wr.pos(startXOffset, thickness + startYOffset, startZOffset).tex(0, (float) v1).endVertex();
        } else {
            startYOffset = -.120f;
            wr.pos(-startXOffset, thickness + startYOffset, startZOffset).tex(0, (float) v1).endVertex();
            wr.pos(xOffset, thickness + yOffset, distance + zOffset).tex(0, (float) v2).endVertex();
            wr.pos(xOffset, -thickness + yOffset, distance + zOffset).tex(1, (float) v2).endVertex();
            wr.pos(-startXOffset, -thickness + startYOffset, startZOffset).tex(1, (float) v1).endVertex();
        }
        Tessellator.getInstance().draw();
    }

}
