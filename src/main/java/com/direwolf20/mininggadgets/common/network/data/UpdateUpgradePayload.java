package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UpdateUpgradePayload(
        String upgrade
) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MiningGadgets.MOD_ID, "update_upgrade");

    public UpdateUpgradePayload(final FriendlyByteBuf buffer) {
        this(buffer.readUtf());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUtf(upgrade());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}

