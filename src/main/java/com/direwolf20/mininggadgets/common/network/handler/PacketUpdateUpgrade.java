package com.direwolf20.mininggadgets.common.network.handler;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.network.data.UpdateUpgradePayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public class PacketUpdateUpgrade {
    public static final PacketUpdateUpgrade INSTANCE = new PacketUpdateUpgrade();

    public static PacketUpdateUpgrade get() {
        return INSTANCE;
    }

    public void handle(final UpdateUpgradePayload payload, final PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            Optional<Player> senderOptional = context.player();
            if (senderOptional.isEmpty())
                return;
            ServerPlayer player = (ServerPlayer) senderOptional.get();

            Upgrade upgrade = UpgradeTools.getUpgradeByName(payload.upgrade());
            if (upgrade == null)
                return;

            ItemStack stack = MiningGadget.getGadget(player);
            UpgradeTools.updateUpgrade(stack, upgrade); //todo: change.
        });
    }
}
