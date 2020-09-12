package com.direwolf20.mininggadgets.common.sounds;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;

public class LaserLoopSound extends TickableSound {
    private final PlayerEntity player;
    private float distance = 0.0F;

    public LaserLoopSound(PlayerEntity player, float volume) {
        super(OurSounds.LASER_LOOP.getSound(), SoundCategory.PLAYERS);
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = volume;
        this.x = (float) player.getPosX();
        this.y = (float) player.getPosY();
        this.z = (float) player.getPosZ();
    }

    public boolean canBeSilent() {
        return true;
    }

    public void tick() {
        ItemStack heldItem = MiningGadget.getGadget(player);
        if (!(this.player.isHandActive() && heldItem.getItem() instanceof MiningGadget)) {
            this.finishPlaying();
        } else {
//            PlayerEntity myplayer = Minecraft.getInstance().player;
            this.x = (float) this.player.getPosX();
            this.y = (float) this.player.getPosY();
            this.z = (float) this.player.getPosZ();
            //this.distance = MathHelper.clamp(this.distance + 0.0025F, 0.0F, 1.0F);
            //this.volume = (float) MathHelper.lerp((1.0f - (player.getDistanceSq(myplayer)/10)), 0.0F, 1.0f);
        }
    }
}