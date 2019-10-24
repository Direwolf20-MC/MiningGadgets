package com.direwolf20.mininggadgets.common.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.energy.EnergyStorage;

public class EnergisedItem extends EnergyStorage {
    private ItemStack stack;

    public EnergisedItem(ItemStack stack, int capacity) {
        super(getMaxCapacity(stack, capacity), Integer.MAX_VALUE, Integer.MAX_VALUE);

        this.stack = stack;
        this.energy = stack.hasTag() && stack.getTag().contains("energy") ? stack.getTag().getInt("energy") : 0;
    }

    private static int getMaxCapacity(ItemStack stack, int capacity) {
        if( !stack.hasTag() || !stack.getTag().contains("max_energy") )
            return capacity;

        return stack.getTag().getInt("max_energy");
    }

    public void updatedMaxEnergy(int max) {
        if( !stack.hasTag() )
            return;

        stack.getTag().putInt("max_energy", max);
        this.capacity = max;

        // Ensure the current stored energy is up to date with the new max.
        this.receiveEnergy(1500, false);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int amount = super.receiveEnergy(maxReceive, simulate);

        CompoundNBT compound = MiscTools.getOrNewTag(stack);
        compound.putInt("energy", this.energy);

        return amount;
    }

    @Override
    public int getEnergyStored() {
        int amount = super.getEnergyStored();

        CompoundNBT compound = MiscTools.getOrNewTag(stack);
        compound.putInt("energy", amount);

        return amount;
    }
}
