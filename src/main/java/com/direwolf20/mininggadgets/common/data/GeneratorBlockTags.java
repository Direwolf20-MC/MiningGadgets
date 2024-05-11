package com.direwolf20.mininggadgets.common.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class GeneratorBlockTags extends BlockTagsProvider {
    public GeneratorBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, DataGenerator generator, ExistingFileHelper helper) {
        super(output, lookupProvider, MiningGadgets.MOD_ID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(Registration.MODIFICATION_TABLE.get());
    }
}
