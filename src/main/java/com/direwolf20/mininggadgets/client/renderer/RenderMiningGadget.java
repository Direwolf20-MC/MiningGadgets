package com.direwolf20.mininggadgets.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

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
