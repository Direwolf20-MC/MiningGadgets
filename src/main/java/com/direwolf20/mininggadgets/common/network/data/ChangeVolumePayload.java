package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ChangeVolumePayload(
        float volume
) implements CustomPacketPayload {
    public static final Type<ChangeVolumePayload> TYPE = new Type<>(new ResourceLocation(MiningGadgets.MOD_ID, "change_volume"));

    @Override
    public Type<ChangeVolumePayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, ChangeVolumePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, ChangeVolumePayload::volume,
            ChangeVolumePayload::new
    );
}

