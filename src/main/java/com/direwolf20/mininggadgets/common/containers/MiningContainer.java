package com.direwolf20.mininggadgets.common.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class MiningContainer extends Container {
    MiningContainer(int windowId, PlayerInventory playerInventory, PacketBuffer buf) {
        this(windowId, playerInventory, new ItemStackHandler(30));
    }

    public MiningContainer(int windowId, PlayerInventory playerInventory, IItemHandler ghostInventory) {
        super(ModContainers.FILTER_CONTAINER.get(), windowId);
        this.setup(playerInventory, 8, 84, ghostInventory);
    }

    private void setup(PlayerInventory playerInventory, int left, int top, IItemHandler ghostInventory) {
        IItemHandler playerInventory1 = new InvWrapper(playerInventory);

        int index = 0;
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 9; y ++) {
                addSlot(new GhostSlot(ghostInventory, index , left + (18 * y), top - 84 + (x * 18)));
                index ++;
            }
        }

        // Player inventory
        addSlotBox(playerInventory1, 9, left, top, 9, 18, 3, 18);
        addSlotRange(playerInventory1, 0, left, top + 58, 9, 18);
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private void addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) { return true; }
}
