package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ChangeRangePayload(
        int range
) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MiningGadgets.MOD_ID, "change_range");

    public ChangeRangePayload(final FriendlyByteBuf buffer) {
        this(buffer.readInt());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(range());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}

