package com.direwolf20.mininggadgets.client.events;

import com.direwolf20.mininggadgets.client.renderer.RenderMiningLaser;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.ModItems;
import com.direwolf20.mininggadgets.common.util.MiscTools;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "mininggadgets", value = Dist.CLIENT)
public class EventRenderWorldLast {
    @SubscribeEvent
    static void renderWorldLastEvent(RenderWorldLastEvent evt) {
        PlayerEntity player = Minecraft.getInstance().player;
        ItemStack heldItem = MiscTools.getGadget(player);

        if (heldItem.isEmpty())
            return;

        if (player.isHandActive()) {
            RenderMiningLaser.renderLaser(player, evt.getPartialTicks());
        }

    }
}
