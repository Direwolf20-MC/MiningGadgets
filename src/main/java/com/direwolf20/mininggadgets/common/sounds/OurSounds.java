package com.direwolf20.mininggadgets.common.sounds;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public interface OurSounds {
    DeferredRegister<SoundEvent> SOUND_REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MiningGadgets.MOD_ID);

    RegistryObject<SoundEvent> LASER_LOOP = SOUND_REGISTRY.register("mining_laser_loop", () -> new SoundEvent(new ResourceLocation(MiningGadgets.MOD_ID, "mining_laser_loop")));
    RegistryObject<SoundEvent> LASER_START = SOUND_REGISTRY.register("mining_laser_start1", () -> new SoundEvent(new ResourceLocation(MiningGadgets.MOD_ID, "mining_laser_start1")));
    RegistryObject<SoundEvent> LASER_END = SOUND_REGISTRY.register("mining_laser_end1", () -> new SoundEvent(new ResourceLocation(MiningGadgets.MOD_ID, "mining_laser_end1")));
}
