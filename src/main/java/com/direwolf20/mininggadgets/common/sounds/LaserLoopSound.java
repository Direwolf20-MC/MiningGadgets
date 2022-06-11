package com.direwolf20.mininggadgets.common.sounds;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;

public class LaserLoopSound extends AbstractTickableSoundInstance {
    private final Player player;
    private float distance = 0.0F;

    public LaserLoopSound(Player player, float volume, RandomSource source) {
        super(OurSounds.LASER_LOOP.get(), SoundSource.PLAYERS, source);
        this.player = player;
        this.looping = true;
        this.delay = 0;
        this.volume = volume;
        this.x = (float) player.getX();
        this.y = (float) player.getY();
        this.z = (float) player.getZ();
    }

    public boolean canStartSilent() {
        return true;
    }

    public void tick() {
        ItemStack heldItem = MiningGadget.getGadget(player);
        if (!(this.player.isUsingItem() && heldItem.getItem() instanceof MiningGadget)) {
            this.stop();
        } else {
//            PlayerEntity myplayer = Minecraft.getInstance().player;
            this.x = (float) this.player.getX();
            this.y = (float) this.player.getY();
            this.z = (float) this.player.getZ();
            //this.distance = MathHelper.clamp(this.distance + 0.0025F, 0.0F, 1.0F);
            //this.volume = (float) MathHelper.lerp((1.0f - (player.getDistanceSq(myplayer)/10)), 0.0F, 1.0f);
        }
    }
}