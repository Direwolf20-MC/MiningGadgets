package com.direwolf20.mininggadgets.common.containers;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.UpgradeCard;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.setup.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ModificationTableContainer extends AbstractContainerMenu {

    private BlockEntity tileEntity;
    private List<Upgrade> upgradesCache = new ArrayList<>();

    public ModificationTableContainer(int windowId, Inventory playerInventory, FriendlyByteBuf extraData) {
        super(Registration.MODIFICATIONTABLE_CONTAINER.get(), windowId);

        this.tileEntity = Minecraft.getInstance().level.getBlockEntity(extraData.readBlockPos());

        setupContainerSlots();
        layoutPlayerInventorySlots(playerInventory, 8, 84);
    }

    public ModificationTableContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory) {
        super(Registration.MODIFICATIONTABLE_CONTAINER.get(), windowId);
        this.tileEntity = world.getBlockEntity(pos);

        setupContainerSlots();
        layoutPlayerInventorySlots(playerInventory, 10, 70);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(getTE().getLevel(), tileEntity.getBlockPos()), playerIn, Registration.MODIFICATION_TABLE.get());
    }

    private void setupContainerSlots() {
        var inv = this.tileEntity.getLevel().getCapability(Capabilities.Item.BLOCK,  tileEntity.getBlockPos(), tileEntity.getBlockState(), tileEntity, null);
        if (inv instanceof ModificationTableHandler handler) {
            addSlot(new WatchedSlot(handler, 0, -16, 84) {
                @Override
                protected void setStackCopy(@NonNull ItemStack stack)
                {
                    super.setStackCopy(stack);
                    updateUpgradeCache(0);
                }
            });
        }
    }

    private void updateUpgradeCache(int index) {
        ItemStack stack = this.getSlot(index).getItem();
        if((stack.isEmpty() && !upgradesCache.isEmpty()) || !(stack.getItem() instanceof MiningGadget)) {
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

    public BlockEntity getTE() {
        return this.tileEntity;
    }

    private int addSlotRange(Inventory handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new Slot(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(Inventory handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(Inventory inventory, int leftCol, int topRow) {
        // Player inventory
        addSlotBox(inventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(inventory, 0, leftCol, topRow, 9, 18);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
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
                    if(ModificationTableCommands.insertButton(this, stack) ) {
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
