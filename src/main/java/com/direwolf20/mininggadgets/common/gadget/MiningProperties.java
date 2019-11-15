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
    private static final String KEY_MAX_BEAM_RANGE = "maxBeamRange";
    private static final String KEY_RANGE = "range";
    private static final String KEY_SPEED = "speed";
    private static final String BREAK_TYPE = "breakType";
    public static final String COLOR_RED = "colorRed";
    public static final String COLOR_GREEN = "colorGreen";
    public static final String COLOR_BLUE = "colorBlue";
    public static final String COLOR_RED_INNER = "colorRedInner";
    public static final String COLOR_GREEN_INNER = "colorGreenInner";
    public static final String COLOR_BLUE_INNER = "colorBlueInner";

    public static final int MIN_RANGE = 5;

    public enum BreakTypes {
        SHRINK,
        FADE
    }

    public static short getColor(ItemStack gadget, String color) {
        CompoundNBT compound = gadget.getOrCreateTag();
        if (color == COLOR_RED || color.contains("Inner")) {
            return !compound.contains(color) ? setColor(gadget, (short) 255, color) : compound.getShort(color);
        } else {
            return !compound.contains(color) ? setColor(gadget, (short) 0, color) : compound.getShort(color);
        }
    }

    public static short setColor(ItemStack gadget, short colorValue, String color) {
        gadget.getOrCreateTag().putShort(color, colorValue);
        return colorValue;
    }

    public static BreakTypes setBreakType(ItemStack gadget, BreakTypes breakType) {
        gadget.getOrCreateTag().putByte(BREAK_TYPE, (byte) breakType.ordinal());
        return breakType;
    }

    public static void nextBreakType(ItemStack gadget) {
        CompoundNBT compound = gadget.getOrCreateTag();
        if (compound.contains(BREAK_TYPE)) {
            int type = getBreakType(gadget).ordinal() == BreakTypes.values().length - 1 ? 0 : getBreakType(gadget).ordinal() + 1;
            setBreakType(gadget, BreakTypes.values()[type]);
        } else {
            setBreakType(gadget, BreakTypes.FADE);
        }
    }

    public static BreakTypes getBreakType(ItemStack gadget) {
        CompoundNBT compound = gadget.getOrCreateTag();
        return !compound.contains(BREAK_TYPE) ? setBreakType(gadget, BreakTypes.SHRINK) : BreakTypes.values()[compound.getByte(BREAK_TYPE)];
    }

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

    public static int setBeamMaxRange(ItemStack gadget, int range) {
        gadget.getOrCreateTag().putInt(KEY_MAX_BEAM_RANGE, range);
        return range;
    }

    public static int getBeamRange(ItemStack gadget) {
        CompoundNBT compound = gadget.getOrCreateTag();
        return !compound.contains(KEY_BEAM_RANGE) ? setBeamRange(gadget, MIN_RANGE) : compound.getInt(KEY_BEAM_RANGE);
    }

    public static int getBeamMaxRange(ItemStack gadget) {
        CompoundNBT compound = gadget.getOrCreateTag();
        return !compound.contains(KEY_MAX_BEAM_RANGE) ? setBeamMaxRange(gadget, MIN_RANGE) : compound.getInt(KEY_MAX_BEAM_RANGE);
    }
}