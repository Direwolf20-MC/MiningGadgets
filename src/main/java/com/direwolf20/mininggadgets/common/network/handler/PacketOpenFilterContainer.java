package com.direwolf20.mininggadgets.common.network.handler;

import com.direwolf20.mininggadgets.common.containers.FilterContainer;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.network.data.OpenFilterContainerPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public class PacketOpenFilterContainer {
    public static final PacketOpenFilterContainer INSTANCE = new PacketOpenFilterContainer();

    public static PacketOpenFilterContainer get() {
        return INSTANCE;
    }

    public void handle(final OpenFilterContainerPayload payload, final PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            Optional<Player> senderOptional = context.player();
            if (senderOptional.isEmpty())
                return;

            Player player = senderOptional.get();
            AbstractContainerMenu container = player.containerMenu;
            if (container == null)
                return;

            ItemStack stack = MiningGadget.getGadget(player);
            if (stack.isEmpty())
                return;

            ItemStackHandler ghostInventory = new ItemStackHandler(30) {
                @Override
                protected void onContentsChanged(int slot) {
                    stack.getOrCreateTag().put(MiningProperties.KEY_FILTERS, serializeNBT());
                }
            };

            ghostInventory.deserializeNBT(stack.getOrCreateTagElement(MiningProperties.KEY_FILTERS));
            player.openMenu(new SimpleMenuProvider(
                    (windowId, playerInventory, playerEntity) -> new FilterContainer(windowId, playerInventory, ghostInventory), Component.literal("")
            ));
        });
    }
}
