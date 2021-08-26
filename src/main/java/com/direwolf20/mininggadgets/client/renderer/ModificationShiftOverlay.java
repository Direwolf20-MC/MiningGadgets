package com.direwolf20.mininggadgets.client.renderer;

import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.tiles.ModificationTableTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import java.util.List;

public class ModificationShiftOverlay {

    public static void render(RenderWorldLastEvent evt, Player player) {
        HitResult pick = player.pick(5, 0, false);
        if (pick.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockHitResult trace = (BlockHitResult) pick;
        if (player.level.getBlockState(trace.getBlockPos()).getBlock() != ModBlocks.MODIFICATION_TABLE.get()) {
            return;
        }

        BlockEntity blockEntity = player.level.getBlockEntity(trace.getBlockPos());
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

        Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        BlockPos blockPos = ((BlockHitResult) pick).getBlockPos();

        double distance = player.getPosition(evt.getPartialTicks()).distanceTo(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        float scaleFactor = Math.max(.2f, ((float) distance / 10) + .1f);

        PoseStack matrix = evt.getMatrixStack();
        matrix.pushPose();
        matrix.translate(-view.x, -view.y, -view.z);
        matrix.translate(blockPos.getX() + .5f, blockPos.getY() + 1, blockPos.getZ() + .5f);
        matrix.scale(scaleFactor, scaleFactor, scaleFactor);
        matrix.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());

        MultiBufferSource.BufferSource outlineLayerBuffer = Minecraft.getInstance().renderBuffers().bufferSource();

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
            BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(upgradeStack, Minecraft.getInstance().level, null, 0);
            Minecraft.getInstance().getItemRenderer().render(upgradeStack, ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, false, matrix, outlineLayerBuffer, 15728880, OverlayTexture.NO_OVERLAY, model);
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
