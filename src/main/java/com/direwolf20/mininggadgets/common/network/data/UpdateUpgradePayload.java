package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UpdateUpgradePayload(
        String upgrade
) implements CustomPacketPayload {
    public static final Type<UpdateUpgradePayload> TYPE = new Type<>(new ResourceLocation(MiningGadgets.MOD_ID, "update_upgrade"));

    @Override
    public Type<UpdateUpgradePayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, UpdateUpgradePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, UpdateUpgradePayload::upgrade,
            UpdateUpgradePayload::new
    );
}

