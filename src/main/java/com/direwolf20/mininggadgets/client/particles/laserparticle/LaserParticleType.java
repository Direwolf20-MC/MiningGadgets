package com.direwolf20.mininggadgets.client.particles.laserparticle;

import net.minecraft.particles.ParticleType;

public class LaserParticleType extends ParticleType<LaserParticleData> {
    public LaserParticleType() {
        super(true, LaserParticleData.DESERIALIZER);
    }


}
