package com.direwolf20.mininggadgets.client.renderer;

import com.direwolf20.mininggadgets.common.tiles.ModificationTableTileEntity;
import com.direwolf20.mininggadgets.setup.Registration;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;

public class ModificationTableTER implements BlockEntityRenderer<ModificationTableTileEntity> {
    public ModificationTableTER(BlockEntityRendererProvider.Context p_173636_) {

    }

    @Override
    public void render(ModificationTableTileEntity tile, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLights, int combinedOverlay) {
        var cap = tile.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, tile.getBlockPos(), tile.getBlockState(), tile, null);
        if (cap == null) return;
        ItemStack stack = cap.getStackInSlot(0);
        if (stack.isEmpty()) {
            return;
        }

        boolean isSimple = stack.getItem().equals(Registration.MININGGADGET_SIMPLE.get());
        boolean isFancy = stack.getItem().equals(Registration.MININGGADGET_FANCY.get());
        boolean isCool = !isSimple && !isFancy;

        Direction facing = tile.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        matrix.pushPose();
        matrix.translate(0, .81f, 0);

        float leftModifier = isCool ? 0 : (isFancy ? .15f : .2f);
        if (facing == Direction.SOUTH) {
            matrix.translate(.7f - leftModifier, 0, .85f);
            matrix.mulPose(Axis.YP.rotationDegrees(90));
        } else if (facing == Direction.EAST) {
            matrix.translate(.85f, 0, .3f + leftModifier);
            matrix.mulPose(Axis.YP.rotationDegrees(180));
        } else if (facing == Direction.NORTH) {
            matrix.translate(.3f + leftModifier, 0, .15f);
            matrix.mulPose(Axis.YP.rotationDegrees(270));
        } else {
            matrix.translate(.15f, 0, .7f - leftModifier);
        }
        matrix.mulPose(Axis.ZN.rotationDegrees(90));

        if (isCool) {
            matrix.scale(.65f, .65f, .65f);
        } else {
            matrix.scale(.8f, .8f, .8f);
        }

        BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(stack, Minecraft.getInstance().level, null, 0);
        Minecraft.getInstance().getItemRenderer().render(stack, ItemDisplayContext.FIRST_PERSON_LEFT_HAND, false, matrix,buffer, combinedLights, combinedOverlay, model);
        matrix.popPose();
    }
}
