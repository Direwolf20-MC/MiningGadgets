package com.direwolf20.mininggadgets.common.upgrades.impl;

import com.direwolf20.mininggadgets.api.upgrades.GadgetHooks;
import com.direwolf20.mininggadgets.api.upgrades.GadgetUseContext;
import com.direwolf20.mininggadgets.api.upgrades.MinerUpgrade;
import com.direwolf20.mininggadgets.api.upgrades.RenderBlockHooks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class MagnetUpgrade extends MinerUpgrade implements GadgetHooks, RenderBlockHooks {
    public MagnetUpgrade(ResourceLocation id) {
        super(id);
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
