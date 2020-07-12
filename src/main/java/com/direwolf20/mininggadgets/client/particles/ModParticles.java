package com.direwolf20.mininggadgets.client.particles;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.client.particles.laserparticle.LaserParticleData;
import com.direwolf20.mininggadgets.client.particles.laserparticle.LaserParticleType;
import com.direwolf20.mininggadgets.client.particles.lightparticle.LightParticleType;
import com.direwolf20.mininggadgets.client.particles.playerparticle.PlayerParticleData;
import com.direwolf20.mininggadgets.client.particles.playerparticle.PlayerParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

// TODO: 12/07/2020 Replaces this with a deffered register
@Mod.EventBusSubscriber(modid = MiningGadgets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(MiningGadgets.MOD_ID)
public class ModParticles {
    @ObjectHolder("laserparticle")
    public static ParticleType<LaserParticleData> LASERPARTICLE;

    @ObjectHolder("playerparticle")
    public static ParticleType<PlayerParticleData> PLAYERPARTICLE;

    @ObjectHolder("light_particle")
    public static LightParticleType LIGHT_PARTICLE;

    @SubscribeEvent
    public static void registerParticles(RegistryEvent.Register<ParticleType<?>> evt) {
        evt.getRegistry().registerAll(
                new LaserParticleType().setRegistryName("laserparticle"),
                new PlayerParticleType().setRegistryName("playerparticle"),
                new LightParticleType().setRegistryName("light_particle")
        );
    }
}
