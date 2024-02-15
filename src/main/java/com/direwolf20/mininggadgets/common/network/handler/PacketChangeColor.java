package com.direwolf20.mininggadgets.common.network.handler;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.network.data.ChangeColorPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public class PacketChangeColor {
    public static final PacketChangeColor INSTANCE = new PacketChangeColor();

    public static PacketChangeColor get() {
        return INSTANCE;
    }

    public void handle(final ChangeColorPayload payload, final PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            Optional<Player> senderOptional = context.player();
            if (senderOptional.isEmpty())
                return;
            Player player = senderOptional.get();


            ItemStack stack = MiningGadget.getGadget(player);
            MiningProperties.setColor(stack, payload.red(), MiningProperties.COLOR_RED);
            MiningProperties.setColor(stack, payload.green(), MiningProperties.COLOR_GREEN);
            MiningProperties.setColor(stack, payload.blue(), MiningProperties.COLOR_BLUE);
            MiningProperties.setColor(stack, payload.red_inner(), MiningProperties.COLOR_RED_INNER);
            MiningProperties.setColor(stack, payload.green_inner(), MiningProperties.COLOR_GREEN_INNER);
            MiningProperties.setColor(stack, payload.blue_inner(), MiningProperties.COLOR_BLUE_INNER);
        });
    }
}
