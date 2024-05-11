package com.direwolf20.mininggadgets.client.particles;

import com.direwolf20.mininggadgets.client.particles.laserparticle.LaserParticleData;
import com.direwolf20.mininggadgets.client.particles.laserparticle.LaserParticleType;
import com.direwolf20.mininggadgets.client.particles.lightparticle.LightParticleType;
import com.direwolf20.mininggadgets.client.particles.playerparticle.PlayerParticleData;
import com.direwolf20.mininggadgets.client.particles.playerparticle.PlayerParticleType;
import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, MiningGadgets.MOD_ID);

    public static final Supplier<ParticleType<LaserParticleData>> LASERPARTICLE = PARTICLE_TYPES.register("laserparticle", LaserParticleType::new);
    public static final Supplier<ParticleType<PlayerParticleData>> PLAYERPARTICLE = PARTICLE_TYPES.register("playerparticle", PlayerParticleType::new);
    public static final Supplier<ParticleType<SimpleParticleType>> LIGHT_PARTICLE = PARTICLE_TYPES.register("light_particle", LightParticleType::new);
}
