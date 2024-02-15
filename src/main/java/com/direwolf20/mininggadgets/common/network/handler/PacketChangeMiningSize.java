package com.direwolf20.mininggadgets.common.network.handler;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.network.data.ChangeMiningSizePayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public class PacketChangeMiningSize {
    public static final PacketChangeMiningSize INSTANCE = new PacketChangeMiningSize();

    public static PacketChangeMiningSize get() {
        return INSTANCE;
    }

    public void handle(final ChangeMiningSizePayload payload, final PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            Optional<Player> senderOptional = context.player();
            if (senderOptional.isEmpty())
                return;
            Player player = senderOptional.get();


            ItemStack stack = MiningGadget.getGadget(player);
            MiningGadget.changeRange(stack);
        });
    }
}
