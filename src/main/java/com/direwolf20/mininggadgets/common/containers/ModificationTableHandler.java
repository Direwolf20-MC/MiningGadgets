package com.direwolf20.mininggadgets.common.containers;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.tiles.ModificationTableTileEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class ModificationTableHandler extends ItemStackHandler {
    ModificationTableTileEntity blockEntity;

    public ModificationTableHandler(int size) {
        super(size);
    }

    public ModificationTableHandler(int size, ModificationTableTileEntity blockEntity) {
        super(size);
        this.blockEntity = blockEntity;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return slot == 0 && stack.getItem() instanceof MiningGadget;
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (blockEntity != null)
            blockEntity.setChanged();
    }
}
