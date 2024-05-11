package com.direwolf20.mininggadgets.common.network.handler;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.network.data.ChangeRangePayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketChangeRange {
    public static final PacketChangeRange INSTANCE = new PacketChangeRange();

    public static PacketChangeRange get() {
        return INSTANCE;
    }

    public void handle(final ChangeRangePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();


            ItemStack stack = MiningGadget.getGadget(player);
            MiningProperties.setBeamRange(stack, payload.range());
        });
    }
}
