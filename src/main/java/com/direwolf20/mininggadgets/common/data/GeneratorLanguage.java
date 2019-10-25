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
        addItem(ModItems.SILK, "Upgrade: Silk touch");
        addItem(ModItems.FREEZING, "Upgrade: Freezing");
        addItem(ModItems.LIGHT_PLACER, "Upgrade: Light Placer");
        addItem(ModItems.MAGNET, "Upgrade: Magnet");
        addItem(ModItems.THREE_BY_THREE, "Upgrade: 3x3");
        addItem(ModItems.VOID_JUNK, "Upgrade: Void Junk");

        addItem(ModItems.FORTUNE_1, "Upgrade: Fortune, Tier 1");
        addItem(ModItems.FORTUNE_2, "Upgrade: Fortune, Tier 2");
        addItem(ModItems.FORTUNE_3, "Upgrade: Fortune, Tier 3");
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
    }

    private void addPrefixed(String key, String text) {
        add(String.format("%s.%s", MiningGadgets.MOD_ID, key), text);
    }
}
