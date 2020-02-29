package com.direwolf20.mininggadgets.client.renderer;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.common.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.gadget.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.gadget.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import static org.lwjgl.opengl.GL11.*;

public class RenderMiningLaser2 {

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

    private static void blueLine(IVertexBuilder builder, Matrix4f positionMatrix, Matrix3f matrixNormalIn, BlockPos pos, float dx1, float dy1, float dz1, float dx2, float dy2, float dz2) {
        Vector3f vector3f = new Vector3f(0.0f, 1.0f, 0.0f);
        vector3f.transform(matrixNormalIn);
        Vector4f vector4f = new Vector4f(pos.getX()+dx1, pos.getY()+dy1, pos.getZ()+dz1, 1.0F);
        vector4f.transform(positionMatrix);
        Vector4f vector4f2 = new Vector4f(pos.getX()+dx2, pos.getY()+dy2, pos.getZ()+dz2, 1.0F);
        vector4f2.transform(positionMatrix);

        builder.addVertex((float)vector4f.getX(), (float)vector4f.getY(), (float)vector4f.getZ(), 1.0f, 0.0f, 0.0f, 1.0f, 1f, 0.5f, OverlayTexture.NO_OVERLAY, 15728880, vector3f.getX(), vector3f.getY(), vector3f.getZ());
        builder.addVertex((float)vector4f2.getX(), (float)vector4f2.getY(), (float)vector4f2.getZ(), 1.0f, 0.0f, 0.0f, 1.0f, 1f, 0.5f, OverlayTexture.NO_OVERLAY, 15728880, vector3f.getX(), vector3f.getY(), vector3f.getZ());
        builder.addVertex((float)vector4f.getX(), (float)vector4f.getY()+1, (float)vector4f.getZ(), 1.0f, 0.0f, 0.0f, 1.0f, 1f, 0.5f, OverlayTexture.NO_OVERLAY, 15728880, vector3f.getX(), vector3f.getY(), vector3f.getZ());
        builder.addVertex((float)vector4f2.getX(), (float)vector4f2.getY()+1, (float)vector4f2.getZ(), 1.0f, 0.0f, 0.0f, 1.0f, 1f, 0.5f, OverlayTexture.NO_OVERLAY, 15728880, vector3f.getX(), vector3f.getY(), vector3f.getZ());

        /*builder.pos(positionMatrix, pos.getX()+dx1, pos.getY()+dy1, pos.getZ()+dz1)
                .color(0.0f, 0.0f, 1.0f, 1.0f)
                .tex(1, (float) 0.5)
                .lightmap(15728880)
                .overlay(OverlayTexture.NO_OVERLAY)
                .normal(matrixNormalIn, 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder.pos(positionMatrix, pos.getX()+dx2, pos.getY()+dy2, pos.getZ()+dz2)
                .color(0.0f, 0.0f, 1.0f, 1.0f)
                .tex(1, (float) 0.5)
                .lightmap(15728880)
                .overlay(OverlayTexture.NO_OVERLAY)
                .normal(matrixNormalIn, 0.0F, 1.0F, 0.0F)
                .endVertex();*/
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

        Vec3d view = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        IVertexBuilder builder = buffer.getBuffer(RenderType.getBeaconBeam(laserBeam2,false));
        MatrixStack matrix = event.getMatrixStack();

        matrix.push();
        //matrix.translate(-view.getX(), -view.getY(), -view.getZ());
        //matrix.translate(from.x, from.y, from.z);
        //matrix.rotate(new Quaternion(MathHelper.lerp(ticks, -player.rotationYaw, -player.prevRotationYaw), 0, 1, 0));
        //matrix.rotate(new Quaternion(MathHelper.lerp(ticks, player.rotationPitch, player.prevRotationPitch), 1, 0, 0));


        matrix.rotate(Vector3f.YP.rotationDegrees(MathHelper.lerp(ticks, -player.rotationYaw, -player.prevRotationYaw)));
        matrix.rotate(Vector3f.XP.rotationDegrees(MathHelper.lerp(ticks, player.rotationPitch, player.prevRotationPitch)));
        //matrix.rotate(Vector3f.YP.rotationDegrees(85f));
        //matrix.rotate(Vector3f.XP.rotationDegrees(0.6f));
        MatrixStack.Entry matrixstack$entry = matrix.getLast();
        Matrix3f matrixNormal = matrixstack$entry.getNormal();
        Matrix4f positionMatrix = matrixstack$entry.getMatrix();

        BlockPos pos = new BlockPos(0,0,0);

        //blueLine(builder, positionMatrix, matrixNormal,  pos, 1, 0, 0, 2, 0, 1);
        drawBeam(builder, positionMatrix, matrixNormal, thickness, activeHand, distance, 0.5, 1, ticks, r,b,g,1f);
        matrix.pop();
        //RenderSystem.disableDepthTest();
        buffer.finish();
        /*RenderSystem.pushMatrix();
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
        RenderSystem.popMatrix();*/
    }

    private static float calculateLaserFlickerModifier(long gameTime) {
        return 0.9f + 0.1f * MathHelper.sin(gameTime * 0.99f) * MathHelper.sin(gameTime * 0.3f) * MathHelper.sin(gameTime * 0.1f);
    }

