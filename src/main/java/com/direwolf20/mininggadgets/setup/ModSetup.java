package com.direwolf20.mininggadgets.setup;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.events.ServerTickHandler;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeBatteryLevels;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSetup {
    public static void init(final FMLCommonSetupEvent event) {
        NeoForge.EVENT_BUS.register(ServerTickHandler.class);
    }

    public static final String TAB_NAME = "mininggadgets";
    public static DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MiningGadgets.MOD_ID);
    public static DeferredHolder<CreativeModeTab, CreativeModeTab> TAB_MININGGADGETS = TABS.register(TAB_NAME, () -> CreativeModeTab.builder()
            .title(Component.literal("Mining Gadgets"))
            .icon(() -> new ItemStack(Registration.MININGGADGET.get()))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .displayItems((featureFlags, output) -> {
                Registration.ITEMS.getEntries().stream().filter(e -> e != Registration.MINERS_LIGHT_ITEM)
                        .forEach(e -> {
                            // Normal
                            Item item = e.get();
                            output.accept(item);

                            // Charged
                            if (item instanceof MiningGadget) {
                                ItemStack stack = new ItemStack(item);
                                stack.getOrCreateTag().putInt("energy", UpgradeBatteryLevels.BATTERY.getPower());
                                output.accept(stack);
                            }
                        });
                Registration.UPGRADE_ITEMS.getEntries().stream().filter(e -> e != Registration.MINERS_LIGHT_ITEM)
                        .forEach(e -> {
                            // Normal
                            Item item = e.get();
                            output.accept(item);

                            // Charged
                            if (item instanceof MiningGadget) {
                                ItemStack stack = new ItemStack(item);
                                stack.getOrCreateTag().putInt("energy", UpgradeBatteryLevels.BATTERY.getPower());
                                output.accept(stack);
                            }
                        });
            })
            .build());
}
