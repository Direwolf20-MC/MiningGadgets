package com.direwolf20.mininggadgets.common.items;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.common.gadget.upgrade.Upgrade;
import net.minecraft.item.Item;

public class UpgradeCard extends Item {
    private Upgrade upgrade;

    public UpgradeCard(Upgrade upgrade) {
        super(new Properties().group(MiningGadgets.itemGroup).maxStackSize(1));
        this.upgrade = upgrade;
    }

    public Upgrade getUpgrade() {
        return upgrade;
    }
}
