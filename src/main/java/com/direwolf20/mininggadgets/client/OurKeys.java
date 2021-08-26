package com.direwolf20.mininggadgets.client;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.fmlclient.registry.ClientRegistry;

public class OurKeys {
    public static final KeyMapping shiftClickGuiBinding = new KeyMapping("mininggadgets.text.open_gui", InputConstants.UNKNOWN.getValue(), "itemGroup.mininggadgets");

    public static void register() {
        ClientRegistry.registerKeyBinding(shiftClickGuiBinding);
    }
}
