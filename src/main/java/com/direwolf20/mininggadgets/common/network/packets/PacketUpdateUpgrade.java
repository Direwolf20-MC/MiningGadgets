package com.direwolf20.mininggadgets.common.network.packets;

import com.direwolf20.mininggadgets.api.MiningGadgetsApi;
import com.direwolf20.mininggadgets.api.upgrades.MinerUpgrade;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdateUpgrade {
    private final ResourceLocation upgrade;

    public PacketUpdateUpgrade(ResourceLocation upgrade) {
        this.upgrade = upgrade;
    }

    public static void encode(PacketUpdateUpgrade msg, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(msg.upgrade);
    }

    public static PacketUpdateUpgrade decode(FriendlyByteBuf buffer) {
        return new PacketUpdateUpgrade(buffer.readResourceLocation());
    }

    public static class Handler {
        public static void handle(PacketUpdateUpgrade msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player == null)
                    return;

                MinerUpgrade upgrade = MiningGadgetsApi.get().upgradesRegistry().getUpgrade(msg.upgrade);
                if (upgrade == null)
                    return;

                ItemStack stack = MiningGadget.getGadget(player);
                MiningGadget.toggleUpgrade(stack, upgrade.getId());
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
