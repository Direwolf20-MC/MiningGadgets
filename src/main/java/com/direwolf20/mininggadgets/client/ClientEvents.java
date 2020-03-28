package com.direwolf20.mininggadgets.client;

import com.direwolf20.mininggadgets.client.renderer.BlockOverlayRender;
import com.direwolf20.mininggadgets.client.renderer.RenderMiningLaser2;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class ClientEvents {
    @SubscribeEvent
    static void drawBlockHighlightEvent(DrawHighlightEvent evt) {
        if( Minecraft.getInstance().player == null )
            return;

        if(MiningGadget.isHolding(Minecraft.getInstance().player))
            evt.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    static void renderWorldLastEvent(RenderWorldLastEvent evt) {
        List<AbstractClientPlayerEntity> players = Minecraft.getInstance().world.getPlayers();
        PlayerEntity myplayer = Minecraft.getInstance().player;

        Vec3d view = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();

        ItemStack myItem = MiningGadget.getGadget(myplayer);
        if (MiningGadget.is(myItem))
            BlockOverlayRender.render(buffer, view, evt, myItem);

        for (PlayerEntity player : players) {
            if (player.getDistanceSq(myplayer) > 500)
                continue;

            ItemStack heldItem = MiningGadget.getGadget(player);
            if (player.isHandActive() && MiningGadget.is(heldItem)) {
                if (MiningGadget.canMine(heldItem)) {
                    RenderMiningLaser2.renderLaser(buffer, view, evt, player, Minecraft.getInstance().getRenderPartialTicks());
                }
            }
        }
    }
}
