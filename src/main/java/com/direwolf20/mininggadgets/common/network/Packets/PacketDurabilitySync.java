package com.direwolf20.mininggadgets.common.network.Packets;

import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PacketDurabilitySync {
    private final List<Tuple<BlockPos, Integer>> updateList;
    //private final int priorDurability;

    public PacketDurabilitySync(List<Tuple<BlockPos, Integer>> updateList) {
        this.updateList = updateList;
    }

    public static void encode(PacketDurabilitySync msg, PacketBuffer buffer) {
        List<Tuple<BlockPos, Integer>> thisList = msg.updateList;
        CompoundNBT tag = new CompoundNBT();
        ListNBT nbtList = new ListNBT();
        for (int i = 0; i < thisList.size(); i++) {
            CompoundNBT nbt = new CompoundNBT();
            nbt.put("pos", NBTUtil.writeBlockPos(thisList.get(i).getA()));
            nbt.putInt("dur", thisList.get(i).getB());
            nbtList.add(i, nbt);
        }
        tag.put("list", nbtList);
        buffer.writeCompoundTag(tag);
    }

    public static PacketDurabilitySync decode(PacketBuffer buffer) {
        CompoundNBT tag = buffer.readCompoundTag();
        ListNBT nbtList = tag.getList("list", Constants.NBT.TAG_COMPOUND);
        List<Tuple<BlockPos, Integer>> thisList = new ArrayList<>();
        for (int i = 0; i < nbtList.size(); i++) {
            CompoundNBT nbt = nbtList.getCompound(i);
            thisList.add(new Tuple<>(NBTUtil.readBlockPos(nbt.getCompound("pos")), nbt.getInt("dur")));
        }
        return new PacketDurabilitySync(thisList);
    }

    public static class Handler {
        public static void handle(PacketDurabilitySync msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> clientPacketHandler(msg));

            ctx.get().setPacketHandled(true);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientPacketHandler(PacketDurabilitySync msg) {
        List<Tuple<BlockPos, Integer>> thisList = msg.updateList;

        World clientWorld = Minecraft.getInstance().world;
        for (int i = 0; i < thisList.size(); i++) {
            BlockPos pos = thisList.get(i).getA();
            int durability = thisList.get(i).getB();
            TileEntity clientTE = clientWorld.getTileEntity(pos);
            if (!(clientTE instanceof RenderBlockTileEntity)) return;
            ((RenderBlockTileEntity) clientTE).setClientDurability(durability);
        }
    }
}
