package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ChangeFreezeDelayPayload(
        int freezeDelay
) implements CustomPacketPayload {
    public static final Type<ChangeFreezeDelayPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MiningGadgets.MOD_ID, "change_freeze_delay"));

    @Override
    public Type<ChangeFreezeDelayPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, ChangeFreezeDelayPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ChangeFreezeDelayPayload::freezeDelay,
            ChangeFreezeDelayPayload::new
    );
}

