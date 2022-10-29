package com.direwolf20.mininggadgets.common.sounds;

import net.minecraft.world.entity.player.Player;

public enum SoundsHandler {
    INSTANT;

    private LaserLoopSound laserLoop = null;

    public void create(Player player, float volume) {
        if (this.laserLoop != null) {
            return;
        }

        this.laserLoop = new LaserLoopSound(player, volume);
    }

    public void clear() {
        this.laserLoop = null;
    }

    public LaserLoopSound getLaserLoop() {
        return laserLoop;
    }
}
