package com.direwolf20.mininggadgets.client;

import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.sounds.LaserLoopSound;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;
import java.util.Random;

public class ClientSounds
{
    //This was moved out of MiningGadget due to the removal of @OnlyIn
    public static LaserLoopSound laserLoopSound;

    public static void playLoopSound(LivingEntity player, ItemStack stack, Random rand) {
        float volume = MiningProperties.getVolume(stack);
        Player myplayer = Minecraft.getInstance().player;
        if (myplayer.equals(player)) {
            if (volume != 0.0f) {
                if (stack.getHoverName().getString().toLowerCase(Locale.ROOT).contains("mongo")) {
                    if (player.level().getGameTime() % 5 == 0)
                        if (rand.nextDouble() > 0.005d)
                            player.playSound(SoundEvents.STONE_HIT, volume * 0.5f, 1f);
                        else
                            player.playSound(SoundEvents.CREEPER_PRIMED, volume * 1f, 1f);
                }
                else {
                    if (laserLoopSound == null) {
                        laserLoopSound = new LaserLoopSound((Player) player, volume, player.level().getRandom());
                        Minecraft.getInstance().getSoundManager().play(laserLoopSound);
                    }
                }
            }
        }
    }
}
