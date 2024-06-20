package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ChangeBreakTypePayload() implements CustomPacketPayload {
    public static final ChangeBreakTypePayload INSTANCE = new ChangeBreakTypePayload();
    public static final Type<ChangeBreakTypePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MiningGadgets.MOD_ID, "change_break_type"));

    @Override
    public Type<ChangeBreakTypePayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, ChangeBreakTypePayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);
}

