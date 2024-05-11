package com.direwolf20.mininggadgets.common.network.handler;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.network.data.ChangeFreezeDelayPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public class PacketChangeFreezeDelay {
    public static final PacketChangeFreezeDelay INSTANCE = new PacketChangeFreezeDelay();

    public static PacketChangeFreezeDelay get() {
        return INSTANCE;
    }

    public void handle(final ChangeFreezeDelayPayload payload, final PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            Optional<Player> senderOptional = context.player();
            if (senderOptional.isEmpty())
                return;
            Player player = senderOptional.get();


            ItemStack stack = MiningGadget.getGadget(player);

            // Active toggle feature
            MiningProperties.setFreezeDelay(stack, payload.freezeDelay());
        });
    }
}
