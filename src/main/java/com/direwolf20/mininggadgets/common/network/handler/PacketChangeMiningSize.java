package com.direwolf20.mininggadgets.common.network.handler;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.network.data.ChangeMiningSizePayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketChangeMiningSize {
    public static final PacketChangeMiningSize INSTANCE = new PacketChangeMiningSize();

    public static PacketChangeMiningSize get() {
        return INSTANCE;
    }

    public void handle(final ChangeMiningSizePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();


            ItemStack stack = MiningGadget.getGadget(player);
            MiningGadget.changeRange(stack);
        });
    }
}
