package com.direwolf20.mininggadgets.common.network.packets;

import com.direwolf20.mininggadgets.common.containers.ModificationTableCommands;
import com.direwolf20.mininggadgets.common.containers.ModificationTableContainer;
import com.direwolf20.mininggadgets.common.tiles.ModificationTableTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.swing.*;
import java.util.function.Supplier;

public final class PacketInsertUpgrade {
    public static PacketInsertUpgrade decode(PacketBuffer buffer) {
        return new PacketInsertUpgrade(buffer.readBlockPos(), buffer.readItemStack());
    }

    private final BlockPos pos;
    private final ItemStack upgrade;

    public PacketInsertUpgrade(BlockPos blockPos, ItemStack stack) {
        this.pos = blockPos;
        this.upgrade = stack;
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeItemStack(upgrade);
    }

    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player == null) return;

            World world = player.world;
            BlockPos pos = this.pos;

            TileEntity te = world.getTileEntity(pos);
            if (!(te instanceof ModificationTableTileEntity)) return;
            ModificationTableContainer container = ((ModificationTableTileEntity) te).getContainer(player);

            ItemStack stack = player.inventory.getItemStack();
            if (!stack.isItemEqual(upgrade)) {
                return;
            }

            ModificationTableCommands.insertButton(container, this.upgrade);
            player.inventory.setItemStack(ItemStack.EMPTY);
        });

        ctx.get().setPacketHandled(true);
    }
}
