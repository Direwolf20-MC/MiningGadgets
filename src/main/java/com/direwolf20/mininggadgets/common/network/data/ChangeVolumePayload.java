package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ChangeVolumePayload(
        float volume
) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MiningGadgets.MOD_ID, "change_volume");

    public ChangeVolumePayload(final FriendlyByteBuf buffer) {
        this(buffer.readFloat());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeFloat(volume());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}

