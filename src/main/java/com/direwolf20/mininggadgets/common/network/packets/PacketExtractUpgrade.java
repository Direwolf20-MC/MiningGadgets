package com.direwolf20.mininggadgets.common.network.packets;

import com.direwolf20.mininggadgets.common.containers.ModificationTableCommands;
import com.direwolf20.mininggadgets.common.containers.ModificationTableContainer;
import com.direwolf20.mininggadgets.common.tiles.ModificationTableTileEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketExtractUpgrade {

    private final BlockPos pos;
    private final String upgrade;
    private final int nameLength;

    public PacketExtractUpgrade(BlockPos blockPos, String upgrade, int nameLength) {
        this.pos = blockPos;
        this.upgrade = upgrade;
        this.nameLength = nameLength;
    }

    public static void encode(PacketExtractUpgrade msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.nameLength);
        buffer.writeBlockPos(msg.pos);
        buffer.writeUtf(msg.upgrade);
    }

    public static PacketExtractUpgrade decode(FriendlyByteBuf buffer) {
        int strLength = buffer.readInt();
        return new PacketExtractUpgrade(buffer.readBlockPos(), buffer.readUtf(strLength), strLength);
    }

    public static class Handler {
        public static void handle(PacketExtractUpgrade msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player == null) return;

                Level world = player.level;
                BlockPos pos = msg.pos;

                BlockEntity te = world.getBlockEntity(pos);
                if (!(te instanceof ModificationTableTileEntity)) return;
                ModificationTableContainer container = ((ModificationTableTileEntity) te).getContainer(player);

                ModificationTableCommands.extractButton(container, player, msg.upgrade);
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
