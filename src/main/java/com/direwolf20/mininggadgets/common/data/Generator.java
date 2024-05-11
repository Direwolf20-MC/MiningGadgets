package com.direwolf20.mininggadgets.common.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;


@EventBusSubscriber(modid = MiningGadgets.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Generator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        boolean includeServer = event.includeServer();
        boolean includeClient = event.includeClient();
        ExistingFileHelper helper = event.getExistingFileHelper();

        // Client
        generator.addProvider(includeClient, new GeneratorLanguage(packOutput));
        generator.addProvider(includeClient, new GeneratorItemModels(packOutput, helper));

        // Server
        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(GeneratorLoot::new, LootContextParamSets.BLOCK)), event.getLookupProvider()));
        generator.addProvider(includeServer, new GeneratorRecipes(packOutput, event.getLookupProvider()));
        generator.addProvider(includeServer, new GeneratorBlockTags(packOutput, event.getLookupProvider(), generator, helper));
        generator.addProvider(includeServer, new GeneratorBlockStates(packOutput, helper));
    }
}
