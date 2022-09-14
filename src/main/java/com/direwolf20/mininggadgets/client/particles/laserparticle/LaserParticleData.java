package com.direwolf20.mininggadgets.client.particles.laserparticle;

import com.direwolf20.mininggadgets.client.particles.ModParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.Locale;

public class LaserParticleData implements ParticleOptions {
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


    @Nonnull
    @Override
    public ParticleType<LaserParticleData> getType() {
        return ModParticles.LASERPARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeVarInt(Block.BLOCK_STATE_REGISTRY.getId(state));
        buf.writeFloat(size);
        buf.writeFloat(maxAgeMul);
        buf.writeBoolean(depthTest);
    }

    @Nonnull
    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %s",
                Registry.PARTICLE_TYPE.getKey(this.getType()), this.size, this.maxAgeMul, this.depthTest);
    }

    public static final Deserializer<LaserParticleData> DESERIALIZER = new Deserializer<LaserParticleData>() {
        @Nonnull
        @Override
        public LaserParticleData fromCommand(@Nonnull ParticleType<LaserParticleData> type, @Nonnull StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            BlockState state = (BlockStateParser.parseForBlock(Registry.BLOCK, reader, false).blockState());
            reader.expect(' ');
            float size = reader.readFloat();
            reader.expect(' ');
            float mam = reader.readFloat();
            boolean depth = true;
            if (reader.canRead()) {
                reader.expect(' ');
                depth = reader.readBoolean();
            }
            return new LaserParticleData(state, size, mam, depth);
        }

        @Override
        public LaserParticleData fromNetwork(@Nonnull ParticleType<LaserParticleData> type, FriendlyByteBuf buf) {
            return new LaserParticleData(Block.BLOCK_STATE_REGISTRY.byId(buf.readVarInt()), buf.readFloat(), buf.readFloat(), buf.readBoolean());
        }
    };
}
