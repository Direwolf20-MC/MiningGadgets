package com.direwolf20.mininggadgets.common.containers;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.client.screens.MiningScreen;
import com.direwolf20.mininggadgets.client.screens.ModificationTableScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(MiningGadgets.MOD_ID)
@Mod.EventBusSubscriber(bus = Bus.MOD, modid = MiningGadgets.MOD_ID)
public class ModContainers {

    @ObjectHolder("modificationtable")
    public static ContainerType<ModificationTableContainer> MODIFICATIONTABLE_CONTAINER
            = IForgeContainerType.create(ModificationTableContainer::new);

    @ObjectHolder("mininggadget_container")
    public static ContainerType<MiningContainer> MINING_CONTAINER
            = IForgeContainerType.create(MiningContainer::new);

    /**
     * Container registry
     */
    @SubscribeEvent
    public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().registerAll(
                MODIFICATIONTABLE_CONTAINER.setRegistryName("modificationtable"),
                MINING_CONTAINER.setRegistryName("mininggadget_container")
        );
    }

    /**
     * Called from some Client Dist runner in the main class
     */
    @OnlyIn(Dist.CLIENT)
    public static void registerContainerScreens() {
        ScreenManager.registerFactory(MODIFICATIONTABLE_CONTAINER, ModificationTableScreen::new);
        ScreenManager.registerFactory(MINING_CONTAINER, MiningScreen::new);
    }
}
