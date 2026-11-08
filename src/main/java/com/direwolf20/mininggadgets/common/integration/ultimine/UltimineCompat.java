package com.direwolf20.mininggadgets.common.integration.ultimine;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

/**
 * Safe entry point for the optional FTB Ultimine integration. This class never touches Ultimine classes itself;
 * {@link UltimineIntegration} is only loaded when Ultimine is installed.
 */
public final class UltimineCompat {
    private static final boolean LOADED = ModList.get().isLoaded("ftbultimine");

    private UltimineCompat() {}

    public static boolean isLoaded() {
        return LOADED;
    }

    public static void init() {
        if (LOADED) {
            UltimineIntegration.init(NeoForge.EVENT_BUS);
        }
    }

    /**
     * Posts the "render block finished" break event in a way FTB Ultimine can safely act on.
     * Only call when {@link #isLoaded()} is true.
     */
    public static void postGadgetBreak(BreakBlockEvent event, ServerLevel level, BlockPos pos, BlockState realState, Player player) {
        UltimineIntegration.postGadgetBreak(event, level, pos, realState, player);
    }
}
