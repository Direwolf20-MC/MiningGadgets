package com.direwolf20.mininggadgets.client.particles;

import com.direwolf20.mininggadgets.client.particles.laserparticle.LaserParticle;
import com.direwolf20.mininggadgets.client.particles.lightparticle.LightParticleType;
import com.direwolf20.mininggadgets.client.particles.playerparticle.PlayerParticleType;
import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MiningGadgets.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ParticleRenderDispatcher {

    @SubscribeEvent
    public static void registerProviders(RegisterParticleProvidersEvent event) {
        event.registerSpecial(ModParticles.LASERPARTICLE.get(), LaserParticle.FACTORY);
        event.registerSpriteSet(ModParticles.PLAYERPARTICLE.get(), PlayerParticleType.FACTORY::new);
        event.registerSpriteSet(ModParticles.LIGHT_PARTICLE.get(), LightParticleType.LightParticleFactory::new);
    }
}
