package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record InsertUpgradePayload(
        BlockPos pos,
        ItemStack upgrade
) implements CustomPacketPayload {
    public static final Type<InsertUpgradePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MiningGadgets.MOD_ID, "insert_upgrade"));

    @Override
    public Type<InsertUpgradePayload> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, InsertUpgradePayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, InsertUpgradePayload::pos,
            ItemStack.STREAM_CODEC, InsertUpgradePayload::upgrade,
            InsertUpgradePayload::new
    );
}

