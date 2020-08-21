package com.direwolf20.mininggadgets.common.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.items.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class GeneratorLanguage extends LanguageProvider {
    public GeneratorLanguage(DataGenerator gen) {
        super(gen, MiningGadgets.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.mininggadgets", "Mining Gadgets");
        addItem(ModItems.MININGGADGET, "Mining Gadget");
        addItem(ModItems.UPGRADE_EMPTY, "Blank Upgrade Module");

        // This should always match the start of below upgrade names. I use this
        // to actively replace the start of the word. (it's an exact search so this should
        // still work nicely in other languages. It's rare we need the Upgrade: prefix
        // for most of the gui in the mod so it's purely a gui hack.
        addPrefixed("upgrade.replacement", "Upgrade: ");
        addItem(ModItems.SILK, "Upgrade: Silk touch");
        addItem(ModItems.FREEZING, "Upgrade: Freezing");
        addItem(ModItems.LIGHT_PLACER, "Upgrade: Light Placer");
        addItem(ModItems.MAGNET, "Upgrade: Magnet");
        addItem(ModItems.THREE_BY_THREE, "Upgrade: 3x3");
        addItem(ModItems.PAVER, "Upgrade: Paver");
        addItem(ModItems.VOID_JUNK, "Upgrade: Void Junk");
        addItem(ModItems.FORTUNE_1, "Upgrade: Fortune, Tier 1");
        addItem(ModItems.FORTUNE_2, "Upgrade: Fortune, Tier 2");
        addItem(ModItems.FORTUNE_3, "Upgrade: Fortune, Tier 3");
        addItem(ModItems.RANGE_1, "Upgrade: Range, Tier 1");
        addItem(ModItems.RANGE_2, "Upgrade: Range, Tier 2");
        addItem(ModItems.RANGE_3, "Upgrade: Range, Tier 3");
        addItem(ModItems.EFFICIENCY_1, "Upgrade: Efficiency, Tier 1");
        addItem(ModItems.EFFICIENCY_2, "Upgrade: Efficiency, Tier 2");
        addItem(ModItems.EFFICIENCY_3, "Upgrade: Efficiency, Tier 3");
        addItem(ModItems.EFFICIENCY_4, "Upgrade: Efficiency, Tier 4");
        addItem(ModItems.EFFICIENCY_5, "Upgrade: Efficiency, Tier 5");
        addItem(ModItems.BATTERY_1, "Upgrade: Battery, Tier 1");
        addItem(ModItems.BATTERY_2, "Upgrade: Battery, Tier 2");
        addItem(ModItems.BATTERY_3, "Upgrade: Battery, Tier 3");

        // Upgrade tooltips :D
        add("tooltop.mininggadgets.empty", "Used to craft other upgrades");
        add("tooltop.mininggadgets.silk", "Applies the silk touch enchant to the Mining Gadget");
        add("tooltop.mininggadgets.void_junk", "Voids blocks! (adds filtering too)");
        add("tooltop.mininggadgets.magnet", "Deconstructs blocks right into your inventory");
        add("tooltop.mininggadgets.three_by_three", "Allows for a 3x3 mining radius");
        add("tooltop.mininggadgets.light_placer", "Places touches whenever the surroundings light level is lower than 8");
        add("tooltop.mininggadgets.freezing", "Freezes water and stops lava in it's place!");
        add("tooltop.mininggadgets.fortune", "Applies fortune to the Mining Gadget");
        add("tooltop.mininggadgets.battery", "Upgrades the internal capacitors");
        add("tooltop.mininggadgets.range", "Extends your range by 5 times with each tier");
        add("tooltop.mininggadgets.efficiency", "Applies efficiency to the Mining Gadget");
        add("tooltop.mininggadgets.paver", "Creates a path for you as you mine");

        // Blocks
        addBlock(ModBlocks.MINERS_LIGHT, "Miner's Light");
        addBlock(ModBlocks.RENDER_BLOCK, "Render Block (Don't use)");
        addBlock(ModBlocks.MODIFICATION_TABLE, "Modification Table");

        // Fixes oneProbe? Weird
        add("block.mininggadgets.renderblock.name", "What ever it wants to be!");

        addPrefixed("gadget.range_change", "Range Change: %1$d x %1$d");
        addPrefixed("gadget.energy", "Energy: %d/%d");

        // Tooltips?
        addPrefixed("tooltip.single.insert", "Insert");
        addPrefixed("tooltip.single.filters", "Filters");
        addPrefixed("tooltip.item.show_upgrades","Hold %s to show upgrades");
        addPrefixed("tooltip.item.upgrades", "Current Upgrades:");
        addPrefixed("tooltip.item.upgrade_cost", "Upgrade Cost: %1$d");
        addPrefixed("tooltip.item.use_cost", "+%1$d FE per use");
        addPrefixed("tooltip.item.battery_boost", "Battery Boost: %1$d");
        addPrefixed("tooltip.item.break_cost", "Per Block Cost: %1$d");

        addPrefixed("text.modification_table", "Modification Table");
        addPrefixed("text.empty_table_helper", "Shift click to insert from your slots\nor drop upgrade onto this screen.\n\nClick upgrade to remove.");
        addPrefixed("text.open_gui", "Open Mining Gadget Settings");

        // Screen Buttons and text
        addPrefixed("tooltip.screen.size", "Size: %1$d x %1$d");
        addPrefixed("tooltip.screen.range", "Range");
        addPrefixed("tooltip.screen.visuals_menu", "Edit Visuals");
        addPrefixed("tooltip.screen.mining_gadget", "Gadget Settings");
        addPrefixed("tooltip.screen.shrink", "Shrink Blocks");
        addPrefixed("tooltip.screen.fade", "Fade Blocks");
        addPrefixed("tooltip.screen.red", "Red");
        addPrefixed("tooltip.screen.green", "Green");
        addPrefixed("tooltip.screen.blue", "Blue");
        addPrefixed("tooltip.screen.block_break_style", "Breaking Style");
        addPrefixed("tooltip.screen.outer_color", "Outer glow color");
        addPrefixed("tooltip.screen.inner_color", "Inner beam color");
        addPrefixed("tooltip.screen.beam_preview", "Beam preview");
        addPrefixed("tooltip.screen.visual_settings", "Visual Settings");
        addPrefixed("tooltip.screen.toggle_upgrades", "Toggle Upgrades");
        addPrefixed("tooltip.screen.edit_filters", "Edit Filters");
        addPrefixed("tooltip.screen.no_upgrades", "No Upgrades available");
        addPrefixed("tooltip.screen.whitelist", "Whitelist active");
        addPrefixed("tooltip.screen.blacklist", "Blacklist active");
        addPrefixed("tooltip.screen.precision_mode", "Precision Mode: %1$d");
        addPrefixed("tooltip.screen.volume", "Volume");
        addPrefixed("tooltip.screen.freeze_delay", "Freeze Delay");
        addPrefixed("tooltip.screen.ticks", "ticks");
        addPrefixed("tooltip.screen.delay_explain", "Controls how often a freeze particle is spawned\nby default it is one particle every tick, to reduce\nthe amount of particles add more delay");
    }

    /**
     * Very simply, prefixes all the keys with the mod_id.{key} instead of
     * having to input it manually
     */
    private void addPrefixed(String key, String text) {
        add(String.format("%s.%s", MiningGadgets.MOD_ID, key), text);
    }
}
