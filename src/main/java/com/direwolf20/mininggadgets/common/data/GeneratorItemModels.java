package com.direwolf20.mininggadgets.common.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.setup.Registration;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;


public class GeneratorItemModels extends ItemModelProvider {
    public GeneratorItemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MiningGadgets.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // Register all the upgrade items
        Registration.UPGRADE_ITEMS.getEntries().forEach(item -> {
            String path = item.getId().getPath();
            singleTexture(path, mcLoc("item/handheld"), "layer0", modLoc("item/" + path));
        });

        // Our block items
        registerBlockModel(Registration.MODIFICATION_TABLE);
        registerBlockModel(Registration.MINERS_LIGHT);
    }

    private void registerBlockModel(DeferredHolder<Block, ?> block) {
        String path = block.getId().getPath();
        getBuilder(path).parent(new ModelFile.UncheckedModelFile(modLoc("block/" + path)));
    }

    @Override
    public String getName() {
        return "Item Models";
    }
}
