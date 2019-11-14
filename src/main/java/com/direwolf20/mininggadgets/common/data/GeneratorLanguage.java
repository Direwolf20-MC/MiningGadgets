package com.direwolf20.mininggadgets.common.data;

import com.direwolf20.mininggadgets.MiningGadgets;
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
        addItem(ModItems.MININGGADGET, "Mining Gadgets");
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
        addItem(ModItems.VOID_JUNK, "Upgrade: Void Junk");
        addItem(ModItems.HEATSINK, "Upgrade: HeatSink");
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

        // Blocks
        addBlock(ModBlocks.MINERS_LIGHT, "Miner's Light");
        addBlock(ModBlocks.RENDER_BLOCK, "Render Block (Don't use)");
        addBlock(ModBlocks.MODIFICATION_TABLE, "Modification Table");

        addPrefixed("gadget.range_change", "Range Change: %1$d x %1$d");
        addPrefixed("gadget.energy", "Energy: %d/%d");

        // Tooltips?
        addPrefixed("tooltip.single.insert", "Insert");
        addPrefixed("tooltip.item.show_upgrades","Hold %s to show upgrades");
        addPrefixed("tooltip.item.upgrades", "Current Upgrades:");

        // Screen Buttons and text
        addPrefixed("tooltip.screen.size", "Size: %1$d x %1$d");
        addPrefixed("tooltip.screen.range", "Range");
        addPrefixed("tooltip.screen.visuals_menu", "Show Visuals Menu");
        addPrefixed("tooltip.screen.mining_gadget", "Mining Gadget");
        addPrefixed("tooltip.screen.shrink", "Shrink Blocks");
        addPrefixed("tooltip.screen.fade", "Fade Blocks");
        addPrefixed("tooltip.screen.red_inner", "Red Inner");
        addPrefixed("tooltip.screen.green_inner", "Green Inner");
        addPrefixed("tooltip.screen.blue_inner", "Blue Inner");
        addPrefixed("tooltip.screen.red_outer", "Red Outer");
        addPrefixed("tooltip.screen.green_outer", "Green Outer");
        addPrefixed("tooltip.screen.blue_outer", "Blue Outer");
        addPrefixed("tooltip.screen.visual_settings", "Visual Settings");
        addPrefixed("tooltip.screen.toggle_upgrades", "Upgrade Toggles");
    }

    /**
     * Very simply, prefixes all the keys with the mod_id.{key} instead of
     * having to input it manually
     */
    private void addPrefixed(String key, String text) {
        add(String.format("%s.%s", MiningGadgets.MOD_ID, key), text);
    }
}
