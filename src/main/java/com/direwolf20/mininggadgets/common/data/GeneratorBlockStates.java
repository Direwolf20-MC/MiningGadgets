package com.direwolf20.mininggadgets.common.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.setup.Registration;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.resources.Identifier;

public class GeneratorBlockStates {

    private final BlockModelGenerators blockModelGenerators;

    public GeneratorBlockStates(BlockModelGenerators blockModelGenerators) {
        this.blockModelGenerators = blockModelGenerators;
    }

    public void init() {
        blockModelGenerators.createTrivialCube(Registration.MINERS_LIGHT.get());
        blockModelGenerators.createTrivialCube(Registration.RENDER_BLOCK.get());

        Identifier table = Identifier.fromNamespaceAndPath(MiningGadgets.MOD_ID, "block/modificationtable");

        MultiVariant templateManagerVariant = BlockModelGenerators.plainVariant(table);
        blockModelGenerators.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(Registration.MODIFICATION_TABLE.get(), templateManagerVariant)
                        .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING)
        );

//        Identifier table = ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM.create(
//                Registration.MODIFICATION_TABLE.get(),
//                TextureMapping.orientableCube(Registration.MODIFICATION_TABLE.get()),
//                blockModelGenerators.modelOutput);
//
//        MultiVariant templateManagerVariant = BlockModelGenerators.plainVariant(table);
//        blockModelGenerators.blockStateOutput.accept(MultiVariantGenerator.dispatch(Registration.MODIFICATION_TABLE.get(), templateManagerVariant).with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
    }
}
