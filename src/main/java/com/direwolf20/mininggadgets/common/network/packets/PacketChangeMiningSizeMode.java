package com.direwolf20.mininggadgets.common.network.packets;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketChangeMiningSizeMode {
    public PacketChangeMiningSizeMode() {}

    public static void encode(PacketChangeMiningSizeMode msg, FriendlyByteBuf buffer) {}
    public static PacketChangeMiningSizeMode decode(FriendlyByteBuf buffer) { return new PacketChangeMiningSizeMode(); }

    public static class Handler {
        public static void handle(PacketChangeMiningSizeMode msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player == null)
                    return;

                ItemStack stack = MiningGadget.getGadget(player);
                MiningProperties.nextSizeMode(stack);
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
