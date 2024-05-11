package com.direwolf20.mininggadgets.common.network.handler;

import com.direwolf20.mininggadgets.common.containers.ModificationTableCommands;
import com.direwolf20.mininggadgets.common.containers.ModificationTableContainer;
import com.direwolf20.mininggadgets.common.network.data.InsertUpgradePayload;
import com.direwolf20.mininggadgets.common.tiles.ModificationTableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketInsertUpgrade {
    public static final PacketInsertUpgrade INSTANCE = new PacketInsertUpgrade();

    public static PacketInsertUpgrade get() {
        return INSTANCE;
    }

    public void handle(final InsertUpgradePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            Level world = player.level();
            BlockPos pos = payload.pos();

            BlockEntity te = world.getBlockEntity(pos);
            if (!(te instanceof ModificationTableTileEntity)) return;
            ModificationTableContainer container = ((ModificationTableTileEntity) te).getContainer(player);

            ItemStack stack = player.containerMenu.getCarried();
            if (!ItemStack.matches(stack, payload.upgrade())) {
                return;
            }

            if (ModificationTableCommands.insertButton(container, payload.upgrade())) {
                player.containerMenu.setCarried(ItemStack.EMPTY);
            }
        });
    }
}
