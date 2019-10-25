package com.direwolf20.mininggadgets.common.data;

import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraftforge.fml.RegistryObject;

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
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationResults validationresults) {
        map.forEach((name, table) -> LootTableManager.func_215302_a(validationresults, name, table, map::get));
    }

    private static class Blocks extends BlockLootTables {
        /**
         * @implNote This assumes that all of our Blocks drop themselves, if this is ever not the case
         *           then this implementation would need to either be split into two lists in ModBlocks
         *           or itemised into a per block registry call.
         */
        @Override
        protected void addTables() {
            ModBlocks.BLOCKS.getEntries().forEach(blockReg -> this.registerDropSelfLootTable(blockReg.get()));
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
        }
    }
}
