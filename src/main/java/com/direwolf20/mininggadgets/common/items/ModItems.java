package com.direwolf20.mininggadgets.common.items;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    // The item group is the creative tab it will go into.
    public static final Item.Properties ITEM_GROUP = new Item.Properties();
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MiningGadgets.MOD_ID);

    // We have a separate register just to contain all of the upgrades for quick reference
    public static final DeferredRegister<Item> UPGRADE_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MiningGadgets.MOD_ID);

    // Items
    public static final RegistryObject<Item> MININGGADGET_SIMPLE = ITEMS.register("mininggadget_simple", MiningGadget::new);
    public static final RegistryObject<Item> MININGGADGET_FANCY = ITEMS.register("mininggadget_fancy", MiningGadget::new);
    public static final RegistryObject<Item> MININGGADGET = ITEMS.register("mininggadget", MiningGadget::new);

    // Block items
    public static final RegistryObject<Item> MODIFICATION_TABLE_ITEM = ITEMS.register("modificationtable", () -> new BlockItem(ModBlocks.MODIFICATION_TABLE.get(), ITEM_GROUP));
    public static final RegistryObject<Item> MINERS_LIGHT_ITEM = ITEMS.register("minerslight", () -> new BlockItem(ModBlocks.MINERS_LIGHT.get(), ITEM_GROUP.stacksTo(1)));

    /**
     * Upgrades are a bit ugly.. Soz
     * This one is actually kinda
     */
    public static final RegistryObject<Item> UPGRADE_EMPTY = UPGRADE_ITEMS.register("upgrade_empty", () -> new UpgradeCard(Upgrade.EMPTY, 64));
    public static final RegistryObject<Item> SILK = UPGRADE_ITEMS.register("upgrade_silk", () -> new UpgradeCard(Upgrade.SILK));
    public static final RegistryObject<Item> VOID_JUNK = UPGRADE_ITEMS.register("upgrade_void_junk", () -> new UpgradeCard(Upgrade.VOID_JUNK));
    public static final RegistryObject<Item> MAGNET = UPGRADE_ITEMS.register("upgrade_magnet", () -> new UpgradeCard(Upgrade.MAGNET));
    public static final RegistryObject<Item> SIZE_1 = UPGRADE_ITEMS.register("upgrade_size_1", () -> new UpgradeCard(Upgrade.SIZE_1));
    public static final RegistryObject<Item> SIZE_2 = UPGRADE_ITEMS.register("upgrade_size_2", () -> new UpgradeCard(Upgrade.SIZE_2));
    public static final RegistryObject<Item> SIZE_3 = UPGRADE_ITEMS.register("upgrade_size_3", () -> new UpgradeCard(Upgrade.SIZE_3));
    public static final RegistryObject<Item> LIGHT_PLACER = UPGRADE_ITEMS.register("upgrade_light_placer", () -> new UpgradeCard(Upgrade.LIGHT_PLACER));
    public static final RegistryObject<Item> FREEZING = UPGRADE_ITEMS.register("upgrade_freezing", () -> new UpgradeCard(Upgrade.FREEZING));
    public static final RegistryObject<Item> FORTUNE_1 = UPGRADE_ITEMS.register("upgrade_fortune_1", () -> new UpgradeCard(Upgrade.FORTUNE_1));
    public static final RegistryObject<Item> FORTUNE_2 = UPGRADE_ITEMS.register("upgrade_fortune_2", () -> new UpgradeCard(Upgrade.FORTUNE_2));
    public static final RegistryObject<Item> FORTUNE_3 = UPGRADE_ITEMS.register("upgrade_fortune_3", () -> new UpgradeCard(Upgrade.FORTUNE_3));
    public static final RegistryObject<Item> RANGE_1 = UPGRADE_ITEMS.register("upgrade_range_1", () -> new UpgradeCard(Upgrade.RANGE_1));
    public static final RegistryObject<Item> RANGE_2 = UPGRADE_ITEMS.register("upgrade_range_2", () -> new UpgradeCard(Upgrade.RANGE_2));
    public static final RegistryObject<Item> RANGE_3 = UPGRADE_ITEMS.register("upgrade_range_3", () -> new UpgradeCard(Upgrade.RANGE_3));
    public static final RegistryObject<Item> BATTERY_1 = UPGRADE_ITEMS.register("upgrade_battery_1", () -> new UpgradeCard(Upgrade.BATTERY_1));
    public static final RegistryObject<Item> BATTERY_2 = UPGRADE_ITEMS.register("upgrade_battery_2", () -> new UpgradeCard(Upgrade.BATTERY_2));
    public static final RegistryObject<Item> BATTERY_3 = UPGRADE_ITEMS.register("upgrade_battery_3", () -> new UpgradeCard(Upgrade.BATTERY_3));
    public static final RegistryObject<Item> BATTERY_CREATIVE = UPGRADE_ITEMS.register("upgrade_battery_creative", () -> new UpgradeCard(Upgrade.BATTERY_CREATIVE));
    public static final RegistryObject<Item> EFFICIENCY_1 = UPGRADE_ITEMS.register("upgrade_efficiency_1", () -> new UpgradeCard(Upgrade.EFFICIENCY_1));
    public static final RegistryObject<Item> EFFICIENCY_2 = UPGRADE_ITEMS.register("upgrade_efficiency_2", () -> new UpgradeCard(Upgrade.EFFICIENCY_2));
    public static final RegistryObject<Item> EFFICIENCY_3 = UPGRADE_ITEMS.register("upgrade_efficiency_3", () -> new UpgradeCard(Upgrade.EFFICIENCY_3));
    public static final RegistryObject<Item> EFFICIENCY_4 = UPGRADE_ITEMS.register("upgrade_efficiency_4", () -> new UpgradeCard(Upgrade.EFFICIENCY_4));
    public static final RegistryObject<Item> EFFICIENCY_5 = UPGRADE_ITEMS.register("upgrade_efficiency_5", () -> new UpgradeCard(Upgrade.EFFICIENCY_5));
    //    public static final RegistryObject<Item> PAVER =            UPGRADE_ITEMS.register("upgrade_paver", Upgrade.PAVER::getCard);

}
