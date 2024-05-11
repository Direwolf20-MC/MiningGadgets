package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ChangeMiningSizePayload() implements CustomPacketPayload {
    public static final ChangeMiningSizePayload INSTANCE = new ChangeMiningSizePayload();
    public static final Type<ChangeMiningSizePayload> TYPE = new Type<>(new ResourceLocation(MiningGadgets.MOD_ID, "change_mining_size"));

    @Override
    public Type<ChangeMiningSizePayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, ChangeMiningSizePayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);
}

