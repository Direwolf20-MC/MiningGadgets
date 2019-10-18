package com.direwolf20.mininggadgets.common.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

public class MiningContainer extends Container {
    private IItemHandler playerInventory;
    private ItemStack gadget;

    public MiningContainer(int windowId, PlayerInventory playerInventory, PacketBuffer extraData) {
        this(windowId, playerInventory, extraData.readItemStack());
    }

    public MiningContainer(int windowId, PlayerInventory playerInventory, ItemStack gadget) {
        super(ModContainers.MINING_CONTAINER, windowId);
        this.setup(playerInventory, 8, 84, gadget);
    }

    private void setup(PlayerInventory playerInventory, int left, int top, ItemStack gadget) {
        this.playerInventory = new InvWrapper(playerInventory);
        this.gadget = gadget;
        System.out.println(this.gadget);

        // Player inventory
        addSlotBox(this.playerInventory, 9, left, top, 9, 18, 3, 18);
        addSlotRange(this.playerInventory, 0, left, top + 58, 9, 18);
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

    // Apparently containers that don't belong to tiles require a provider
    public static class MiningProvider implements INamedContainerProvider {
        private ItemStack gadget;

        public MiningProvider(ItemStack gadget) {
            this.gadget = gadget;
        }

        @Override
        public ITextComponent getDisplayName() { return new StringTextComponent(""); }

        @Nullable
        @Override
        public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
            return new MiningContainer(i, playerInventory, this.gadget);
        }
    }
}
