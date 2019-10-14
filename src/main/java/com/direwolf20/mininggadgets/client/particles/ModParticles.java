package com.direwolf20.mininggadgets.client.particles;

import com.direwolf20.mininggadgets.MiningGadgets;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = MiningGadgets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(MiningGadgets.MOD_ID)
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
