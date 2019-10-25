package com.direwolf20.mininggadgets.common.data;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

public class GeneratorBlockStates extends BlockStateProvider {
    public GeneratorBlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, MiningGadgets.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // Sorry for the formatting on this one, it's because we have to define all the sides :(
        getVariantBuilder(ModBlocks.MODIFICATION_TABLE.get()).forAllStates(state ->
                ConfiguredModel.builder().modelFile(
                        getBuilder(ModBlocks.MODIFICATION_TABLE.get().getRegistryName().getPath())
                            .parent(getExistingFile(mcLoc("orientable")))
                                .texture("front",  modLoc("block/modificationtable_side"))
                                .texture("top",  modLoc("block/modificationtable_top"))
                                .texture("bottom",  modLoc("block/modificationtable_bottom"))
                                .texture("side",  modLoc("block/modificationtable_side"))
                ).build()
        );

        // Render block
        buildCubeAll(ModBlocks.RENDER_BLOCK.get(), "block/renderblock");

        // Miners Light
        buildCubeAll(ModBlocks.MINERS_LIGHT.get(), "block/minerslight");
    }

    private void buildCubeAll(Block block, String locationPath) {
        getVariantBuilder(block).forAllStates(state ->
                ConfiguredModel.builder().modelFile(
                        getBuilder(block.getRegistryName().getPath())
                                .parent(getExistingFile(mcLoc("cube_all")))
                                .texture("all",  modLoc(locationPath))
                ).build()
        );
    }
}
