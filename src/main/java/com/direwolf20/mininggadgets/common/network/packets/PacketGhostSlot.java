package com.direwolf20.mininggadgets.common.network.packets;

import com.direwolf20.mininggadgets.common.containers.GhostSlot;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketGhostSlot {
    private int slotNumber;
    private ItemStack stack;

    public PacketGhostSlot(int slotNumber, ItemStack stack) {
        this.slotNumber = slotNumber;
        this.stack = stack;
    }

    public static void encode(PacketGhostSlot msg, PacketBuffer buffer) {
        buffer.writeInt(msg.slotNumber);
        buffer.writeItemStack(msg.stack);
    }

    public static PacketGhostSlot decode(PacketBuffer buffer) {
        return new PacketGhostSlot(buffer.readInt(), buffer.readItemStack());
    }

    public static class Handler {
        public static void handle(PacketGhostSlot msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity sender = ctx.get().getSender();
                if (sender == null)
                    return;

                Container container = sender.openContainer;
                if (container == null)
                    return;

                Slot slot = container.inventorySlots.get(msg.slotNumber);
                if (slot instanceof GhostSlot)
                    slot.putStack(msg.stack);
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
