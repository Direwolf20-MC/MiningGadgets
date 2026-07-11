package com.direwolf20.mininggadgets.setup;

import com.direwolf20.mininggadgets.client.particles.ModParticles;
import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.blocks.MinersLight;
import com.direwolf20.mininggadgets.common.blocks.ModificationTable;
import com.direwolf20.mininggadgets.common.blocks.RenderBlock;
import com.direwolf20.mininggadgets.common.containers.FilterContainer;
import com.direwolf20.mininggadgets.common.containers.ModificationTableContainer;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.UpgradeCard;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.sounds.OurSounds;
import com.direwolf20.mininggadgets.common.tiles.ModificationTableTileEntity;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class Registration {
    public static final Item.Properties ITEM_GROUP = new Item.Properties();
    /**
     * Deferred Registers for the our Main class to load.
     */
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MiningGadgets.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MiningGadgets.MOD_ID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, MiningGadgets.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MiningGadgets.MOD_ID);
    // We have a separate register just to contain all of the upgrades for quick reference
    public static final DeferredRegister.Items UPGRADE_ITEMS = DeferredRegister.createItems(MiningGadgets.MOD_ID);


    public static void init(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        UPGRADE_ITEMS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
        CONTAINERS.register(eventBus);
        ModParticles.PARTICLE_TYPES.register(eventBus);
        OurSounds.SOUND_REGISTRY.register(eventBus);
        MGDataComponents.COMPONENTS.register(eventBus);
    }

    /**
     * Register our blocks to the above registers to be loaded when the mod is initialized
     */
    public static final DeferredHolder<Block, RenderBlock> RENDER_BLOCK = BLOCKS.registerBlock("renderblock", RenderBlock::new, () -> BlockBehaviour.Properties.of()
            .strength(50.0F, 1200.0F)
            .noOcclusion()
            .pushReaction(PushReaction.BLOCK)
            .isRedstoneConductor((a, b, c) -> false));

    public static final DeferredHolder<Block, MinersLight> MINERS_LIGHT = BLOCKS.registerBlock("minerslight", MinersLight::new, () -> Block.Properties.of().noCollision().strength(0.0f).lightLevel(e -> 14).replaceable());

    public static final DeferredHolder<Block, ModificationTable> MODIFICATION_TABLE = BLOCKS.registerBlock("modificationtable", ModificationTable::new, () -> BlockBehaviour.Properties.of().strength(2.0f));

    /**
     * TileEntity Registers to the above deferred registers to be loaded in from the mods main class.
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RenderBlockTileEntity>> RENDERBLOCK_TILE = BLOCK_ENTITIES.register("renderblock", () -> new BlockEntityType<>(RenderBlockTileEntity::new, RENDER_BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ModificationTableTileEntity>> MODIFICATIONTABLE_TILE = BLOCK_ENTITIES.register("modificationtable", () -> new BlockEntityType<>(ModificationTableTileEntity::new, MODIFICATION_TABLE.get()));

    // Items
    public static final DeferredHolder<Item, MiningGadget> MININGGADGET_SIMPLE = ITEMS.registerItem("mininggadget_simple", MiningGadget::new, () -> new Item.Properties().stacksTo(1).setNoCombineRepair());
    public static final DeferredHolder<Item, MiningGadget> MININGGADGET_FANCY = ITEMS.registerItem("mininggadget_fancy", MiningGadget::new, () -> new Item.Properties().stacksTo(1).setNoCombineRepair());
    public static final DeferredHolder<Item, MiningGadget> MININGGADGET = ITEMS.registerItem("mininggadget", MiningGadget::new, () -> new Item.Properties().stacksTo(1).setNoCombineRepair());

    public static final Supplier<Item> MODIFICATION_TABLE_ITEM = ITEMS.registerItem("modificationtable", props -> new BlockItem(MODIFICATION_TABLE.get(), props), Item.Properties::new);

    public static final Supplier<Item> MINERS_LIGHT_ITEM = ITEMS.registerItem("minerslight", props -> new BlockItem(MINERS_LIGHT.get(), props), Item.Properties::new);

    /**
     * Upgrades are a bit ugly.. Soz
     * This one is actually kinda
     */
    public static final DeferredHolder<Item, UpgradeCard> UPGRADE_EMPTY = UPGRADE_ITEMS.register("upgrade_empty", () -> new UpgradeCard(Upgrade.EMPTY, 64));
    public static final DeferredHolder<Item, UpgradeCard> SILK = UPGRADE_ITEMS.register("upgrade_silk", () -> new UpgradeCard(Upgrade.SILK));
    public static final DeferredHolder<Item, UpgradeCard> VOID_JUNK = UPGRADE_ITEMS.register("upgrade_void_junk", () -> new UpgradeCard(Upgrade.VOID_JUNK));
    public static final DeferredHolder<Item, UpgradeCard> MAGNET = UPGRADE_ITEMS.register("upgrade_magnet", () -> new UpgradeCard(Upgrade.MAGNET));
    public static final DeferredHolder<Item, UpgradeCard> SIZE_1 = UPGRADE_ITEMS.register("upgrade_size_1", () -> new UpgradeCard(Upgrade.SIZE_1));
    public static final DeferredHolder<Item, UpgradeCard> SIZE_2 = UPGRADE_ITEMS.register("upgrade_size_2", () -> new UpgradeCard(Upgrade.SIZE_2));
    public static final DeferredHolder<Item, UpgradeCard> SIZE_3 = UPGRADE_ITEMS.register("upgrade_size_3", () -> new UpgradeCard(Upgrade.SIZE_3));
    public static final DeferredHolder<Item, UpgradeCard> LIGHT_PLACER = UPGRADE_ITEMS.register("upgrade_light_placer", () -> new UpgradeCard(Upgrade.LIGHT_PLACER));
    public static final DeferredHolder<Item, UpgradeCard> FREEZING = UPGRADE_ITEMS.register("upgrade_freezing", () -> new UpgradeCard(Upgrade.FREEZING));
    public static final DeferredHolder<Item, UpgradeCard> FORTUNE_1 = UPGRADE_ITEMS.register("upgrade_fortune_1", () -> new UpgradeCard(Upgrade.FORTUNE_1));
    public static final DeferredHolder<Item, UpgradeCard> FORTUNE_2 = UPGRADE_ITEMS.register("upgrade_fortune_2", () -> new UpgradeCard(Upgrade.FORTUNE_2));
    public static final DeferredHolder<Item, UpgradeCard> FORTUNE_3 = UPGRADE_ITEMS.register("upgrade_fortune_3", () -> new UpgradeCard(Upgrade.FORTUNE_3));
    public static final DeferredHolder<Item, UpgradeCard> RANGE_1 = UPGRADE_ITEMS.register("upgrade_range_1", () -> new UpgradeCard(Upgrade.RANGE_1));
    public static final DeferredHolder<Item, UpgradeCard> RANGE_2 = UPGRADE_ITEMS.register("upgrade_range_2", () -> new UpgradeCard(Upgrade.RANGE_2));
    public static final DeferredHolder<Item, UpgradeCard> RANGE_3 = UPGRADE_ITEMS.register("upgrade_range_3", () -> new UpgradeCard(Upgrade.RANGE_3));
    public static final DeferredHolder<Item, UpgradeCard> BATTERY_1 = UPGRADE_ITEMS.register("upgrade_battery_1", () -> new UpgradeCard(Upgrade.BATTERY_1));
    public static final DeferredHolder<Item, UpgradeCard> BATTERY_2 = UPGRADE_ITEMS.register("upgrade_battery_2", () -> new UpgradeCard(Upgrade.BATTERY_2));
    public static final DeferredHolder<Item, UpgradeCard> BATTERY_3 = UPGRADE_ITEMS.register("upgrade_battery_3", () -> new UpgradeCard(Upgrade.BATTERY_3));
    public static final DeferredHolder<Item, UpgradeCard> BATTERY_CREATIVE = UPGRADE_ITEMS.register("upgrade_battery_creative", () -> new UpgradeCard(Upgrade.BATTERY_CREATIVE));
    public static final DeferredHolder<Item, UpgradeCard> EFFICIENCY_1 = UPGRADE_ITEMS.register("upgrade_efficiency_1", () -> new UpgradeCard(Upgrade.EFFICIENCY_1));
    public static final DeferredHolder<Item, UpgradeCard> EFFICIENCY_2 = UPGRADE_ITEMS.register("upgrade_efficiency_2", () -> new UpgradeCard(Upgrade.EFFICIENCY_2));
    public static final DeferredHolder<Item, UpgradeCard> EFFICIENCY_3 = UPGRADE_ITEMS.register("upgrade_efficiency_3", () -> new UpgradeCard(Upgrade.EFFICIENCY_3));
    public static final DeferredHolder<Item, UpgradeCard> EFFICIENCY_4 = UPGRADE_ITEMS.register("upgrade_efficiency_4", () -> new UpgradeCard(Upgrade.EFFICIENCY_4));
    public static final DeferredHolder<Item, UpgradeCard> EFFICIENCY_5 = UPGRADE_ITEMS.register("upgrade_efficiency_5", () -> new UpgradeCard(Upgrade.EFFICIENCY_5));
    //    public static final RegistryObject<Item> PAVER =            UPGRADE_ITEMS.register("upgrade_paver", Upgrade.PAVER::getCard);


    // Our containers
    public static final DeferredHolder<MenuType<?>, MenuType<ModificationTableContainer>> MODIFICATIONTABLE_CONTAINER = CONTAINERS.register("modificationtable",
            () -> IMenuTypeExtension.create(ModificationTableContainer::new));
    public static final DeferredHolder<MenuType<?>, MenuType<FilterContainer>> FILTER_CONTAINER = CONTAINERS.register("filter_container",
            () -> IMenuTypeExtension.create(FilterContainer::new));


}
