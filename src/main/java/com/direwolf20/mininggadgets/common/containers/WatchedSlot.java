package com.direwolf20.mininggadgets.common.containers;

import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

/**
 * This isn't required but does make the cache very effective and quick.
 */
public class WatchedSlot extends ResourceHandlerSlot
{
    public WatchedSlot(ModificationTableHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, itemHandler::set, index, xPosition, yPosition);
    }
}
