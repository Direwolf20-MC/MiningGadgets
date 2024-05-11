package com.direwolf20.mininggadgets.client.events;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.setup.Registration;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderHandEvent;


@Mod.EventBusSubscriber(modid = MiningGadgets.MOD_ID, value = Dist.CLIENT)
public class EventRenderGadget {

    @SubscribeEvent
    public static void renderGadget(RenderHandEvent event) {
        if (!(event.getItemStack().getItem() instanceof MiningGadget)) {
            return;
        }

        boolean isFancyGadget = Registration.MININGGADGET_FANCY.get().equals(event.getItemStack().getItem());

        Minecraft mc = Minecraft.getInstance();
        PoseStack matrixStackIn = event.getPoseStack();
        MultiBufferSource buffer = event.getMultiBufferSource();

        float swingProgress = event.getSwingProgress();
        float equipProgress = event.getEquipProgress();
        boolean rightHand = event.getHand() == InteractionHand.MAIN_HAND ^ mc.player.getMainArm() == HumanoidArm.LEFT;

        // renders arm
        matrixStackIn.pushPose();

        float f = rightHand ? 1.0F : -1.0F;
        float f1 = Mth.sqrt(swingProgress);
        float f2 = -0.3F * Mth.sin(f1 * (float)Math.PI);
        float f3 = 0.4F * Mth.sin(f1 * ((float)Math.PI * 2F));
        float f4 = -0.4F * Mth.sin(swingProgress * (float)Math.PI);

        matrixStackIn.translate(f * (f2 + 0.64000005F - .1f), f3 + -0.4F + equipProgress * -0.6F, f4 + -0.71999997F + .3f);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(f * 75.0F));

        float f5 = Mth.sin(swingProgress * swingProgress * (float)Math.PI);
        float f6 = Mth.sin(f1 * (float)Math.PI);

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(f * f6 * 45.0F));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(f * f5 * -20.0F));

        AbstractClientPlayer abstractclientplayerentity = mc.player;
        RenderSystem.setShaderTexture(0, abstractclientplayerentity.getSkin().texture());

        matrixStackIn.translate(f * -1.0F, 3.6F, 3.5D);
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(f * 120.0F));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(200.0F));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(f * -135.0F));
        matrixStackIn.translate(f * 5.6F, 0.0D, 0.0D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(f * 55.0F));

        PlayerRenderer playerrenderer = (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(abstractclientplayerentity);
        if (rightHand) {
            playerrenderer.renderRightHand(matrixStackIn, buffer, event.getPackedLight(), abstractclientplayerentity);
        } else {
            playerrenderer.renderLeftHand(matrixStackIn, buffer, event.getPackedLight(), abstractclientplayerentity);
        }

        matrixStackIn.popPose();

        // renders gadget
        matrixStackIn.pushPose();
        matrixStackIn.translate(f * (f2 + 0.64000005F - .1f), f3 + -0.4F + equipProgress * -0.6F, f4 + -0.71999997F - 0.1f + (isFancyGadget ? -.10f : 0));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(f * f6 * 70.0F));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(f * f5 * -20.0F));
        matrixStackIn.translate(rightHand ? .13f:-.1f, -.25f, -.35f);
        matrixStackIn.scale(1.15f, 1.15f, 1.15f);

        ItemInHandRenderer firstPersonRenderer = mc.gameRenderer.itemInHandRenderer;
        firstPersonRenderer.renderItem(abstractclientplayerentity,
                event.getItemStack(),
                rightHand
                        ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                        : ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
                !rightHand,
                event.getPoseStack(),
                event.getMultiBufferSource(),
                event.getPackedLight()
        );
        matrixStackIn.popPose();

        event.setCanceled(true);
    }
}
