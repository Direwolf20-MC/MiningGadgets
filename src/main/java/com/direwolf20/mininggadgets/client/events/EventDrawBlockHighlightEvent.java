package com.direwolf20.mininggadgets.client.events;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MiningGadgets.MOD_ID, value = Dist.CLIENT)
public class EventDrawBlockHighlightEvent {

    @SubscribeEvent
    static void drawBlockHighlightEvent(DrawHighlightEvent evt) {
        if( Minecraft.getInstance().player == null )
            return;

        if(MiningGadget.isHolding(Minecraft.getInstance().player))
            evt.setCanceled(true);
    }
}
