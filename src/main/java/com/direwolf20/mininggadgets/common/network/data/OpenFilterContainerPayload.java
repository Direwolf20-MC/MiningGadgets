package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OpenFilterContainerPayload() implements CustomPacketPayload {
    public static final OpenFilterContainerPayload INSTANCE = new OpenFilterContainerPayload();
    public static final Type<OpenFilterContainerPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MiningGadgets.MOD_ID, "open_filter_container"));

    @Override
    public Type<OpenFilterContainerPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, OpenFilterContainerPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);
}

