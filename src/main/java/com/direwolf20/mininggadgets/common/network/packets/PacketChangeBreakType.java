package com.direwolf20.mininggadgets.common.network.packets;

import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketChangeBreakType {
    public PacketChangeBreakType() {
    }

    public static void encode(PacketChangeBreakType msg, FriendlyByteBuf buffer) {
    }

    public static PacketChangeBreakType decode(FriendlyByteBuf buffer) {
        return new PacketChangeBreakType();
    }

    public static class Handler {
        public static void handle(PacketChangeBreakType msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player == null)
                    return;

                ItemStack stack = MiningGadget.getGadget(player);
                MiningProperties.nextBreakType(stack);
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
