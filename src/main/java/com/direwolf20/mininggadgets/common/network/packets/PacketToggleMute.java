package com.direwolf20.mininggadgets.common.network.packets;

import com.direwolf20.mininggadgets.common.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketToggleMute {
    public PacketToggleMute() {
    }

    public static void encode(PacketToggleMute msg, PacketBuffer buffer) {

    }

    public static PacketToggleMute decode(PacketBuffer buffer) {
        return new PacketToggleMute();
    }

    public static class Handler {
        public static void handle(PacketToggleMute msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null)
                    return;

                ItemStack stack = MiningGadget.getGadget(player);

                // Active toggle feature
                MiningProperties.setMute(stack, !MiningProperties.getMute(stack));
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
