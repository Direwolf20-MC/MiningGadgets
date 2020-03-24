package com.direwolf20.mininggadgets.common.sounds;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MiningGadgets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public enum OurSounds {
    LASER_LOOP("mining_laser_loop"),
    LASER_START("mining_laser_start1"),
    LASER_END("mining_laser_end1");
    private SoundEvent sound;

    OurSounds(String name) {
        ResourceLocation loc = new ResourceLocation(MiningGadgets.MOD_ID, name);
        sound = new SoundEvent(loc).setRegistryName(name);
    }

    public SoundEvent getSound() {
        return sound;
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        for (OurSounds sound : values()) {
            event.getRegistry().register(sound.getSound());
        }
    }
}
