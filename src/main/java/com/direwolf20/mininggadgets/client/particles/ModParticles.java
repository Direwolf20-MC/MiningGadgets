package com.direwolf20.mininggadgets.client.particles;

import com.direwolf20.mininggadgets.Setup;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Setup.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Setup.MOD_ID)
public class ModParticles {
    @ObjectHolder("laserparticle")
    public static ParticleType<LaserParticleData> LASERPARTICLE;

    @SubscribeEvent
    public static void registerParticles(RegistryEvent.Register<ParticleType<?>> evt) {
        evt.getRegistry().registerAll(
                new LaserParticleType().setRegistryName("laserparticle")
        );
    }
}
