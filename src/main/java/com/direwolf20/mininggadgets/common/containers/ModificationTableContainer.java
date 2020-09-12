package com.direwolf20.mininggadgets.common.containers;

import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.UpgradeCard;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.ArrayList;
import java.util.List;

public class ModificationTableContainer extends Container {

    private TileEntity tileEntity;
    private IItemHandler playerInventory;
    private List<Upgrade> upgradesCache = new ArrayList<>();

    public ModificationTableContainer(int windowId, PlayerInventory playerInventory, PacketBuffer extraData) {
        super(ModContainers.MODIFICATIONTABLE_CONTAINER.get(), windowId);

        this.tileEntity = Minecraft.getInstance().world.getTileEntity(extraData.readBlockPos());
        this.playerInventory = new InvWrapper(playerInventory);

        setupContainerSlots();
        layoutPlayerInventorySlots(8, 84);
    }

    public ModificationTableContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory) {
        super(ModContainers.MODIFICATIONTABLE_CONTAINER.get(), windowId);
        this.tileEntity = world.getTileEntity(pos);
        this.playerInventory = new InvWrapper(playerInventory);

        setupContainerSlots();
        layoutPlayerInventorySlots(10, 70);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(IWorldPosCallable.of(getTE().getWorld(), tileEntity.getPos()), playerIn, ModBlocks.MODIFICATION_TABLE.get());
    }

    private void setupContainerSlots() {
        this.getTE().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            addSlot(new WatchedSlot(h, 0,  -16, 84, this::updateUpgradeCache));
        });
    }

    private void updateUpgradeCache(int index) {
        ItemStack stack = this.getSlot(index).getStack();
        if( (stack.isEmpty() && !upgradesCache.isEmpty()) || !(stack.getItem() instanceof MiningGadget) ) {
            upgradesCache.clear();
            return;
        }

        // Purge and set cache
        upgradesCache.clear();
        upgradesCache = UpgradeTools.getUpgrades(stack);
    }

    public List<Upgrade> getUpgradesCache() {
        return upgradesCache;
    }

    public TileEntity getTE() {
        return this.tileEntity;
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();
            if (index == 0) {
                if (!this.mergeItemStack(stack, 1, this.getInventory().size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(stack, itemstack);
            } else {
                if (stack.getItem() instanceof MiningGadget) {
                    if (!this.mergeItemStack(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stack.getItem() instanceof UpgradeCard) {
                    // Push the item right into the modification table.
                    if( ModificationTableCommands.insertButton(this, stack) ) {
                        int maxSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());
                        int remove = maxSize - itemstack.getCount();
                        stack.shrink(remove == 0 ? 1 : remove);
                        updateUpgradeCache(0);
                    }
                    else
                        return ItemStack.EMPTY;
                } else if (index < 29) {
                    if (!this.mergeItemStack(stack, 29, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 38 && !this.mergeItemStack(stack, 1, 29, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack);
        }

        return itemstack;
    }
}
