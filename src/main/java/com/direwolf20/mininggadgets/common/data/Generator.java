package com.direwolf20.mininggadgets.common.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MiningGadgets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Generator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var includeServer = event.includeServer();
        var includeClient = event.includeClient();
        var generator = event.getGenerator();
        var helper = event.getExistingFileHelper();

        // Client
        generator.addProvider(includeClient, new GeneratorLanguage(generator));
        generator.addProvider(includeClient, new GeneratorItemModels(generator, helper));

        // Server
        generator.addProvider(includeServer, new GeneratorLoot(generator));
        generator.addProvider(includeServer, new GeneratorRecipes(generator));
        generator.addProvider(includeServer, new GeneratorBlockTags(generator, helper));
        generator.addProvider(includeServer, new GeneratorBlockStates(generator, helper));
    }
}
