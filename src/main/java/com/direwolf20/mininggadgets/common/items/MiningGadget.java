package com.direwolf20.mininggadgets.common.items;

import com.direwolf20.mininggadgets.MiningGadgets;
import net.minecraft.item.Item;

public class MiningGadget extends Item {
    public MiningGadget() {
        super(new Item.Properties().maxStackSize(1).group(MiningGadgets.setup.itemGroup));
        setRegistryName("mininggadget");
    }
}
