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
        Minecraft.getInstance().particleEngine.register(ModParticles.LASERPARTICLE.get(), LaserParticle.FACTORY);
        Minecraft.getInstance().particleEngine.register(ModParticles.PLAYERPARTICLE.get(), PlayerParticleType.FACTORY::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.LIGHT_PARTICLE.get(), LightParticleType.LightParticleFactory::new);
    }
}
