package com.direwolf20.mininggadgets.common.network;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.network.data.*;
import com.direwolf20.mininggadgets.common.network.handler.*;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;


public class PacketHandler {
    public static void registerNetworking(final RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(MiningGadgets.MOD_ID);

        // Server side
        registrar.play(ExtractUpgradePayload.ID, ExtractUpgradePayload::new, handler -> handler.server(PacketExtractUpgrade.get()::handle));
        registrar.play(UpdateUpgradePayload.ID, UpdateUpgradePayload::new, handler -> handler.server(PacketUpdateUpgrade.get()::handle));
        registrar.play(ChangeMiningSizePayload.ID, ChangeMiningSizePayload::new, handler -> handler.server(PacketChangeMiningSize.get()::handle));
        registrar.play(ChangeMiningSizeModePayload.ID, ChangeMiningSizeModePayload::new, handler -> handler.server(PacketChangeMiningSizeMode.get()::handle));
        registrar.play(ChangeRangePayload.ID, ChangeRangePayload::new, handler -> handler.server(PacketChangeRange.get()::handle));
        registrar.play(ChangeBreakTypePayload.ID, ChangeBreakTypePayload::new, handler -> handler.server(PacketChangeBreakType.get()::handle));
        registrar.play(ChangeColorPayload.ID, ChangeColorPayload::new, handler -> handler.server(PacketChangeColor.get()::handle));
        registrar.play(GhostSlotPayload.ID, GhostSlotPayload::new, handler -> handler.server(PacketGhostSlot.get()::handle));
        registrar.play(OpenFilterContainerPayload.ID, OpenFilterContainerPayload::new, handler -> handler.server(PacketOpenFilterContainer.get()::handle));
        registrar.play(ToggleFiltersPayload.ID, ToggleFiltersPayload::new, handler -> handler.server(PacketToggleFilters.get()::handle));
        registrar.play(TogglePrecisionPayload.ID, TogglePrecisionPayload::new, handler -> handler.server(PacketTogglePrecision.get()::handle));
        registrar.play(ChangeVolumePayload.ID, ChangeVolumePayload::new, handler -> handler.server(PacketChangeVolume.get()::handle));
        registrar.play(ChangeFreezeDelayPayload.ID, ChangeFreezeDelayPayload::new, handler -> handler.server(PacketChangeFreezeDelay.get()::handle));
        registrar.play(InsertUpgradePayload.ID, InsertUpgradePayload::new, handler -> handler.server(PacketInsertUpgrade.get()::handle));

        //Client Side
        registrar.play(DurabilitySyncPayload.ID, DurabilitySyncPayload::new, handler -> handler.client(PacketDurabilitySync.get()::handle));

    }
}
