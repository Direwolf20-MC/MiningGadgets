package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ExtractUpgradePayload(
        BlockPos pos,
        String upgrade,
        int nameLength
) implements CustomPacketPayload {
    public static final Type<ExtractUpgradePayload> TYPE = new Type<>(new ResourceLocation(MiningGadgets.MOD_ID, "extract_upgrade"));

    @Override
    public Type<ExtractUpgradePayload> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, ExtractUpgradePayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ExtractUpgradePayload::pos,
            ByteBufCodecs.STRING_UTF8, ExtractUpgradePayload::upgrade,
            ByteBufCodecs.VAR_INT, ExtractUpgradePayload::nameLength,
            ExtractUpgradePayload::new
    );
}

