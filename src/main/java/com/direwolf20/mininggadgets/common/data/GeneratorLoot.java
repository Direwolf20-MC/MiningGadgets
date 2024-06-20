package com.direwolf20.mininggadgets.common.data;

import com.direwolf20.mininggadgets.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.List;

public class GeneratorLoot extends VanillaBlockLoot {

    public GeneratorLoot(HolderLookup.Provider p_344962_) {
        super(p_344962_);
    }

    @Override
    protected void generate() {
        dropSelf(Registration.MODIFICATION_TABLE.get());
        add(Registration.RENDER_BLOCK.get(), noDrop());
        add(Registration.MINERS_LIGHT.get(), noDrop());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        List<Block> knownBlocks = new ArrayList<>();
        knownBlocks.addAll(Registration.BLOCKS.getEntries().stream().map(DeferredHolder::get).toList());
        return knownBlocks;
    }
}
