package com.direwolf20.mininggadgets.client.renderer;

import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;

public class RenderBlockTER extends TileEntityRenderer<RenderBlockTileEntity> {
    public RenderBlockTER() {
    }

    @Override
    public void render(RenderBlockTileEntity tile, double x, double y, double z, float partialTicks, int destroyStage) {
        BlockState renderState = tile.getRenderBlock();

        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        Minecraft mc = Minecraft.getInstance();
        GlStateManager.pushMatrix();
        mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

        int durability = tile.getDurability();
        int originalDurability = tile.getOriginalDurability();
        float scale = (float) (durability) / (float) originalDurability;
        if (scale >= 1.0f)
            scale = 1f;
        if (scale <= 0)
            scale = 0;
        float trans = (1 - scale) / 2;


        GlStateManager.translated(x, y, z);
        GlStateManager.translatef(trans, trans, trans);
        GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.scalef(scale, scale, scale);

        try {
            blockrendererdispatcher.renderBlockBrightness(renderState, 1.0f);
        } catch (Throwable t) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            try {
                // If the buffer is already not drawing then it'll throw
                // and IllegalStateException... Very rare
                bufferBuilder.finishDrawing();
            } catch (IllegalStateException ex) {

            }
        }
        GlStateManager.popMatrix();
    }
}
