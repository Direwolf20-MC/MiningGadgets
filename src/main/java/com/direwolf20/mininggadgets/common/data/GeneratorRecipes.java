package com.direwolf20.mininggadgets.common.data;

import com.direwolf20.mininggadgets.setup.Registration;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import static com.direwolf20.mininggadgets.setup.Registration.*;

public class GeneratorRecipes extends RecipeProvider {
    public GeneratorRecipes(PackOutput output) {
        super(output);
    }

    /**
     * This is basically just a code version of the json file meaning you type less and generate more. To use
     * Tags and to specific normal Items use their Items class. A Criterion is what the game will
     * use to see if you can make that recipe. I've been pretty lazy and just done the higher tier ones
     * for now. Hopefully this should mean we write less json in the long run :D
     */

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, UPGRADE_EMPTY.get())
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('g', Tags.Items.GLASS_PANES)
                .define('l', Tags.Items.GEMS_LAPIS)
                .define('d', Tags.Items.GEMS_DIAMOND)
                .pattern("rlr")
                .pattern("gdg")
                .pattern("rlr")
                .unlockedBy("has_diamonds", has(Tags.Items.GEMS_DIAMOND))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, MININGGADGET.get())
                .define('u', UPGRADE_EMPTY.get())
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('i', Tags.Items.INGOTS_IRON)
                .define('d', Tags.Items.GEMS_DIAMOND)
                .define('g', Tags.Items.INGOTS_GOLD)
                .pattern("dig")
                .pattern("dur")
                .pattern("dig")
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, MININGGADGET_SIMPLE.get())
                .define('u', UPGRADE_EMPTY.get())
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('i', Tags.Items.INGOTS_IRON)
                .define('d', Tags.Items.GEMS_DIAMOND)
                .define('g', Tags.Items.INGOTS_GOLD)
                .pattern("dig")
                .pattern("dur")
                .pattern("dii")
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, MININGGADGET_FANCY.get())
                .define('u', UPGRADE_EMPTY.get())
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('i', Tags.Items.INGOTS_IRON)
                .define('d', Tags.Items.GEMS_DIAMOND)
                .define('g', Tags.Items.INGOTS_GOLD)
                .pattern("dii")
                .pattern("dur")
                .pattern("dig")
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.MODIFICATION_TABLE.get())
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('b', Tags.Items.BOOKSHELVES)
                .define('c', Tags.Items.CHESTS)
                .define('i', Tags.Items.INGOTS_IRON)
                .define('x', Tags.Items.STORAGE_BLOCKS_IRON)
                .pattern("ici")
                .pattern("rbr")
                .pattern("ixi")
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BATTERY_1.get())
                .define('q', Items.QUARTZ)
                .define('u', UPGRADE_EMPTY.get())
                .pattern("qqq")
                .pattern("quq")
                .pattern("qqq")
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BATTERY_2.get())
                .define('q', Items.QUARTZ)
                .define('b', Items.QUARTZ_BLOCK)
                .define('g', Tags.Items.INGOTS_IRON)
                .define('u', BATTERY_1.get())
                .pattern("qbq")
                .pattern("gug")
                .pattern("qbq")
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .unlockedBy("has_battery_1", has(BATTERY_1.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BATTERY_3.get())
                .define('q', Items.QUARTZ)
                .define('b', Items.QUARTZ_BLOCK)
                .define('g', Tags.Items.INGOTS_GOLD)
                .define('u', BATTERY_2.get())
                .pattern("bqb")
                .pattern("gug")
                .pattern("bqb")
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .unlockedBy("has_battery_2", has(BATTERY_2.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EFFICIENCY_1.get())
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('u', UPGRADE_EMPTY.get())
                .pattern("rrr")
                .pattern("rur")
                .pattern("rrr")
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EFFICIENCY_2.get())
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('u', EFFICIENCY_1.get())
                .define('b', Items.REDSTONE_BLOCK)
                .pattern("rbr")
                .pattern("rur")
                .pattern("rbr")
                .unlockedBy("has_efficiency_1", has(EFFICIENCY_1.get()))
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EFFICIENCY_3.get())
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('u', EFFICIENCY_2.get())
                .define('b', Items.REDSTONE_BLOCK)
                .pattern("rbr")
                .pattern("bub")
                .pattern("rbr")
                .unlockedBy("has_efficiency_2", has(EFFICIENCY_2.get()))
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EFFICIENCY_4.get())
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('u', EFFICIENCY_3.get())
                .define('b', Items.REDSTONE_BLOCK)
                .pattern("brb")
                .pattern("bub")
                .pattern("brb")
                .unlockedBy("has_efficiency_3", has(EFFICIENCY_3.get()))
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EFFICIENCY_5.get())
                .define('u', EFFICIENCY_4.get())
                .define('b', Items.REDSTONE_BLOCK)
                .pattern("bbb")
                .pattern("bub")
                .pattern("bbb")
                .unlockedBy("has_efficiency_4", has(EFFICIENCY_4.get()))
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EFFICIENCY_6.get())
                .define('u', EFFICIENCY_5.get())
                .define('b', Items.REDSTONE_BLOCK)
                .define('g', Items.GOLD_BLOCK)
                .pattern("bbb")
                .pattern("gug")
                .pattern("bbb")
                .unlockedBy("has_efficiency_5", has(EFFICIENCY_5.get()))
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FORTUNE_1.get())
                .define('l', Items.LAPIS_BLOCK)
                .define('g', Items.IRON_BLOCK)
                .define('u', UPGRADE_EMPTY.get())
                .pattern("lgl")
                .pattern("lul")
                .pattern("lgl")
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FORTUNE_2.get())
                .define('l', Items.LAPIS_BLOCK)
                .define('g', Items.GOLD_BLOCK)
                .define('u', FORTUNE_1.get())
                .pattern("lgl")
                .pattern("lul")
                .pattern("lgl")
                .unlockedBy("has_fortune_1", has(FORTUNE_1.get()))
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FORTUNE_3.get())
                .define('l', Items.LAPIS_BLOCK)
                .define('d', Items.DIAMOND)
                .define('u', FORTUNE_2.get())
                .pattern("ldl")
                .pattern("lul")
                .pattern("ldl")
                .unlockedBy("has_fortune_2", has(FORTUNE_2.get()))
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RANGE_1.get())
                .define('l', Items.LAPIS_LAZULI)
                .define('g', Items.GLASS)
                .define('d', Items.DIAMOND)
                .define('u', UPGRADE_EMPTY.get())
                .pattern("lgl")
                .pattern("dud")
                .pattern("lgl")
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RANGE_2.get())
                .define('l', Items.LAPIS_LAZULI)
                .define('g', Items.GLASS)
                .define('e', Items.EMERALD)
                .define('u', RANGE_1.get())
                .pattern("lgl")
                .pattern("eue")
                .pattern("lgl")
                .unlockedBy("has_range_1", has(RANGE_1.get()))
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RANGE_3.get())
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
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FREEZING.get())
                .define('s', Items.SNOWBALL)
                .define('u', UPGRADE_EMPTY.get())
                .pattern("sss")
                .pattern("sus")
                .pattern("sss")
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LIGHT_PLACER.get())
                .define('g', Items.GLOWSTONE_DUST)
                .define('b', Items.GLOWSTONE)
                .define('r', Items.REDSTONE_LAMP)
                .define('a', Items.LANTERN)
                .define('u', UPGRADE_EMPTY.get())
                .pattern("ara")
                .pattern("bub")
                .pattern("grg")
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MAGNET.get())
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('u', UPGRADE_EMPTY.get())
                .define('i', Tags.Items.INGOTS_IRON)
                .define('g', Tags.Items.INGOTS_GOLD)
                .pattern("rgr")
                .pattern("iui")
                .pattern("rgr")
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SILK.get())
                .define('d', Items.GOLDEN_APPLE)
                .define('s', Tags.Items.SLIMEBALLS)
                .define('g', Tags.Items.INGOTS_GOLD)
                .define('u', UPGRADE_EMPTY.get())
                .pattern("sds")
                .pattern("gug")
                .pattern("sss")
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SIZE_1.get())
                .define('r', Items.REDSTONE_BLOCK)
                .define('u', UPGRADE_EMPTY.get())
                .define('d', Items.DIAMOND_BLOCK)
                .define('p', Items.DIAMOND_PICKAXE)
                .define('e', Tags.Items.ENDER_PEARLS)
                .pattern("rdr")
                .pattern("eue")
                .pattern("rpr")
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SIZE_2.get())
                .define('n', Items.NETHERITE_BLOCK)
                .define('u', SIZE_1.get())
                .define('d', Items.DIAMOND_BLOCK)
                .define('p', Items.NETHERITE_PICKAXE)
                .define('e', Tags.Items.ENDER_PEARLS)
                .pattern("ene")
                .pattern("dud")
                .pattern("epe")
                .unlockedBy("has_size_1", has(SIZE_1.get()))
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SIZE_3.get())
                .define('n', Items.NETHERITE_BLOCK)
                .define('u', SIZE_2.get())
                .define('s', Tags.Items.NETHER_STARS)
                .define('p', Items.NETHERITE_PICKAXE)
                .define('e', Tags.Items.ENDER_PEARLS)
                .pattern("sns")
                .pattern("eue")
                .pattern("sps")
                .unlockedBy("has_size_2", has(SIZE_2.get()))
                .unlockedBy("has_size_1", has(SIZE_1.get()))
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, VOID_JUNK.get())
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('u', UPGRADE_EMPTY.get())
                .define('o', Blocks.OBSIDIAN)
                .define('e', Tags.Items.ENDER_PEARLS)
                .pattern("ror")
                .pattern("eue")
                .pattern("ror")
                .unlockedBy("has_upgrade", has(UPGRADE_EMPTY.get()))
                .save(recipeOutput);
    }
}
