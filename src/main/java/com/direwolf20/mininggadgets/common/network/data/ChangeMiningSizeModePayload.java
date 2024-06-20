package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ChangeMiningSizeModePayload() implements CustomPacketPayload {
    public static final ChangeMiningSizeModePayload INSTANCE = new ChangeMiningSizeModePayload();
    public static final Type<ChangeMiningSizeModePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MiningGadgets.MOD_ID, "change_mining_size_mode"));

    @Override
    public Type<ChangeMiningSizeModePayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, ChangeMiningSizeModePayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);
}

