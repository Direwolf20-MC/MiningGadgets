package com.direwolf20.mininggadgets.setup;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {

    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_POWER = "power";
    public static final String SUBCATEGORY_MININGGADGET = "mining_gadget";
    public static final String SUBCATEGORY_UPGRADES = "upgrades";

    private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

    public static ModConfigSpec COMMON_CONFIG;
    public static ModConfigSpec CLIENT_CONFIG;

    public static ModConfigSpec.IntValue MININGGADGET_MAXPOWER;
    public static ModConfigSpec.IntValue MININGGADGET_BASECOST;
    public static ModConfigSpec.IntValue UPGRADECOST_SILKTOUCH;
    public static ModConfigSpec.IntValue UPGRADECOST_MAGNET;
    public static ModConfigSpec.IntValue UPGRADECOST_VOID;
    public static ModConfigSpec.IntValue UPGRADECOST_FORTUNE1;
    public static ModConfigSpec.IntValue UPGRADECOST_FORTUNE2;
    public static ModConfigSpec.IntValue UPGRADECOST_FORTUNE3;
    public static ModConfigSpec.IntValue UPGRADECOST_EFFICIENCY1;
    public static ModConfigSpec.IntValue UPGRADECOST_EFFICIENCY2;
    public static ModConfigSpec.IntValue UPGRADECOST_EFFICIENCY3;
    public static ModConfigSpec.IntValue UPGRADECOST_EFFICIENCY4;
    public static ModConfigSpec.IntValue UPGRADECOST_EFFICIENCY5;
    public static ModConfigSpec.IntValue UPGRADECOST_EFFICIENCY6;
    public static ModConfigSpec.IntValue UPGRADECOST_LIGHT;
    public static ModConfigSpec.IntValue UPGRADECOST_FREEZE;
    public static ModConfigSpec.IntValue UPGRADECOST_BATTERY1;
    public static ModConfigSpec.IntValue UPGRADECOST_BATTERY2;
    public static ModConfigSpec.IntValue UPGRADECOST_BATTERY3;


    public static void register() {
        //registerServerConfigs();
        registerCommonConfigs();
        //registerClientConfigs();
    }

    private static void registerClientConfigs() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_BUILDER.build());
    }

    private static void registerCommonConfigs() {
        COMMON_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Power settings").push(CATEGORY_POWER);

        setupMiningGadgetConfig();

        COMMON_BUILDER.pop();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_BUILDER.build());
    }

    private static void setupMiningGadgetConfig() {
        COMMON_BUILDER.comment("Mining Gadget Settings").push(SUBCATEGORY_MININGGADGET);

        MININGGADGET_MAXPOWER = COMMON_BUILDER.comment("Maximum power for the Mining Gadget")
                .defineInRange("maxPower", 1000000, 0, Integer.MAX_VALUE);
        MININGGADGET_BASECOST = COMMON_BUILDER.comment("Base cost per block broken")
                .defineInRange("baseCost", 10, 0, Integer.MAX_VALUE);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Upgrade Cost Settings").push(SUBCATEGORY_UPGRADES);

        UPGRADECOST_SILKTOUCH = COMMON_BUILDER.comment("Cost per block for Silk Touch upgrade")
                .defineInRange("upgradeSilkCost", 10, 0, Integer.MAX_VALUE);
        UPGRADECOST_MAGNET = COMMON_BUILDER.comment("Cost per block for Magnet upgrade")
                .defineInRange("upgradeMagnet", 0, 0, Integer.MAX_VALUE);
        UPGRADECOST_VOID = COMMON_BUILDER.comment("Cost per block for Void Junk upgrade")
                .defineInRange("upgradeVoid", 0, 0, Integer.MAX_VALUE);
        UPGRADECOST_FORTUNE1 = COMMON_BUILDER.comment("Cost per block for Fortune 1 upgrade")
                .defineInRange("upgradeFortune1", 5, 0, Integer.MAX_VALUE);
        UPGRADECOST_FORTUNE2 = COMMON_BUILDER.comment("Cost per block for Fortune 2 upgrade")
                .defineInRange("upgradeFortune2", 10, 0, Integer.MAX_VALUE);
        UPGRADECOST_FORTUNE3 = COMMON_BUILDER.comment("Cost per block for Fortune 3 upgrade")
                .defineInRange("upgradeFortune3", 15, 0, Integer.MAX_VALUE);
        UPGRADECOST_EFFICIENCY1 = COMMON_BUILDER.comment("Cost per block for Efficiency 1 upgrade")
                .defineInRange("upgradeEfficiency1", 5, 0, Integer.MAX_VALUE);
        UPGRADECOST_EFFICIENCY2 = COMMON_BUILDER.comment("Cost per block for Efficiency 2 upgrade")
                .defineInRange("upgradeEfficiency2", 10, 0, Integer.MAX_VALUE);
        UPGRADECOST_EFFICIENCY3 = COMMON_BUILDER.comment("Cost per block for Efficiency 3 upgrade")
                .defineInRange("upgradeEfficiency3", 15, 0, Integer.MAX_VALUE);
        UPGRADECOST_EFFICIENCY4 = COMMON_BUILDER.comment("Cost per block for Efficiency 4 upgrade")
                .defineInRange("upgradeEfficiency4", 20, 0, Integer.MAX_VALUE);
        UPGRADECOST_EFFICIENCY5 = COMMON_BUILDER.comment("Cost per block for Efficiency 5 upgrade")
                .defineInRange("upgradeEfficiency5", 25, 0, Integer.MAX_VALUE);
        UPGRADECOST_EFFICIENCY6 = COMMON_BUILDER.comment("Cost per block for Efficiency 6 upgrade")
                .defineInRange("upgradeEfficiency6", 30, 0, Integer.MAX_VALUE);
        UPGRADECOST_LIGHT = COMMON_BUILDER.comment("Cost per Light Block placed")
                .defineInRange("upgradeLight", 10, 0, Integer.MAX_VALUE);
        UPGRADECOST_FREEZE = COMMON_BUILDER.comment("Cost per block Frozen")
                .defineInRange("upgradeFreeze", 5, 0, Integer.MAX_VALUE);
        UPGRADECOST_BATTERY1 = COMMON_BUILDER.comment("Capacity Boost from Battery 1 Upgrade")
                .defineInRange("battery1", 2000000, 0, Integer.MAX_VALUE);
        UPGRADECOST_BATTERY2 = COMMON_BUILDER.comment("Capacity Boost from Battery 2 Upgrade")
                .defineInRange("battery2", 5000000, 0, Integer.MAX_VALUE);
        UPGRADECOST_BATTERY3 = COMMON_BUILDER.comment("Capacity Boost from Battery 3 Upgrade")
                .defineInRange("battery3", 10000000, 0, Integer.MAX_VALUE);

        COMMON_BUILDER.pop();
    }

    /*public static void loadConfig(ModConfigSpec spec, Path path) {

        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        spec.setConfig(configData);
    }*/
}

