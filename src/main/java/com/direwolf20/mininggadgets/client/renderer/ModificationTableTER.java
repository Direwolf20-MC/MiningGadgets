package com.direwolf20.mininggadgets.client.renderer;

import com.direwolf20.mininggadgets.common.tiles.ModificationTableTileEntity;
import com.direwolf20.mininggadgets.setup.Registration;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class ModificationTableTER implements BlockEntityRenderer<ModificationTableTileEntity, BlockEntityRenderState> {
    public ModificationTableTER(BlockEntityRendererProvider.Context context) {
    }

    public static class ModificationTableRenderState extends BlockEntityRenderState {
        public ItemStack stack = ItemStack.EMPTY;
        public Direction facing = Direction.NORTH;
        public final ItemStackRenderState item = new ItemStackRenderState();
    }

    @Override
    public @NonNull BlockEntityRenderState createRenderState() {
        return new ModificationTableRenderState();
    }

    @Override
    public void extractRenderState(ModificationTableTileEntity tile, BlockEntityRenderState blockEntityRenderState, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(tile, blockEntityRenderState, partialTick, cameraPos, crumblingOverlay);

        ModificationTableRenderState state = (ModificationTableRenderState) blockEntityRenderState;
        state.stack = ItemStack.EMPTY;
        state.facing = tile.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        state.item.clear();

        if (tile.getLevel() == null) {
            return;
        }

        var cap = tile.getLevel().getCapability(Capabilities.Item.BLOCK, tile.getBlockPos(), tile.getBlockState(), tile, null);

        if (cap == null) {
            return;
        }

        ItemResource stack = cap.getResource(0);
        if (stack.isEmpty()) {
            return;
        }

        state.stack = stack.toStack().copy();

        Minecraft.getInstance().getItemModelResolver().updateForTopItem(state.item, state.stack, ItemDisplayContext.FIRST_PERSON_LEFT_HAND, tile.getLevel(), null, 0);
    }

    @Override
    public void submit(BlockEntityRenderState blockEntityRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        ModificationTableRenderState state = (ModificationTableRenderState) blockEntityRenderState;
        if (state.stack.isEmpty()) {
            return;
        }

        boolean isSimple = state.stack.getItem() == Registration.MININGGADGET_SIMPLE.get();
        boolean isFancy = state.stack.getItem() == Registration.MININGGADGET_FANCY.get();
        boolean isCool = !isSimple && !isFancy;

        Direction facing = state.facing;

        poseStack.pushPose();
        poseStack.translate(0.0F, 0.81F, 0.0F);

        float leftModifier = isCool ? 0.0F : (isFancy ? 0.15F : 0.2F);

        if (facing == Direction.SOUTH) {
            poseStack.translate(0.7F - leftModifier, 0.0F, 0.85F);
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        } else if (facing == Direction.EAST) {
            poseStack.translate(0.85F, 0.0F, 0.3F + leftModifier);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        } else if (facing == Direction.NORTH) {
            poseStack.translate(0.3F + leftModifier, 0.0F, 0.15F);
            poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
        } else {
            poseStack.translate(0.15F, 0.0F, 0.7F - leftModifier);
        }

        poseStack.mulPose(Axis.ZN.rotationDegrees(90.0F));

        if (isCool) {
            poseStack.scale(0.65F, 0.65F, 0.65F);
        } else {
            poseStack.scale(0.8F, 0.8F, 0.8F);
        }

        state.item.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();
    }
}