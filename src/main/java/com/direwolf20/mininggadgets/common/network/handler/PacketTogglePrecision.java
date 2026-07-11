package com.direwolf20.mininggadgets.common.network.handler;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.network.data.TogglePrecisionPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketTogglePrecision {
    public static final PacketTogglePrecision INSTANCE = new PacketTogglePrecision();

    public static PacketTogglePrecision get() {
        return INSTANCE;
    }

    public void handle(final TogglePrecisionPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            ItemStack stack = MiningGadget.getGadget(player);
            MiningProperties.setPrecisionMode(stack, !MiningProperties.getPrecisionMode(stack));
        });
    }
}
