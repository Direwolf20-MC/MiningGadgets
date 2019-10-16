package com.direwolf20.mininggadgets.common.items;

import com.direwolf20.mininggadgets.Setup;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import net.minecraft.item.Item;

public class UpgradeCard extends Item {
    private Upgrade upgrade;

    public UpgradeCard(Upgrade upgrade) {
        super(new Properties().group(Setup.getItemGroup()).maxStackSize(1));

        setRegistryName("upgrade_" + upgrade.getName());
        this.upgrade = upgrade;
    }

    // Temp way of applying upgrades
    /*@Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);

        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if( stack.getItem() instanceof MiningGadget ) {
                if( !player.isSneaking() )
                    MiningGadget.applyUpgrade(stack, ((UpgradeCard) itemstack.getItem()));
                else
                    UpgradeTools.removeUpgrade(stack, ((UpgradeCard) itemstack.getItem()).getUpgrade());

                itemstack.shrink(1);
                player.sendMessage(new StringTextComponent("Upgrades applied so far: "));
                UpgradeTools.getUpgrades(stack).forEach(e -> player.sendMessage(new StringTextComponent(e.getUpgrade() + ": " + e.getTier())));
                break;
            }
        }

        return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
    }*/

    public Upgrade getUpgrade() {
        return upgrade;
    }
}
