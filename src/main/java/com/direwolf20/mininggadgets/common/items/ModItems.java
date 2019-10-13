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
        Item.Properties properties = new Item.Properties().group(Setup.getItemGroup());

        event.getRegistry().registerAll(
                // Items
                new MiningGadget(),

                // BlockItems
                new BlockItem(ModBlocks.RENDERBLOCK, properties).setRegistryName("renderblock"),
                new BlockItem(ModBlocks.MINERSLIGHT, properties).setRegistryName("minerslight")
        );
    }
}
