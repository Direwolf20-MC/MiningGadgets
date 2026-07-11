package com.direwolf20.mininggadgets.common.network.handler;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.network.data.ChangeFreezeDelayPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketChangeFreezeDelay {
    public static final PacketChangeFreezeDelay INSTANCE = new PacketChangeFreezeDelay();

    public static PacketChangeFreezeDelay get() {
        return INSTANCE;
    }

    public void handle(final ChangeFreezeDelayPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();


            ItemStack stack = MiningGadget.getGadget(player);

            // Active toggle feature
            MiningProperties.setFreezeDelay(stack, payload.freezeDelay());
        });
    }
}
