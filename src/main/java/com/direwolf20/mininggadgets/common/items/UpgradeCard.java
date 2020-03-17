package com.direwolf20.mininggadgets.common.items;

import com.direwolf20.mininggadgets.Config;
import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.common.gadget.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.gadget.upgrade.UpgradeBatteryLevels;
import com.direwolf20.mininggadgets.common.util.MiscTools;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class UpgradeCard extends Item {
    private Upgrade upgrade;

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        if (stack.getItem() instanceof UpgradeCard) {
            Upgrade upgrade = ((UpgradeCard) stack.getItem()).upgrade;
            int cost = upgrade.getCostPerBlock();
            if (cost > 0)
                tooltip.add(new TranslationTextComponent("mininggadgets.tooltip.item.upgrade_cost", cost).applyTextStyle(TextFormatting.AQUA));

            cost = 0;
            if (upgrade == Upgrade.LIGHT_PLACER)
                cost = Config.UPGRADECOST_LIGHT.get();
            if (upgrade == Upgrade.FREEZING)
                cost = Config.UPGRADECOST_FREEZE.get();
            if (cost > 0)
                tooltip.add(new TranslationTextComponent("mininggadgets.tooltip.item.use_cost", cost).applyTextStyle(TextFormatting.AQUA));

            if( upgrade.getBaseName().equals(Upgrade.BATTERY_1.getBaseName()) ) {
                UpgradeBatteryLevels.getBatteryByLevel(upgrade.getTier()).ifPresent(e -> {
                    tooltip.add(new TranslationTextComponent("mininggadgets.tooltip.item.battery_boost", MiscTools.tidyValue(e.getPower())).applyTextStyle(TextFormatting.AQUA));
                });
            }

            tooltip.add(new TranslationTextComponent(this.upgrade.getTooltop()).applyTextStyle(TextFormatting.GRAY));
        }

    }

    public UpgradeCard(Upgrade upgrade, int maxStack) {
        super(new Properties().group(MiningGadgets.itemGroup).maxStackSize(maxStack));
        this.upgrade = upgrade;
    }

    public Upgrade getUpgrade() {
        return upgrade;
    }
}
