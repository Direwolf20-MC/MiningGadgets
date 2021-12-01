package com.direwolf20.mininggadgets.common.network.packets;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketChangeMiningSize {
    public PacketChangeMiningSize() {}

    public static void encode(PacketChangeMiningSize msg, FriendlyByteBuf buffer) {}
    public static PacketChangeMiningSize decode(FriendlyByteBuf buffer) { return new PacketChangeMiningSize(); }

    public static class Handler {
        public static void handle(PacketChangeMiningSize msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player == null)
                    return;

                ItemStack stack = MiningGadget.getGadget(player);
                MiningGadget.changeRange(stack);
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