    private static void drawBeam(IVertexBuilder builder, Matrix4f positionMatrix, Matrix3f matrixNormalIn, float thickness, Hand hand, double distance, double v1, double v2, float ticks, float r, float g, float b, float alpha) {
        //Vector3f vector3f = new Vector3f(0.0f, 1.0f, 0.0f);
        //vector3f.transform(matrixNormalIn);
        ClientPlayerEntity player = Minecraft.getInstance().player;

        float startXOffset = 0.25f;
        float startYOffset = -.115f;
        float startZOffset = 0.65f;// + (1 - player.getFovModifier());

        float f = (MathHelper.lerp(ticks, player.prevRotationPitch, player.rotationPitch) - MathHelper.lerp(ticks, player.prevRenderArmPitch, player.renderArmPitch));
        float f1 = (MathHelper.lerp(ticks, player.prevRotationYaw, player.rotationYaw) - MathHelper.lerp(ticks, player.prevRenderArmYaw, player.renderArmYaw));
        startXOffset = startXOffset + (f1 / 100000000);
        startYOffset = startYOffset + (f / 100000000);

        // Support for hand sides remembering to take into account of Skin options
        if( Minecraft.getInstance().gameSettings.mainHand != HandSide.RIGHT )
            hand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;

        /*Vector4f vec1 = new Vector4f(1, 0, 0, 1.0F);
        vec1.transform(positionMatrix);
        Vector4f vec2 = new Vector4f(2, 0, 1, 1.0F);
        vec2.transform(positionMatrix);
        Vector4f vec3 = new Vector4f(1, 1, 0, 1.0F);
        vec3.transform(positionMatrix);
        Vector4f vec4 = new Vector4f(2, 2, 1, 1.0F);
        vec4.transform(positionMatrix);*/
        //Vector4f vec1 = new Vector4f(startXOffset, -thickness + startYOffset, startZOffset, 1.0F);
        //vec1.transform(positionMatrix);
        Vector4f vec2 = new Vector4f(0, -thickness, (float) distance, 1.0F);
        vec2.transform(positionMatrix);
        Vector4f vec3 = new Vector4f(0, thickness, (float) distance, 1.0F);
        vec3.transform(positionMatrix);
        //Vector4f vec4 = new Vector4f(startXOffset, thickness + startYOffset, startZOffset, 1.0F);
        //vec4.transform(positionMatrix);

        if (hand == Hand.MAIN_HAND) {
            builder.pos(startXOffset, -thickness + startYOffset, startZOffset)
                    .color(r,g,b,alpha)
                    .tex(1, (float) v1)
                    .overlay(OverlayTexture.NO_OVERLAY)
                    .lightmap(15278880)
                    .normal(0,0,0)
                    .endVertex();
            /*builder.pos(0, -thickness, (float) distance)
                    .color(r,g,b,alpha)
                    .tex(1, (float) v2)
                    .overlay(OverlayTexture.NO_OVERLAY)
                    .lightmap(15278880)
                    .normal(0,0,0)
                    .endVertex();
            builder.pos(0, thickness, (float) distance)
                    .color(r,g,b,alpha)
                    .tex(0, (float) v2)
                    .overlay(OverlayTexture.NO_OVERLAY)
                    .lightmap(15278880)
                    .normal(0,0,0)
                    .endVertex();*/
            builder.pos(startXOffset, thickness + startYOffset, startZOffset)
                    .color(r,g,b,alpha)
                    .tex(0, (float) v1)
                    .overlay(OverlayTexture.NO_OVERLAY)
                    .lightmap(15278880)
                    .normal(0,0,0)
                    .endVertex();
            //builder.addVertex(vec1.getX(), vec1.getY(), vec1.getZ(), r, g, b, alpha, 1, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.getX(), vector3f.getY(), vector3f.getZ());
            builder.addVertex(vec2.getX(), vec2.getY(), vec2.getZ(), r, g, b, alpha, 1, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, 0,0,0);
            builder.addVertex(vec3.getX(), vec3.getY(), vec3.getZ(), r, g, b, alpha, 0, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, 0,0,0);
            //builder.addVertex(vec4.getX(), vec4.getY(), vec4.getZ(), r, g, b, alpha, 0, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.getX(), vector3f.getY(), vector3f.getZ());
            /*wr.pos(startXOffset, -thickness + startYOffset, startZOffset).tex(1, (float) v1).endVertex();
            wr.pos(0, -thickness, distance).tex(1, (float) v2).endVertex();
            wr.pos(0, thickness, distance).tex(0, (float) v2).endVertex();
            wr.pos(startXOffset, thickness + startYOffset, startZOffset).tex(0, (float) v1).endVertex();*/
        } else {
            startYOffset = -.120f;
            /*wr.pos(-startXOffset, thickness + startYOffset, startZOffset).tex(0, (float) v1).endVertex();
            wr.pos(0, thickness, distance).tex(0, (float) v2).endVertex();
            wr.pos(0, -thickness, distance).tex(1, (float) v2).endVertex();
            wr.pos(-startXOffset, -thickness + startYOffset, startZOffset).tex(1, (float) v1).endVertex();*/
        }
    }

}
