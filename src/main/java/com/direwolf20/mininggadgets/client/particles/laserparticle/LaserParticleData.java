package com.direwolf20.mininggadgets.client.particles.laserparticle;

import com.direwolf20.mininggadgets.client.particles.ModParticles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class LaserParticleData implements ParticleOptions {
    public static final MapCodec<LaserParticleData> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BlockState.CODEC.fieldOf("state").forGetter(p -> p.state),
                    Codec.FLOAT.fieldOf("size").forGetter(p -> p.size),
                    Codec.FLOAT.fieldOf("maxAgeMul").forGetter(p -> p.maxAgeMul),
                    Codec.BOOL.fieldOf("depthTest").forGetter(p -> p.depthTest)
            ).apply(instance, LaserParticleData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, LaserParticleData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY),
            LaserParticleData::getState,
            ByteBufCodecs.FLOAT,
            LaserParticleData::getSize,
            ByteBufCodecs.FLOAT,
            LaserParticleData::getMaxAgeMul,
            ByteBufCodecs.BOOL,
            LaserParticleData::isDepthTest,
            LaserParticleData::new
    );

    public final float size;
    public final float maxAgeMul;
    public final boolean depthTest;
    public final BlockState state;

    public static LaserParticleData laserparticle(BlockState state, float size, float maxAgeMul) {
        return laserparticle(state, size, maxAgeMul, true);
    }

    public static LaserParticleData laserparticle(BlockState state, float size, float maxAgeMul, boolean depthTest) {
        return new LaserParticleData(state, size, maxAgeMul, depthTest);
    }

    private LaserParticleData(BlockState state, float size, float maxAgeMul, boolean depthTest) {
        this.size = size;
        this.maxAgeMul = maxAgeMul;
        this.depthTest = depthTest;
        this.state = state;
    }

    public float getSize() {
        return size;
    }

    public float getMaxAgeMul() {
        return maxAgeMul;
    }

    public boolean isDepthTest() {
        return depthTest;
    }

    public BlockState getState() {
        return state;
    }


    @Nonnull
    @Override
    public ParticleType<LaserParticleData> getType() {
        return ModParticles.LASERPARTICLE.get();
    }

}
