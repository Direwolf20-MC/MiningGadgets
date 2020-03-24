package com.direwolf20.mininggadgets.client.particles;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.client.particles.laserparticle.LaserParticle;
import com.direwolf20.mininggadgets.client.particles.lightparticle.LightParticleType;
import com.direwolf20.mininggadgets.client.particles.playerparticle.PlayerParticleType;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MiningGadgets.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ParticleRenderDispatcher {

    @SubscribeEvent
    public static void registerFactories(ParticleFactoryRegisterEvent evt) {
        Minecraft.getInstance().particles.registerFactory(ModParticles.LASERPARTICLE, LaserParticle.FACTORY);
        Minecraft.getInstance().particles.registerFactory(ModParticles.PLAYERPARTICLE, PlayerParticleType.FACTORY::new);
        Minecraft.getInstance().particles.registerFactory(ModParticles.LIGHT_PARTICLE, LightParticleType.LightParticleFactory::new);
    }
}
