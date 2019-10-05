package com.direwolf20.mininggadgets.common.util;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

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
}
