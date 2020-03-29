package com.direwolf20.mininggadgets.common.network.packets;

import com.direwolf20.mininggadgets.common.containers.FilterContainer;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Supplier;

public class PacketOpenFilterContainer {
    public PacketOpenFilterContainer() { }

    public static void encode(PacketOpenFilterContainer msg, PacketBuffer buffer) {}
    public static PacketOpenFilterContainer decode(PacketBuffer buffer) { return new PacketOpenFilterContainer(); }

    public static class Handler {
        public static void handle(PacketOpenFilterContainer msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity sender = ctx.get().getSender();
                if (sender == null)
                    return;

                Container container = sender.openContainer;
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

                ghostInventory.deserializeNBT(stack.getOrCreateChildTag(MiningProperties.KEY_FILTERS));
                sender.openContainer(new SimpleNamedContainerProvider(
                        (windowId, playerInventory, playerEntity) -> new FilterContainer(windowId, playerInventory, ghostInventory), new StringTextComponent("")
                ));
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
