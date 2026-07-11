package com.direwolf20.mininggadgets.common.containers;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.UpgradeCard;
import com.direwolf20.mininggadgets.common.tiles.ModificationTableTileEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import javax.annotation.Nonnull;

public class ModificationTableHandler extends ItemStacksResourceHandler {

    ModificationTableTileEntity blockEntity;

    public ModificationTableHandler(int size, ModificationTableTileEntity blockEntity) {
        super(size);
        this.blockEntity = blockEntity;
    }

    @Override
    public boolean isValid(int slot, @Nonnull ItemResource stack) {
        return slot == 0 && stack.getItem() instanceof MiningGadget;
    }

    @Override
    protected void onContentsChanged(int slot, ItemStack stack) {
        if (blockEntity != null)
            blockEntity.setChanged();
    }
}
