package com.direwolf20.mininggadgets.common.network.handler;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.network.data.ChangeMiningSizeModePayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public class PacketChangeMiningSizeMode {
    public static final PacketChangeMiningSizeMode INSTANCE = new PacketChangeMiningSizeMode();

    public static PacketChangeMiningSizeMode get() {
        return INSTANCE;
    }

    public void handle(final ChangeMiningSizeModePayload payload, final PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            Optional<Player> senderOptional = context.player();
            if (senderOptional.isEmpty())
                return;
            Player player = senderOptional.get();


            ItemStack stack = MiningGadget.getGadget(player);
            MiningProperties.nextSizeMode(stack);
        });
    }
}
