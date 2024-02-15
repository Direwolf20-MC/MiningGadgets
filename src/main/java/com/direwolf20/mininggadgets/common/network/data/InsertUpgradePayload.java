package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record InsertUpgradePayload(
        BlockPos pos,
        ItemStack upgrade
) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MiningGadgets.MOD_ID, "insert_upgrade");

    public InsertUpgradePayload(final FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readItem());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos());
        buffer.writeItem(upgrade());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}

