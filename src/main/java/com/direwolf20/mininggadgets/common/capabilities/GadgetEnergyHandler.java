package com.direwolf20.mininggadgets.common.capabilities;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

//This is a copy of ItemAccessEnergyHandler that allows modification of the capacity
public class GadgetEnergyHandler implements EnergyHandler
{
    protected final ItemAccess itemAccess;
    protected final Item validItem;
    protected final DataComponentType<Integer> energyComponent;
    protected int capacity;
    protected final int maxInsert;
    protected final int maxExtract;

    public GadgetEnergyHandler(ItemAccess itemAccess, DataComponentType<Integer> energyComponent, int capacity) {
        this(itemAccess, energyComponent, capacity, capacity);
    }

    public GadgetEnergyHandler(ItemAccess itemAccess, DataComponentType<Integer> energyComponent, int capacity, int maxTransfer) {
        this(itemAccess, energyComponent, capacity, maxTransfer, maxTransfer);
    }

    public GadgetEnergyHandler(ItemAccess itemAccess, DataComponentType<Integer> energyComponent, int capacity, int maxInsert, int maxExtract) {
        TransferPreconditions.checkNonNegative(capacity);
        TransferPreconditions.checkNonNegative(maxInsert);
        TransferPreconditions.checkNonNegative(maxExtract);

        this.itemAccess = itemAccess;
        this.validItem = itemAccess.getResource().getItem();
        this.energyComponent = energyComponent;
        this.capacity = capacity;
        this.maxInsert = maxInsert;
        this.maxExtract = maxExtract;
    }

    public void updateCapacity(int value, TransactionContext transaction) {
        this.capacity = value;
        itemAccess.exchange(itemAccess.getResource(), itemAccess.getAmount(), transaction);
    }

    protected int getAmountFrom(ItemResource accessResource) {
        if (!accessResource.is(validItem)) {
            return 0;
        }
        return accessResource.getOrDefault(energyComponent, 0);
    }

    protected ItemResource update(ItemResource accessResource, int newAmount) {
        return accessResource.with(energyComponent, newAmount);
    }

    @Override
    public long getAmountAsLong() {
        return (long) itemAccess.getAmount() * getAmountFrom(itemAccess.getResource());
    }

    @Override
    public long getCapacityAsLong() {
        var accessResource = itemAccess.getResource();
        if (!accessResource.is(validItem)) {
            return 0;
        }

        return (long) itemAccess.getAmount() * this.capacity;
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonNegative(amount);

        int accessAmount = itemAccess.getAmount();
        if (accessAmount == 0) {
            return 0;
        }
        int amountPerItem = Math.min(maxInsert, amount / accessAmount);
        if (amountPerItem == 0) {
            return 0;
        }

        ItemResource accessResource = itemAccess.getResource();
        if (!accessResource.is(validItem)) {
            return 0;
        }
        int currentAmountPerItem = getAmountFrom(accessResource);

        int insertedPerItem = Math.min(amountPerItem, capacity - currentAmountPerItem);
        if (insertedPerItem > 0) {
            ItemResource filledResource = update(accessResource, currentAmountPerItem + insertedPerItem);

            if (!filledResource.isEmpty()) {
                return insertedPerItem * itemAccess.exchange(filledResource, accessAmount, transaction);
            }
        }

        return 0;
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonNegative(amount);

        int accessAmount = itemAccess.getAmount();
        if (accessAmount == 0) {
            return 0;
        }
        int amountPerItem = Math.min(maxExtract, amount / accessAmount);
        if (amountPerItem == 0) {
            return 0;
        }

        ItemResource accessResource = itemAccess.getResource();
        int currentAmountPerItem = getAmountFrom(accessResource);

        int extractedPerItem = Math.min(amountPerItem, currentAmountPerItem);
        if (extractedPerItem > 0) {
            ItemResource emptiedResource = update(accessResource, currentAmountPerItem - extractedPerItem);

            if (!emptiedResource.isEmpty()) {
                return extractedPerItem * itemAccess.exchange(emptiedResource, accessAmount, transaction);
            }
        }

        return 0;
    }
}
