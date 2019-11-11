package com.direwolf20.mininggadgets.common.network.packets;

import com.direwolf20.mininggadgets.common.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketChangeColor {
    private final short red;
    private final short green;
    private final short blue;

    public PacketChangeColor(int red, int green, int blue) {
        this.red = (short) red;
        this.green = (short) green;
        this.blue = (short) blue;
    }

    public static void encode(PacketChangeColor msg, PacketBuffer buffer) {
        buffer.writeShort(msg.red);
        buffer.writeShort(msg.green);
        buffer.writeShort(msg.blue);
    }

    public static PacketChangeColor decode(PacketBuffer buffer) {
        return new PacketChangeColor(buffer.readShort(), buffer.readShort(), buffer.readShort());
    }

    public static class Handler {
        public static void handle(PacketChangeColor msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null)
                    return;

                ItemStack stack = MiningGadget.getGadget(player);
                MiningProperties.setColor(stack, msg.red, MiningProperties.COLOR_RED);
                MiningProperties.setColor(stack, msg.green, MiningProperties.COLOR_GREEN);
                MiningProperties.setColor(stack, msg.blue, MiningProperties.COLOR_BLUE);
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
