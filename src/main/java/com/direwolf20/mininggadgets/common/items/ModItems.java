package com.direwolf20.mininggadgets.common.items;

import com.direwolf20.mininggadgets.Setup;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(bus = Bus.MOD, modid = Setup.MOD_ID)
@ObjectHolder(Setup.MOD_ID)
public class ModItems {

    @ObjectHolder("mininggadget")
    public static MiningGadget MININGGADGET;

    @SubscribeEvent
    public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
        Item.Properties groupedProps = new Item.Properties().group(Setup.getItemGroup());

        // Keep in mind that items & item blocks are loaded into the ItemGroup
        // in order of registry so don't put things below the minerslight / renderblock
        event.getRegistry().registerAll(
                // Items
                new MiningGadget(),

                // BlockItems
                new BlockItem(ModBlocks.MINERSLIGHT, groupedProps).setRegistryName("minerslight")
        );
    }
}
