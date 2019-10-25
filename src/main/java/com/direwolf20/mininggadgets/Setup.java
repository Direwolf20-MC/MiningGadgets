package com.direwolf20.mininggadgets;

import com.direwolf20.mininggadgets.common.items.ModItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class Setup {

    private static ItemGroup itemGroup = new ItemGroup(MiningGadgets.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.MININGGADGET.get());
        }
    };

    public void init() {

    }

    public static ItemGroup getItemGroup() {
        return itemGroup;
    }
}
