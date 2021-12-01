package com.direwolf20.mininggadgets.common.network.packets;

import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketChangeVolume {
    private float volume;

    public PacketChangeVolume(float volume) {
        this.volume = volume;
    }

    public static void encode(PacketChangeVolume msg, FriendlyByteBuf buffer) {
        buffer.writeFloat(msg.volume);
    }

    public static PacketChangeVolume decode(FriendlyByteBuf buffer) {
        return new PacketChangeVolume(buffer.readFloat());
    }

    public static class Handler {
        public static void handle(PacketChangeVolume msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player == null)
                    return;

                ItemStack stack = MiningGadget.getGadget(player);

                // Active toggle feature
                MiningProperties.setVolume(stack, msg.volume);
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
