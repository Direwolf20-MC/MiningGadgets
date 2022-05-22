package com.direwolf20.mininggadgets.api.upgrades;

public interface RenderBlockHooks {
    default void onTick() {}

    /**
     * Run after the effect block has been removed (aka, the block has been broken)
     *
     * @param context contains all the relevant info for the block that was removed and what removed it
     */
    default void afterBlockRemoved(RenderBlockContext context) {}

    /**
     * Run just before the block is removed this can be useful for doing state checks before it's removed
     *
     * @param context contains all the relevant info for the block that was removed and what removed it
     */
    default void beforeBlockRemoved(RenderBlockContext context) {}
}
