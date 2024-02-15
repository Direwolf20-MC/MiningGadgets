package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ExtractUpgradePayload(
        BlockPos pos,
        String upgrade,
        int nameLength
) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MiningGadgets.MOD_ID, "extract_upgrade");

    public ExtractUpgradePayload(final FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readUtf(), buffer.readInt());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos());
        buffer.writeUtf(upgrade());
        buffer.writeInt(nameLength());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}

