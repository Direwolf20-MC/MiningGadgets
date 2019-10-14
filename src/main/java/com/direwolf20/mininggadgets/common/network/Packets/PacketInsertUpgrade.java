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

public class PacketInsertUpgrade {

    private final BlockPos pos;

    public PacketInsertUpgrade(BlockPos blockPos) {
        this.pos = blockPos;
    }

    public static void encode(PacketInsertUpgrade msg, PacketBuffer buffer) {
        buffer.writeBlockPos(msg.pos);
    }

    public static PacketInsertUpgrade decode(PacketBuffer buffer) {
        return new PacketInsertUpgrade(buffer.readBlockPos());
    }

    public static class Handler {
        public static void handle(PacketInsertUpgrade msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null) return;

                World world = player.world;
                BlockPos pos = msg.pos;

                TileEntity te = world.getTileEntity(pos);
                if (!(te instanceof ModificationTableTileEntity)) return;
                ModificationTableContainer container = ((ModificationTableTileEntity) te).getContainer(player);

                ModificationTableCommands.insertButton(container);
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
