package com.direwolf20.mininggadgets.common.containers;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.UpgradeCard;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class FilterContainer extends Container {
    FilterContainer(int windowId, PlayerInventory playerInventory, PacketBuffer buf) {
        this(windowId, playerInventory, new ItemStackHandler(30));
    }

    public FilterContainer(int windowId, PlayerInventory playerInventory, IItemHandler ghostInventory) {
        super(ModContainers.FILTER_CONTAINER.get(), windowId);
        this.setup(new InvWrapper(playerInventory), ghostInventory);
    }

    private void setup(InvWrapper playerInventory, IItemHandler ghostInventory) {
        int index = 0, x = 8, y = 143;

        // Build the players inventory first, building from bottom to top, right to left. The (i>0) magic handles the
        // space between the hotbar inventory and the players remaining inventory.
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new SlotItemHandler(playerInventory, index, x + (j * 18), (y - (i > 0 ? 4 : 0)) - (i * 18)));
                index++;
            }
        }

        // Build the filter slots, this time building from top to bottom, left to right. Starting at index 0
        index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new GhostSlot(ghostInventory, index, 8 + (18 * j), 18 + (i * 18)));
                index++;
            }
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack currentStack = slot.getStack();

            // Stop our items at the very least :P
            if (currentStack.getItem() instanceof MiningGadget || currentStack.getItem() instanceof UpgradeCard)
                return itemstack;

            if (currentStack.isEmpty())
                return itemstack;

            // Find the first empty slot number
            int slotNumber = -1;
            for (int i = 36; i <= 62; i++) {
                if (this.inventorySlots.get(i).getStack().isEmpty()) {
                    slotNumber = i;
                    break;
                } else {
                    if (this.inventorySlots.get(i).getStack().getItem() == currentStack.getItem()) {
                        break;
                    }
                }
            }

            if (slotNumber == -1)
                return itemstack;

            this.inventorySlots.get(slotNumber).putStack(currentStack.copy().split(1));
        }

        return itemstack;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        if ((slotId < this.inventorySlots.size()
                && slotId >= 0
                && this.inventorySlots.get(slotId).getStack().getItem() instanceof MiningGadget)
                || clickTypeIn == ClickType.SWAP) {
            return ItemStack.EMPTY;
        }

        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }
}
