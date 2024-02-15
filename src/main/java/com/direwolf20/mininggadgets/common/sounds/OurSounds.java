package com.direwolf20.mininggadgets.common.sounds;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public interface OurSounds {
    DeferredRegister<SoundEvent> SOUND_REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, MiningGadgets.MOD_ID);

    Supplier<SoundEvent> LASER_LOOP = SOUND_REGISTRY.register("mining_laser_loop", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MiningGadgets.MOD_ID, "mining_laser_loop")));
    Supplier<SoundEvent> LASER_START = SOUND_REGISTRY.register("mining_laser_start1", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MiningGadgets.MOD_ID, "mining_laser_start1")));
    Supplier<SoundEvent> LASER_END = SOUND_REGISTRY.register("mining_laser_end1", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MiningGadgets.MOD_ID, "mining_laser_end1")));
}
