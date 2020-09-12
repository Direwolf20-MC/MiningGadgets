package com.direwolf20.mininggadgets.common.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class GeneratorItemModels extends ItemModelProvider {
    public GeneratorItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, MiningGadgets.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // Register all of the upgrade items
        ModItems.UPGRADE_ITEMS.getEntries().forEach(item -> {
            String path = item.get().getRegistryName().getPath();
            singleTexture(path, mcLoc("item/handheld"), "layer0", modLoc("item/" + path));
        });

        // Our block items
        registerBlockModel(ModBlocks.MODIFICATION_TABLE.get());
        registerBlockModel(ModBlocks.MINERS_LIGHT.get());
    }

    private void registerBlockModel(Block block) {
        String path = block.getRegistryName().getPath();
        getBuilder(path).parent(new ModelFile.UncheckedModelFile(modLoc("block/" + path)));
    }

    @Override
    public String getName() {
        return "Item Models";
    }
}
