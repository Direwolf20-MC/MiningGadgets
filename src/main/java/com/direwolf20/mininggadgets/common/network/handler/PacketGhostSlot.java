package com.direwolf20.mininggadgets.common.network.handler;

import com.direwolf20.mininggadgets.common.containers.GhostSlot;
import com.direwolf20.mininggadgets.common.network.data.GhostSlotPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketGhostSlot {
    public static final PacketGhostSlot INSTANCE = new PacketGhostSlot();

    public static PacketGhostSlot get() {
        return INSTANCE;
    }

    public void handle(final GhostSlotPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            AbstractContainerMenu container = player.containerMenu;
            if (container == null)
                return;

            Slot slot = container.slots.get(payload.slotNumber());
            if (slot instanceof GhostSlot)
                slot.set(payload.stack());
        });
    }
}
