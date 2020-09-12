package com.direwolf20.mininggadgets.common.data;

import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

import static com.direwolf20.mininggadgets.common.items.ModItems.*;

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
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(UPGRADE_EMPTY.get())
                .key('r', Tags.Items.DUSTS_REDSTONE)
                .key('g', Tags.Items.GLASS_PANES)
                .key('l', Tags.Items.GEMS_LAPIS)
                .key('d', Tags.Items.GEMS_DIAMOND)
                .patternLine("rlr")
                .patternLine("dgd")
                .patternLine("rlr")
                .addCriterion("has_diamonds", hasItem(Tags.Items.GEMS_DIAMOND))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(MININGGADGET.get())
                .key('u', UPGRADE_EMPTY.get())
                .key('r', Tags.Items.DUSTS_REDSTONE)
                .key('i', Tags.Items.INGOTS_IRON)
                .key('d', Tags.Items.GEMS_DIAMOND)
                .patternLine("dii")
                .patternLine("dur")
                .patternLine("dii")
                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModBlocks.MODIFICATION_TABLE.get())
                .key('r', Tags.Items.DUSTS_REDSTONE)
                .key('u', UPGRADE_EMPTY.get())
                .key('i', Tags.Items.INGOTS_IRON)
                .patternLine("iii")
                .patternLine("rur")
                .patternLine("iii")
                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(BATTERY_1.get())
                .key('q', Items.QUARTZ)
                .key('u', UPGRADE_EMPTY.get())
                .patternLine("qqq")
                .patternLine("quq")
                .patternLine("qqq")
                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(BATTERY_2.get())
                .key('q', Items.QUARTZ)
                .key('b', Items.QUARTZ_BLOCK)
                .key('g', Tags.Items.INGOTS_IRON)
                .key('u', BATTERY_1.get())
                .patternLine("qbq")
                .patternLine("gug")
                .patternLine("qbq")
                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
                .addCriterion("has_battery_1", hasItem(BATTERY_1.get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(BATTERY_3.get())
                .key('q', Items.QUARTZ)
                .key('b', Items.QUARTZ_BLOCK)
                .key('g', Tags.Items.INGOTS_GOLD)
                .key('u', BATTERY_2.get())
                .patternLine("bqb")
                .patternLine("gug")
                .patternLine("bqb")
                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
                .addCriterion("has_battery_2", hasItem(BATTERY_2.get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(EFFICIENCY_1.get())
                .key('r', Tags.Items.DUSTS_REDSTONE)
                .key('u', UPGRADE_EMPTY.get())
                .patternLine("rrr")
                .patternLine("rur")
                .patternLine("rrr")
                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(EFFICIENCY_2.get())
                .key('r', Tags.Items.DUSTS_REDSTONE)
                .key('u', EFFICIENCY_1.get())
                .key('b', Items.REDSTONE_BLOCK)
                .patternLine("rbr")
                .patternLine("rur")
                .patternLine("rbr")
                .addCriterion("has_efficiency_1", hasItem(EFFICIENCY_1.get()))
                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(EFFICIENCY_3.get())
                .key('r', Tags.Items.DUSTS_REDSTONE)
                .key('u', EFFICIENCY_2.get())
                .key('b', Items.REDSTONE_BLOCK)
                .patternLine("rbr")
                .patternLine("bub")
                .patternLine("rbr")
                .addCriterion("has_efficiency_2", hasItem(EFFICIENCY_2.get()))
                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(EFFICIENCY_4.get())
                .key('r', Tags.Items.DUSTS_REDSTONE)
                .key('u', EFFICIENCY_3.get())
                .key('b', Items.REDSTONE_BLOCK)
                .patternLine("brb")
                .patternLine("bub")
                .patternLine("brb")
                .addCriterion("has_efficiency_3", hasItem(EFFICIENCY_3.get()))
                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(EFFICIENCY_5.get())
                .key('u', EFFICIENCY_4.get())
                .key('b', Items.REDSTONE_BLOCK)
                .patternLine("bbb")
                .patternLine("bub")
                .patternLine("bbb")
                .addCriterion("has_efficiency_4", hasItem(EFFICIENCY_4.get()))
                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FORTUNE_1.get())
                .key('l', Items.LAPIS_BLOCK)
                .key('g', Items.IRON_BLOCK)
                .key('u', UPGRADE_EMPTY.get())
                .patternLine("lgl")
                .patternLine("lul")
                .patternLine("lgl")
                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FORTUNE_2.get())
                .key('l', Items.LAPIS_BLOCK)
                .key('g', Items.GOLD_BLOCK)
                .key('u', FORTUNE_1.get())
                .patternLine("lgl")
                .patternLine("lul")
                .patternLine("lgl")
                .addCriterion("has_fortune_1", hasItem(FORTUNE_1.get()))
                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FORTUNE_3.get())
                .key('l', Items.LAPIS_BLOCK)
                .key('d', Items.DIAMOND)
                .key('u', FORTUNE_2.get())
                .patternLine("ldl")
                .patternLine("lul")
                .patternLine("ldl")
                .addCriterion("has_fortune_2", hasItem(FORTUNE_2.get()))
                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(RANGE_1.get())
                .key('l', Items.LAPIS_LAZULI)
                .key('g', Items.GLASS)
                .key('d', Items.DIAMOND)
                .key('u', UPGRADE_EMPTY.get())
                .patternLine("lgl")
                .patternLine("dud")
                .patternLine("lgl")
                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(RANGE_2.get())
                .key('l', Items.LAPIS_LAZULI)
                .key('g', Items.GLASS)
                .key('e', Items.EMERALD)
                .key('u', RANGE_1.get())
                .patternLine("lgl")
                .patternLine("eue")
                .patternLine("lgl")
                .addCriterion("has_range_1", hasItem(RANGE_1.get()))
                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(RANGE_3.get())
                .key('l', Items.LAPIS_BLOCK)
                .key('g', Items.GLASS)
                .key('d', Items.DIAMOND_BLOCK)
                .key('e', Items.EMERALD_BLOCK)
                .key('u', RANGE_2.get())
                .patternLine("lgl")
                .patternLine("eud")
                .patternLine("lgl")
                .addCriterion("has_range_2", hasItem(RANGE_2.get()))
                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FREEZING.get())
                .key('s', Items.SNOWBALL)
                .key('u', UPGRADE_EMPTY.get())
                .patternLine("sss")
                .patternLine("sus")
                .patternLine("sss")
                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(LIGHT_PLACER.get())
                .key('g', Items.GLOWSTONE_DUST)
                .key('b', Items.GLOWSTONE)
                .key('r', Items.REDSTONE_LAMP)
                .key('a', Items.LANTERN)
                .key('u', UPGRADE_EMPTY.get())
                .patternLine("ara")
                .patternLine("bub")
                .patternLine("grg")
                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(MAGNET.get())
                .key('r', Tags.Items.DUSTS_REDSTONE)
                .key('u', UPGRADE_EMPTY.get())
                .key('i', Tags.Items.INGOTS_IRON)
                .key('g', Tags.Items.INGOTS_GOLD)
                .patternLine("rgr")
                .patternLine("iui")
                .patternLine("rgr")
                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(SILK.get())
                .key('d', Items.GOLDEN_APPLE)
                .key('s', Tags.Items.SLIMEBALLS)
                .key('g', Tags.Items.INGOTS_GOLD)
                .key('u', UPGRADE_EMPTY.get())
                .patternLine("sds")
                .patternLine("gug")
                .patternLine("sss")
                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(THREE_BY_THREE.get())
                .key('r', Items.REDSTONE_BLOCK)
                .key('u', UPGRADE_EMPTY.get())
                .key('d', Items.DIAMOND_BLOCK)
                .key('p', Items.DIAMOND_PICKAXE)
                .key('e', Tags.Items.ENDER_PEARLS)
                .patternLine("rdr")
                .patternLine("eue")
                .patternLine("rpr")
                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(VOID_JUNK.get())
                .key('r', Tags.Items.DUSTS_REDSTONE)
                .key('u', UPGRADE_EMPTY.get())
                .key('o', Blocks.OBSIDIAN)
                .key('e', Tags.Items.ENDER_PEARLS)
                .patternLine("ror")
                .patternLine("eue")
                .patternLine("ror")
                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(PAVER.get())
                .key('r', Tags.Items.DUSTS_REDSTONE)
                .key('c', Tags.Items.COBBLESTONE)
                .key('u', UPGRADE_EMPTY.get())
                .key('g', Tags.Items.NUGGETS_GOLD)
                .key('l', Items.LAVA_BUCKET)
                .key('p', Blocks.PISTON)
                .key('w', Items.WATER_BUCKET)
                .patternLine("rgr")
                .patternLine("cuc")
                .patternLine("lpw")
                .addCriterion("has_upgrade", hasItem(UPGRADE_EMPTY.get()))
                .build(consumer);
    }
}
