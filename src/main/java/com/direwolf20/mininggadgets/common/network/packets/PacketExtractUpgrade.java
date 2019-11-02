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
    private final boolean isShiftHeld;

    public PacketExtractUpgrade(BlockPos blockPos, String upgrade, int nameLength, boolean isShiftHeld) {
        this.pos = blockPos;
        this.upgrade = upgrade;
        this.nameLength = nameLength;
        this.isShiftHeld = isShiftHeld;
    }

    public static void encode(PacketExtractUpgrade msg, PacketBuffer buffer) {
        buffer.writeInt(msg.nameLength);
        buffer.writeBlockPos(msg.pos);
        buffer.writeString(msg.upgrade);
        buffer.writeBoolean(msg.isShiftHeld);
    }

    public static PacketExtractUpgrade decode(PacketBuffer buffer) {
        int strLength = buffer.readInt();
        return new PacketExtractUpgrade(buffer.readBlockPos(), buffer.readString(strLength), strLength, buffer.readBoolean());
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

                ModificationTableCommands.extractButton(container, player, msg.upgrade, msg.isShiftHeld);
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
