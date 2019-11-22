package com.direwolf20.mininggadgets.client.events;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.client.renderer.MiningGadgetModel;
import com.direwolf20.mininggadgets.common.items.ModItems;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.function.Function;

public class EventModelBake
{
    public static void addListeners(IEventBus modEventBus) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            modEventBus.addListener(EventModelBake::onModelBake);
            modEventBus.addListener(EventModelBake::onModelRegistry);
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static void onModelBake(ModelBakeEvent event) {
        Map<ResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
        swapModels(modelRegistry, getItemModelLocation(ModItems.MININGGADGET.get()),
                t -> new MiningGadgetModel(t).loadPartials(event));
    }

    @OnlyIn(Dist.CLIENT)
    public static void onModelRegistry(ModelRegistryEvent event) {
        for (String location : MiningGadgetModel.getCustomModelLocations())
            ModelLoader.addSpecialModel(new ResourceLocation(MiningGadgets.MOD_ID, "item/" + location));
    }

    protected static ModelResourceLocation getItemModelLocation(Item item) {
        return new ModelResourceLocation(item.getRegistryName(), "inventory");
    }

    @OnlyIn(Dist.CLIENT)
    protected static <T extends IBakedModel> void swapModels(Map<ResourceLocation, IBakedModel> modelRegistry,
                                                             ModelResourceLocation location, Function<IBakedModel, T> factory) {
        modelRegistry.put(location, factory.apply(modelRegistry.get(location)));
    }
}
