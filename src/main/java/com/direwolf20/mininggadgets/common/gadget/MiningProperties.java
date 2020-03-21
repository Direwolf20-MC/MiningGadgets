package com.direwolf20.mininggadgets.common.gadget;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Slightly nicer way of storing properties on the gadget. Still no where near what I'd
 * want it to be but as we have to handle types it makes any complex implementation
 * very complex which kinda misses the point of a nicer class.
 *
 * todo: turn this entire class into a cap as it'll reduce the amount of hash
 *       getting that we're doing on each getOrCreate methods.
 */
public class MiningProperties {
    private MiningProperties() {}

    private static final String KEY_BEAM_RANGE = "beamRange";
    private static final String KEY_MAX_BEAM_RANGE = "maxBeamRange";
    private static final String KEY_WHITELIST = "isWhitelist";
    private static final String KEY_RANGE = "range";
    private static final String KEY_SPEED = "speed";
    private static final String BREAK_TYPE = "breakType";
    private static final String CAN_MINE = "canMine";
    private static final String PRECISION_MODE = "precisionMode";
    private static final String VOLUME = "volume";

    public static final String KEY_FILTERS = "filters";
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

    public static boolean setWhitelist(ItemStack gadget, boolean isWhitelist) {
        gadget.getOrCreateTag().putBoolean(KEY_WHITELIST, isWhitelist);
        return isWhitelist;
    }

    public static boolean getWhiteList(ItemStack gadget) {
        CompoundNBT compound = gadget.getOrCreateTag();
        return !compound.contains(KEY_WHITELIST) ? setWhitelist(gadget, true) : compound.getBoolean(KEY_WHITELIST);
    }

    public static boolean setCanMine(ItemStack gadget, boolean canMine) {
        gadget.getOrCreateTag().putBoolean(CAN_MINE, canMine);
        return canMine;
    }

    public static boolean getCanMine(ItemStack gadget) {
        CompoundNBT compound = gadget.getOrCreateTag();
        return !compound.contains(CAN_MINE) ? setCanMine(gadget, true) : compound.getBoolean(CAN_MINE);
    }

    public static boolean setPrecisionMode(ItemStack gadget, boolean precisionMode) {
        gadget.getOrCreateTag().putBoolean(PRECISION_MODE, precisionMode);
        return precisionMode;
    }

    public static boolean getPrecisionMode(ItemStack gadget) {
        CompoundNBT compound = gadget.getOrCreateTag();
        return !compound.contains(PRECISION_MODE) ? setPrecisionMode(gadget, false) : compound.getBoolean(PRECISION_MODE);
    }

    public static float setVolume(ItemStack gadget, float volume) {
        gadget.getOrCreateTag().putFloat(VOLUME, Math.max(0.0f, Math.min(1.0f, volume)));
        return volume;
    }

    public static float getVolume(ItemStack gadget) {
        CompoundNBT compound = gadget.getOrCreateTag();
        return !compound.contains(VOLUME) ? setVolume(gadget, 1.0f) : compound.getFloat(VOLUME);
    }

    /**
     * So this is a bit fun, because we only need the items in our list we're ditching half the data
     * that the `Items` actually contains.
     *
     * @implNote Please do not use {@link #deserializeItemStackList(CompoundNBT)} or {@link #serializeItemStackList(List)}
     *           if you wish to maintain the original tag data on the gadget. These have specific uses.
     *
     *           See {@link com.direwolf20.mininggadgets.common.network.packets.PacketOpenFilterContainer.Handler} for an
     *           understanding on why you shouldn't change the tad data on the gadget directly.
     */
    public static List<ItemStack> getFiltersAsList(ItemStack gadget) {
        return deserializeItemStackList(gadget.getOrCreateChildTag(MiningProperties.KEY_FILTERS));
    }

    // mostly stolen from ItemStackHandler
    public static List<ItemStack> deserializeItemStackList(CompoundNBT nbt) {
        List<ItemStack> stacks = new ArrayList<>();
        ListNBT tagList = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < tagList.size(); i++) {
            CompoundNBT itemTags = tagList.getCompound(i);
            stacks.add(ItemStack.read(itemTags));
        }

        return stacks;
    }

    public static CompoundNBT serializeItemStackList(List<ItemStack> stacks) {
        ListNBT nbtTagList = new ListNBT();
        for (int i = 0; i < stacks.size(); i++)
        {
            if (stacks.get(i).isEmpty())
                continue;

            CompoundNBT itemTag = new CompoundNBT();
            stacks.get(i).write(itemTag);
            nbtTagList.add(itemTag);
        }

        CompoundNBT nbt = new CompoundNBT();
        nbt.put("Items", nbtTagList);
        return nbt;
    }
}