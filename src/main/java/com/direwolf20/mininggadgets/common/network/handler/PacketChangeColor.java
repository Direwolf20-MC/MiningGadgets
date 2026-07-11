package com.direwolf20.mininggadgets.common.network.handler;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.network.data.ChangeColorPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketChangeColor {
    public static final PacketChangeColor INSTANCE = new PacketChangeColor();

    public static PacketChangeColor get() {
        return INSTANCE;
    }

    public void handle(final ChangeColorPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();


            ItemStack stack = MiningGadget.getGadget(player);
            MiningProperties.setColor(stack, payload.laserColor());
        });
    }
}
