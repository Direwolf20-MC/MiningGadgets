package com.direwolf20.mininggadgets.client;

import com.direwolf20.mininggadgets.client.renderer.BlockOverlayRender;
import com.direwolf20.mininggadgets.client.renderer.RenderMiningLaser2;
import com.direwolf20.mininggadgets.client.screens.ModScreens;
import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
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

    @SubscribeEvent
    static void renderWorldLastEvent(RenderWorldLastEvent evt) {
        List<AbstractClientPlayerEntity> players = Minecraft.getInstance().world.getPlayers();
        PlayerEntity myplayer = Minecraft.getInstance().player;

        ItemStack myItem = MiningGadget.getGadget(myplayer);
        if (myItem.getItem() instanceof MiningGadget)
            BlockOverlayRender.render(evt, myItem);

        for (PlayerEntity player : players) {
            if (player.getDistanceSq(myplayer) > 500)
                continue;

            ItemStack heldItem = MiningGadget.getGadget(player);
            if (player.isHandActive() && heldItem.getItem() instanceof MiningGadget) {
                if (MiningGadget.canMine(heldItem)) {
                    RenderMiningLaser2.renderLaser(evt, player, Minecraft.getInstance().getRenderPartialTicks());
                }
            }
        }
    }

    @SubscribeEvent
    static void keyPressed(InputEvent.KeyInputEvent event) {
        if (OurKeys.shiftClickGuiBinding.isPressed() && Minecraft.getInstance().currentScreen == null) {
            if (Minecraft.getInstance().player == null) {
                return;
            }

            ItemStack gadget = MiningGadget.getGadget(Minecraft.getInstance().player);
            if (gadget.isEmpty()) {
                return;
            }

            ModScreens.openGadgetSettingsScreen(gadget);
        }
    }
}
