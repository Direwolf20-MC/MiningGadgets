package com.direwolf20.mininggadgets.client.renderer;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.tiles.QuarryBlockTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class QuarryBlockTER extends TileEntityRenderer<QuarryBlockTileEntity> {

    private final static ResourceLocation laserBeam = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/misc/laser.png");
    private final static ResourceLocation laserBeam2 = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/misc/laser2.png");
    private final static ResourceLocation laserBeamGlow = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/misc/laser_glow.png");

    public QuarryBlockTER(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    private void renderModelBrightnessColorQuads(MatrixStack.Entry matrixEntry, IVertexBuilder builder, float red, float green, float blue, float alpha, List<BakedQuad> listQuads, int combinedLightsIn, int combinedOverlayIn) {
        for (BakedQuad bakedquad : listQuads) {
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
    public void render(QuarryBlockTileEntity tile, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightsIn, int combinedOverlayIn) {
        if (tile.getMarkerX().equals(BlockPos.ZERO) || tile.getMarkerZ().equals(BlockPos.ZERO)) return;
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        IVertexBuilder builder = buffer.getBuffer(MyRenderType.OVERLAY_LINES);

        matrixStackIn.push();
        matrixStackIn.translate(.5, .5, .5);
        matrixStackIn.translate(-tile.getPos().getX(), -tile.getPos().getY(), -tile.getPos().getZ());
        Matrix4f positionMatrix = matrixStackIn.getLast().getMatrix();
        BlockPos markerX = tile.getMarkerX();
        BlockPos markerZ = tile.getMarkerZ();
        BlockPos corner = new BlockPos(markerX.getX(), markerX.getY(), markerZ.getZ());
        drawLasers(builder, positionMatrix, tile.getPos(), markerX, 1f, 0f, 0f, 1f);
        drawLasers(builder, positionMatrix, tile.getPos(), markerZ, 1f, 0f, 0f, 1f);
        drawLasers(builder, positionMatrix, markerX, corner, 1f, 0f, 0f, 1f);
        drawLasers(builder, positionMatrix, markerZ, corner, 1f, 0f, 0f, 1f);

        matrixStackIn.pop();
        buffer.finish(MyRenderType.OVERLAY_LINES);
    }

    private static void drawLasers(IVertexBuilder builder, Matrix4f positionMatrix, BlockPos from, BlockPos to, float r, float g, float b, float thickness) {
        builder.pos(positionMatrix, from.getX(), from.getY(), from.getZ())
                .color(r, g, b, 1.0f)
                .endVertex();
        builder.pos(positionMatrix, to.getX(), to.getY(), to.getZ())
                .color(r, g, b, 1.0f)
                .endVertex();
    }

    @Override
    public boolean isGlobalRenderer(QuarryBlockTileEntity te) {
        return true;
    }
}
