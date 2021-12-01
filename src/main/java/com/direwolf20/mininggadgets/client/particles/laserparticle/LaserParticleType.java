package com.direwolf20.mininggadgets.client.particles.laserparticle;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;

public class LaserParticleType extends ParticleType<LaserParticleData> {
    public LaserParticleType() {
        super(false, LaserParticleData.DESERIALIZER);
    }

    @Override
    public Codec<LaserParticleData> codec() {
        return null;
    }
}
