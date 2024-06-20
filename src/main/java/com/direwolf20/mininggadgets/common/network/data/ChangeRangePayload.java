package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ChangeRangePayload(
        int range
) implements CustomPacketPayload {
    public static final Type<ChangeRangePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MiningGadgets.MOD_ID, "change_range"));

    @Override
    public Type<ChangeRangePayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, ChangeRangePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ChangeRangePayload::range,
            ChangeRangePayload::new
    );
}

