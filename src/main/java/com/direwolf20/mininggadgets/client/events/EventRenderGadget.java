package com.direwolf20.mininggadgets.client.events;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.items.ModItems;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MiningGadgets.MOD_ID, value = Dist.CLIENT)
public class EventRenderGadget
{
    
    @SubscribeEvent
    static void renderGadget(RenderHandEvent event)
    {
        if (!ModItems.MININGGADGET.get().equals(event.getItemStack().getItem())) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        MatrixStack matrixStackIn = event.getMatrixStack();
        IRenderTypeBuffer buffer = event.getBuffers();

        float swingProgress = event.getSwingProgress();
        float equipProgress = event.getEquipProgress();
        boolean rightHand = event.getHand() == Hand.MAIN_HAND ^ mc.player.getMainArm() == HandSide.LEFT;

        // renders arm
        matrixStackIn.pushPose();

        float f = rightHand ? 1.0F : -1.0F;
        float f1 = MathHelper.sqrt(swingProgress);
        float f2 = -0.3F * MathHelper.sin(f1 * (float)Math.PI);
        float f3 = 0.4F * MathHelper.sin(f1 * ((float)Math.PI * 2F));
        float f4 = -0.4F * MathHelper.sin(swingProgress * (float)Math.PI);

        matrixStackIn.translate(f * (f2 + 0.64000005F - .1f), f3 + -0.4F + equipProgress * -0.6F, f4 + -0.71999997F + .3f);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f * 75.0F));

        float f5 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float f6 = MathHelper.sin(f1 * (float)Math.PI);

        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f * f6 * 45.0F));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(f * f5 * -20.0F));

        AbstractClientPlayerEntity abstractclientplayerentity = mc.player;
        mc.getTextureManager().bind(abstractclientplayerentity.getSkinTextureLocation());

        matrixStackIn.translate(f * -1.0F, 3.6F, 3.5D);
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(f * 120.0F));
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(200.0F));
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f * -135.0F));
        matrixStackIn.translate(f * 5.6F, 0.0D, 0.0D);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f * 55.0F));

        PlayerRenderer playerrenderer = (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(abstractclientplayerentity);
        if (rightHand) {
            playerrenderer.renderRightHand(matrixStackIn, buffer, event.getLight(), abstractclientplayerentity);
        } else {
            playerrenderer.renderLeftHand(matrixStackIn, buffer, event.getLight(), abstractclientplayerentity);
        }

        matrixStackIn.popPose();

        // renders gadget
        matrixStackIn.pushPose();
        matrixStackIn.translate(f * (f2 + 0.64000005F - .1f), f3 + -0.4F + equipProgress * -0.6F, f4 + -0.71999997F - 0.1f);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f * f6 * 70.0F));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(f * f5 * -20.0F));
        matrixStackIn.translate(rightHand ? .13f:-.1f, -.25f, -.35f);
        matrixStackIn.scale(1.15f, 1.15f, 1.15f);

        FirstPersonRenderer firstPersonRenderer = mc.getItemInHandRenderer();
        firstPersonRenderer.renderItem(abstractclientplayerentity,
                event.getItemStack(),
                rightHand
                        ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND
                        : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND,
                !rightHand,
                event.getMatrixStack(),
                event.getBuffers(),
                event.getLight()
        );
        matrixStackIn.popPose();

        event.setCanceled(true);
    }
}
