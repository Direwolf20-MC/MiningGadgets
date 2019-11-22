package com.direwolf20.mininggadgets.client.renderer;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.animation.Animation;

public class RenderMiningGadget extends ItemStackTileEntityRenderer
{
    @Override
    public void renderByItem(ItemStack stack)
    {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        MiningGadgetModel model = (MiningGadgetModel) itemRenderer.getModelWithOverrides(stack);

        itemRenderer.renderItem(stack, model.getBakedModel());
        itemRenderer.renderItem(stack, model.rf_capacitor);
    }
}
