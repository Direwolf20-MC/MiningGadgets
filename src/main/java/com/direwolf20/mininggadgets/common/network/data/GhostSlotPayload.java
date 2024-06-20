package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record GhostSlotPayload(
        int slotNumber,
        ItemStack stack
) implements CustomPacketPayload {
    public static final Type<GhostSlotPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MiningGadgets.MOD_ID, "ghost_slot"));

    @Override
    public Type<GhostSlotPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, GhostSlotPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, GhostSlotPayload::slotNumber,
            ItemStack.OPTIONAL_STREAM_CODEC, GhostSlotPayload::stack,
            GhostSlotPayload::new
    );
}

