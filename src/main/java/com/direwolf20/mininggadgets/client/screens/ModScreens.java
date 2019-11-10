package com.direwolf20.mininggadgets.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class ModScreens {
    public static void openGadgetSettingsScreen(ItemStack itemstack) {
        Minecraft.getInstance().displayGuiScreen(new MiningSettingScreen(itemstack));
    }
}
