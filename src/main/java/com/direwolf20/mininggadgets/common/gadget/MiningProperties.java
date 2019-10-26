package com.direwolf20.mininggadgets.common.gadget;

import com.direwolf20.mininggadgets.common.util.MiscTools;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

/**
 * Slightly nicer way of storing properties on the gadget. Still no where near what I'd
 * want it to be but as we have to handle types it makes any complex implementation
 * very complex which kinda misses the point of a nicer class.
 */
public class MiningProperties {
    private MiningProperties() {}

    public static int setSpeed(ItemStack gadget, int speed) {
        getCompound(gadget).putInt("range", speed);
        return speed;
    }

    public static int getSpeed(ItemStack gadget) {
        CompoundNBT compound = getCompound(gadget);
        return !compound.contains("speed") ? setSpeed(gadget, 1) : compound.getInt("speed");
    }

    public static int setRange(ItemStack gadget, int range) {
        getCompound(gadget).putInt("range", range);
        return range;
    }

    public static int getRange(ItemStack gadget) {
        CompoundNBT compound = getCompound(gadget);
        return !compound.contains("range") ? setRange(gadget, 1) : compound.getInt("range");
    }

    private static CompoundNBT getCompound(ItemStack gadget) {
        return MiscTools.getOrNewTag(gadget);
    }
}