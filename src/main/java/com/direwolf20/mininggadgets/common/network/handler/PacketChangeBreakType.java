package com.direwolf20.mininggadgets.common.network.handler;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.network.data.ChangeBreakTypePayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public class PacketChangeBreakType {
    public static final PacketChangeBreakType INSTANCE = new PacketChangeBreakType();

    public static PacketChangeBreakType get() {
        return INSTANCE;
    }

    public void handle(final ChangeBreakTypePayload payload, final PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            Optional<Player> senderOptional = context.player();
            if (senderOptional.isEmpty())
                return;
            Player player = senderOptional.get();


            ItemStack stack = MiningGadget.getGadget(player);
            MiningProperties.nextBreakType(stack);
        });
    }
}
