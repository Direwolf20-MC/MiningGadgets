package com.direwolf20.mininggadgets.common.network.packets;

import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketChangeRange {
    private final int range;

    public PacketChangeRange(int range) {
        this.range = range;
    }

    public static void encode(PacketChangeRange msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.range);
    }

    public static PacketChangeRange decode(FriendlyByteBuf buffer) {
        return new PacketChangeRange(buffer.readInt());
    }

    public static class Handler {
        public static void handle(PacketChangeRange msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player == null)
                    return;

                ItemStack stack = MiningGadget.getGadget(player);
                MiningProperties.setBeamRange(stack, msg.range);
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
