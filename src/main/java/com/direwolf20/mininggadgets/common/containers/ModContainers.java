package com.direwolf20.mininggadgets.common.containers;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.client.ClientSetup;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @implNote Container screens are registered in {@link ClientSetup#setup()}
 */
public class ModContainers {
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MiningGadgets.MOD_ID);

    // Our containers
    public static final RegistryObject<ContainerType<ModificationTableContainer>> MODIFICATIONTABLE_CONTAINER
            = CONTAINERS.register("modificationtable", () -> IForgeContainerType.create(ModificationTableContainer::new));

    public static final RegistryObject<ContainerType<FilterContainer>> FILTER_CONTAINER
            = CONTAINERS.register("filter_container", () -> IForgeContainerType.create(FilterContainer::new));
}
