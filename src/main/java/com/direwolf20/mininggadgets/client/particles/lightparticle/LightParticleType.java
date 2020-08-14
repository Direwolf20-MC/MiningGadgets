package com.direwolf20.mininggadgets.client.particles.lightparticle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LightParticleType extends BasicParticleType {
    public LightParticleType() {
        super(true);
    }

    @OnlyIn(Dist.CLIENT)
    public static class LightParticleFactory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public LightParticleFactory(IAnimatedSprite p_i50522_1_) {
            this.spriteSet = p_i50522_1_;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            LightParticle particle = new LightParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.selectSpriteRandomly(this.spriteSet);
            particle.setColor(.1F, .5f, .5F);
            return particle;
        }
    }
}