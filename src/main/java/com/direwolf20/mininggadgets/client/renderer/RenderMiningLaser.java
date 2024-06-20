package com.direwolf20.mininggadgets.client.renderer;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.util.CodecHelpers;
import com.direwolf20.mininggadgets.setup.Registration;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class RenderMiningLaser {

    private final static ResourceLocation laserBeam = ResourceLocation.fromNamespaceAndPath(MiningGadgets.MOD_ID, "textures/misc/laser.png");
    private final static ResourceLocation laserBeam2 = ResourceLocation.fromNamespaceAndPath(MiningGadgets.MOD_ID, "textures/misc/laser2.png");
    private final static ResourceLocation laserBeamGlow = ResourceLocation.fromNamespaceAndPath(MiningGadgets.MOD_ID, "textures/misc/laser_glow.png");

    public static void renderLaser(RenderLevelStageEvent event, Player player, float ticks) {
        ItemStack stack = MiningGadget.getGadget(player);

        if (!MiningProperties.getCanMine(stack))
            return;

        int range = MiningProperties.getBeamRange(stack);

        Vec3 playerPos = player.getEyePosition(ticks);
        HitResult trace = player.pick(range, ticks, false);

        // parse data from item
        float speedModifier = getSpeedModifier(stack);

        CodecHelpers.LaserColor laserColor = MiningProperties.getColors(stack);

        float red = laserColor.red() / 255f;
        float green = laserColor.green() / 255f;
        float blue = laserColor.blue() / 255f;

        drawLasers(stack, event, playerPos, trace, 0, 0, 0, red, green, blue, 0.02f, player, ticks, speedModifier);
    }

    private static float getSpeedModifier(ItemStack stack) {
        if (UpgradeTools.containsUpgrade(stack, Upgrade.EFFICIENCY_1)) {
            double efficiency = UpgradeTools.getUpgradeFromGadget(stack, Upgrade.EFFICIENCY_1).get().getTier() / 5f;
            double speedModifier = Mth.lerp(efficiency, 0.02, 0.05);
            return (float) -speedModifier;
        } else {
            return -0.02f;
        }
    }

    private static void drawLasers(ItemStack stack, RenderLevelStageEvent event, Vec3 from, HitResult trace, double xOffset, double yOffset, double zOffset, float r, float g, float b, float thickness, Player player, float ticks, float speedModifier) {
        InteractionHand activeHand;
        if (player.getMainHandItem().getItem() instanceof MiningGadget) {
            activeHand = InteractionHand.MAIN_HAND;
        } else if (player.getOffhandItem().getItem() instanceof MiningGadget) {
            activeHand = InteractionHand.OFF_HAND;
        } else {
            return;
        }

        VertexConsumer builder;
        double distance = Math.max(1, from.subtract(trace.getLocation()).length());
        long gameTime = player.level().getGameTime();
        double v = gameTime * speedModifier;
        float additiveThickness = (thickness * 3.5f) * calculateLaserFlickerModifier(gameTime);

        CodecHelpers.LaserColor laserColor = MiningProperties.getColors(stack);

        float beam2r = laserColor.innerRed() / 255f;
        float beam2g = laserColor.innerGreen() / 255f;
        float beam2b = laserColor.innerBlue() / 255f;

        Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        PoseStack matrix = event.getPoseStack();

        matrix.pushPose();

        matrix.translate(-view.x(), -view.y(), -view.z());
        matrix.translate(from.x, from.y, from.z);
        matrix.mulPose(Axis.YP.rotationDegrees(Mth.lerp(ticks, -player.getYRot(), -player.yRotO)));
        matrix.mulPose(Axis.XP.rotationDegrees(Mth.lerp(ticks, player.getXRot(), player.xRotO)));

        PoseStack.Pose matrixstack$entry = matrix.last();
        Matrix3f matrixNormal = matrixstack$entry.normal();
        Matrix4f positionMatrix = matrixstack$entry.pose();

        //additive laser beam
        builder = buffer.getBuffer(MyRenderType.LASER_MAIN_ADDITIVE);
        drawBeam(stack, xOffset, yOffset, zOffset, builder, positionMatrix, matrixNormal, additiveThickness, activeHand, distance, 0.5, 1, ticks, r,g,b,0.7f);

        //main laser, colored part
        builder = buffer.getBuffer(MyRenderType.LASER_MAIN_BEAM);
        drawBeam(stack, xOffset, yOffset, zOffset, builder, positionMatrix, matrixNormal, thickness, activeHand, distance, v, v + distance * 1.5, ticks, r,g,b,1f);

        //core
        builder = buffer.getBuffer(MyRenderType.LASER_MAIN_CORE);
        drawBeam(stack, xOffset, yOffset, zOffset, builder, positionMatrix, matrixNormal, thickness/2, activeHand, distance, v, v + distance * 1.5, ticks, beam2r,beam2g,beam2b,1f);
        matrix.popPose();
//        RenderSystem.disableDepthTest();
        buffer.endBatch();
    }

    private static float calculateLaserFlickerModifier(long gameTime) {
        return 0.9f + 0.1f * Mth.sin(gameTime * 0.99f) * Mth.sin(gameTime * 0.3f) * Mth.sin(gameTime * 0.1f);
    }

    private static void drawBeam(ItemStack stack, double xOffset, double yOffset, double zOffset, VertexConsumer builder, Matrix4f positionMatrix, Matrix3f matrixNormalIn, float thickness, InteractionHand hand, double distance, double v1, double v2, float ticks, float r, float g, float b, float alpha) {
        boolean isFancy = stack.getItem().equals(Registration.MININGGADGET_FANCY.get());
        boolean isSimple = stack.getItem().equals(Registration.MININGGADGET_SIMPLE.get());

        Vector3f vector3f = new Vector3f(0.0f, 1.0f, 0.0f);
        vector3f.mul(matrixNormalIn);
        LocalPlayer player = Minecraft.getInstance().player;
        // Support for hand sides remembering to take into account of Skin options
        if (Minecraft.getInstance().options.mainHand().get() != HumanoidArm.RIGHT)
            hand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        float startXOffset = -0.20f;
        float startYOffset = -.108f;
        float startZOffset = 0.60f;
        // Adjust for different gadgets
        if (isFancy) {
            startYOffset += .02f;
        }
        if (isSimple) {
            startXOffset -= .02f;
            startZOffset += .05f;
            startYOffset -= .005f;
        }
        // Adjust for fov changing
        startZOffset += (1 - player.getFieldOfViewModifier());
        if (hand == InteractionHand.OFF_HAND) {
            startYOffset = -.120f;
            startXOffset = 0.25f;
        }
        float f = (Mth.lerp(ticks, player.xRotO, player.getXRot()) - Mth.lerp(ticks, player.xBobO, player.xBob));
        float f1 = (Mth.lerp(ticks, player.yRotO, player.getYRot()) - Mth.lerp(ticks, player.yBobO, player.yBob));
        startXOffset = startXOffset + (f1 / 750);
        startYOffset = startYOffset + (f / 750);

        Vector4f vec1 = new Vector4f(startXOffset, -thickness + startYOffset, startZOffset, 1.0F);
        vec1.mul(positionMatrix);
        Vector4f vec2 = new Vector4f((float) xOffset, -thickness + (float) yOffset, (float) distance + (float) zOffset, 1.0F);
        vec2.mul(positionMatrix);
        Vector4f vec3 = new Vector4f((float) xOffset, thickness + (float) yOffset, (float) distance + (float) zOffset, 1.0F);
        vec3.mul(positionMatrix);
        Vector4f vec4 = new Vector4f(startXOffset, thickness + startYOffset, startZOffset, 1.0F);
        vec4.mul(positionMatrix);

        if (hand == InteractionHand.MAIN_HAND) {
            builder.addVertex(vec4.x(), vec4.y(), vec4.z()).setColor(r, g, b, alpha).setUv(0, (float) v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(vector3f.x(), vector3f.y(), vector3f.z());
            builder.addVertex(vec3.x(), vec3.y(), vec3.z()).setColor(r, g, b, alpha).setUv(0, (float) v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(vector3f.x(), vector3f.y(), vector3f.z());
            builder.addVertex(vec2.x(), vec2.y(), vec2.z()).setColor(r, g, b, alpha).setUv(1, (float) v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(vector3f.x(), vector3f.y(), vector3f.z());
            builder.addVertex(vec1.x(), vec1.y(), vec1.z()).setColor(r, g, b, alpha).setUv(1, (float) v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(vector3f.x(), vector3f.y(), vector3f.z());
            //Rendering a 2nd time to allow you to see both sides in multiplayer, shouldn't be necessary with culling disabled but here we are....
            builder.addVertex(vec1.x(), vec1.y(), vec1.z()).setColor(r, g, b, alpha).setUv(1, (float) v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(vector3f.x(), vector3f.y(), vector3f.z());
            builder.addVertex(vec2.x(), vec2.y(), vec2.z()).setColor(r, g, b, alpha).setUv(1, (float) v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(vector3f.x(), vector3f.y(), vector3f.z());
            builder.addVertex(vec3.x(), vec3.y(), vec3.z()).setColor(r, g, b, alpha).setUv(0, (float) v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(vector3f.x(), vector3f.y(), vector3f.z());
            builder.addVertex(vec4.x(), vec4.y(), vec4.z()).setColor(r, g, b, alpha).setUv(0, (float) v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(vector3f.x(), vector3f.y(), vector3f.z());
        } else {
            builder.addVertex(vec1.x(), vec1.y(), vec1.z()).setColor(r, g, b, alpha).setUv(1, (float) v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(vector3f.x(), vector3f.y(), vector3f.z());
            builder.addVertex(vec2.x(), vec2.y(), vec2.z()).setColor(r, g, b, alpha).setUv(1, (float) v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(vector3f.x(), vector3f.y(), vector3f.z());
            builder.addVertex(vec3.x(), vec3.y(), vec3.z()).setColor(r, g, b, alpha).setUv(0, (float) v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(vector3f.x(), vector3f.y(), vector3f.z());
            builder.addVertex(vec4.x(), vec4.y(), vec4.z()).setColor(r, g, b, alpha).setUv(0, (float) v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(vector3f.x(), vector3f.y(), vector3f.z());
            //Rendering a 2nd time to allow you to see both sides in multiplayer, shouldn't be necessary with culling disabled but here we are....
            builder.addVertex(vec4.x(), vec4.y(), vec4.z()).setColor(r, g, b, alpha).setUv(0, (float) v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(vector3f.x(), vector3f.y(), vector3f.z());
            builder.addVertex(vec3.x(), vec3.y(), vec3.z()).setColor(r, g, b, alpha).setUv(0, (float) v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(vector3f.x(), vector3f.y(), vector3f.z());
            builder.addVertex(vec2.x(), vec2.y(), vec2.z()).setColor(r, g, b, alpha).setUv(1, (float) v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(vector3f.x(), vector3f.y(), vector3f.z());
            builder.addVertex(vec1.x(), vec1.y(), vec1.z()).setColor(r, g, b, alpha).setUv(1, (float) v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(vector3f.x(), vector3f.y(), vector3f.z());
        }
    }

}
