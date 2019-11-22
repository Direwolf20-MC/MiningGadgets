package com.direwolf20.mininggadgets.client.renderer;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraftforge.client.event.ModelBakeEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import java.util.Arrays;
import java.util.List;

public class MiningGadgetModel extends CustomRenderItemBakedModel
{
    public IBakedModel rf_capacitor;

    public MiningGadgetModel(IBakedModel template) {
        super(template);
    }

    public static List<String> getCustomModelLocations() {
        String p = "mininggadget/";
        return Arrays.asList(p + "rf_capacitor");
    }

    @Override
    public CustomRenderItemBakedModel loadPartials(ModelBakeEvent event)
    {
        String p = "mininggadget/";
        this.rf_capacitor = loadCustomModel(event, p+"rf_capacitor");
        return this;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return super.handlePerspective(cameraTransformType);
    }
}