package com.direwolf20.mininggadgets.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public class CodecHelpers {
    public record DurabilitySyncData(BlockPos blockPos, int durability) {
        public static final Codec<DurabilitySyncData> CODEC = RecordCodecBuilder.create(
                cooldownInstance -> cooldownInstance.group(
                                BlockPos.CODEC.fieldOf("blockPos").forGetter(DurabilitySyncData::blockPos),
                                Codec.INT.fieldOf("durability").forGetter(DurabilitySyncData::durability)
                        )
                        .apply(cooldownInstance, DurabilitySyncData::new)
        );
        public static final Codec<List<DurabilitySyncData>> LIST_CODEC = CODEC.listOf();
        public static final StreamCodec<RegistryFriendlyByteBuf, DurabilitySyncData> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC,
                DurabilitySyncData::blockPos,
                ByteBufCodecs.VAR_INT,
                DurabilitySyncData::durability,
                DurabilitySyncData::new
        );
    }

    public record UpgradeData(String upgradeName, boolean isActive) {
        public static final Codec<UpgradeData> CODEC = RecordCodecBuilder.create(
                cooldownInstance -> cooldownInstance.group(
                                Codec.STRING.fieldOf("upgradeName").forGetter(UpgradeData::upgradeName),
                                Codec.BOOL.fieldOf("isActive").forGetter(UpgradeData::isActive)
                        )
                        .apply(cooldownInstance, UpgradeData::new)
        );
        public static final Codec<List<UpgradeData>> LIST_CODEC = CODEC.listOf();
        public static final StreamCodec<RegistryFriendlyByteBuf, UpgradeData> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                UpgradeData::upgradeName,
                ByteBufCodecs.BOOL,
                UpgradeData::isActive,
                UpgradeData::new
        );
    }

    public record LaserColor(short red, short green, short blue, short innerRed, short innerGreen, short innerBlue) {
        public static final Codec<LaserColor> CODEC = RecordCodecBuilder.create(
                cooldownInstance -> cooldownInstance.group(
                                Codec.SHORT.fieldOf("red").forGetter(LaserColor::red),
                                Codec.SHORT.fieldOf("green").forGetter(LaserColor::green),
                                Codec.SHORT.fieldOf("blue").forGetter(LaserColor::blue),
                                Codec.SHORT.fieldOf("innerRed").forGetter(LaserColor::innerRed),
                                Codec.SHORT.fieldOf("innerGreen").forGetter(LaserColor::innerGreen),
                                Codec.SHORT.fieldOf("innerBlue").forGetter(LaserColor::innerBlue)
                        )
                        .apply(cooldownInstance, LaserColor::new)
        );
        public static final Codec<List<LaserColor>> LIST_CODEC = CODEC.listOf();
        public static final StreamCodec<RegistryFriendlyByteBuf, LaserColor> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.SHORT,
                LaserColor::red,
                ByteBufCodecs.SHORT,
                LaserColor::green,
                ByteBufCodecs.SHORT,
                LaserColor::blue,
                ByteBufCodecs.SHORT,
                LaserColor::innerRed,
                ByteBufCodecs.SHORT,
                LaserColor::innerGreen,
                ByteBufCodecs.SHORT,
                LaserColor::innerBlue,
                LaserColor::new
        );
    }
}
