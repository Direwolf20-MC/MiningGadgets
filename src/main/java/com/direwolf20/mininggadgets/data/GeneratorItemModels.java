package com.direwolf20.mininggadgets.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.items.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Objects;
import java.util.function.Supplier;

public class GeneratorItemModels extends ItemModelProvider {
    public GeneratorItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, MiningGadgets.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // Register all the upgrade items
        ModItems.UPGRADE_ITEMS.getEntries().forEach(this::registerItemModel);

        // Our block items
        registerBlockModel(ModBlocks.MODIFICATION_TABLE.get());
        registerBlockModel(ModBlocks.MINERS_LIGHT.get());
        registerItemModel(ModItems.UPGRADE_EMPTY);
    }

    public void registerItemModel(Supplier<Item> item) {
        String path = Objects.requireNonNull(item.get().getRegistryName()).getPath();
        singleTexture(path, mcLoc("item/handheld"), "layer0", modLoc("item/" + path));
    }

    private void registerBlockModel(Block block) {
        String path = Objects.requireNonNull(block.getRegistryName()).getPath();
        getBuilder(path).parent(new ModelFile.UncheckedModelFile(modLoc("block/" + path)));
    }

    @Override
    public String getName() {
        return "Item Models";
    }
}
