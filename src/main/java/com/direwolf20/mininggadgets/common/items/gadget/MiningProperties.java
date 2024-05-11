package com.direwolf20.mininggadgets.common.items.gadget;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.containers.handlers.DataComponentHandler;
import com.direwolf20.mininggadgets.common.util.CodecHelpers;
import com.direwolf20.mininggadgets.setup.MGDataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

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
    private static final String KEY_MAX_MINING_RANGE = "maxMiningRange";
    private static final String KEY_SIZE_MODE = "sizeMode"; // Normal, Walkway, Others
    private static final String KEY_SPEED = "speed";
    private static final String BREAK_TYPE = "breakType";
    private static final String CAN_MINE = "canMine";
    private static final String PRECISION_MODE = "precisionMode";
    private static final String VOLUME = "volume";
    private static final String FREEZE_PARTICLE_DELAY = "freeze_particle_delay";
    private static final String KEY_BATTERY_TIER = "battery_tier";

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

    public enum SizeMode {
        AUTO("auto"),
        NORMAL("normal"),
        PATHWAY("pathway");

        private final String baseName;

        SizeMode(String baseName) {
            this.baseName = baseName;
        }

        public Component getTooltip() {
            return Component.translatable(MiningGadgets.MOD_ID + ".tooltip.screen.sizemode." + baseName);
        }
    }

    public static CodecHelpers.LaserColor getColors(ItemStack gadget) {
        return gadget.getOrDefault(MGDataComponents.LASER_COLOR, new CodecHelpers.LaserColor((short) 255, (short) 0, (short) 0, (short) 255, (short) 255, (short) 255));
    }

    public static void setColor(ItemStack gadget, CodecHelpers.LaserColor laserColor) {
        gadget.set(MGDataComponents.LASER_COLOR, laserColor);
    }

    public static void setBreakType(ItemStack gadget, BreakTypes breakType) {
        gadget.set(MGDataComponents.BREAK_TYPE, (byte) breakType.ordinal());
    }

    public static void nextBreakType(ItemStack gadget) {
        if (gadget.has(MGDataComponents.BREAK_TYPE)) {
            int type = getBreakType(gadget).ordinal() == BreakTypes.values().length - 1 ? 0 : getBreakType(gadget).ordinal() + 1;
            setBreakType(gadget, BreakTypes.values()[type]);
        } else {
            setBreakType(gadget, BreakTypes.FADE);
        }
    }

    public static BreakTypes getBreakType(ItemStack gadget) {
        return BreakTypes.values()[gadget.getOrDefault(MGDataComponents.BREAK_TYPE, (byte) 0)];
    }

    public static void setSpeed(ItemStack gadget, int speed) {
        gadget.set(MGDataComponents.SPEED, speed);
    }

    public static int getSpeed(ItemStack gadget) {
        return gadget.getOrDefault(MGDataComponents.SPEED, 1);
    }

    public static void setRange(ItemStack gadget, int range) {
        gadget.set(MGDataComponents.RANGE, range);
    }

    public static int getRange(ItemStack gadget) {
        return gadget.getOrDefault(MGDataComponents.RANGE, 1);
    }

    public static void setBeamRange(ItemStack gadget, int range) {
        gadget.set(MGDataComponents.BEAM_RANGE, range);
    }

    public static void setBeamMaxRange(ItemStack gadget, int range) {
        gadget.set(MGDataComponents.MAX_BEAM_RANGE, range);
    }

    public static int getBeamRange(ItemStack gadget) {
        return gadget.getOrDefault(MGDataComponents.BEAM_RANGE, MIN_RANGE);
    }

    public static int getBeamMaxRange(ItemStack gadget) {
        return gadget.getOrDefault(MGDataComponents.MAX_BEAM_RANGE, MIN_RANGE);
    }

    public static void setMaxMiningRange(ItemStack gadget, int range) {
        gadget.set(MGDataComponents.MAX_MINING_RANGE, range);
    }

    public static int getMaxMiningRange(ItemStack gadget) {
        return gadget.getOrDefault(MGDataComponents.MAX_MINING_RANGE, 1);
    }

    public static void setWhitelist(ItemStack gadget, boolean isWhitelist) {
        gadget.set(MGDataComponents.WHITELIST, isWhitelist);
    }

    public static boolean getWhiteList(ItemStack gadget) {
        return gadget.getOrDefault(MGDataComponents.WHITELIST, true);
    }

    public static void setCanMine(ItemStack gadget, boolean canMine) {
        gadget.set(MGDataComponents.CAN_MINE, canMine);
    }

    public static boolean getCanMine(ItemStack gadget) {
        return gadget.getOrDefault(MGDataComponents.CAN_MINE, true);
    }

    public static void setPrecisionMode(ItemStack gadget, boolean precisionMode) {
        gadget.set(MGDataComponents.PRECISION_MODE, precisionMode);
    }

    public static boolean getPrecisionMode(ItemStack gadget) {
        return gadget.getOrDefault(MGDataComponents.PRECISION_MODE, false);
    }


    public static void setSizeMode(ItemStack gadget, SizeMode sizeMode) {
        gadget.set(MGDataComponents.SIZE_MODE, (byte) sizeMode.ordinal());
    }

    public static SizeMode nextSizeMode(ItemStack gadget) {
        if (gadget.has(MGDataComponents.SIZE_MODE)) {
            int type = getSizeMode(gadget).ordinal() == SizeMode.values().length - 1 ? 0 : getSizeMode(gadget).ordinal() + 1;
            setSizeMode(gadget, SizeMode.values()[type]);
        } else {
            setSizeMode(gadget, SizeMode.NORMAL);
        }
        return getSizeMode(gadget);
    }

    public static SizeMode getSizeMode(ItemStack gadget) {
        return SizeMode.values()[gadget.getOrDefault(MGDataComponents.SIZE_MODE, (byte) SizeMode.AUTO.ordinal())];
    }

    public static void setVolume(ItemStack gadget, float volume) {
        gadget.set(MGDataComponents.VOLUME, volume);
    }

    public static float getVolume(ItemStack gadget) {
        return gadget.getOrDefault(MGDataComponents.VOLUME, 1.0f);
    }

    public static void setFreezeDelay(ItemStack gadget, int freezeDelay) {
        gadget.set(MGDataComponents.FREEZE_DELAY, freezeDelay);
    }

    public static int getFreezeDelay(ItemStack gadget) {
        return gadget.getOrDefault(MGDataComponents.FREEZE_DELAY, 0);
    }

    public static void setBatteryTier(ItemStack gadget, int tier) {
        gadget.set(MGDataComponents.BATTERY_TIER, tier);
    }

    public static int getBatteryTier(ItemStack gadget) {
        return gadget.getOrDefault(MGDataComponents.BATTERY_TIER, 0);
    }

    public static List<ItemStack> getFiltersAsList(ItemStack gadget) {
        List<ItemStack> returnList = new ArrayList<>();
        DataComponentHandler ghostInventory = new DataComponentHandler(gadget, 30);
        for (int i = 0; i < ghostInventory.getSlots(); i++) {
            ItemStack itemStack = ghostInventory.getStackInSlot(i);
            if (!itemStack.isEmpty())
                returnList.add(itemStack);
        }
        return returnList;
    }

    // mostly stolen from ItemStackHandler
    public static List<ItemStack> deserializeItemStackList(CompoundTag nbt, HolderLookup.Provider provider) {
        List<ItemStack> stacks = new ArrayList<>();
        ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag itemTags = tagList.getCompound(i);
            stacks.add(ItemStack.parse(provider, itemTags).orElse(ItemStack.EMPTY));
        }

        return stacks;
    }

    public static CompoundTag serializeItemStackList(List<ItemStack> stacks, HolderLookup.Provider provider) {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < stacks.size(); i++) {
            if (!stacks.get(i).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                nbtTagList.add(stacks.get(i).save(provider, itemTag));
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", stacks.size());
        return nbt;
    }
}
