package com.direwolf20.mininggadgets.common.collectors;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.util.BlockTagConfigMatcher;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

// Borrowed from FTB Ulitmine by LatvianModder
// original code found here https://github.com/FTBTeam/FTB-Ultimine/blob/c8169d1f703beeb11a2dfb41be2e5b10114c207e/common/src/main/java/dev/ftb/mods/ftbultimine/shape/BlockMatcher.java
/**
 * @author LatvianModder
 */
public interface BlockMatcher {
    boolean check(BlockState original, BlockState state);

    default boolean actualCheck(BlockState original, BlockState state) {
        return !state.is(MiningGadgets.EXCLUDED_BLOCKS) && check(original, state);
    }

    BlockMatcher MATCH = (original, state) -> original.getBlock() == state.getBlock();
    BlockMatcher TAGS_MATCH_SHAPELESS = BlockTagConfigMatcher.MERGE_TAGS_SHAPELESS::match;
    BlockMatcher TAGS_MATCH_SHAPED = BlockTagConfigMatcher.MERGE_TAGS_SHAPED::match;
    BlockMatcher CROP_LIKE = (original, state) -> (state.getBlock() instanceof BushBlock || state.getBlock() instanceof CocoaBlock)
            && getBushType(state.getBlock()) == getBushType(original.getBlock());

    static int getBushType(Block block) {
        if (block instanceof CropBlock) {
            return 1;
        } else if (block instanceof SaplingBlock) {
            return 2;
        } else if (block instanceof CocoaBlock) {
            return 3;
        }

        return 0;
    }
}
