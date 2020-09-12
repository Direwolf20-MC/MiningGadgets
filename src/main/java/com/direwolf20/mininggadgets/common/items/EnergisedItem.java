package com.direwolf20.mininggadgets.common.items;

import net.minecraft.item.ItemStack;
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
        stack.getOrCreateTag().putInt("max_energy", max);
        this.capacity = max;

        // Ensure the current stored energy is up to date with the new max.
        this.receiveEnergy(1, false);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int stored = this.getEnergyStored() + maxReceive;
        if (stored < 0) {
            return 0;
        }

        int amount = super.receiveEnergy(maxReceive, simulate);
        if( !simulate )
            stack.getOrCreateTag().putInt("energy", this.energy);

        return amount;
    }
}
