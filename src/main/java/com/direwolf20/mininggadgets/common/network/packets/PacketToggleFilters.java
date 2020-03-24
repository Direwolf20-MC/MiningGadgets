package com.direwolf20.mininggadgets.common.network.packets;

import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketToggleFilters {
    public PacketToggleFilters() {
    }

    public static void encode(PacketToggleFilters msg, PacketBuffer buffer) {

    }

    public static PacketToggleFilters decode(PacketBuffer buffer) {
        return new PacketToggleFilters();
    }

    public static class Handler {
        public static void handle(PacketToggleFilters msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null)
                    return;

                ItemStack stack = MiningGadget.getGadget(player);

                // Active toggle feature
                MiningProperties.setWhitelist(stack, !MiningProperties.getWhiteList(stack));
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
