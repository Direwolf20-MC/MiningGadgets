package com.direwolf20.mininggadgets.common.network;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.common.network.packets.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = Integer.toString(2);
    private static short index = 0;

    public static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MiningGadgets.MOD_ID, "main_network_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public static void register() {
        // Server side
        registerMessage(PacketExtractUpgrade.class,     PacketExtractUpgrade::encode,       PacketExtractUpgrade::decode,       PacketExtractUpgrade.Handler::handle);
        registerMessage(PacketUpdateUpgrade.class,      PacketUpdateUpgrade::encode,        PacketUpdateUpgrade::decode,        PacketUpdateUpgrade.Handler::handle);
        registerMessage(PacketChangeMiningSize.class,   PacketChangeMiningSize::encode,     PacketChangeMiningSize::decode,     PacketChangeMiningSize.Handler::handle);
        registerMessage(PacketChangeRange.class,        PacketChangeRange::encode,          PacketChangeRange::decode,          PacketChangeRange.Handler::handle);
        registerMessage(PacketChangeBreakType.class,    PacketChangeBreakType::encode,      PacketChangeBreakType::decode,      PacketChangeBreakType.Handler::handle);
        registerMessage(PacketChangeColor.class,        PacketChangeColor::encode,          PacketChangeColor::decode,          PacketChangeColor.Handler::handle);
        registerMessage(PacketGhostSlot.class,          PacketGhostSlot::encode,            PacketGhostSlot::decode,            PacketGhostSlot.Handler::handle);
        registerMessage(PacketOpenFilterContainer.class,PacketOpenFilterContainer::encode,  PacketOpenFilterContainer::decode,  PacketOpenFilterContainer.Handler::handle);
        registerMessage(PacketToggleFilters.class,      PacketToggleFilters::encode,        PacketToggleFilters::decode,        PacketToggleFilters.Handler::handle);
        registerMessage(PacketTogglePrecision.class,    PacketTogglePrecision::encode,      PacketTogglePrecision::decode,      PacketTogglePrecision.Handler::handle);
        registerMessage(PacketChangeVolume.class,       PacketChangeVolume::encode,         PacketChangeVolume::decode,         PacketChangeVolume.Handler::handle);

        //Client Side
        registerMessage(PacketDurabilitySync.class,     PacketDurabilitySync::encode,       PacketDurabilitySync::decode,       PacketDurabilitySync.Handler::handle);
    }

    public static void sendTo(Object msg, ServerPlayerEntity player) {
        if (!(player instanceof FakePlayer))
            HANDLER.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToAll(Object msg, World world) {
        //Todo Maybe only send to nearby players?
        for (PlayerEntity player : world.getPlayers()) {
            if (!(player instanceof FakePlayer))
                HANDLER.sendTo(msg, ((ServerPlayerEntity) player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static void sendToServer(Object msg) {
        HANDLER.sendToServer(msg);
    }

    private static <MSG> void registerMessage(Class<MSG> messageType, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        HANDLER.registerMessage(index, messageType, encoder, decoder, messageConsumer);
        index++;
        if (index > 0xFF)
            throw new RuntimeException("Too many messages!");
    }
}
