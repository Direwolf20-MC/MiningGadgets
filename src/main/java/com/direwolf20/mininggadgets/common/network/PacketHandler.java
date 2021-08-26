package com.direwolf20.mininggadgets.common.network;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.network.packets.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

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
        int id = 0;

        // Server side
        HANDLER.registerMessage(id++, PacketExtractUpgrade.class,     PacketExtractUpgrade::encode,       PacketExtractUpgrade::decode,       PacketExtractUpgrade.Handler::handle);
        HANDLER.registerMessage(id++, PacketUpdateUpgrade.class,      PacketUpdateUpgrade::encode,        PacketUpdateUpgrade::decode,        PacketUpdateUpgrade.Handler::handle);
        HANDLER.registerMessage(id++, PacketChangeMiningSize.class,   PacketChangeMiningSize::encode,     PacketChangeMiningSize::decode,     PacketChangeMiningSize.Handler::handle);
        HANDLER.registerMessage(id++, PacketChangeRange.class,        PacketChangeRange::encode,          PacketChangeRange::decode,          PacketChangeRange.Handler::handle);
        HANDLER.registerMessage(id++, PacketChangeBreakType.class,    PacketChangeBreakType::encode,      PacketChangeBreakType::decode,      PacketChangeBreakType.Handler::handle);
        HANDLER.registerMessage(id++, PacketChangeColor.class,        PacketChangeColor::encode,          PacketChangeColor::decode,          PacketChangeColor.Handler::handle);
        HANDLER.registerMessage(id++, PacketGhostSlot.class,          PacketGhostSlot::encode,            PacketGhostSlot::decode,            PacketGhostSlot.Handler::handle);
        HANDLER.registerMessage(id++, PacketOpenFilterContainer.class,PacketOpenFilterContainer::encode,  PacketOpenFilterContainer::decode,  PacketOpenFilterContainer.Handler::handle);
        HANDLER.registerMessage(id++, PacketToggleFilters.class,      PacketToggleFilters::encode,        PacketToggleFilters::decode,        PacketToggleFilters.Handler::handle);
        HANDLER.registerMessage(id++, PacketTogglePrecision.class,    PacketTogglePrecision::encode,      PacketTogglePrecision::decode,      PacketTogglePrecision.Handler::handle);
        HANDLER.registerMessage(id++, PacketChangeVolume.class,       PacketChangeVolume::encode,         PacketChangeVolume::decode,         PacketChangeVolume.Handler::handle);
        HANDLER.registerMessage(id++, PacketChangeFreezeDelay.class,  PacketChangeFreezeDelay::encode,    PacketChangeFreezeDelay::decode,    PacketChangeFreezeDelay.Handler::handle);

        //Client Side
        HANDLER.registerMessage(id++, PacketDurabilitySync.class,     PacketDurabilitySync::encode,       PacketDurabilitySync::decode,       PacketDurabilitySync.Handler::handle);
        HANDLER.registerMessage(id++, PacketInsertUpgrade.class,      PacketInsertUpgrade::encode,        PacketInsertUpgrade::decode,        PacketInsertUpgrade::handler);
    }

    public static void sendTo(Object msg, ServerPlayer player) {
        if (!(player instanceof FakePlayer))
            HANDLER.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToAll(Object msg, Level world) {
        //Todo Maybe only send to nearby players?
        for (Player player : world.players()) {
            if (!(player instanceof FakePlayer))
                HANDLER.sendTo(msg, ((ServerPlayer) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static void sendToServer(Object msg) {
        HANDLER.sendToServer(msg);
    }
}
