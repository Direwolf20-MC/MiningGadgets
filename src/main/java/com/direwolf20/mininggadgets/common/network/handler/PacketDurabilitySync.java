package com.direwolf20.mininggadgets.common.network.handler;

import com.direwolf20.mininggadgets.common.network.data.DurabilitySyncPayload;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.List;

public class PacketDurabilitySync {
    public static final PacketDurabilitySync INSTANCE = new PacketDurabilitySync();

    public static PacketDurabilitySync get() {
        return INSTANCE;
    }

    public void handle(final DurabilitySyncPayload payload, final PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            List<Tuple<BlockPos, Integer>> thisList = payload.updateList();

            for (int i = 0; i < thisList.size(); i++) {
                BlockPos pos = thisList.get(i).getA();
                int durability = thisList.get(i).getB();
                BlockEntity clientTE = Minecraft.getInstance().level.getBlockEntity(pos);
                if (!(clientTE instanceof RenderBlockTileEntity)) return;
                ((RenderBlockTileEntity) clientTE).setClientDurability(durability);
            }
        });
    }
}
