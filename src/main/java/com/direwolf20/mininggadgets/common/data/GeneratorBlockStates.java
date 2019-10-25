package com.direwolf20.mininggadgets.common.data;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

public class GeneratorBlockStates extends BlockStateProvider {
    public GeneratorBlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, MiningGadgets.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ResourceLocation side = modLoc("block/modificationtable_side");
        // Sorry for the formatting on this one, it's because we have to define all the sides :(
        getVariantBuilder(ModBlocks.MODIFICATION_TABLE.get()).forAllStates(state ->
                ConfiguredModel.builder().modelFile(cube(
                        ModBlocks.MODIFICATION_TABLE.get().getRegistryName().getPath(),
                        modLoc("block/modificationtable_bottom"),
                        modLoc("block/modificationtable_top"),
                        side, side, side, side
                ).texture("particle", side)).build()
        );

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
