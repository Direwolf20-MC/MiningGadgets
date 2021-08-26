package com.direwolf20.mininggadgets.common.containers;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.UpgradeCard;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class FilterContainer extends AbstractContainerMenu {
    FilterContainer(int windowId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(windowId, playerInventory, new ItemStackHandler(30));
    }

    public FilterContainer(int windowId, Inventory playerInventory, IItemHandler ghostInventory) {
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
    public boolean stillValid(Player playerIn) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack currentStack = slot.getItem();

            // Stop our items at the very least :P
            if (currentStack.getItem() instanceof MiningGadget || currentStack.getItem() instanceof UpgradeCard)
                return itemstack;

            if (currentStack.isEmpty())
                return itemstack;

            // Find the first empty slot number
            int slotNumber = -1;
            for (int i = 36; i <= 62; i++) {
                if (this.slots.get(i).getItem().isEmpty()) {
                    slotNumber = i;
                    break;
                } else {
                    if (this.slots.get(i).getItem().getItem() == currentStack.getItem()) {
                        break;
                    }
                }
            }

            if (slotNumber == -1)
                return itemstack;

            this.slots.get(slotNumber).set(currentStack.copy().split(1));
        }

        return itemstack;
    }

//    @Override
//    public ItemStack clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
//        if ((slotId < this.slots.size()
//                && slotId >= 0
//                && this.slots.get(slotId).getItem().getItem() instanceof MiningGadget)
//                || clickTypeIn == ClickType.SWAP) {
//            return ItemStack.EMPTY;
//        }
//
//        return super.clicked(slotId, dragType, clickTypeIn, player);
//    }
}
