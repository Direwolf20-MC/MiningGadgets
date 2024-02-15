package com.direwolf20.mininggadgets.common.events;

import com.direwolf20.mininggadgets.common.network.data.DurabilitySyncPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class ServerTickHandler {

    private static List<Tuple<BlockPos, Integer>> updateList = new ArrayList<>();
    private static Level serverWorld;

    @SubscribeEvent
    public static void handleTickEndEvent(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (!updateList.isEmpty()) {
                PacketDistributor.ALL.noArg().send(new DurabilitySyncPayload(List.copyOf(updateList)));
                updateList.clear();
            }
        }
    }

    public static void addToList(BlockPos pos, int durability, Level world) {
        updateList.add(new Tuple<>(pos, durability));
        serverWorld = world;
    }

}
