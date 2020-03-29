package com.direwolf20.mininggadgets.common.network.packets;

import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdateUpgrade {
    private final String upgrade;

    public PacketUpdateUpgrade(String upgrade) {
        this.upgrade = upgrade;
    }

    public static void encode(PacketUpdateUpgrade msg, PacketBuffer buffer) {
        buffer.writeString(msg.upgrade);
    }

    public static PacketUpdateUpgrade decode(PacketBuffer buffer) {
        return new PacketUpdateUpgrade(buffer.readString(100));
    }

    public static class Handler {
        public static void handle(PacketUpdateUpgrade msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null)
                    return;

                Upgrade upgrade = UpgradeTools.getUpgradeByName(msg.upgrade);
                if( upgrade == null )
                    return;

                ItemStack stack = MiningGadget.getGadget(player);
                UpgradeTools.updateUpgrade(stack, upgrade); //todo: change.
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
