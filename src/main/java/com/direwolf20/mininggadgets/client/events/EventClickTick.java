package com.direwolf20.mininggadgets.client.events;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.client.ClientSetup;
import com.direwolf20.mininggadgets.client.screens.MiningSettingScreen;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MiningGadgets.MOD_ID, value = Dist.CLIENT)
public class EventClickTick {
    private static final Minecraft MC = Minecraft.getInstance();

    @SubscribeEvent
    public static void onClickTick(ClientTickEvent event) {
        // No wasteful tick evals
        if( MC.world == null || MC.player == null || event.phase == TickEvent.Phase.START )
            return;

        if( MC.currentScreen instanceof MiningSettingScreen )
            return;

        if(ClientSetup.gadgetMenu.isKeyDown()) {
            ItemStack stack = MiningGadget.getGadget(MC.player);
            MC.displayGuiScreen(new MiningSettingScreen(stack));
        }
    }
}
