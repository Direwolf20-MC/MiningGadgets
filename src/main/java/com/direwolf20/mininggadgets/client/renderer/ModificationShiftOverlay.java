package com.direwolf20.mininggadgets.client.renderer;

import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.tiles.ModificationTableTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import java.util.List;

public class ModificationShiftOverlay {

    public static void render(RenderWorldLastEvent evt, PlayerEntity player) {
        RayTraceResult pick = player.pick(5, 0, false);
        if (pick.getType() != RayTraceResult.Type.BLOCK) {
            return;
        }

        BlockRayTraceResult trace = (BlockRayTraceResult) pick;
        if (player.level.getBlockState(trace.getBlockPos()).getBlock() != ModBlocks.MODIFICATION_TABLE.get()) {
            return;
        }

        TileEntity blockEntity = player.level.getBlockEntity(trace.getBlockPos());
        if (!(blockEntity instanceof ModificationTableTileEntity)) {
            return;
        }

        // Finally, lets try and render something if we have a gadget in the main slot
        ItemStack stack = ((ModificationTableTileEntity) blockEntity).handler.map(e -> e.getStackInSlot(0)).orElse(ItemStack.EMPTY);
        if (stack.isEmpty() || !(stack.getItem() instanceof MiningGadget)) {
            return;
        }

        List<Upgrade> upgrades = UpgradeTools.getUpgrades(stack);
        if (upgrades.isEmpty()) {
            return;
        }

        Vector3d view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        BlockPos blockPos = ((BlockRayTraceResult) pick).getBlockPos();

        double distance = player.getPosition(evt.getPartialTicks()).distanceTo(new Vector3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        float scaleFactor = (float) distance / 10;

        MatrixStack matrix = evt.getMatrixStack();
        matrix.pushPose();
        matrix.translate(-view.x, -view.y, -view.z);
        matrix.translate(blockPos.getX() + .5f, blockPos.getY() + 1, blockPos.getZ() + .5f);
        matrix.scale(scaleFactor, scaleFactor, scaleFactor);
        matrix.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());

        IRenderTypeBuffer.Impl outlineLayerBuffer = Minecraft.getInstance().renderBuffers().bufferSource();

        int x = 0, y = 0;

        // Fixes centering issues with 1 or 2 upgrades
        float offset = upgrades.size() / 3 > 0
            ? -1.15f
            : (upgrades.size() > 1
                ? -(upgrades.size() / 3f)
                : -.2f);

        for (Upgrade upgrade : upgrades) {
            matrix.pushPose();
            matrix.translate(offset + x, y, 0);
            matrix.mulPose(Vector3f.YP.rotationDegrees(90));
            matrix.mulPose(Vector3f.XP.rotationDegrees(26));
            ItemStack upgradeStack = upgrade.getStack();
            IBakedModel model = Minecraft.getInstance().getItemRenderer().getModel(upgradeStack, Minecraft.getInstance().level, null);
            Minecraft.getInstance().getItemRenderer().render(upgradeStack, ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, false, matrix, outlineLayerBuffer, 15728880, OverlayTexture.NO_OVERLAY, model);
            x += 1;
            if (x > 2) {
                x = 0;
                y += 1;
            }
            matrix.popPose();
        }

        outlineLayerBuffer.endBatch();

        matrix.popPose();
    }
}
