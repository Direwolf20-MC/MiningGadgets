package com.direwolf20.mininggadgets.client.particles.playerparticle;

import com.mojang.serialization.Codec;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleType;

public class PlayerParticleType extends ParticleType<PlayerParticleData> {
    public PlayerParticleType() {
        super(false, PlayerParticleData.DESERIALIZER);
    }

    @Override
    public Codec<PlayerParticleData> codec() {
        return null;
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
