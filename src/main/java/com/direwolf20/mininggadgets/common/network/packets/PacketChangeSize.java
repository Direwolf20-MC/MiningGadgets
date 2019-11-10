package com.direwolf20.mininggadgets.common.network.packets;

import com.direwolf20.mininggadgets.common.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketChangeSize {
    private final int size;

    public PacketChangeSize(int size) {
        this.size = size;
    }

    public static void encode(PacketChangeSize msg, PacketBuffer buffer) {
        buffer.writeInt(msg.size);
    }

    public static PacketChangeSize decode(PacketBuffer buffer) {
        return new PacketChangeSize(buffer.readInt());
    }

    public static class Handler {
        public static void handle(PacketChangeSize msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null)
                    return;

                ItemStack stack = MiningGadget.getGadget(player);
                MiningProperties.setRange(stack, msg.size);
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
