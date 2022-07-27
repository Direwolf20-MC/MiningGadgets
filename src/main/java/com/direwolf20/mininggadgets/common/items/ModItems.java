package com.direwolf20.mininggadgets.common.items;

import com.direwolf20.mininggadgets.api.upgrades.StandardUpgrades;
import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public interface ModItems {
    // The item group is the creative tab it will go into.
    Item.Properties ITEM_GROUP = new Item.Properties().tab(MiningGadgets.itemGroup);
    DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MiningGadgets.MOD_ID);

    // We have a separate register just to contain all of the upgrades for quick reference
    DeferredRegister<Item> UPGRADE_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MiningGadgets.MOD_ID);

    // Items
    RegistryObject<Item> MININGGADGET_SIMPLE = ITEMS.register("mininggadget_simple", MiningGadget::new);
    RegistryObject<Item> MININGGADGET_FANCY = ITEMS.register("mininggadget_fancy", MiningGadget::new);
    RegistryObject<Item> MININGGADGET = ITEMS.register("mininggadget", MiningGadget::new);

    // Block items
    RegistryObject<Item> MODIFICATION_TABLE_ITEM = ITEMS.register("modificationtable", () -> new BlockItem(ModBlocks.MODIFICATION_TABLE.get(), ITEM_GROUP));
    RegistryObject<Item> MINERS_LIGHT_ITEM = ITEMS.register("minerslight", () -> new BlockItem(ModBlocks.MINERS_LIGHT.get(), ITEM_GROUP.stacksTo(1)));

    RegistryObject<Item> UPGRADE_EMPTY = ITEMS.register("upgrade_empty", () -> new Item(ITEM_GROUP));

    RegistryObject<Item> SILK = UPGRADE_ITEMS.register("upgrade_silk", () -> new UpgradeCard(StandardUpgrades.SILK));
    RegistryObject<Item> VOID_JUNK = UPGRADE_ITEMS.register("upgrade_void_junk", () -> new UpgradeCard(StandardUpgrades.VOID));
    RegistryObject<Item> MAGNET = UPGRADE_ITEMS.register("upgrade_magnet", () -> new UpgradeCard(StandardUpgrades.MAGNET));
    RegistryObject<Item> THREE_BY_THREE = UPGRADE_ITEMS.register("upgrade_three_by_three", () -> new UpgradeCard(StandardUpgrades.THREE_BY_THREE));
    RegistryObject<Item> LIGHT_PLACER = UPGRADE_ITEMS.register("upgrade_light_placer", () -> new UpgradeCard(StandardUpgrades.LIGHT_PLACER));
    RegistryObject<Item> FREEZING = UPGRADE_ITEMS.register("upgrade_freezing", () -> new UpgradeCard(StandardUpgrades.FREEZING));
    RegistryObject<Item> FORTUNE_1 = UPGRADE_ITEMS.register("upgrade_fortune_1", () -> new UpgradeCard(StandardUpgrades.FORTUNE_1));
    RegistryObject<Item> FORTUNE_2 = UPGRADE_ITEMS.register("upgrade_fortune_2", () -> new UpgradeCard(StandardUpgrades.FORTUNE_2));
    RegistryObject<Item> FORTUNE_3 = UPGRADE_ITEMS.register("upgrade_fortune_3", () -> new UpgradeCard(StandardUpgrades.FORTUNE_3));
    RegistryObject<Item> RANGE_1 = UPGRADE_ITEMS.register("upgrade_range_1", () -> new UpgradeCard(StandardUpgrades.RANGE_1));
    RegistryObject<Item> RANGE_2 = UPGRADE_ITEMS.register("upgrade_range_2", () -> new UpgradeCard(StandardUpgrades.RANGE_2));
    RegistryObject<Item> RANGE_3 = UPGRADE_ITEMS.register("upgrade_range_3", () -> new UpgradeCard(StandardUpgrades.RANGE_3));
    RegistryObject<Item> BATTERY_1 = UPGRADE_ITEMS.register("upgrade_battery_1", () -> new UpgradeCard(StandardUpgrades.BATTERY_1));
    RegistryObject<Item> BATTERY_2 = UPGRADE_ITEMS.register("upgrade_battery_2", () -> new UpgradeCard(StandardUpgrades.BATTERY_2));
    RegistryObject<Item> BATTERY_3 = UPGRADE_ITEMS.register("upgrade_battery_3", () -> new UpgradeCard(StandardUpgrades.BATTERY_3));
    RegistryObject<Item> EFFICIENCY_1 = UPGRADE_ITEMS.register("upgrade_efficiency_1", () -> new UpgradeCard(StandardUpgrades.EFFICIENCY_1));
    RegistryObject<Item> EFFICIENCY_2 = UPGRADE_ITEMS.register("upgrade_efficiency_2", () -> new UpgradeCard(StandardUpgrades.EFFICIENCY_2));
    RegistryObject<Item> EFFICIENCY_3 = UPGRADE_ITEMS.register("upgrade_efficiency_3", () -> new UpgradeCard(StandardUpgrades.EFFICIENCY_3));
    RegistryObject<Item> EFFICIENCY_4 = UPGRADE_ITEMS.register("upgrade_efficiency_4", () -> new UpgradeCard(StandardUpgrades.EFFICIENCY_4));
    RegistryObject<Item> EFFICIENCY_5 = UPGRADE_ITEMS.register("upgrade_efficiency_5", () -> new UpgradeCard(StandardUpgrades.EFFICIENCY_5));
}
