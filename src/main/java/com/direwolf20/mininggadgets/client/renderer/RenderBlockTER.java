package com.direwolf20.mininggadgets.client.renderer;

import com.direwolf20.mininggadgets.common.blocks.RenderBlock;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.List;

public class RenderBlockTER implements BlockEntityRenderer<RenderBlockTileEntity> {

    public RenderBlockTER(BlockEntityRendererProvider.Context p_173636_) {

    }

    private void renderModelBrightnessColorQuads(PoseStack.Pose matrixEntry, VertexConsumer builder, float red, float green, float blue, float alpha, List<BakedQuad> listQuads, int combinedLightsIn, int combinedOverlayIn) {
        for(BakedQuad bakedquad : listQuads) {
            float f;
            float f1;
            float f2;

            if (bakedquad.isTinted()) {
                f = red * 1f;
                f1 = green * 1f;
                f2 = blue * 1f;
            } else {
                f = 1f;
                f1 = 1f;
                f2 = 1f;
            }

            builder.putBulkData(matrixEntry, bakedquad, f, f1, f2, alpha, combinedLightsIn, combinedOverlayIn, true);
        }
    }

    @Override
    public void render(RenderBlockTileEntity tile, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightsIn, int combinedOverlayIn) {
        int durability = tile.getDurability();
        int originalDurability = tile.getOriginalDurability();
        int prevDurability = tile.getPriorDurability();
        float nowScale = (float) (durability) / (float) originalDurability;
        float prevScale = (float) (prevDurability) / (float) originalDurability;
        float scale = (Mth.lerp(partialTicks, prevScale, nowScale));

        if (scale >= 1.0f)
            scale = 1f;
        if (scale <= 0)
            scale = 0;

        BlockState renderState = tile.getRenderBlock();
        // We're checking here as sometimes the tile can not have a render block as it's yet to be synced
        if( renderState == null )
            return;

        BlockRenderDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        MiningProperties.BreakTypes breakType = tile.getBreakType();
        BakedModel ibakedmodel = blockrendererdispatcher.getBlockModel(renderState);
        BlockColors blockColors = Minecraft.getInstance().getBlockColors();
        int color = blockColors.getColor(renderState, tile.getLevel(), tile.getBlockPos(), 0);
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;

        matrixStackIn.pushPose();

        if (breakType == MiningProperties.BreakTypes.SHRINK) {
            matrixStackIn.translate((1 - scale) / 2, (1 - scale) / 2, (1 - scale) / 2);
            matrixStackIn.scale(scale, scale, scale);

            VertexConsumer builder = bufferIn.getBuffer(RenderType.cutout());
            for (Direction direction : Direction.values()) {
                renderModelBrightnessColorQuads(matrixStackIn.last(), builder, f, f1, f2, 1f, getQuads(ibakedmodel, tile, direction, RenderType.cutout()), combinedLightsIn, combinedOverlayIn);
            }
            renderModelBrightnessColorQuads(matrixStackIn.last(), builder, f, f1, f2, 1f, getQuads(ibakedmodel, tile, null, RenderType.cutout()), combinedLightsIn, combinedOverlayIn);
        } else if (breakType == MiningProperties.BreakTypes.FADE) {
            scale = Mth.lerp(scale, 0.1f, 1.0f);
            VertexConsumer builder = bufferIn.getBuffer(MyRenderType.RenderBlock);
            for (Direction direction : Direction.values()) {
                if (!(tile.getLevel().getBlockState(tile.getBlockPos().relative(direction)).getBlock() instanceof RenderBlock)) {
                    renderModelBrightnessColorQuads(matrixStackIn.last(), builder, f, f1, f2, scale, getQuads(ibakedmodel, tile, direction, MyRenderType.RenderBlock), combinedLightsIn, combinedOverlayIn);
                }
            }
            renderModelBrightnessColorQuads(matrixStackIn.last(), builder, f, f1, f2, scale, getQuads(ibakedmodel, tile, null, MyRenderType.RenderBlock), combinedLightsIn, combinedOverlayIn);
        }
        matrixStackIn.popPose();
    }

    private List<BakedQuad> getQuads(BakedModel model, RenderBlockTileEntity tile, Direction side, RenderType type) {
        return model.getQuads(tile.getRenderBlock(), side, RandomSource.create(Mth.getSeed(tile.getBlockPos())), ModelData.EMPTY, type);
    }
}
