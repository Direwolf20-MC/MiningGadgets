package com.direwolf20.mininggadgets.common.network;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.network.data.*;
import com.direwolf20.mininggadgets.common.network.handler.*;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;


public class PacketHandler {
    public static void registerNetworking(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MiningGadgets.MOD_ID);

        // Server side
        registrar.playToServer(ExtractUpgradePayload.TYPE, ExtractUpgradePayload.STREAM_CODEC, PacketExtractUpgrade.get()::handle);
        registrar.playToServer(UpdateUpgradePayload.TYPE, UpdateUpgradePayload.STREAM_CODEC, PacketUpdateUpgrade.get()::handle);
        registrar.playToServer(ChangeMiningSizePayload.TYPE, ChangeMiningSizePayload.STREAM_CODEC, PacketChangeMiningSize.get()::handle);
        registrar.playToServer(ChangeMiningSizeModePayload.TYPE, ChangeMiningSizeModePayload.STREAM_CODEC, PacketChangeMiningSizeMode.get()::handle);
        registrar.playToServer(ChangeRangePayload.TYPE, ChangeRangePayload.STREAM_CODEC, PacketChangeRange.get()::handle);
        registrar.playToServer(ChangeBreakTypePayload.TYPE, ChangeBreakTypePayload.STREAM_CODEC, PacketChangeBreakType.get()::handle);
        registrar.playToServer(ChangeColorPayload.TYPE, ChangeColorPayload.STREAM_CODEC, PacketChangeColor.get()::handle);
        registrar.playToServer(GhostSlotPayload.TYPE, GhostSlotPayload.STREAM_CODEC, PacketGhostSlot.get()::handle);
        registrar.playToServer(OpenFilterContainerPayload.TYPE, OpenFilterContainerPayload.STREAM_CODEC, PacketOpenFilterContainer.get()::handle);
        registrar.playToServer(ToggleFiltersPayload.TYPE, ToggleFiltersPayload.STREAM_CODEC, PacketToggleFilters.get()::handle);
        registrar.playToServer(TogglePrecisionPayload.TYPE, TogglePrecisionPayload.STREAM_CODEC, PacketTogglePrecision.get()::handle);
        registrar.playToServer(ChangeVolumePayload.TYPE, ChangeVolumePayload.STREAM_CODEC, PacketChangeVolume.get()::handle);
        registrar.playToServer(ChangeFreezeDelayPayload.TYPE, ChangeFreezeDelayPayload.STREAM_CODEC, PacketChangeFreezeDelay.get()::handle);
        registrar.playToServer(InsertUpgradePayload.TYPE, InsertUpgradePayload.STREAM_CODEC, PacketInsertUpgrade.get()::handle);

        //Client Side
        registrar.playToClient(DurabilitySyncPayload.TYPE, DurabilitySyncPayload.STREAM_CODEC, PacketDurabilitySync.get()::handle);

    }
}
