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
        var packOutput = event.getGenerator().getPackOutput();

        // Client
        generator.addProvider(includeClient, new GeneratorLanguage(packOutput));
        generator.addProvider(includeClient, new GeneratorItemModels(packOutput, helper));

        // Server
        generator.addProvider(includeServer, new GeneratorLoot(packOutput));
        generator.addProvider(includeServer, new GeneratorRecipes(packOutput));
        generator.addProvider(includeServer, new GeneratorBlockTags(packOutput, event.getLookupProvider(), generator, helper));
        generator.addProvider(includeServer, new GeneratorBlockStates(packOutput, helper));
    }
}
