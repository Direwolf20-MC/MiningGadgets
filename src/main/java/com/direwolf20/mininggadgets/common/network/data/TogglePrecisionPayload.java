package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TogglePrecisionPayload() implements CustomPacketPayload {
    public static final TogglePrecisionPayload INSTANCE = new TogglePrecisionPayload();
    public static final Type<TogglePrecisionPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MiningGadgets.MOD_ID, "toggle_precision"));

    @Override
    public Type<TogglePrecisionPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, TogglePrecisionPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);
}

