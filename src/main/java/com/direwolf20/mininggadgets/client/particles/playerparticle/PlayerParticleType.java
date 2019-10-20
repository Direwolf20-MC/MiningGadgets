package com.direwolf20.mininggadgets.client.particles.playerparticle;

import net.minecraft.particles.ParticleType;

public class PlayerParticleType extends ParticleType<PlayerParticleData> {
    public PlayerParticleType() {
        super(false, PlayerParticleData.DESERIALIZER);
    }
}
