package com.direwolf20.mininggadgets.common.items;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeBatteryLevels;
import com.direwolf20.mininggadgets.common.util.MagicHelpers;
import com.direwolf20.mininggadgets.setup.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.List;
import java.util.function.Consumer;

public class UpgradeCard extends Item {
    private Upgrade upgrade;

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, display, tooltip, flagIn);
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        if (stack.getItem() instanceof UpgradeCard) {
            Upgrade upgrade = ((UpgradeCard) stack.getItem()).upgrade;
            int cost = upgrade.getCostPerBlock();
            if (cost > 0)
                tooltip.accept(Component.translatable("mininggadgets.tooltip.item.upgrade_cost", cost).withStyle(ChatFormatting.AQUA));

            cost = 0;
            if (upgrade == Upgrade.LIGHT_PLACER)
                cost = Config.UPGRADECOST_LIGHT.get();
            if (upgrade == Upgrade.FREEZING)
                cost = Config.UPGRADECOST_FREEZE.get();
            if (cost > 0)
                tooltip.accept(Component.translatable("mininggadgets.tooltip.item.use_cost", cost).withStyle(ChatFormatting.AQUA));

            if( upgrade.getBaseName().equals(Upgrade.BATTERY_1.getBaseName()) ) {
                UpgradeBatteryLevels.getBatteryByLevel(upgrade.getTier()).ifPresent(e -> {
                    tooltip.accept(Component.translatable("mininggadgets.tooltip.item.battery_boost", MagicHelpers.tidyValue(e.getPower())).withStyle(ChatFormatting.AQUA));
                });
            }

            tooltip.accept(Component.translatable(this.upgrade.getToolTip()).withStyle(ChatFormatting.GRAY));
        }

    }

    public UpgradeCard(Upgrade upgrade, int maxStack) {
        super(new Properties().stacksTo(maxStack).setId(modKey("upgrade_" + upgrade.getName())));
        this.upgrade = upgrade;
    }

    public UpgradeCard(Upgrade upgrade) {
        super(new Properties().stacksTo(1).setId(modKey("upgrade_" + upgrade.getName())));
        this.upgrade = upgrade;
    }

    public Upgrade getUpgrade() {
        return upgrade;
    }

    static ResourceKey<Item> modKey (String name)
    {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MiningGadgets.MOD_ID, name));
    }
}
