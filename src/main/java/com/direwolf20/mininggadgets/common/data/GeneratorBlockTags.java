package com.direwolf20.mininggadgets.common.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class GeneratorBlockTags extends BlockTagsProvider {
    public GeneratorBlockTags(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, MiningGadgets.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).addTag(TagKey.create(Registry.BLOCK_REGISTRY, ModBlocks.MODIFICATION_TABLE.getId()));
    }
}
