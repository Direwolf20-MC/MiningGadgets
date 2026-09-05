package com.direwolf20.mininggadgets.client;

import com.direwolf20.mininggadgets.client.sounds.LaserLoopSound;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientSoundHandler {

    public static void playLaserLoop(Player player, ItemStack stack) {
        float volume = MiningGadget.getVolume(stack);

        if (volume <= 0)
            return;

        Minecraft.getInstance().getSoundManager().play(
                new LaserLoopSound(player, volume, player.level().random)
        );
    }
}
