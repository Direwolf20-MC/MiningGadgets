package com.direwolf20.mininggadgets.common.items;

import com.direwolf20.mininggadgets.Setup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class UpgradeCard extends Item {
    private int tier;
    private Upgrade upgrade;

    public UpgradeCard(Upgrade upgrade, int tier) {
        super(new Properties().group(Setup.getItemGroup()).maxStackSize(1));

        setRegistryName("upgrade_" + upgrade.getName() + (tier != -1 ? "_" + tier : ""));
        this.upgrade = upgrade;
        this.tier = tier;
    }

    // Temp way of applying upgrades
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);

        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if( stack.getItem() instanceof MiningGadget ) {
                MiningGadget.applyUpgrade(stack, ((UpgradeCard) itemstack.getItem()));
                itemstack.shrink(1);
                player.sendMessage(new StringTextComponent("Upgrades applied so far: "));
                MiningGadget.getUpgrades(stack).forEach(e -> player.sendMessage(new StringTextComponent(e.getUpgrade() + ": " + e.getTier())));
                break;
            }
        }

        return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
    }

    public Upgrade getUpgrade() {
        return upgrade;
    }

    public int getTier() {
        return tier;
    }
}
