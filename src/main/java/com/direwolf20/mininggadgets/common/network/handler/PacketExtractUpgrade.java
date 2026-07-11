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
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketExtractUpgrade {
    public static final PacketExtractUpgrade INSTANCE = new PacketExtractUpgrade();

    public static PacketExtractUpgrade get() {
        return INSTANCE;
    }

    public void handle(final ExtractUpgradePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            Level world = player.level();
            BlockPos pos = payload.pos();

            BlockEntity te = world.getBlockEntity(pos);
            if (!(te instanceof ModificationTableTileEntity)) return;
            ModificationTableContainer container = ((ModificationTableTileEntity) te).getContainer(player);

            ModificationTableCommands.extractButton(container, (ServerPlayer) player, payload.upgrade());
        });
    }
}
