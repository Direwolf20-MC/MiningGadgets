package com.direwolf20.mininggadgets.common.setup;

import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModSetup {
    public ItemGroup itemGroup = new ItemGroup("mininggadgets") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModBlocks.RENDERBLOCK);
        }
    };

    public void init() {

    }
}
