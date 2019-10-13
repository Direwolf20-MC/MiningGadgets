package com.direwolf20.mininggadgets.common.items;

import com.direwolf20.mininggadgets.Setup;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
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

    @ObjectHolder("upgrade_silk")       public static UpgradeCard   UPGRADE_SILK;
    @ObjectHolder("upgrade_fortune_1")    public static UpgradeCard  UPGRADE_FORTUNE_1;
    @ObjectHolder("upgrade_fortune_2")    public static UpgradeCard  UPGRADE_FORTUNE_2;
    @ObjectHolder("upgrade_fortune_3")    public static UpgradeCard  UPGRADE_FORTUNE_3;

    @SubscribeEvent
    public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
        Item.Properties groupedProps = new Item.Properties().group(Setup.getItemGroup());

        // Keep in mind that items & item blocks are loaded into the ItemGroup
        // in order of registry so don't put things below the minerslight / renderblock

        // Items
        event.getRegistry().register(new MiningGadget());

        event.getRegistry().register(new UpgradeCard(Upgrade.SILK, -1)); // example of no tier
        event.getRegistry().register(new UpgradeCard(Upgrade.FORTUNE, 1));
        event.getRegistry().register(new UpgradeCard(Upgrade.FORTUNE, 2));
        event.getRegistry().register(new UpgradeCard(Upgrade.FORTUNE, 3));

        // BlockItems
        event.getRegistry().register(new BlockItem(ModBlocks.MINERSLIGHT, groupedProps).setRegistryName("minerslight"));
    }
}
