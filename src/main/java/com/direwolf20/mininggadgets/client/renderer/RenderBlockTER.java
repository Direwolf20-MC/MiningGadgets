package com.direwolf20.mininggadgets.client.renderer;

import com.direwolf20.mininggadgets.common.blocks.RenderBlock;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RenderBlockTER implements BlockEntityRenderer<RenderBlockTileEntity, RenderBlockTER.RenderState> {
    public RenderBlockTER(BlockEntityRendererProvider.Context context) {
    }

    public static class RenderState extends net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState {
        public BlockState renderState;
        public MiningProperties.BreakTypes breakType;
        public float scale;
        public ClientLevel level;
        public int[] tintLayers = new int[] { -1 };
        public final boolean[] cullFaces = new boolean[6];
    }

    @Override
    public @NonNull RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(
            RenderBlockTileEntity tile,
            RenderState state,
            float partialTick,
            Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(tile, state, partialTick, cameraPosition, breakProgress);

        state.renderState = tile.getRenderBlock();
        state.breakType = tile.getBreakType();
        state.level = tile.getLevel() instanceof ClientLevel clientLevel ? clientLevel : null;
        state.scale = 0.0F;
        state.tintLayers = new int[] { -1 };
        Arrays.fill(state.cullFaces, false);

        if (state.renderState == null) {
            return;
        }

        int originalDurability = tile.getOriginalDurability();
        if (originalDurability > 0) {
            float nowScale = (float) tile.getDurability() / (float) originalDurability;
            float prevScale = (float) tile.getPriorDurability() / (float) originalDurability;
            state.scale = Mth.clamp(Mth.lerp(partialTick, prevScale, nowScale), 0.0F, 1.0F);
        }

        if (state.level != null) {
            state.lightCoords = LevelRenderer.getLightCoords(state.level, tile.getBlockPos());

            if (state.breakType == MiningProperties.BreakTypes.FADE) {
                for (Direction direction : Direction.values()) {
                    if (state.level.getBlockState(tile.getBlockPos().relative(direction)).getBlock() instanceof RenderBlock) {
                        state.cullFaces[direction.ordinal()] = true;
                    }
                }
            }

            List<BlockTintSource> sources = Minecraft.getInstance().getBlockColors().getTintSources(state.renderState);
            if (!sources.isEmpty()) {
                state.tintLayers = new int[sources.size()];
                for (int i = 0; i < sources.size(); i++) {
                    state.tintLayers[i] = sources.get(i).colorInWorld(state.renderState, state.level, tile.getBlockPos());
                }
            }
        }
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        if (state.renderState == null || state.level == null) {
            return;
        }

        BlockState renderBlock = state.renderState;
        BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(renderBlock);
        List<BlockStateModelPart> parts = new ArrayList<>();
        model.collectParts(RandomSource.create(), parts);

        if (state.breakType == MiningProperties.BreakTypes.SHRINK) {
            renderShrink(state, poseStack, collector, renderBlock, parts, model);
        } else if (state.breakType == MiningProperties.BreakTypes.FADE) {
            renderFade(state, poseStack, collector, renderBlock, parts, model);
        }
    }

    private void renderShrink(
            RenderState state,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            BlockState renderBlock,
            List<BlockStateModelPart> parts,
            BlockStateModel model
    ) {
        float scale = state.scale;

        poseStack.pushPose();
        poseStack.translate((1.0F - scale) / 2.0F, (1.0F - scale) / 2.0F, (1.0F - scale) / 2.0F);
        poseStack.scale(scale, scale, scale);

        if (parts.isEmpty()) {
            poseStack.popPose();
            return;
        }

        collector.submitCustomGeometry(poseStack, Sheets.cutoutBlockSheet(), (pose, buffer) -> {
            ModelBlockRenderer renderer = new ModelBlockRenderer(true, false, Minecraft.getInstance().getBlockColors());
            renderer.tesselateBlock(
                    (x, y, z, quad, instance) -> buffer.putBakedQuad(pose, quad, instance),
                    0,
                    0,
                    0,
                    state.level,
                    state.blockPos,
                    renderBlock,
                    model,
                    renderBlock.getSeed(state.blockPos)
            );
        });

        poseStack.popPose();
    }

    private void renderFade(
            RenderState state,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            BlockState renderBlock,
            List<BlockStateModelPart> parts,
            BlockStateModel model
    ) {
        if (parts.isEmpty()) {
            return;
        }

        float alpha = Mth.lerp(state.scale, 0.1F, 1.0F);
        int alphaInt = Math.round(alpha * 255.0F);

        RenderType renderType = renderBlock.isSolidRender()
                ? MyRenderType.RENDER_BLOCK
                : MyRenderType.RENDER_BLOCK;

        collector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
            ModelBlockRenderer renderer = new ModelBlockRenderer(true, false, Minecraft.getInstance().getBlockColors());
            renderer.tesselateBlock(
                    (x, y, z, quad, instance) -> {
                        Direction dir = quad.direction();
                        if (dir != null && state.cullFaces[dir.ordinal()]) {
                            return;
                        }

                        for (int v = 0; v < 4; v++) {
                            int color = instance.getColor(v);
                            instance.setColor(v, ARGB.color(alphaInt, ARGB.red(color), ARGB.green(color), ARGB.blue(color)));
                        }

                        buffer.putBakedQuad(pose, quad, instance);
                    },
                    0,
                    0,
                    0,
                    state.level,
                    state.blockPos,
                    renderBlock,
                    model,
                    renderBlock.getSeed(state.blockPos)
            );
        });
    }
}