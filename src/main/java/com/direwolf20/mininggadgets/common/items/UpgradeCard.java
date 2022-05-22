package com.direwolf20.mininggadgets.common.items;

import com.direwolf20.mininggadgets.api.MiningGadgetsApi;
import com.direwolf20.mininggadgets.api.upgrades.UpgradeItem;
import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class UpgradeCard extends Item implements UpgradeItem {
    private final ResourceLocation upgradeId;

    public UpgradeCard(ResourceLocation upgradeId) {
        super(new Properties().tab(MiningGadgets.itemGroup).stacksTo(64));
        this.upgradeId = upgradeId;

        if (!MiningGadgetsApi.get().upgradesRegistry().has(upgradeId)) {
            throw new RuntimeException("Attempted to setup an UpgradeItem with an invalid upgrade id");
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
//            Upgrade upgrade = ((UpgradeCard) stack.getItem()).upgrade;
//            int cost = upgrade.getCostPerBlock();
//            if (cost > 0)
//                tooltip.add(new TranslatableComponent("mininggadgets.tooltip.item.upgrade_cost", cost).withStyle(ChatFormatting.AQUA));
//
//            cost = 0;
//            if (upgrade == Upgrade.LIGHT_PLACER)
//                cost = Config.UPGRADECOST_LIGHT.get();
//            if (upgrade == Upgrade.FREEZING)
//                cost = Config.UPGRADECOST_FREEZE.get();
//            if (cost > 0)
//                tooltip.add(new TranslatableComponent("mininggadgets.tooltip.item.use_cost", cost).withStyle(ChatFormatting.AQUA));
//
//            if( upgrade.getBaseName().equals(Upgrade.BATTERY_1.getBaseName()) ) {
//                UpgradeBatteryLevels.getBatteryByLevel(upgrade.getTier()).ifPresent(e -> {
//                    tooltip.add(new TranslatableComponent("mininggadgets.tooltip.item.battery_boost", MagicHelpers.tidyValue(e.getPower())).withStyle(ChatFormatting.AQUA));
//                });
//            }
//
//            tooltip.add(new TranslatableComponent(this.upgrade.getToolTip()).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public ResourceLocation getUpgradeId() {
        return upgradeId;
    }
}
