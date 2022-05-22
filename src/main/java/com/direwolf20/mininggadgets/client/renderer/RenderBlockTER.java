package com.direwolf20.mininggadgets.client.renderer;

import com.direwolf20.mininggadgets.common.blocks.RenderBlock;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.List;
import java.util.Random;

public class RenderBlockTER implements BlockEntityRenderer<RenderBlockTileEntity> {

    public RenderBlockTER(BlockEntityRendererProvider.Context p_173636_) {}

    @Override
    public void render(RenderBlockTileEntity tile, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightsIn, int combinedOverlayIn) {
        int durability = tile.getDurability();
        int originalDurability = tile.getOriginalDurability();
        int prevDurability = tile.getPriorDurability();
        float nowScale = (float) (durability) / (float) originalDurability;
        float prevScale = (float) (prevDurability) / (float) originalDurability;
        float scale = Mth.lerp(partialTicks, prevScale, nowScale);

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
        BlockColors blockColors = Minecraft.getInstance().getBlockColors();
        int color = blockColors.getColor(renderState, tile.getLevel(), tile.getBlockPos(), 0);

        matrixStackIn.pushPose();
        if (breakType == MiningProperties.BreakTypes.SHRINK) {
            matrixStackIn.translate((1 - scale) / 2, (1 - scale) / 2, (1 - scale) / 2);
            matrixStackIn.scale(scale, scale, scale);
            renderSingleBlock(blockrendererdispatcher, tile, renderState, matrixStackIn, bufferIn, combinedLightsIn, combinedOverlayIn, color, -1);
        } else if (breakType == MiningProperties.BreakTypes.FADE) {
            scale = Mth.lerp(scale, 0.1f, 1.0f);
            renderSingleBlock(blockrendererdispatcher, tile, renderState, matrixStackIn, bufferIn, combinedLightsIn, combinedOverlayIn, color, scale);
        }
        matrixStackIn.popPose();
    }

    private List<BakedQuad> getQuads(BakedModel model, RenderBlockTileEntity tile, Direction side) {
        return model.getQuads(tile.getRenderBlock(), side, new Random(Mth.getSeed(tile.getBlockPos())), EmptyModelData.INSTANCE);
    }

    // Stolen from MC to support color properly...
    public void renderSingleBlock(BlockRenderDispatcher dispatcher, RenderBlockTileEntity tile, BlockState state, PoseStack pose, MultiBufferSource buffer, int light, int overlay, int color, float alpha) {
        RenderShape rendershape = state.getRenderShape();
        if (rendershape != RenderShape.INVISIBLE) {
            switch (rendershape) {
                case MODEL -> {
                    BakedModel bakedmodel = dispatcher.getBlockModel(state);
                    float f = (float) (color >> 16 & 255) / 255.0F;
                    float f1 = (float) (color >> 8 & 255) / 255.0F;
                    float f2 = (float) (color & 255) / 255.0F;
                    this.renderModel(tile, pose.last(), alpha == -1 ? buffer.getBuffer(ItemBlockRenderTypes.getRenderType(state, false)) : buffer.getBuffer(MyRenderType.RenderBlock), bakedmodel, f, f1, f2, alpha, light, overlay);
                }
                case ENTITYBLOCK_ANIMATED -> {
                    ItemStack stack = new ItemStack(state.getBlock());
                    net.minecraftforge.client.RenderProperties.get(stack).getItemStackRenderer().renderByItem(stack, ItemTransforms.TransformType.NONE, pose, buffer, light, overlay);
                }
            }

        }
    }

    public void renderModel(RenderBlockTileEntity tile, PoseStack.Pose pose, VertexConsumer consumer, BakedModel model, float red, float green, float blue, float alpha, int light, int overlay) {
        for (Direction direction : LevelRenderer.DIRECTIONS) {
            if (alpha == -1 || !(tile.getLevel().getBlockState(tile.getBlockPos().relative(direction)).getBlock() instanceof RenderBlock)) {
                renderModelBrightnessColorQuads(pose, consumer, red, green, blue, alpha, getQuads(model, tile, direction), light, overlay);
            }
        }
        renderModelBrightnessColorQuads(pose, consumer, red, green, blue, alpha, getQuads(model, tile, null), light, overlay);
    }

    private void renderModelBrightnessColorQuads(PoseStack.Pose matrixEntry, VertexConsumer builder, float red, float green, float blue, float alpha, List<BakedQuad> listQuads, int combinedLightsIn, int combinedOverlayIn) {
        for(BakedQuad bakedquad : listQuads) {
            boolean tinted = bakedquad.isTinted();
            
            float f = tinted ? red : 1F;
            float f1 = tinted ? green : 1F;
            float f2 = tinted ? blue : 1F;

            builder.putBulkData(matrixEntry, bakedquad, f, f1, f2, alpha, combinedLightsIn, combinedOverlayIn, true);
        }
    }
}
