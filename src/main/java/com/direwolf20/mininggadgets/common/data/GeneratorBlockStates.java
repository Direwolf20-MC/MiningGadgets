package com.direwolf20.mininggadgets.common.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class GeneratorBlockStates extends BlockStateProvider {
    public GeneratorBlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, MiningGadgets.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
//        horizontalBlock(ModBlocks.MODIFICATION_TABLE.get(), models().orientableWithBottom(
//                Objects.requireNonNull(ModBlocks.MODIFICATION_TABLE.get().getRegistryName()).getPath(),
//                modLoc("block/modificationtable_side"),
//                modLoc("block/modificationtable_front"),
//                modLoc("block/modificationtable_bottom"),
//                modLoc("block/modificationtable_top")
//        ).texture("particle", modLoc("block/modificationtable_side")));

        horizontalBlock(ModBlocks.MODIFICATION_TABLE.get(), new ModelFile.UncheckedModelFile(modLoc("block/modificationtable")));

        // Render block
        buildCubeAll(ModBlocks.RENDER_BLOCK.get());
        buildCubeAll(ModBlocks.MINERS_LIGHT.get());
    }

    private void buildCubeAll(Block block) {
        getVariantBuilder(block).forAllStates(state ->
                ConfiguredModel.builder().modelFile(cubeAll(block)).build()
        );
    }
}
