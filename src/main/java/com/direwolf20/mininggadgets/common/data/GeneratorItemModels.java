package com.direwolf20.mininggadgets.common.data;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;

public class GeneratorItemModels extends ItemModelProvider {
    public GeneratorItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, MiningGadgets.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // The mining gadget gets to use some super call stuffs
        /*getBuilder(ModItems.MININGGADGET.get().getRegistryName().getPath())
                .parent(new ModelFile.ExistingModelFile(mcLoc("item/handheld"), existingFileHelper))
                .texture("layer0", modLoc("item/mininggadget"))
                .transforms()
                    .transform(ModelBuilder.Perspective.THIRDPERSON_RIGHT)
                        .rotation(0, 80, 0)
                        .translation(0, 0, 0)
                        .scale(0.5f)
                    .end()
                    .transform(ModelBuilder.Perspective.FIRSTPERSON_RIGHT)
                        .rotation(0, 80, 0)
                        .translation(6, 0, -7)
                        .scale(1f)
                    .end()
                .end();*/

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
