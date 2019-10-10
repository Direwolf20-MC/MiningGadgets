package com.direwolf20.mininggadgets.client.particles;

import net.minecraft.particles.ParticleType;

public class LaserParticleType extends ParticleType<LaserParticleData> {
    public LaserParticleType() {
        super(false, LaserParticleData.DESERIALIZER);
    }


}
