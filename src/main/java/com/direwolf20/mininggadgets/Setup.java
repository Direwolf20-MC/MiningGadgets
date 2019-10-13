package com.direwolf20.mininggadgets;

import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.items.ModItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class Setup {
    public static final String MOD_ID = "mininggadgets";

    private static ItemGroup itemGroup = new ItemGroup(MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.MININGGADGET);
        }
    };

    public void init() {

    }

    public static ItemGroup getItemGroup() {
        return itemGroup;
    }
}
