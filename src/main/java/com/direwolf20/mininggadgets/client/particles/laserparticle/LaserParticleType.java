package com.direwolf20.mininggadgets.client.particles.laserparticle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class LaserParticleType extends ParticleType<LaserParticleData> {
    public LaserParticleType(boolean pOverrideLimiter) {
        super(pOverrideLimiter);
    }

    public LaserParticleType getType() {
        return this;
    }

    @Override
    public MapCodec<LaserParticleData> codec() {
        return LaserParticleData.MAP_CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, LaserParticleData> streamCodec() {
        return LaserParticleData.STREAM_CODEC;
    }
}
