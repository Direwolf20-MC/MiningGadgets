package com.direwolf20.mininggadgets.common.network.handler;

import com.direwolf20.mininggadgets.common.network.data.DurabilitySyncPayload;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import com.direwolf20.mininggadgets.common.util.CodecHelpers;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public class PacketDurabilitySync {
    public static final PacketDurabilitySync INSTANCE = new PacketDurabilitySync();

    public static PacketDurabilitySync get() {
        return INSTANCE;
    }

    public void handle(final DurabilitySyncPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            List<CodecHelpers.DurabilitySyncData> thisList = payload.updateList();

            for (int i = 0; i < thisList.size(); i++) {
                BlockPos pos = thisList.get(i).blockPos();
                int durability = thisList.get(i).durability();
                BlockEntity clientTE = Minecraft.getInstance().level.getBlockEntity(pos);
                if (!(clientTE instanceof RenderBlockTileEntity)) return;
                ((RenderBlockTileEntity) clientTE).setClientDurability(durability);
            }
        });
    }
}
