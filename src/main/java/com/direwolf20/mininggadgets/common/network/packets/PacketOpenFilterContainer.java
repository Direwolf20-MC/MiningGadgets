package com.direwolf20.mininggadgets.common.network.packets;

import com.direwolf20.mininggadgets.common.containers.FilterContainer;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Supplier;

public class PacketOpenFilterContainer {
    public PacketOpenFilterContainer() { }

    public static void encode(PacketOpenFilterContainer msg, FriendlyByteBuf buffer) {}
    public static PacketOpenFilterContainer decode(FriendlyByteBuf buffer) { return new PacketOpenFilterContainer(); }

    public static class Handler {
        public static void handle(PacketOpenFilterContainer msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer sender = ctx.get().getSender();
                if (sender == null)
                    return;

                AbstractContainerMenu container = sender.containerMenu;
                if (container == null)
                    return;

                ItemStack stack = MiningGadget.getGadget(sender);
                if( stack.isEmpty() )
                    return;

                ItemStackHandler ghostInventory = new ItemStackHandler(30) {
                    @Override
                    protected void onContentsChanged(int slot) {
                        stack.getOrCreateTag().put(MiningProperties.KEY_FILTERS, serializeNBT());
                    }
                };

                ghostInventory.deserializeNBT(stack.getOrCreateTagElement(MiningProperties.KEY_FILTERS));
                sender.openMenu(new SimpleMenuProvider(
                        (windowId, playerInventory, playerEntity) -> new FilterContainer(windowId, playerInventory, ghostInventory), new TextComponent("")
                ));
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
