package com.direwolf20.mininggadgets.common.network.Packets;

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

    public PacketExtractUpgrade(BlockPos blockPos) {
        this.pos = blockPos;
    }

    public static void encode(PacketExtractUpgrade msg, PacketBuffer buffer) {
        buffer.writeBlockPos(msg.pos);
    }

    public static PacketExtractUpgrade decode(PacketBuffer buffer) {
        return new PacketExtractUpgrade(buffer.readBlockPos());
    }

    public static class Handler {
        public static void handle(PacketExtractUpgrade msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null) return;

                World world = player.world;
                BlockPos pos = msg.pos;

                TileEntity te = world.getTileEntity(pos);
                if (!(te instanceof ModificationTableTileEntity)) return;
                ModificationTableContainer container = ((ModificationTableTileEntity) te).getContainer(player);

                ModificationTableCommands.extractButton(container);
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
