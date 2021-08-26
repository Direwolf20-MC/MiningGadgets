package com.direwolf20.mininggadgets.common.network.packets;

import com.direwolf20.mininggadgets.common.containers.GhostSlot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketGhostSlot {
    private int slotNumber;
    private ItemStack stack;

    public PacketGhostSlot(int slotNumber, ItemStack stack) {
        this.slotNumber = slotNumber;
        this.stack = stack;
    }

    public static void encode(PacketGhostSlot msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.slotNumber);
        buffer.writeItem(msg.stack);
    }

    public static PacketGhostSlot decode(FriendlyByteBuf buffer) {
        return new PacketGhostSlot(buffer.readInt(), buffer.readItem());
    }

    public static class Handler {
        public static void handle(PacketGhostSlot msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer sender = ctx.get().getSender();
                if (sender == null)
                    return;

                AbstractContainerMenu container = sender.containerMenu;
                if (container == null)
                    return;

                Slot slot = container.slots.get(msg.slotNumber);
                if (slot instanceof GhostSlot)
                    slot.set(msg.stack);
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
