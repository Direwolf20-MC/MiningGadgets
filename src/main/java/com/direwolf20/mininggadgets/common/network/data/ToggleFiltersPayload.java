package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ToggleFiltersPayload() implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MiningGadgets.MOD_ID, "toggle_filters");

    public ToggleFiltersPayload(final FriendlyByteBuf buffer) {
        this();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {

    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}

