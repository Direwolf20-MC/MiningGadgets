package com.direwolf20.mininggadgets.client.particles.playerparticle;

import com.direwolf20.mininggadgets.client.particles.ModParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import javax.annotation.Nonnull;
import java.util.Locale;

public class PlayerParticleData implements ParticleOptions {
    public final float size;
    public final float r, g, b;
    public final float maxAgeMul;
    public final boolean depthTest;
    public final double targetX;
    public final double targetY;
    public final double targetZ;
    public final String partType;

    public static PlayerParticleData playerparticle(String type, double targetX, double targetY, double targetZ, float size, float r, float g, float b) {
        return playerparticle(type, targetX, targetY, targetZ, size, r, g, b, 1);
    }

    public static PlayerParticleData playerparticle(String type, double targetX, double targetY, double targetZ, float size, float r, float g, float b, float maxAgeMul) {
        return playerparticle(type, targetX, targetY, targetZ, size, r, g, b, maxAgeMul, true);
    }

    public static PlayerParticleData playerparticle(String type, double targetX, double targetY, double targetZ, float size, float r, float g, float b, boolean depth) {
        return playerparticle(type, targetX, targetY, targetZ, size, r, g, b, 1, depth);
    }

    public static PlayerParticleData playerparticle(String type, double targetX, double targetY, double targetZ, float size, float r, float g, float b, float maxAgeMul, boolean depthTest) {
        return new PlayerParticleData(type, targetX, targetY, targetZ, size, r, g, b, maxAgeMul, depthTest);
    }

    private PlayerParticleData(String type, double targetX, double targetY, double targetZ, float size, float r, float g, float b, float maxAgeMul, boolean depthTest) {
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.size = size;
        this.r = r;
        this.g = g;
        this.b = b;
        this.maxAgeMul = maxAgeMul;
        this.depthTest = depthTest;
        this.partType = type;
    }


    @Nonnull
    @Override
    public ParticleType<PlayerParticleData> getType() {
        return ModParticles.PLAYERPARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeUtf(partType);
        buf.writeDouble(targetX);
        buf.writeDouble(targetY);
        buf.writeDouble(targetZ);
        buf.writeFloat(size);
        buf.writeFloat(r);
        buf.writeFloat(g);
        buf.writeFloat(b);
        buf.writeFloat(maxAgeMul);
        buf.writeBoolean(depthTest);
    }

    @Nonnull
    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %s",
                Registry.PARTICLE_TYPE.getKey(this.getType()), this.size, this.r, this.g, this.b, this.maxAgeMul, this.depthTest);
    }

    public static final ParticleOptions.Deserializer<PlayerParticleData> DESERIALIZER = new ParticleOptions.Deserializer<PlayerParticleData>() {
        @Nonnull
        @Override
        public PlayerParticleData fromCommand(@Nonnull ParticleType<PlayerParticleData> type, @Nonnull StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            String partType = reader.readString();
            reader.expect(' ');
            double targetX = reader.readDouble();
            reader.expect(' ');
            double targetY = reader.readDouble();
            reader.expect(' ');
            double targetZ = reader.readDouble();
            reader.expect(' ');
            float size = reader.readFloat();
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            reader.expect(' ');
            float mam = reader.readFloat();
            boolean depth = true;
            if (reader.canRead()) {
                reader.expect(' ');
                depth = reader.readBoolean();
            }
            return new PlayerParticleData(partType, targetX, targetY, targetZ, size, r, g, b, mam, depth);
        }

        @Override
        public PlayerParticleData fromNetwork(@Nonnull ParticleType<PlayerParticleData> type, FriendlyByteBuf buf) {
            return new PlayerParticleData(buf.readUtf(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readBoolean());
        }
    };
}
