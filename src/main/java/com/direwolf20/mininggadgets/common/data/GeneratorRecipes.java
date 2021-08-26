package com.direwolf20.mininggadgets.common.data;

import static com.direwolf20.mininggadgets.common.items.ModItems.*;

import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class GeneratorRecipes extends RecipeProvider {
    public GeneratorRecipes(DataGenerator generator) {
        super(generator);
    }

    /**
     * This is basically just a code version of the json file meaning you type less and generate more. To use
     * Tags use Tags and to specific normal Items use their Items class. A Criterion is what the game will
     * use to see if you can make that recipe. I've been pretty lazy and just done the higher tier ones
     * for now. Hopefully this should mean we write less json in the long run :D
     */

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(UPGRADE_EMPTY.get())
            .define('r', Tags.Items.DUSTS_REDSTONE)
            .define('g', Tags.Items.GLASS_PANES)
            .define('l', Tags.Items.GEMS_LAPIS)
            .define('d', Tags.Items.GEMS_DIAMOND)
            .pattern("rlr")
            .pattern("dgd")
            .pattern("rlr")
            .unlockedBy("has_diamonds", has(Tags.Items.GEMS_DIAMOND))
            .save(consumer);

        ShapedRecipeBuilder.shaped(MININGGADGET.get())
            .define('u', UPGRADE_EMPTY.get())
            .define('r', Tags.Items.DUSTS_REDSTONE)
            .define('i', Tags.Items.INGOTS_IRON)
            .define('d', Tags.Items.GEMS_DIAMOND)
            .define('g', Tags.Items.INGOTS_GOLD)
            .pattern("dig")
            .pattern("dur")
            .pattern("dig")
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .save(consumer);

        ShapedRecipeBuilder.shaped(MININGGADGET_SIMPLE.get())
            .define('u', UPGRADE_EMPTY.get())
            .define('r', Tags.Items.DUSTS_REDSTONE)
            .define('i', Tags.Items.INGOTS_IRON)
            .define('d', Tags.Items.GEMS_DIAMOND)
            .define('g', Tags.Items.INGOTS_GOLD)
            .pattern("dig")
            .pattern("dur")
            .pattern("dii")
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .save(consumer);

        ShapedRecipeBuilder.shaped(MININGGADGET_FANCY.get())
            .define('u', UPGRADE_EMPTY.get())
            .define('r', Tags.Items.DUSTS_REDSTONE)
            .define('i', Tags.Items.INGOTS_IRON)
            .define('d', Tags.Items.GEMS_DIAMOND)
            .define('g', Tags.Items.INGOTS_GOLD)
            .pattern("dii")
            .pattern("dur")
            .pattern("dig")
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.MODIFICATION_TABLE.get())
            .define('r', Tags.Items.DUSTS_REDSTONE)
            .define('u', UPGRADE_EMPTY.get())
            .define('i', Tags.Items.INGOTS_IRON)
            .pattern("iii")
            .pattern("rur")
            .pattern("iii")
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .save(consumer);

        ShapedRecipeBuilder.shaped(BATTERY_1.get())
            .define('q', Items.QUARTZ)
            .define('u', UPGRADE_EMPTY.get())
            .pattern("qqq")
            .pattern("quq")
            .pattern("qqq")
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .save(consumer);

        ShapedRecipeBuilder.shaped(BATTERY_2.get())
            .define('q', Items.QUARTZ)
            .define('b', Items.QUARTZ_BLOCK)
            .define('g', Tags.Items.INGOTS_IRON)
            .define('u', BATTERY_1.get())
            .pattern("qbq")
            .pattern("gug")
            .pattern("qbq")
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .unlockedBy("has_battery_1", has(BATTERY_1.get()))
            .save(consumer);

        ShapedRecipeBuilder.shaped(BATTERY_3.get())
            .define('q', Items.QUARTZ)
            .define('b', Items.QUARTZ_BLOCK)
            .define('g', Tags.Items.INGOTS_GOLD)
            .define('u', BATTERY_2.get())
            .pattern("bqb")
            .pattern("gug")
            .pattern("bqb")
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .unlockedBy("has_battery_2", has(BATTERY_2.get()))
            .save(consumer);

        ShapedRecipeBuilder.shaped(EFFICIENCY_1.get())
            .define('r', Tags.Items.DUSTS_REDSTONE)
            .define('u', UPGRADE_EMPTY.get())
            .pattern("rrr")
            .pattern("rur")
            .pattern("rrr")
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .save(consumer);

        ShapedRecipeBuilder.shaped(EFFICIENCY_2.get())
            .define('r', Tags.Items.DUSTS_REDSTONE)
            .define('u', EFFICIENCY_1.get())
            .define('b', Items.REDSTONE_BLOCK)
            .pattern("rbr")
            .pattern("rur")
            .pattern("rbr")
            .unlockedBy("has_efficiency_1", has(EFFICIENCY_1.get()))
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .save(consumer);

        ShapedRecipeBuilder.shaped(EFFICIENCY_3.get())
            .define('r', Tags.Items.DUSTS_REDSTONE)
            .define('u', EFFICIENCY_2.get())
            .define('b', Items.REDSTONE_BLOCK)
            .pattern("rbr")
            .pattern("bub")
            .pattern("rbr")
            .unlockedBy("has_efficiency_2", has(EFFICIENCY_2.get()))
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .save(consumer);

        ShapedRecipeBuilder.shaped(EFFICIENCY_4.get())
            .define('r', Tags.Items.DUSTS_REDSTONE)
            .define('u', EFFICIENCY_3.get())
            .define('b', Items.REDSTONE_BLOCK)
            .pattern("brb")
            .pattern("bub")
            .pattern("brb")
            .unlockedBy("has_efficiency_3", has(EFFICIENCY_3.get()))
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .save(consumer);

        ShapedRecipeBuilder.shaped(EFFICIENCY_5.get())
            .define('u', EFFICIENCY_4.get())
            .define('b', Items.REDSTONE_BLOCK)
            .pattern("bbb")
            .pattern("bub")
            .pattern("bbb")
            .unlockedBy("has_efficiency_4", has(EFFICIENCY_4.get()))
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .save(consumer);

        ShapedRecipeBuilder.shaped(FORTUNE_1.get())
            .define('l', Items.LAPIS_BLOCK)
            .define('g', Items.IRON_BLOCK)
            .define('u', UPGRADE_EMPTY.get())
            .pattern("lgl")
            .pattern("lul")
            .pattern("lgl")
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .save(consumer);

        ShapedRecipeBuilder.shaped(FORTUNE_2.get())
            .define('l', Items.LAPIS_BLOCK)
            .define('g', Items.GOLD_BLOCK)
            .define('u', FORTUNE_1.get())
            .pattern("lgl")
            .pattern("lul")
            .pattern("lgl")
            .unlockedBy("has_fortune_1", has(FORTUNE_1.get()))
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .save(consumer);

        ShapedRecipeBuilder.shaped(FORTUNE_3.get())
            .define('l', Items.LAPIS_BLOCK)
            .define('d', Items.DIAMOND)
            .define('u', FORTUNE_2.get())
            .pattern("ldl")
            .pattern("lul")
            .pattern("ldl")
            .unlockedBy("has_fortune_2", has(FORTUNE_2.get()))
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .save(consumer);

        ShapedRecipeBuilder.shaped(RANGE_1.get())
            .define('l', Items.LAPIS_LAZULI)
            .define('g', Items.GLASS)
            .define('d', Items.DIAMOND)
            .define('u', UPGRADE_EMPTY.get())
            .pattern("lgl")
            .pattern("dud")
            .pattern("lgl")
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .save(consumer);

        ShapedRecipeBuilder.shaped(RANGE_2.get())
            .define('l', Items.LAPIS_LAZULI)
            .define('g', Items.GLASS)
            .define('e', Items.EMERALD)
            .define('u', RANGE_1.get())
            .pattern("lgl")
            .pattern("eue")
            .pattern("lgl")
            .unlockedBy("has_range_1", has(RANGE_1.get()))
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .save(consumer);

        ShapedRecipeBuilder.shaped(RANGE_3.get())
            .define('l', Items.LAPIS_BLOCK)
            .define('g', Items.GLASS)
            .define('d', Items.DIAMOND_BLOCK)
            .define('e', Items.EMERALD_BLOCK)
            .define('u', RANGE_2.get())
            .pattern("lgl")
            .pattern("eud")
            .pattern("lgl")
            .unlockedBy("has_range_2", has(RANGE_2.get()))
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .save(consumer);

        ShapedRecipeBuilder.shaped(FREEZING.get())
            .define('s', Items.SNOWBALL)
            .define('u', UPGRADE_EMPTY.get())
            .pattern("sss")
            .pattern("sus")
            .pattern("sss")
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .save(consumer);

        ShapedRecipeBuilder.shaped(LIGHT_PLACER.get())
            .define('g', Items.GLOWSTONE_DUST)
            .define('b', Items.GLOWSTONE)
            .define('r', Items.REDSTONE_LAMP)
            .define('a', Items.LANTERN)
            .define('u', UPGRADE_EMPTY.get())
            .pattern("ara")
            .pattern("bub")
            .pattern("grg")
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .save(consumer);

        ShapedRecipeBuilder.shaped(MAGNET.get())
            .define('r', Tags.Items.DUSTS_REDSTONE)
            .define('u', UPGRADE_EMPTY.get())
            .define('i', Tags.Items.INGOTS_IRON)
            .define('g', Tags.Items.INGOTS_GOLD)
            .pattern("rgr")
            .pattern("iui")
            .pattern("rgr")
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .save(consumer);

        ShapedRecipeBuilder.shaped(SILK.get())
            .define('d', Items.GOLDEN_APPLE)
            .define('s', Tags.Items.SLIMEBALLS)
            .define('g', Tags.Items.INGOTS_GOLD)
            .define('u', UPGRADE_EMPTY.get())
            .pattern("sds")
            .pattern("gug")
            .pattern("sss")
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .save(consumer);

        ShapedRecipeBuilder.shaped(THREE_BY_THREE.get())
            .define('r', Items.REDSTONE_BLOCK)
            .define('u', UPGRADE_EMPTY.get())
            .define('d', Items.DIAMOND_BLOCK)
            .define('p', Items.DIAMOND_PICKAXE)
            .define('e', Tags.Items.ENDER_PEARLS)
            .pattern("rdr")
            .pattern("eue")
            .pattern("rpr")
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .save(consumer);

        ShapedRecipeBuilder.shaped(VOID_JUNK.get())
            .define('r', Tags.Items.DUSTS_REDSTONE)
            .define('u', UPGRADE_EMPTY.get())
            .define('o', Blocks.OBSIDIAN)
            .define('e', Tags.Items.ENDER_PEARLS)
            .pattern("ror")
            .pattern("eue")
            .pattern("ror")
            .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
            .save(consumer);

        //        ShapedRecipeBuilder.shapedRecipe(PAVER.get())
        //                .key('r', Tags.Items.DUSTS_REDSTONE)
        //                .key('c', Tags.Items.COBBLESTONE)
        //                .key('u', UPGRADE_EMPTY.get())
        //                .key('g', Tags.Items.NUGGETS_GOLD)
        //                .key('l', Items.LAVA_BUCKET)
        //                .key('p', Blocks.PISTON)
        //                .key('w', Items.WATER_BUCKET)
        //                .patternLine("rgr")
        //                .patternLine("cuc")
        //                .patternLine("lpw")
        //                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
        //                .build(consumer);
    }
}
