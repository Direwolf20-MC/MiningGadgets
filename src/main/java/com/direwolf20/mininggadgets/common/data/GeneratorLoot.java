package com.direwolf20.mininggadgets.common.data;

import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.*;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

// Thanks Lex
public class GeneratorLoot extends LootTableProvider {
    public GeneratorLoot(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return ImmutableList.of(
                Pair.of(Blocks::new, LootParameterSets.BLOCK)
        );
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationresults) {
        map.forEach((name, table) -> LootTableManager.validateLootTable(validationresults, name, table));
    }

    private static class Blocks extends BlockLootTables {
        @Override
        protected void addTables() {
            this.registerDropSelfLootTable(ModBlocks.MODIFICATION_TABLE.get());
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return Collections.singletonList(ModBlocks.MODIFICATION_TABLE.get());
        }
    }
}
