package com.direwolf20.mininggadgets.common.network.data;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.List;

public record DurabilitySyncPayload(
        List<Tuple<BlockPos, Integer>> updateList
) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MiningGadgets.MOD_ID, "durability_sync");

    public DurabilitySyncPayload(final FriendlyByteBuf buffer) {
        this(decodeList(buffer));
    }

    public static List<Tuple<BlockPos, Integer>> decodeList(final FriendlyByteBuf buffer) {
        CompoundTag tag = buffer.readNbt();
        ListTag nbtList = tag.getList("list", Tag.TAG_COMPOUND);
        List<Tuple<BlockPos, Integer>> thisList = new ArrayList<>();
        for (int i = 0; i < nbtList.size(); i++) {
            CompoundTag nbt = nbtList.getCompound(i);
            thisList.add(new Tuple<>(NbtUtils.readBlockPos(nbt.getCompound("pos")), nbt.getInt("dur")));
        }
        return thisList;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        List<Tuple<BlockPos, Integer>> thisList = List.copyOf(updateList());
        CompoundTag tag = new CompoundTag();
        ListTag nbtList = new ListTag();
        for (int i = 0; i < thisList.size(); i++) {
            CompoundTag nbt = new CompoundTag();
            nbt.put("pos", NbtUtils.writeBlockPos(thisList.get(i).getA()));
            nbt.putInt("dur", thisList.get(i).getB());
            nbtList.add(i, nbt);
        }
        tag.put("list", nbtList);
        buffer.writeNbt(tag);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}

