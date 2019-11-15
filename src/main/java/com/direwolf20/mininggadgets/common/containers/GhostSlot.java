package com.direwolf20.mininggadgets.common.containers;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class GhostSlot extends SlotItemHandler {
    public GhostSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }

    @Override
    public void putStack(@Nonnull ItemStack stack) {
        if( !(stack.getItem() instanceof BlockItem) && stack.getItem() != Items.AIR )
            return;

        super.putStack(stack);
    }
}
