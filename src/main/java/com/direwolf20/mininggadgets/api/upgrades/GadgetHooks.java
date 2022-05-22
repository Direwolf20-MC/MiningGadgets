package com.direwolf20.mininggadgets.api.upgrades;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * All the current hooks used for our upgrades, if you need another one, just PR or ask us.
 *
 * Gadget usage flow:
 * - User looks at a valid block and starts holding right click to break the block {@link #usingTick(GadgetUseContext)}
 * - An effect block is spawned, this is a fake block that holds the original blocks' data
 * - As the user continues to look at the block and holds right click, we remove durability from the block
 * - Upon the durability of the block hitting 0, we remove the effect block and run actions for the original block
 */
public interface GadgetHooks {
    /**
     * The higher the priority, the higher the order in the execution list. If you need something to happen
     * first then you'd want a higher priority
     *
     * @return the upgrades' priority in the upgrade list
     */
    default int getPriority() {
        return 1;
    }

    /**
     * Run an action for tick the item is being used for
     *
     * @param context contains all the relevant info for the block that was removed and what removed it
     * @param targetLocations the locations the gadget is targeting
     */
    default void usingTick(GadgetUseContext context, List<BlockPos> targetLocations, int tickCount) {}

    /**
     * Allows you to add enchantments and other modifications to the tool that is used
     *
     * @param mockItem the mockItem is used as the item that broke the block meaning you can modify how the game sees
     *                 the block being broken and what broke the block
     *
     * @param context contains all the relevant info for the block that was removed and what removed it
     */
    default void applyToTool(GadgetUseContext context, ItemStack mockItem) {}

    /**
     * Call right before the items enter the world, you should use this if you plan to do something special with
     * the items but beware that the Magnet upgrade will modify this list if you don't have a higher priority.
     *
     * @param context contains all the relevant info for the block that was removed and what removed it
     * @param drops the drops that are just about to enter the world
     *
     * @return gives back any remaining items or the input if not handled
     */
    default List<ItemStack> beforeItemsDrop(GadgetUseContext context, List<ItemStack> drops) {
        return drops;
    }

    /**
     * Allows the upgrade to modify the drops of a block
     *
     * @param drops the drops passed through each upgrade, by default this will just be the normal blocks drops.
     *              Don't mutate! Return a new list or the original
     * @param blockBroken the block that was broken (not the effect block)
     * @param context contains all the relevant info for the block that was removed and what removed it
     *
     * @return the new list of drops. Do not modify the drops list! Your modified drops are passed to the next upgrade
     *         in order of the upgrades added
     */
    default List<ItemStack> modifyBlockDrops(List<ItemStack> drops, GadgetUseContext context, BlockState blockBroken) {
        return drops;
    }

    /**
     * Allows you to do something with the items that are about to drop
     *
     * @param context contains all the relevant info for the block that was removed and what removed it
     * @param itemsToDrop the items that are about to drop. Don't mutate! Return a new list or the original
     */
    default List<ItemStack> onItemsDropped(GadgetUseContext context, List<ItemStack> itemsToDrop) {
        return itemsToDrop;
    }
}
