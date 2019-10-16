package com.direwolf20.mininggadgets.common.items;

import com.direwolf20.mininggadgets.MiningGadgets;
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

import java.util.Arrays;

@EventBusSubscriber(bus = Bus.MOD, modid = MiningGadgets.MOD_ID)
@ObjectHolder(MiningGadgets.MOD_ID)
public class ModItems {

    @ObjectHolder("mininggadget")
    public static MiningGadget MININGGADGET;
    @ObjectHolder("upgrade_empty")
    public static UpgradeCard UPGRADE_EMPTY;
    @ObjectHolder("upgrade_silk")               public static UpgradeCard UPGRADE_SILK;
    @ObjectHolder("upgrade_fortune_1")          public static UpgradeCard UPGRADE_FORTUNE_1;
    @ObjectHolder("upgrade_fortune_2")          public static UpgradeCard UPGRADE_FORTUNE_2;
    @ObjectHolder("upgrade_fortune_3")          public static UpgradeCard UPGRADE_FORTUNE_3;
    @ObjectHolder("upgrade_light_placer")       public static UpgradeCard LIGHT_PLACER;
    @ObjectHolder("upgrade_three_by_three")     public static UpgradeCard THREE_BY_THREE;
    @ObjectHolder("upgrade_void_junk")          public static UpgradeCard VOID_JUNK;
    @ObjectHolder("upgrade_magnet")             public static UpgradeCard MAGNET;
    @ObjectHolder("upgrade_efficiency_1")       public static UpgradeCard UPGRADE_EFFICIENCY_1;
    @ObjectHolder("upgrade_efficiency_2")       public static UpgradeCard UPGRADE_EFFICIENCY_2;
    @ObjectHolder("upgrade_efficiency_3")       public static UpgradeCard UPGRADE_EFFICIENCY_3;
    @ObjectHolder("upgrade_efficiency_4")       public static UpgradeCard UPGRADE_EFFICIENCY_4;
    @ObjectHolder("upgrade_efficiency_5")       public static UpgradeCard UPGRADE_EFFICIENCY_5;

    @SubscribeEvent
    public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
        Item.Properties groupedProps = new Item.Properties().group(Setup.getItemGroup());

        // Keep in mind that items & item blocks are loaded into the ItemGroup
        // in order of registry so don't put things below the minerslight / renderblock

        // Items
        event.getRegistry().register(new MiningGadget());

        // Register our upgrades :)
        // Dire, note that all upgrade tiers are now handled in the Enum :+1:
        Arrays.stream(Upgrade.values()).forEach( upgrade -> event.getRegistry().register(upgrade.getCard()) );

        // BlockItems
        event.getRegistry().register(new BlockItem(ModBlocks.MINERSLIGHT, groupedProps).setRegistryName("minerslight"));
        event.getRegistry().register(new BlockItem(ModBlocks.MODIFICATIONTABLE, groupedProps).setRegistryName("modificationtable"));
    }
}
