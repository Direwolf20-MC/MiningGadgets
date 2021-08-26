package com.direwolf20.mininggadgets.common.network.packets;

import com.direwolf20.mininggadgets.common.containers.ModificationTableCommands;
import com.direwolf20.mininggadgets.common.containers.ModificationTableContainer;
import com.direwolf20.mininggadgets.common.tiles.ModificationTableTileEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import javax.swing.*;
import java.util.function.Supplier;

public final class PacketInsertUpgrade {
    public static PacketInsertUpgrade decode(FriendlyByteBuf buffer) {
        return new PacketInsertUpgrade(buffer.readBlockPos(), buffer.readItem());
    }

    private final BlockPos pos;
    private final ItemStack upgrade;

    public PacketInsertUpgrade(BlockPos blockPos, ItemStack stack) {
        this.pos = blockPos;
        this.upgrade = stack;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeItem(upgrade);
    }

    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            Level world = player.level;
            BlockPos pos = this.pos;

            BlockEntity te = world.getBlockEntity(pos);
            if (!(te instanceof ModificationTableTileEntity)) return;
            ModificationTableContainer container = ((ModificationTableTileEntity) te).getContainer(player);

            ItemStack stack = player.getInventory().getSelected();
            if (!stack.sameItem(upgrade)) {
                return;
            }

            if (ModificationTableCommands.insertButton(container, this.upgrade)) {
                player.getInventory().setPickedItem(ItemStack.EMPTY);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
