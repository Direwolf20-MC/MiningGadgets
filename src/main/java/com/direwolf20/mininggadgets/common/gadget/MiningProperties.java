package com.direwolf20.mininggadgets.common.gadget;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

/**
 * Slightly nicer way of storing properties on the gadget. Still no where near what I'd
 * want it to be but as we have to handle types it makes any complex implementation
 * very complex which kinda misses the point of a nicer class.
 */
public class MiningProperties {
    private MiningProperties() {}

    private static final String KEY_BEAM_RANGE = "beamRange";
    private static final String KEY_RANGE = "range";
    private static final String KEY_SPEED = "speed";

    public static int setSpeed(ItemStack gadget, int speed) {
        gadget.getOrCreateTag().putInt(KEY_SPEED, speed);
        return speed;
    }

    public static int getSpeed(ItemStack gadget) {
        CompoundNBT compound = gadget.getOrCreateTag();
        return !compound.contains(KEY_SPEED) ? setSpeed(gadget, 1) : compound.getInt(KEY_SPEED);
    }

    public static int setRange(ItemStack gadget, int range) {
        gadget.getOrCreateTag().putInt(KEY_RANGE, range);
        return range;
    }

    public static int getRange(ItemStack gadget) {
        CompoundNBT compound = gadget.getOrCreateTag();
        return !compound.contains(KEY_RANGE) ? setRange(gadget, 1) : compound.getInt(KEY_RANGE);
    }

    public static int setBeamRange(ItemStack gadget, int range) {
        gadget.getOrCreateTag().putInt(KEY_BEAM_RANGE, range);
        return range;
    }

    public static int getBeamRange(ItemStack gadget) {
        CompoundNBT compound = gadget.getOrCreateTag();
        return !compound.contains(KEY_BEAM_RANGE) ? setRange(gadget, 1) : compound.getInt(KEY_BEAM_RANGE);
    }
}