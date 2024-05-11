package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ToggleFiltersPayload() implements CustomPacketPayload {
    public static final ToggleFiltersPayload INSTANCE = new ToggleFiltersPayload();
    public static final Type<ToggleFiltersPayload> TYPE = new Type<>(new ResourceLocation(MiningGadgets.MOD_ID, "toggle_filters"));

    @Override
    public Type<ToggleFiltersPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, ToggleFiltersPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);
}

