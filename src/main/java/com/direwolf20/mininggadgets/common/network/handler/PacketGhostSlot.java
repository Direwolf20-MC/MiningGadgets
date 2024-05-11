package com.direwolf20.mininggadgets.common.network.handler;

import com.direwolf20.mininggadgets.common.containers.GhostSlot;
import com.direwolf20.mininggadgets.common.network.data.GhostSlotPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public class PacketGhostSlot {
    public static final PacketGhostSlot INSTANCE = new PacketGhostSlot();

    public static PacketGhostSlot get() {
        return INSTANCE;
    }

    public void handle(final GhostSlotPayload payload, final PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            Optional<Player> senderOptional = context.player();
            if (senderOptional.isEmpty())
                return;
            Player player = senderOptional.get();

            AbstractContainerMenu container = player.containerMenu;
            if (container == null)
                return;

            Slot slot = container.slots.get(payload.slotNumber());
            if (slot instanceof GhostSlot)
                slot.set(payload.stack());
        });
    }
}
