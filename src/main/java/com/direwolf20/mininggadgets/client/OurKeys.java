package com.direwolf20.mininggadgets.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class OurKeys {
    public static final KeyBinding shiftClickGuiBinding = new KeyBinding("mininggadgets.text.open_gui", InputMappings.INPUT_INVALID.getKeyCode(), "itemGroup.mininggadgets");

    public static void register() {
        ClientRegistry.registerKeyBinding(shiftClickGuiBinding);
    }
}
