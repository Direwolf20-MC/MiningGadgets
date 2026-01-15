package com.direwolf20.mininggadgets.client.renderer;

import net.minecraft.client.resources.model.ModelLoadingPlugin;
import net.minecraft.resources.ResourceLocation;

public class GadgetModelLoader implements ModelLoadingPlugin {

    @Override
    public void onInitializeModelLoader(Context context) {
        context.addModels(
                new ResourceLocation("mininggadgets", "item/mininggadget"),
                new ResourceLocation("mininggadgets", "item/mininggadget_fancy"),
                new ResourceLocation("mininggadgets", "item/mininggadget_simple")
        );
    }
}
