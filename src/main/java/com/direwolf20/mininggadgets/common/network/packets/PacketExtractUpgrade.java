package com.direwolf20.mininggadgets.common.network.packets;

import com.direwolf20.mininggadgets.common.containers.ModificationTableCommands;
import com.direwolf20.mininggadgets.common.containers.ModificationTableContainer;
import com.direwolf20.mininggadgets.common.tiles.ModificationTableTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

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

    public static void encode(PacketExtractUpgrade msg, PacketBuffer buffer) {
        buffer.writeInt(msg.nameLength);
        buffer.writeBlockPos(msg.pos);
        buffer.writeUtf(msg.upgrade);
    }

    public static PacketExtractUpgrade decode(PacketBuffer buffer) {
        int strLength = buffer.readInt();
        return new PacketExtractUpgrade(buffer.readBlockPos(), buffer.readUtf(strLength), strLength);
    }

    public static class Handler {
        public static void handle(PacketExtractUpgrade msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null) return;

                World world = player.level;
                BlockPos pos = msg.pos;

                TileEntity te = world.getBlockEntity(pos);
                if (!(te instanceof ModificationTableTileEntity)) return;
                ModificationTableContainer container = ((ModificationTableTileEntity) te).getContainer(player);

                ModificationTableCommands.extractButton(container, player, msg.upgrade);
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
