package com.direwolf20.mininggadgets.common.network.handler;

import com.direwolf20.mininggadgets.common.containers.ModificationTableCommands;
import com.direwolf20.mininggadgets.common.containers.ModificationTableContainer;
import com.direwolf20.mininggadgets.common.network.data.ExtractUpgradePayload;
import com.direwolf20.mininggadgets.common.tiles.ModificationTableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public class PacketExtractUpgrade {
    public static final PacketExtractUpgrade INSTANCE = new PacketExtractUpgrade();

    public static PacketExtractUpgrade get() {
        return INSTANCE;
    }

    public void handle(final ExtractUpgradePayload payload, final PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            Optional<Player> senderOptional = context.player();
            if (senderOptional.isEmpty())
                return;
            ServerPlayer player = (ServerPlayer) senderOptional.get();

            Level world = player.level();
            BlockPos pos = payload.pos();

            BlockEntity te = world.getBlockEntity(pos);
            if (!(te instanceof ModificationTableTileEntity)) return;
            ModificationTableContainer container = ((ModificationTableTileEntity) te).getContainer(player);

            ModificationTableCommands.extractButton(container, player, payload.upgrade());
        });
    }
}
