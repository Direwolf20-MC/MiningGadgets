package com.direwolf20.mininggadgets.client.particles.lightparticle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class LightParticleType extends SimpleParticleType {
    public LightParticleType() {
        super(true);
    }

    public static class LightParticleFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public LightParticleFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double v, double v1, double v2, double v3, double v4, double v5, @NonNull RandomSource randomSource) {
            LightParticle particle = new LightParticle(clientLevel, v, v1, v2, v3, v4, v5, spriteSet);
            particle.setColor(.1F, .5f, .5F);
            return particle;
        }
    }
}