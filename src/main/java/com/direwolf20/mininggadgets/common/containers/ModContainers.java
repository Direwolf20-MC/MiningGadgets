package com.direwolf20.mininggadgets.common.containers;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.client.ClientSetup;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @implNote Container screens are registered in {@link ClientSetup#setup()}
 */
public class ModContainers {
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MiningGadgets.MOD_ID);

    // Our containers
    public static final RegistryObject<MenuType<ModificationTableContainer>> MODIFICATIONTABLE_CONTAINER
            = CONTAINERS.register("modificationtable", () -> IForgeMenuType.create(ModificationTableContainer::new));

    public static final RegistryObject<MenuType<FilterContainer>> FILTER_CONTAINER
            = CONTAINERS.register("filter_container", () -> IForgeMenuType.create(FilterContainer::new));
}
