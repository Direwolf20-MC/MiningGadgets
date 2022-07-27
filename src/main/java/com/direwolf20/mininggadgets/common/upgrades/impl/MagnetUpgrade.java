package com.direwolf20.mininggadgets.common.upgrades.impl;

import com.direwolf20.mininggadgets.api.upgrades.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

public class MagnetUpgrade extends MinerUpgrade implements GadgetHooks, RenderBlockHooks {
    private final Supplier<UpgradeItem> item;

    public MagnetUpgrade(ResourceLocation id, Supplier<UpgradeItem> item) {
        super(id);
        this.item = item;
    }

    @Override
    public UpgradeItem item() {
        return item.get();
    }

    @Override
    public int costPerOperation() {
        return 100;
    }

    @Override
    public List<ItemStack> beforeItemsDrop(GadgetUseContext context, List<ItemStack> drops) {
        return GadgetHooks.super.beforeItemsDrop(context, drops);
    }

    @Override
    public void onTick() {
//        if (UpgradeTools.containsActiveUpgradeFromList(this.gadgetUpgrades, Upgrade.MAGNET) && this.originalDurability > 0) {
//            int PartCount = 20 / this.originalDurability;
//            if (PartCount <= 1) {
//                PartCount = 1;
//            }
//            for (int i = 0; i <= PartCount; i++) {
//                double randomPartSize = 0.125 + this.rand.nextDouble() * 0.5;
//                double randomX = this.rand.nextDouble();
//                double randomY = this.rand.nextDouble();
//                double randomZ = this.rand.nextDouble();
//
//                LaserParticleData data = LaserParticleData.laserparticle(this.renderBlock, (float) randomPartSize, 1f, 1f, 1f, 200);
//                this.getLevel().addParticle(data, this.getBlockPos().getX() + randomX, this.getBlockPos().getY() + randomY, this.getBlockPos().getZ() + randomZ, 0, 0.0f, 0);
//            }
//        }
    }
}
