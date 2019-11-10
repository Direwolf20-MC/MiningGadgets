package com.direwolf20.mininggadgets.common.network.packets;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketChangeRange  {
    public PacketChangeRange() {}

    public static void encode(PacketChangeRange msg, PacketBuffer buffer) {}
    public static PacketChangeRange decode(PacketBuffer buffer) { return new PacketChangeRange(); }

    public static class Handler {
        public static void handle(PacketChangeRange msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null)
                    return;

                ItemStack stack = MiningGadget.getGadget(player);
                MiningGadget.changeRange(stack);
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
