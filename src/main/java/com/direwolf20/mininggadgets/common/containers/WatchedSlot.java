package com.direwolf20.mininggadgets.common.containers;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.function.Consumer;

/**
 * This isn't required but does make the cache very effective and quick.
 */
public class WatchedSlot extends SlotItemHandler {
    private Consumer<Integer> onPress;

    public WatchedSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Consumer<Integer> onPress) {
        super(itemHandler, index, xPosition, yPosition);
        this.onPress = onPress;
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();

        this.onPress.accept(this.getSlotIndex());
    }
}
