package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.util.CodecHelpers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ChangeColorPayload(
        CodecHelpers.LaserColor laserColor
) implements CustomPacketPayload {
    public static final Type<ChangeColorPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MiningGadgets.MOD_ID, "change_color"));

    @Override
    public Type<ChangeColorPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeColorPayload> STREAM_CODEC = StreamCodec.composite(
            CodecHelpers.LaserColor.STREAM_CODEC, ChangeColorPayload::laserColor,
            ChangeColorPayload::new
    );
}

