package com.direwolf20.mininggadgets.client.renderer;

import com.direwolf20.mininggadgets.common.blocks.RenderBlock;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import com.direwolf20.mininggadgets.common.util.VectorHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL14;

import java.util.List;
import java.util.Random;

public class RenderBlockTER extends TileEntityRenderer<RenderBlockTileEntity> {
    public RenderBlockTER() {
    }

    private void renderModelBrightnessColorQuads(float brightness, float red, float green, float blue, List<BakedQuad> listQuads) {
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

    }

    @Override
    public void render(RenderBlockTileEntity tile, double x, double y, double z, float partialTicks, int destroyStage) {

        int durability = tile.getDurability();
        int originalDurability = tile.getOriginalDurability();
        float scale = (float) (durability) / (float) originalDurability;
        if (scale >= 1.0f)
            scale = 1f;
        if (scale <= 0)
            scale = 0;
        float trans = (1 - scale) / 2;

        BlockState renderState = tile.getRenderBlock();

        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        Minecraft mc = Minecraft.getInstance();
        mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

        //Particles
        World world = tile.getWorld();
        PlayerEntity player = world.getPlayerByUuid(tile.getPlayerUUID());
        if (player == null) return;
        float blockSizeScale = 0.1f;
        double yOffset = -.25;
        double startXOffset = -0.35;

        BlockRayTraceResult lookingAt = VectorHelper.getLookingAt(player, RayTraceContext.FluidMode.NONE);
        Vec3d lookBlockHit = lookingAt.getHitVec();
        BlockPos lookBlockPos = lookingAt.getPos();
        Vec3d playerEye = player.getEyePosition(partialTicks);
        Vec3d blockPos = new Vec3d(tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5, tile.getPos().getZ() + 0.5);

        //Vec3d partPos = new Vec3d(blockPos.x, blockPos.y, blockPos.z);
        int maxParts = 8;
        for (int j = 1; j <= maxParts; j++) {
            float tempScale = ((scale * maxParts) - (maxParts - j));
            double xDiff = blockPos.getX() - lookBlockHit.getX() + (lookBlockPos.getX() - tile.getPos().getX()) * tempScale;
            double yDiff = blockPos.getY() - lookBlockHit.getY() + (lookBlockPos.getY() - tile.getPos().getY()) * tempScale;
            double zDiff = blockPos.getZ() - lookBlockHit.getZ() + (lookBlockPos.getZ() - tile.getPos().getZ()) * tempScale;
            GlStateManager.pushMatrix();
            //Put the particle in the center of the block
            GlStateManager.translated(x, y, z);
            //If we are mining, then move the particle relative to where the player is looking (So it follows the laser beam)
            if (player.isHandActive())
                GlStateManager.translated(-xDiff * tempScale, -yDiff * tempScale, -zDiff * tempScale);
            //else
            //GlStateManager.translated(-xDiff * tempScale, -yDiff * tempScale, -zDiff * tempScale);
            //Place the particle along the beam, based on the player's eye and block position, and scale (Progress of the mine)
            Vec3d partPos = new Vec3d((playerEye.x - blockPos.x) * (1 - tempScale), (playerEye.y - blockPos.y) * (1 - tempScale), (playerEye.z - blockPos.z) * (1 - tempScale));
            GlStateManager.translated(partPos.x, partPos.y, partPos.z);
            //GlStateManager.translated(0, (scale*2-1)/4, 0);

            //We're about to shrink the block. Before doing so, we move it relative to the amount we're shrinking it. Also shrink it more based on proximity to player
            GlStateManager.translatef((1 - blockSizeScale * tempScale) / 2, (1 - blockSizeScale * tempScale) / 2, (1 - blockSizeScale * tempScale) / 2);

            //We want the particle to go into the player's shoulder instead of middle of his eyes, so we move it a bit
            //Rotate to players look vector. Then move particle. Then rotate back
            GlStateManager.rotatef(-player.getRotationYawHead(), 0, 1, 0);
            GlStateManager.rotatef(player.rotationPitch, 1, 0, 0);
            GlStateManager.translated(startXOffset * (1 - scale), yOffset * (1 - scale), 0);
            GlStateManager.rotatef(player.rotationPitch, -1, 0, 0);
            GlStateManager.rotatef(-player.getRotationYawHead(), 0, -1, 0);

            //Adjust scale of particle
            GlStateManager.scalef(blockSizeScale * tempScale, blockSizeScale * tempScale, blockSizeScale * tempScale);

            //The render call below rotates the particle for some reason. We rotate it the opposite way first to negate it.
            GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);

            try {
                if (Math.abs(Math.floor(tempScale)) < 1)
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

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        //This blend function allows you to use a constant alpha, which is defined later
        GlStateManager.blendFunc(GL14.GL_CONSTANT_ALPHA, GL14.GL_ONE_MINUS_CONSTANT_ALPHA);

        //todo Have the gadget have an option for fading/shrinking blocks in the table.

        GlStateManager.translated(x, y, z);
        //GlStateManager.translatef((1-0.99f)/2, (1-0.99f)/2, (1-0.99f)/2);
        //GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
        //GlStateManager.scalef(0.99f, 0.99f, 0.99f);
        GL14.glBlendColor(1F, 1F, 1F, scale); //Set the alpha of the blocks we are rendering
        try {
            IBakedModel ibakedmodel = blockrendererdispatcher.getModelForState(renderState);
            Random random = new Random(42L);
            //blockrendererdispatcher.renderBlockBrightness(renderState, 1.0f);
            for (Direction direction : Direction.values()) {
                if (!(getWorld().getBlockState(tile.getPos().offset(direction)).getBlock() instanceof RenderBlock)) {
                    renderModelBrightnessColorQuads(1f, 1f, 1f, 1f, ibakedmodel.getQuads(renderState, direction, random));
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
        //Disable blend
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
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
