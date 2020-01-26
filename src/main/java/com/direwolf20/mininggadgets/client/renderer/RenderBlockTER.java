package com.direwolf20.mininggadgets.client.renderer;

import com.direwolf20.mininggadgets.common.blocks.RenderBlock;
import com.direwolf20.mininggadgets.common.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.opengl.GL14;

import java.util.List;
import java.util.Random;

public class RenderBlockTER extends TileEntityRenderer<RenderBlockTileEntity> {

    public RenderBlockTER(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    //Todo reinstate
    /*private void renderModelBrightnessColorQuads(float brightness, float red, float green, float blue, List<BakedQuad> listQuads) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        int i = 0;

        for (int j = listQuads.size(); i < j; ++i) {
            BakedQuad bakedquad = listQuads.get(i);
            bufferbuilder.begin(7, DefaultVertexFormats.ITEM);
            bufferbuilder.addVertexData(bakedquad.getVertexData());
            if (bakedquad.hasTintIndex()) {
                bufferbuilder.putColorRGB_F4(red * brightness, green * brightness, blue * brightness);
            } else {
                bufferbuilder.putColorRGB_F4(brightness, brightness, brightness);
            }

            Vec3i vec3i = bakedquad.getFace().getDirectionVec();
            bufferbuilder.putNormal((float) vec3i.getX(), (float) vec3i.getY(), (float) vec3i.getZ());
            tessellator.draw();
        }

    }*/

    @Override
    public void render(RenderBlockTileEntity tile, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightsIn, int combinedOverlayIn) {
    //public void render(RenderBlockTileEntity tile, double x, double y, double z, float partialTicks, int destroyStage) {

        double x = tile.getPos().getX();
        double y = tile.getPos().getY();
        double z = tile.getPos().getZ();
        int durability = tile.getDurability();
        int originalDurability = tile.getOriginalDurability();
        int prevDurability = tile.getPriorDurability();
        float nowScale = (float) (durability) / (float) originalDurability;
        float prevScale = (float) (prevDurability) / (float) originalDurability;
        float scale = (float) (MathHelper.lerp(partialTicks, prevScale, nowScale));
        if (scale >= 1.0f)
            scale = 1f;
        if (scale <= 0)
            scale = 0;
        float trans = (1 - scale) / 2;

        BlockState renderState = tile.getRenderBlock();

        // We're checking here as sometimes the tile can not have a render block as it's yet to be synced
        if( renderState == null )
            return;

        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        Minecraft mc = Minecraft.getInstance();
        mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        //This blend function allows you to use a constant alpha, which is defined later
        GlStateManager.blendFunc(GL14.GL_CONSTANT_ALPHA, GL14.GL_ONE_MINUS_CONSTANT_ALPHA);

        //GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
        MiningProperties.BreakTypes breakType = tile.getBreakType();
        GlStateManager.translated(x, y, z);

        IBakedModel ibakedmodel = blockrendererdispatcher.getModelForState(renderState);
        //Random random = new Random();
        BlockColors blockColors = Minecraft.getInstance().getBlockColors();

        int color = blockColors.getColor(renderState, tile.getWorld(), tile.getPos(), 0);

        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;

        if (breakType == MiningProperties.BreakTypes.SHRINK) {
            GlStateManager.translatef((1 - scale) / 2, (1 - scale) / 2, (1 - scale) / 2);
            GlStateManager.scalef(scale, scale, scale);
            GL14.glBlendColor(1F, 1F, 1F, 1f); //Set the alpha of the blocks we are rendering
            try {
                for (Direction direction : Direction.values()) {
                    //renderModelBrightnessColorQuads(1f, f, f1, f2, ibakedmodel.getQuads(renderState, direction, new Random(MathHelper.getPositionRandom(tile.getPos()))));
                }
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
        } else if (breakType == MiningProperties.BreakTypes.FADE) {
            //scale = (scale < 0.1f) ? 0.1f : scale;
            scale = MathHelper.lerp(scale, 0.1f, 1.0f);
            GL14.glBlendColor(1F, 1F, 1F, scale); //Set the alpha of the blocks we are rendering
            GlStateManager.depthMask(false);
            try {
                for (Direction direction : Direction.values()) {
                    if (!(tile.getWorld().getBlockState(tile.getPos().offset(direction)).getBlock() instanceof RenderBlock)) {
                        //renderModelBrightnessColorQuads(1f, f, f1, f2, ibakedmodel.getQuads(renderState, direction, new Random(MathHelper.getPositionRandom(tile.getPos()))));
                    }
                }
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
            GlStateManager.depthMask(true);
        }


        //Disable blend
        //GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        /*if (UpgradeTools.containsUpgradeFromList(tile.getGadgetUpgrades(), Upgrade.FREEZING)) {
            for (BlockPos sourcePos : tile.findSources()) {
                if (tile.getDurability() == 0 || tile.getDurability() == tile.getOriginalDurability() || (!(tile instanceof RenderBlockTileEntity))) {
                    extraRenderList.remove(sourcePos);
                } else {
                    if (!extraRenderList.containsKey(sourcePos) || extraRenderList.get(sourcePos) == tile.getPos()) {
                        extraRenderList.put(sourcePos, tile.getPos());
                        GlStateManager.pushMatrix();
                        GlStateManager.enableBlend();
                        //This blend function allows you to use a constant alpha, which is defined later
                        GlStateManager.blendFunc(GL14.GL_CONSTANT_ALPHA, GL14.GL_ONE_MINUS_CONSTANT_ALPHA);
                        GlStateManager.translated(x, y, z);
                        GlStateManager.translated(sourcePos.getX() - tile.getPos().getX(), sourcePos.getY() - tile.getPos().getY(), sourcePos.getZ() - tile.getPos().getZ());
                        //GlStateManager.translatef((1 - scale) / 2, (1 - scale) / 2, (1 - scale) / 2);
                        GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                        //GlStateManager.scalef(scale, scale, scale);
                        GL14.glBlendColor(1F, 1F, 1F, 1 - scale); //Set the alpha of the blocks we are rendering
                        if (tile.getWorld().getFluidState(sourcePos).getFluid().isEquivalentTo(Fluids.WATER))
                            blockrendererdispatcher.renderBlockBrightness(Blocks.PACKED_ICE.getDefaultState(), 1.0f);
                        else if (tile.getWorld().getFluidState(sourcePos).getFluid().isEquivalentTo(Fluids.LAVA))
                            blockrendererdispatcher.renderBlockBrightness(Blocks.OBSIDIAN.getDefaultState(), 1.0f);
                        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                        GlStateManager.disableBlend();
                        GlStateManager.popMatrix();
                    }
                }
            }
        }*/
/*

        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture();
        //GlStateManager.depthMask(false);
        Tessellator t = Tessellator.getInstance();
        BufferBuilder bufferBuilder = t.getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        double maxX = x + 1;
        double maxY = y + 1;
        double maxZ = z + 1;
        float red = 1f;
        float green = 0.0f;
        float blue = 0.0f;
        float alpha = (1f - (scale));
        if (alpha < 0.051f) {
            alpha = 0.051f;
        }
        if (alpha > 0.33f) {
            alpha = 0.33f;
        }
        alpha = 0.33f;
        // Down
        bufferBuilder.pos(x, y, z).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, y, z).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, y, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(x, y, maxZ).color(red, green, blue, alpha).endVertex();

        // Up
        bufferBuilder.pos(x, maxY, z).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(x, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, z).color(red, green, blue, alpha).endVertex();

        // North
        bufferBuilder.pos(x, y, z).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(x, maxY, z).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, z).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, y, z).color(red, green, blue, alpha).endVertex();

        // South
        bufferBuilder.pos(x, y, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, y, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(x, maxY, maxZ).color(red, green, blue, alpha).endVertex();

        // East
        bufferBuilder.pos(maxX, y, z).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, z).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, y, maxZ).color(red, green, blue, alpha).endVertex();

        // West
        bufferBuilder.pos(x, y, z).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(x, y, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(x, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(x, maxY, z).color(red, green, blue, alpha).endVertex();
        t.draw();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture();
        //GlStateManager.depthMask(true);
*/

    }
}
