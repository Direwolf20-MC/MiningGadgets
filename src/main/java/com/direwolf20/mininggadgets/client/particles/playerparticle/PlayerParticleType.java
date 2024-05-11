package com.direwolf20.mininggadgets.client.particles.playerparticle;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class PlayerParticleType extends ParticleType<PlayerParticleData> {
    public PlayerParticleType(boolean pOverrideLimiter) {
        super(pOverrideLimiter);
    }

    public PlayerParticleType getType() {
        return this;
    }

    @Override
    public MapCodec<PlayerParticleData> codec() {
        return PlayerParticleData.MAP_CODEC;
    }

    @Override
    public StreamCodec<FriendlyByteBuf, PlayerParticleData> streamCodec() {
        return PlayerParticleData.STREAM_CODEC;
    }

    public static class FACTORY implements ParticleProvider<PlayerParticleData> {
        private final SpriteSet sprites;

        public FACTORY(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(PlayerParticleData data, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new PlayerParticle(world, x, y, z, data.targetX, data.targetY, data.targetZ, xSpeed, ySpeed, zSpeed, data.size, data.r, data.g, data.b, data.depthTest, data.maxAgeMul, data.partType, this.sprites);
        }
    }
}
