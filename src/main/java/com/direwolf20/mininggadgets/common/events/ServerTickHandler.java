package com.direwolf20.mininggadgets.common.events;

import com.direwolf20.mininggadgets.common.network.data.DurabilitySyncPayload;
import com.direwolf20.mininggadgets.common.util.CodecHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class ServerTickHandler {

    private static List<CodecHelpers.DurabilitySyncData> updateList = new ArrayList<>();
    private static Level serverWorld;

    @SubscribeEvent
    public static void handleTickEndEvent(ServerTickEvent.Post event) {
        if (!updateList.isEmpty()) {
            PacketDistributor.sendToAllPlayers(new DurabilitySyncPayload(List.copyOf(updateList)));
            updateList.clear();
        }
    }

    public static void addToList(BlockPos pos, int durability, Level world) {
        updateList.add(new CodecHelpers.DurabilitySyncData(pos, durability));
        serverWorld = world;
    }

}
