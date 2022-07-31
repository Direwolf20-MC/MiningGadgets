package com.direwolf20.mininggadgets.client;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OurKeys {
    public static final KeyMapping shiftClickGuiBinding = new KeyMapping("mininggadgets.text.open_gui", InputConstants.UNKNOWN.getValue(), "itemGroup.mininggadgets");

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(shiftClickGuiBinding);
    }
}
