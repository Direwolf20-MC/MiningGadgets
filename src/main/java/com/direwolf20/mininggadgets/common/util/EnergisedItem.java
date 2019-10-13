package com.direwolf20.mininggadgets.common.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.EnergyStorage;

public class EnergisedItem extends EnergyStorage {
    public EnergisedItem(ItemStack stack, int capacity) {
        super(capacity, Integer.MAX_VALUE, Integer.MAX_VALUE);

        this.energy = stack.hasTag() && stack.getTag().contains("energy") ? stack.getTag().getInt("energy") : 0;
    }
}
