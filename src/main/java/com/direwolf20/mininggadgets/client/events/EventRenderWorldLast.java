package com.direwolf20.mininggadgets.client.events;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.client.renderer.RenderMiningLaser;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.util.BlockOverlayRender;
import com.direwolf20.mininggadgets.common.util.MiscTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = MiningGadgets.MOD_ID, value = Dist.CLIENT)
public class EventRenderWorldLast {
    @SubscribeEvent
    static void renderWorldLastEvent(RenderWorldLastEvent evt) {
        List<AbstractClientPlayerEntity> players = Minecraft.getInstance().world.getPlayers();
        PlayerEntity myplayer = Minecraft.getInstance().player;

        ItemStack myItem = MiscTools.getGadget(myplayer);
        if (myItem.getItem() instanceof MiningGadget)
            BlockOverlayRender.render(myItem);

        for (PlayerEntity player : players) {
            if (player.getDistanceSq(myplayer) > 500)
                continue;

            ItemStack heldItem = MiscTools.getGadget(player);
            if (player.isHandActive() && heldItem.getItem() instanceof MiningGadget) {
                if (MiningGadget.canMine(heldItem, myplayer.world)) {
                    RenderMiningLaser.renderLaser(player, evt.getPartialTicks());
                }
            }
        }
    }
}

