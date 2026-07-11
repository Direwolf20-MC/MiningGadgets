package com.direwolf20.mininggadgets.common.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;


@EventBusSubscriber(modid = MiningGadgets.MOD_ID)
public class Generator {
    @SubscribeEvent
    public static void gatherDataClient(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();

        // Client
        generator.addProvider(true, new GeneratorLanguage(packOutput));
        generator.addProvider(true, new GeneratorModels(packOutput));
    }

    @SubscribeEvent
    public static void gatherDataServer(GatherDataEvent.Server event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();

        generator.addProvider(true, new LootTableProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(GeneratorLoot::new, LootContextParamSets.BLOCK)), event.getLookupProvider()));
        generator.addProvider(true, new GeneratorRecipes.Runner(packOutput, event.getLookupProvider()));
        generator.addProvider(true, new GeneratorBlockTags(packOutput, event.getLookupProvider()));
    }
}
