package com.direwolf20.mininggadgets.client.events;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.setup.Registration;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;

@EventBusSubscriber(modid = MiningGadgets.MOD_ID, value = Dist.CLIENT)
public class EventRenderGadget {

    @SubscribeEvent
    public static void renderArm(RenderArmEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        if (event.getPlayer() == mc.player && MiningGadget.isHolding(mc.player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void renderGadget(RenderHandEvent event) {
        if (!(event.getItemStack().getItem() instanceof MiningGadget)) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        boolean isFancyGadget = Registration.MININGGADGET_FANCY.get() == event.getItemStack().getItem();

        PoseStack poseStack = event.getPoseStack();
        SubmitNodeCollector submitNodeCollector = event.getSubmitNodeCollector();

        float swingProgress = event.getSwingProgress();
        float equipProgress = event.getEquipProgress();
        boolean rightHand = event.getHand() == InteractionHand.MAIN_HAND ^ mc.player.getMainArm() == HumanoidArm.LEFT;

        float f = rightHand ? 1.0F : -1.0F;
        float f1 = Mth.sqrt(swingProgress);
        float f2 = -0.3F * Mth.sin(f1 * (float)Math.PI);
        float f3 = 0.4F * Mth.sin(f1 * ((float)Math.PI * 2F));
        float f4 = -0.4F * Mth.sin(swingProgress * (float)Math.PI);
        float f5 = Mth.sin(swingProgress * swingProgress * (float)Math.PI);
        float f6 = Mth.sin(f1 * (float)Math.PI);

        poseStack.pushPose();
        poseStack.translate(
                f * (f2 + 0.64000005F - 0.1f),
                f3 - 0.4F + equipProgress * -0.6F,
                f4 - 0.71999997F - 0.1f + (isFancyGadget ? -0.10f : 0.0f)
        );
        poseStack.mulPose(Axis.YP.rotationDegrees(f * f6 * 70.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(f * f5 * -20.0F));
        poseStack.translate(rightHand ? 0.13f : -0.1f, -0.25f, -0.35f);
        poseStack.scale(1.15f, 1.15f, 1.15f);

        ItemInHandRenderer firstPersonRenderer = mc.gameRenderer.itemInHandRenderer;
        firstPersonRenderer.renderItem(
                mc.player,
                event.getItemStack(),
                rightHand
                        ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                        : ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
                poseStack,
                submitNodeCollector,
                event.getPackedLight()
        );

        poseStack.popPose();
        event.setCanceled(true);
    }
}
