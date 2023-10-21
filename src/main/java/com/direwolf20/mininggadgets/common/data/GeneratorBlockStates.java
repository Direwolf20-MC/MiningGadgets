package com.direwolf20.mininggadgets.common.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class GeneratorBlockStates extends BlockStateProvider {
    public GeneratorBlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MiningGadgets.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
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
