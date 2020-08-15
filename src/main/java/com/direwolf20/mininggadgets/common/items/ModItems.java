package com.direwolf20.mininggadgets.common.items;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    // The item group is the creative tab it will go into.
    public static final Item.Properties ITEM_GROUP = new Item.Properties().group(MiningGadgets.itemGroup);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MiningGadgets.MOD_ID);

    // We have a separate register just to contain all of the upgrades for quick reference
    public static final DeferredRegister<Item> UPGRADE_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MiningGadgets.MOD_ID);

    // Items
    public static final RegistryObject<Item> MININGGADGET = ITEMS.register("mininggadget", MiningGadget::new);

    // Block items
    public static final RegistryObject<Item> MODIFICATION_TABLE_ITEM = ITEMS.register("modificationtable", () -> new BlockItem(ModBlocks.MODIFICATION_TABLE.get(), ITEM_GROUP));
    public static final RegistryObject<Item> MINERS_LIGHT_ITEM = ITEMS.register("minerslight", () -> new BlockItem(ModBlocks.MINERS_LIGHT.get(), ITEM_GROUP.maxStackSize(1)));

    /**
     * Upgrades are a bit ugly.. Soz
     * This one is actually kinda
     */
    public static final RegistryObject<Item> UPGRADE_EMPTY =    UPGRADE_ITEMS.register("upgrade_empty", Upgrade.EMPTY::getCard);
    public static final RegistryObject<Item> SILK =             UPGRADE_ITEMS.register("upgrade_silk", Upgrade.SILK::getCard);
    public static final RegistryObject<Item> VOID_JUNK =        UPGRADE_ITEMS.register("upgrade_void_junk", Upgrade.VOID_JUNK::getCard);
    public static final RegistryObject<Item> MAGNET =           UPGRADE_ITEMS.register("upgrade_magnet", Upgrade.MAGNET::getCard);
    public static final RegistryObject<Item> THREE_BY_THREE =   UPGRADE_ITEMS.register("upgrade_three_by_three", Upgrade.THREE_BY_THREE::getCard);
    public static final RegistryObject<Item> LIGHT_PLACER =     UPGRADE_ITEMS.register("upgrade_light_placer", Upgrade.LIGHT_PLACER::getCard);
    public static final RegistryObject<Item> FREEZING =         UPGRADE_ITEMS.register("upgrade_freezing", Upgrade.FREEZING::getCard);
    public static final RegistryObject<Item> FORTUNE_1 =        UPGRADE_ITEMS.register("upgrade_fortune_1", Upgrade.FORTUNE_1::getCard);
    public static final RegistryObject<Item> FORTUNE_2 =        UPGRADE_ITEMS.register("upgrade_fortune_2", Upgrade.FORTUNE_2::getCard);
    public static final RegistryObject<Item> FORTUNE_3 =        UPGRADE_ITEMS.register("upgrade_fortune_3", Upgrade.FORTUNE_3::getCard);
    public static final RegistryObject<Item> RANGE_1 =          UPGRADE_ITEMS.register("upgrade_range_1", Upgrade.RANGE_1::getCard);
    public static final RegistryObject<Item> RANGE_2 =          UPGRADE_ITEMS.register("upgrade_range_2", Upgrade.RANGE_2::getCard);
    public static final RegistryObject<Item> RANGE_3 =          UPGRADE_ITEMS.register("upgrade_range_3", Upgrade.RANGE_3::getCard);
    public static final RegistryObject<Item> BATTERY_1 =        UPGRADE_ITEMS.register("upgrade_battery_1", Upgrade.BATTERY_1::getCard);
    public static final RegistryObject<Item> BATTERY_2 =        UPGRADE_ITEMS.register("upgrade_battery_2", Upgrade.BATTERY_2::getCard);
    public static final RegistryObject<Item> BATTERY_3 =        UPGRADE_ITEMS.register("upgrade_battery_3", Upgrade.BATTERY_3::getCard);
    public static final RegistryObject<Item> EFFICIENCY_1 =     UPGRADE_ITEMS.register("upgrade_efficiency_1", Upgrade.EFFICIENCY_1::getCard);
    public static final RegistryObject<Item> EFFICIENCY_2 =     UPGRADE_ITEMS.register("upgrade_efficiency_2", Upgrade.EFFICIENCY_2::getCard);
    public static final RegistryObject<Item> EFFICIENCY_3 =     UPGRADE_ITEMS.register("upgrade_efficiency_3", Upgrade.EFFICIENCY_3::getCard);
    public static final RegistryObject<Item> EFFICIENCY_4 =     UPGRADE_ITEMS.register("upgrade_efficiency_4", Upgrade.EFFICIENCY_4::getCard);
    public static final RegistryObject<Item> EFFICIENCY_5 =     UPGRADE_ITEMS.register("upgrade_efficiency_5", Upgrade.EFFICIENCY_5::getCard);
    public static final RegistryObject<Item> PAVER =            UPGRADE_ITEMS.register("upgrade_paver", Upgrade.PAVER::getCard);
}
