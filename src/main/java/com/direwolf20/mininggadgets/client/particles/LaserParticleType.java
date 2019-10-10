package com.direwolf20.mininggadgets.client.particles;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.particles.ParticleType;

public class LaserParticleType extends ParticleType<LaserParticleData> {
    public LaserParticleType() {
        super(false, LaserParticleData.DESERIALIZER);
    }

    public static IParticleFactory<LaserParticleData> FACTORY =
            (data, world, x, y, z, xSpeed, ySpeed, zSpeed) ->
                    new LaserParticle(world, x, y, z, xSpeed, ySpeed, zSpeed, data.size, data.r, data.g, data.b, data.depthTest, data.maxAgeMul);
}
