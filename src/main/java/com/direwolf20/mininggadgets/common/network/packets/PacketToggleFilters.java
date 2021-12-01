package com.direwolf20.mininggadgets.common.network.packets;

import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketToggleFilters {
    public PacketToggleFilters() {
    }

    public static void encode(PacketToggleFilters msg, FriendlyByteBuf buffer) {

    }

    public static PacketToggleFilters decode(FriendlyByteBuf buffer) {
        return new PacketToggleFilters();
    }

    public static class Handler {
        public static void handle(PacketToggleFilters msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
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
