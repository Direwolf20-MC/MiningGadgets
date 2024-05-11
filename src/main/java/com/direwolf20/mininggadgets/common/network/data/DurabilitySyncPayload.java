package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.util.CodecHelpers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record DurabilitySyncPayload(
        List<CodecHelpers.DurabilitySyncData> updateList
) implements CustomPacketPayload {
    public static final Type<DurabilitySyncPayload> TYPE = new Type<>(new ResourceLocation(MiningGadgets.MOD_ID, "durability_sync"));

    @Override
    public Type<DurabilitySyncPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, DurabilitySyncPayload> STREAM_CODEC = StreamCodec.composite(
            CodecHelpers.DurabilitySyncData.STREAM_CODEC.apply(ByteBufCodecs.list()), DurabilitySyncPayload::updateList,
            DurabilitySyncPayload::new
    );
}

