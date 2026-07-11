package com.direwolf20.mininggadgets.common.network.handler;

import com.direwolf20.mininggadgets.common.containers.FilterContainer;
import com.direwolf20.mininggadgets.common.containers.handlers.DataComponentHandler;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.network.data.OpenFilterContainerPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketOpenFilterContainer {
    public static final PacketOpenFilterContainer INSTANCE = new PacketOpenFilterContainer();

    public static PacketOpenFilterContainer get() {
        return INSTANCE;
    }

    public void handle(final OpenFilterContainerPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            AbstractContainerMenu container = player.containerMenu;
            if (container == null)
                return;

            ItemStack stack = MiningGadget.getGadget(player);
            if (stack.isEmpty())
                return;

            DataComponentHandler ghostInventory = new DataComponentHandler(stack, 30);
            player.openMenu(new SimpleMenuProvider(
                    (windowId, playerInventory, playerEntity) -> new FilterContainer(windowId, playerInventory, ghostInventory), Component.literal("")), (buf -> {
                ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stack);
            }));
        });
    }
}
