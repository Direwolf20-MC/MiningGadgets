package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ChangeColorPayload(
        short red,
        short green,
        short blue,
        short red_inner,
        short green_inner,
        short blue_inner
) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MiningGadgets.MOD_ID, "change_color");

    public ChangeColorPayload(final FriendlyByteBuf buffer) {
        this(buffer.readShort(), buffer.readShort(), buffer.readShort(), buffer.readShort(), buffer.readShort(), buffer.readShort());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeShort(red());
        buffer.writeShort(green());
        buffer.writeShort(blue());
        buffer.writeShort(red_inner());
        buffer.writeShort(green_inner());
        buffer.writeShort(blue_inner());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}

