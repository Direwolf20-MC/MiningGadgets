package com.direwolf20.mininggadgets.common.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.PackOutput;
import org.jspecify.annotations.NonNull;

public class GeneratorModels extends ModelProvider
{
    public GeneratorModels(PackOutput output)
    {
        super(output, MiningGadgets.MOD_ID);
    }

    @Override
    protected void registerModels(@NonNull BlockModelGenerators blockModels, @NonNull ItemModelGenerators itemModels)
    {
        new GeneratorItemModels(itemModels).init();
        new GeneratorBlockStates(blockModels).init();
    }
}
