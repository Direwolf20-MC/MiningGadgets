package com.direwolf20.mininggadgets.client.events;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.common.items.ModItems;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MiningGadgets.MOD_ID, value = Dist.CLIENT)
public class EventRenderSpecificHand
{
    @SubscribeEvent
    static void renderWorldLastEvent(RenderSpecificHandEvent event)
    {
        if(ModItems.MININGGADGET.get().equals(event.getItemStack().getItem()))
        {
            Minecraft mc = Minecraft.getInstance();
            boolean rightHand = event.getHand() == Hand.MAIN_HAND ^ mc.player.getPrimaryHand() == HandSide.LEFT;

            GlStateManager.pushMatrix();

            float equipProgress = event.getEquipProgress();

            // Render arm
            float f = rightHand ? 1.0F : -1.0F;
            float f1 = MathHelper.sqrt(event.getSwingProgress());
            float f2 = -0.3F * MathHelper.sin(f1 * (float) Math.PI);
            float f3 = 0.4F * MathHelper.sin(f1 * ((float) Math.PI * 2F));
            float f4 = -0.4F * MathHelper.sin(event.getSwingProgress() * (float) Math.PI);
            GlStateManager.translatef(f * (f2 + 0.64000005F - .1f), f3 + -0.4F + equipProgress * -0.6F,
                    f4 + -0.71999997F + .3f );
            GlStateManager.rotatef(f * 75.0F, 0.0F, 1.0F, 0.0F);
            float f5 = MathHelper.sin(event.getSwingProgress() * event.getSwingProgress() * (float) Math.PI);
            float f6 = MathHelper.sin(f1 * (float) Math.PI);
            GlStateManager.rotatef(f * f6 * 45.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(f * f5 * -20.0F, 0.0F, 0.0F, 1.0F);
            AbstractClientPlayerEntity abstractclientplayerentity = mc.player;
            mc.getTextureManager().bindTexture(abstractclientplayerentity.getLocationSkin());
            GlStateManager.translatef(f * -1.0F, 3.6F, 3.5F);
            GlStateManager.rotatef(f * 120.0F, 0.0F, 0.0F, 7.0F);
            GlStateManager.rotatef(200.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(f * -135.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translatef(f * 5.6F, 0.0F, 0.0F);
            GlStateManager.rotatef(f * 55.0F, 0.0F, 1.0F, 0.0F);
            PlayerRenderer playerrenderer = mc.getRenderManager().getRenderer(abstractclientplayerentity);
            GlStateManager.disableCull();
            if (rightHand) {
                playerrenderer.renderRightArm(abstractclientplayerentity);
            } else {
                playerrenderer.renderLeftArm(abstractclientplayerentity);
            }
            GlStateManager.enableCull();
            GlStateManager.popMatrix();

            // Render gun
            GlStateManager.pushMatrix();
            GlStateManager.translatef(rightHand ? 1.1f:-.25f, -.28f, -.75f);
            GlStateManager.scalef(1.15f, 1.15f, 1.15f);

            FirstPersonRenderer firstPersonRenderer = mc.getFirstPersonRenderer();
            firstPersonRenderer.renderItemSide(mc.player, event.getItemStack(),
                    rightHand ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND
                            : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND,
                    !rightHand);


            GlStateManager.popMatrix();
            event.setCanceled(true);
        }
    }
}
