package com.direwolf20.mininggadgets.common.items;

import com.direwolf20.mininggadgets.common.Config;
import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeBatteryLevels;
import com.direwolf20.mininggadgets.common.util.MagicHelpers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class UpgradeCard extends Item {
    private Upgrade upgrade;

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        if (stack.getItem() instanceof UpgradeCard) {
            Upgrade upgrade = ((UpgradeCard) stack.getItem()).upgrade;
            int cost = upgrade.getCostPerBlock();
            if (cost > 0)
                tooltip.add(Component.translatable("mininggadgets.tooltip.item.upgrade_cost", cost).withStyle(ChatFormatting.AQUA));

            cost = 0;
            if (upgrade == Upgrade.LIGHT_PLACER)
                cost = Config.UPGRADECOST_LIGHT.get();
            if (upgrade == Upgrade.FREEZING)
                cost = Config.UPGRADECOST_FREEZE.get();
            if (cost > 0)
                tooltip.add(Component.translatable("mininggadgets.tooltip.item.use_cost", cost).withStyle(ChatFormatting.AQUA));

            if( upgrade.getBaseName().equals(Upgrade.BATTERY_1.getBaseName()) ) {
                UpgradeBatteryLevels.getBatteryByLevel(upgrade.getTier()).ifPresent(e -> {
                    tooltip.add(Component.translatable("mininggadgets.tooltip.item.battery_boost", MagicHelpers.tidyValue(e.getPower())).withStyle(ChatFormatting.AQUA));
                });
            }

            tooltip.add(Component.translatable(this.upgrade.getToolTip()).withStyle(ChatFormatting.GRAY));
        }

    }

    public UpgradeCard(Upgrade upgrade, int maxStack) {
        super(new Properties().tab(MiningGadgets.itemGroup).stacksTo(maxStack));
        this.upgrade = upgrade;
    }

    public UpgradeCard(Upgrade upgrade) {
        super(new Properties().tab(MiningGadgets.itemGroup).stacksTo(1));
        this.upgrade = upgrade;
    }

    public Upgrade getUpgrade() {
        return upgrade;
    }
}
