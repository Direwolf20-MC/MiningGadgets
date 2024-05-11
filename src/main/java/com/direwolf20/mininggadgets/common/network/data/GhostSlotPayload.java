package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record GhostSlotPayload(
        int slotNumber,
        ItemStack stack
) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MiningGadgets.MOD_ID, "ghost_slot");

    public GhostSlotPayload(final FriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readItem());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(slotNumber());
        buffer.writeItem(stack());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}

