package com.direwolf20.mininggadgets.common.capabilities;

import com.direwolf20.mininggadgets.setup.MGDataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.EnergyStorage;

public class EnergyStorageItemstack extends EnergyStorage {
    protected final ItemStack itemStack;

    public EnergyStorageItemstack(int capacity, ItemStack itemStack) {
        super(getMaxCapacity(itemStack, capacity), Integer.MAX_VALUE, Integer.MAX_VALUE);
        this.itemStack = itemStack;
        this.energy = itemStack.getOrDefault(MGDataComponents.FORGE_ENERGY, 0);
    }

    public void setEnergy(int energy) {
        this.energy = energy;
        itemStack.set(MGDataComponents.FORGE_ENERGY, energy);
    }

    private static int getMaxCapacity(ItemStack stack, int capacity) {
        return stack.getOrDefault(MGDataComponents.FORGE_ENERGY_MAX_ENERGY, capacity);
    }

    public void updatedMaxEnergy(int max) {
        itemStack.set(MGDataComponents.FORGE_ENERGY_MAX_ENERGY, max);
        this.capacity = max;
        this.energy = Math.min(max, this.energy);

        // Ensure the current stored energy is up to date with the new max.
        this.receiveEnergy(1, false);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;

        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate) {
            energy += energyReceived;
            itemStack.set(MGDataComponents.FORGE_ENERGY, energy);
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;

        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate) {
            energy -= energyExtracted;
            itemStack.set(MGDataComponents.FORGE_ENERGY, energy);
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return itemStack.getOrDefault(MGDataComponents.FORGE_ENERGY, 0);
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return this.maxReceive > 0;
    }

    @Override
    public Tag serializeNBT(HolderLookup.Provider provider) {
        return IntTag.valueOf(this.getEnergyStored());
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, Tag nbt) {
        if (!(nbt instanceof IntTag intNbt))
            throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
        this.energy = intNbt.getAsInt();
    }
}
