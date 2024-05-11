package com.direwolf20.mininggadgets.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;


@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OurKeys {
    public static final KeyMapping shiftClickGuiBinding = new KeyMapping("mininggadgets.text.open_gui", InputConstants.UNKNOWN.getValue(), "itemGroup.mininggadgets");

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(shiftClickGuiBinding);
    }
}
