package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ChangeFreezeDelayPayload(
        int freezeDelay
) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MiningGadgets.MOD_ID, "change_freeze_delay");

    public ChangeFreezeDelayPayload(final FriendlyByteBuf buffer) {
        this(buffer.readInt());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(freezeDelay());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}

