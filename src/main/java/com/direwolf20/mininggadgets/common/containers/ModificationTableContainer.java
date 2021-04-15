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

        this.tileEntity = Minecraft.getInstance().level.getBlockEntity(extraData.readBlockPos());
        this.playerInventory = new InvWrapper(playerInventory);

        setupContainerSlots();
        layoutPlayerInventorySlots(8, 84);
    }

    public ModificationTableContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory) {
        super(ModContainers.MODIFICATIONTABLE_CONTAINER.get(), windowId);
        this.tileEntity = world.getBlockEntity(pos);
        this.playerInventory = new InvWrapper(playerInventory);

        setupContainerSlots();
        layoutPlayerInventorySlots(10, 70);
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return stillValid(IWorldPosCallable.create(getTE().getLevel(), tileEntity.getBlockPos()), playerIn, ModBlocks.MODIFICATION_TABLE.get());
    }

    private void setupContainerSlots() {
        this.getTE().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            addSlot(new WatchedSlot(h, 0,  -16, 84, this::updateUpgradeCache));
        });
    }

    private void updateUpgradeCache(int index) {
        ItemStack stack = this.getSlot(index).getItem();
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
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (index == 0) {
                if (!this.moveItemStackTo(stack, 1, this.getItems().size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, itemstack);
            } else {
                if (stack.getItem() instanceof MiningGadget) {
                    if (!this.moveItemStackTo(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stack.getItem() instanceof UpgradeCard) {
                    // Push the item right into the modification table.
                    if( ModificationTableCommands.insertButton(this, stack) ) {
                        int maxSize = Math.min(slot.getMaxStackSize(), stack.getMaxStackSize());
                        int remove = maxSize - itemstack.getCount();
                        stack.shrink(remove == 0 ? 1 : remove);
                        updateUpgradeCache(0);
                    }
                    else
                        return ItemStack.EMPTY;
                } else if (index < 29) {
                    if (!this.moveItemStackTo(stack, 29, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 38 && !this.moveItemStackTo(stack, 1, 29, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack);
        }

        return itemstack;
    }
}
