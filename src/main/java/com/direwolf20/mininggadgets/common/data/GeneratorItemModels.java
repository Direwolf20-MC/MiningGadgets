package com.direwolf20.mininggadgets.common.data;

import com.direwolf20.mininggadgets.setup.Registration;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;

public class GeneratorItemModels {

    private final ItemModelGenerators generator;

    public GeneratorItemModels(ItemModelGenerators generator) {
        this.generator = generator;
    }

    public void init() {
        Registration.UPGRADE_ITEMS.getEntries().forEach(itemDeferredHolder -> {
            generator.generateFlatItem(itemDeferredHolder.get(), ModelTemplates.FLAT_ITEM);
        });

        generator.itemModelOutput.accept(Registration.MININGGADGET.get(),
                ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(Registration.MININGGADGET.get())));

        generator.itemModelOutput.accept(Registration.MININGGADGET_FANCY.get(),
                ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(Registration.MININGGADGET_FANCY.get())));

        generator.itemModelOutput.accept(Registration.MININGGADGET_SIMPLE.get(),
                ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(Registration.MININGGADGET_SIMPLE.get())));
    }
}
