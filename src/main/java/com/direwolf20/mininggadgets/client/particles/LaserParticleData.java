package com.direwolf20.mininggadgets.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;

import javax.annotation.Nonnull;
import java.util.Locale;

public class LaserParticleData implements IParticleData {
    public final float size;
    public final float r, g, b;
    public final float maxAgeMul;
    public final boolean depthTest;

    public static LaserParticleData laserparticle(float size, float r, float g, float b) {
        return laserparticle(size, r, g, b, 1);
    }

    public static LaserParticleData laserparticle(float size, float r, float g, float b, float maxAgeMul) {
        return laserparticle(size, r, g, b, maxAgeMul, true);
    }

    public static LaserParticleData laserparticle(float size, float r, float g, float b, boolean depth) {
        return laserparticle(size, r, g, b, 1, depth);
    }

    public static LaserParticleData laserparticle(float size, float r, float g, float b, float maxAgeMul, boolean depthTest) {
        return new LaserParticleData(size, r, g, b, maxAgeMul, depthTest);
    }

    private LaserParticleData(float size, float r, float g, float b, float maxAgeMul, boolean depthTest) {
        this.size = size;
        this.r = r;
        this.g = g;
        this.b = b;
        this.maxAgeMul = maxAgeMul;
        this.depthTest = depthTest;
    }


    @Nonnull
    @Override
    public ParticleType<LaserParticleData> getType() {
        return ModParticles.LASERPARTICLE;
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeFloat(size);
        buf.writeFloat(r);
        buf.writeFloat(g);
        buf.writeFloat(b);
        buf.writeFloat(maxAgeMul);
        buf.writeBoolean(depthTest);
    }

    @Nonnull
    @Override
    public String getParameters() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %s",
                this.getType().getRegistryName(), this.size, this.r, this.g, this.b, this.maxAgeMul, this.depthTest);
    }

    public static final IDeserializer<LaserParticleData> DESERIALIZER = new IDeserializer<LaserParticleData>() {
        @Nonnull
        @Override
        public LaserParticleData deserialize(@Nonnull ParticleType<LaserParticleData> type, @Nonnull StringReader reader) throws CommandSyntaxException {
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
            return new LaserParticleData(size, r, g, b, mam, depth);
        }

        @Override
        public LaserParticleData read(@Nonnull ParticleType<LaserParticleData> type, PacketBuffer buf) {
            return new LaserParticleData(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readBoolean());
        }
    };
}
