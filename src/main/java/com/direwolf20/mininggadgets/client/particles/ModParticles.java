package com.direwolf20.mininggadgets.client.particles;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.client.particles.laserparticle.LaserParticleData;
import com.direwolf20.mininggadgets.client.particles.laserparticle.LaserParticleType;
import com.direwolf20.mininggadgets.client.particles.lightparticle.LightParticleType;
import com.direwolf20.mininggadgets.client.particles.playerparticle.PlayerParticleData;
import com.direwolf20.mininggadgets.client.particles.playerparticle.PlayerParticleType;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MiningGadgets.MOD_ID);

    public static final RegistryObject<ParticleType<LaserParticleData>> LASERPARTICLE = PARTICLE_TYPES.register("laserparticle", LaserParticleType::new);
    public static final RegistryObject<ParticleType<PlayerParticleData>> PLAYERPARTICLE = PARTICLE_TYPES.register("playerparticle", PlayerParticleType::new);
    public static final RegistryObject<ParticleType<SimpleParticleType>> LIGHT_PARTICLE = PARTICLE_TYPES.register("light_particle", LightParticleType::new);
}
