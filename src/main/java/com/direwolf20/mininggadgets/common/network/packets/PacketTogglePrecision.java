package com.direwolf20.mininggadgets.common.network.packets;

import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketTogglePrecision {
    public PacketTogglePrecision() {
    }

    public static void encode(PacketTogglePrecision msg, FriendlyByteBuf buffer) {

    }

    public static PacketTogglePrecision decode(FriendlyByteBuf buffer) {
        return new PacketTogglePrecision();
    }

    public static class Handler {
        public static void handle(PacketTogglePrecision msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player == null)
                    return;

                ItemStack stack = MiningGadget.getGadget(player);

                // Active toggle feature
                MiningProperties.setPrecisionMode(stack, !MiningProperties.getPrecisionMode(stack));
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
