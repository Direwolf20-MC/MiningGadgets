package com.direwolf20.mininggadgets.common.util;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class MiscTools {
    public static ItemStack getGadget(PlayerEntity player) {
        ItemStack heldItem = player.getHeldItemMainhand();
        if (!(heldItem.getItem() instanceof MiningGadget)) {
            heldItem = player.getHeldItemOffhand();
            if (!(heldItem.getItem() instanceof MiningGadget)) {
                return ItemStack.EMPTY;
            }
        }
        return heldItem;
    }

    public static CompoundNBT getOrNewTag(ItemStack stack) {
        if (stack.hasTag()) {
            return stack.getTag();
        }
        CompoundNBT tag = new CompoundNBT();
        stack.setTag(tag);
        return tag;
    }

    public static String tidyValue(float value) {
        if (value < 1000)
            return String.valueOf(value);

        int exp = (int) (Math.log(value) / Math.log(1000));
        return String.format("%.1f%c",
                value / Math.pow(1000, exp),
                "kMGTPE_____".charAt(exp - 1));
    }
}
