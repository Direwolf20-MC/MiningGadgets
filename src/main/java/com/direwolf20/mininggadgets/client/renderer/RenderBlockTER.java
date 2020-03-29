package com.direwolf20.mininggadgets.client.renderer;

import com.direwolf20.mininggadgets.common.blocks.RenderBlock;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.List;
import java.util.Random;

public class RenderBlockTER extends TileEntityRenderer<RenderBlockTileEntity> {

    public RenderBlockTER(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    private void renderModelBrightnessColorQuads(MatrixStack.Entry matrixEntry, IVertexBuilder builder, float red, float green, float blue, float alpha, List<BakedQuad> listQuads, int combinedLightsIn, int combinedOverlayIn) {
        for(BakedQuad bakedquad : listQuads) {
            float f;
            float f1;
            float f2;

            if (bakedquad.hasTintIndex()) {
                f = red * 1f;
                f1 = green * 1f;
                f2 = blue * 1f;
            } else {
                f = 1f;
                f1 = 1f;
                f2 = 1f;
            }

            builder.addVertexData(matrixEntry, bakedquad, f, f1, f2, alpha, combinedLightsIn, combinedOverlayIn);
        }
    }

    @Override
    public void render(RenderBlockTileEntity tile, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightsIn, int combinedOverlayIn) {
        int durability = tile.getDurability();
        int originalDurability = tile.getOriginalDurability();
        int prevDurability = tile.getPriorDurability();
        float nowScale = (float) (durability) / (float) originalDurability;
        float prevScale = (float) (prevDurability) / (float) originalDurability;
        float scale = (MathHelper.lerp(partialTicks, prevScale, nowScale));

        if (scale >= 1.0f)
            scale = 1f;
        if (scale <= 0)
            scale = 0;

        BlockState renderState = tile.getRenderBlock();
        // We're checking here as sometimes the tile can not have a render block as it's yet to be synced
        if( renderState == null )
            return;

        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        Minecraft.getInstance().getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
        MiningProperties.BreakTypes breakType = tile.getBreakType();
        IBakedModel ibakedmodel = blockrendererdispatcher.getModelForState(renderState);
        BlockColors blockColors = Minecraft.getInstance().getBlockColors();
        int color = blockColors.getColor(renderState, tile.getWorld(), tile.getPos(), 0);
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;

        matrixStackIn.push();

        if (breakType == MiningProperties.BreakTypes.SHRINK) {
            matrixStackIn.translate((1 - scale) / 2, (1 - scale) / 2, (1 - scale) / 2);
            matrixStackIn.scale(scale, scale, scale);

            for (Direction direction : Direction.values()) {
                renderModelBrightnessColorQuads(matrixStackIn.getLast(), bufferIn.getBuffer(RenderType.getCutout()), f, f1, f2, 1f, ibakedmodel.getQuads(renderState, direction, new Random(MathHelper.getPositionRandom(tile.getPos())), EmptyModelData.INSTANCE), combinedLightsIn, combinedOverlayIn);
            }

        } else if (breakType == MiningProperties.BreakTypes.FADE) {
            scale = MathHelper.lerp(scale, 0.1f, 1.0f);
            for (Direction direction : Direction.values()) {
                if (!(tile.getWorld().getBlockState(tile.getPos().offset(direction)).getBlock() instanceof RenderBlock)) {
                    renderModelBrightnessColorQuads(matrixStackIn.getLast(), bufferIn.getBuffer(MyRenderType.RenderBlock), f, f1, f2, scale, ibakedmodel.getQuads(renderState, direction, new Random(MathHelper.getPositionRandom(tile.getPos())), EmptyModelData.INSTANCE), combinedLightsIn, combinedOverlayIn);
                }
            }
        }
        matrixStackIn.pop();
    }
}
