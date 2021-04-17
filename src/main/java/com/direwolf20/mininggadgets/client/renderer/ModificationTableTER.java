package com.direwolf20.mininggadgets.client.renderer;

import com.direwolf20.mininggadgets.common.items.ModItems;
import com.direwolf20.mininggadgets.common.tiles.ModificationTableTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.items.CapabilityItemHandler;

public class ModificationTableTER extends TileEntityRenderer<ModificationTableTileEntity> {
    public ModificationTableTER(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(ModificationTableTileEntity tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLights, int combinedOverlay) {
        ItemStack stack = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(e -> e.getStackInSlot(0)).orElse(ItemStack.EMPTY);
        if (stack.isEmpty()) {
            return;
        }

        boolean isSimple = stack.getItem().equals(ModItems.MININGGADGET_SIMPLE.get());
        boolean isFancy = stack.getItem().equals(ModItems.MININGGADGET_FANCY.get());
        boolean isCool = !isSimple && !isFancy;

        Direction facing = tile.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        matrix.pushPose();
        matrix.translate(0, .81f, 0);

        float leftModifier = isCool ? 0 : (isFancy ? .15f : .2f);
        if (facing == Direction.SOUTH) {
            matrix.translate(.7f - leftModifier, 0, .85f);
            matrix.mulPose(Vector3f.YP.rotationDegrees(90));
        } else if (facing == Direction.EAST) {
            matrix.translate(.85f, 0, .3f + leftModifier);
            matrix.mulPose(Vector3f.YP.rotationDegrees(180));
        } else if (facing == Direction.NORTH) {
            matrix.translate(.3f + leftModifier, 0, .15f);
            matrix.mulPose(Vector3f.YP.rotationDegrees(270));
        } else {
            matrix.translate(.15f, 0, .7f - leftModifier);
        }
        matrix.mulPose(Vector3f.ZN.rotationDegrees(90));

        if (isCool) {
            matrix.scale(.65f, .65f, .65f);
        } else {
            matrix.scale(.8f, .8f, .8f);
        }

        IBakedModel model = Minecraft.getInstance().getItemRenderer().getModel(stack, Minecraft.getInstance().level, null);
        Minecraft.getInstance().getItemRenderer().render(stack, ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, false, matrix,buffer, combinedLights, combinedOverlay, model);
        matrix.popPose();
    }
}
