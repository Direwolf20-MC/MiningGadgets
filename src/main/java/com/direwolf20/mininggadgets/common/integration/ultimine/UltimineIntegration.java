package com.direwolf20.mininggadgets.common.integration.ultimine;

import com.direwolf20.mininggadgets.common.blocks.RenderBlock;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import com.direwolf20.mininggadgets.common.util.GadgetHarvest;
import dev.ftb.mods.ftbultimine.FTBUltimine;
import dev.ftb.mods.ftbultimine.api.blockbreaking.BlockBreakHandler;
import dev.ftb.mods.ftbultimine.api.neoforge.FTBUltimineEvent;
import dev.ftb.mods.ftbultimine.api.shape.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.List;

/**
 * FTB Ultimine support.
 * <p>
 * Ultimine reacts to every {@link BreakBlockEvent}, cancels it, and breaks the selected blocks the vanilla way with the
 * held item as the tool. With a gadget that went wrong in three ways:
 * <ul>
 *     <li>the gadget's permission check ({@link MiningGadget#canMineBlock}) posts a break event before the laser even
 *     starts, so Ultimine fired immediately;</li>
 *     <li>the cancelled "finished" event made the render block skip its drops;</li>
 *     <li>the gadget is not a valid harvest tool and render blocks have no loot, so every ultimined block was voided.</li>
 * </ul>
 * Now Ultimine may only run from the "laser finished a block" event, and every block it breaks goes through
 * {@link GadgetHarvest}, so upgrades (silk, fortune, magnet, void filter) and energy cost apply as usual.
 */
final class UltimineIntegration {
    /** Player whose gadget is currently posting a "block finished" event (server thread only). */
    private static Player completingPlayer = null;

    private UltimineIntegration() {}

    static void init(IEventBus bus) {
        bus.addListener(FTBUltimineEvent.RegisterRestrictionHandler.class,
                event -> event.getEventData().register(UltimineIntegration::canUltimine));
        bus.addListener(FTBUltimineEvent.RegisterBlockBreakHandler.class,
                event -> event.getEventData().registerHandler(UltimineIntegration::breakBlock));
    }

    static void postGadgetBreak(BreakBlockEvent event, ServerLevel level, BlockPos pos, BlockState realState, Player player) {
        if (player instanceof ServerPlayer serverPlayer && FTBUltimine.getInstance().getOrCreatePlayerData(serverPlayer).isPressed()) {
            // Ultimine selects matching blocks from what is in the world, so put the real block back in place of the
            // render block (which is removed right after this event either way). If the break gets cancelled by
            // something else (claims...), the block is simply left intact.
            level.setBlock(pos, realState, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        }

        Player previous = completingPlayer;
        completingPlayer = player;
        try {
            NeoForge.EVENT_BUS.post(event);
        } finally {
            completingPlayer = previous;
        }
    }

    private static boolean canUltimine(Player player) {
        if (player.level().isClientSide() || MiningGadget.getGadget(player).isEmpty()) {
            return true;
        }
        // With a gadget in hand, only the "laser finished a block" event may start Ultimine.
        return completingPlayer == player;
    }

    private static BlockBreakHandler.Result breakBlock(Player player, BlockPos pos, BlockState state, Shape shape, BlockHitResult hitResult) {
        ItemStack gadget = MiningGadget.getGadget(player);
        if (gadget.isEmpty() || !(player.level() instanceof ServerLevel level)) {
            return BlockBreakHandler.Result.PASS;
        }

        BlockState inWorld = level.getBlockState(pos);
        boolean renderBlock = inWorld.getBlock() instanceof RenderBlock;
        BlockState realState;
        List<Upgrade> upgrades;
        List<ItemStack> filters;
        boolean whitelist;
        if (renderBlock) {
            // A block the laser is still working on: use what it was mined with.
            if (!(level.getBlockEntity(pos) instanceof RenderBlockTileEntity te) || te.getRenderBlock() == null) {
                return BlockBreakHandler.Result.FAIL;
            }
            realState = te.getRenderBlock();
            upgrades = te.getGadgetUpgrades();
            filters = te.getGadgetFilters();
            whitelist = te.isGadgetIsWhitelist();
        } else {
            // Gadgets never mine block entities (see MiningCollect).
            if (level.getBlockEntity(pos) != null) {
                return BlockBreakHandler.Result.FAIL;
            }
            realState = inWorld;
            upgrades = UpgradeTools.getUpgrades(gadget);
            filters = MiningProperties.getFiltersAsList(gadget);
            whitelist = MiningProperties.getWhiteList(gadget);
        }

        if (realState.isAir() || realState.getDestroySpeed(level, pos) < 0) {
            return BlockBreakHandler.Result.FAIL;
        }
        if (!renderBlock && !MiningGadget.canMineBlock(gadget, level, player, pos, realState)) {
            return BlockBreakHandler.Result.FAIL;
        }
        if (!useEnergy(gadget)) {
            return BlockBreakHandler.Result.FAIL;
        }

        ItemStack tempTool = GadgetHarvest.createTempTool(level, upgrades);
        boolean blockAllowed = !UpgradeTools.containsActiveUpgradeFromList(upgrades, Upgrade.VOID_JUNK)
                || RenderBlockTileEntity.blockAllowed(Block.getDrops(realState, level, pos, null, player, tempTool), filters, whitelist);

        level.levelEvent(2001, pos, Block.getId(realState)); // break particles + sound
        // Ultimine swallows item entities spawned during its run, so magnet pickups happen next tick.
        GadgetHarvest.harvest(level, pos, realState, player, upgrades, tempTool, blockAllowed, true);
        return BlockBreakHandler.Result.SUCCESS;
    }

    /** Charges one block's worth of energy, like the laser does for every block it finishes. */
    private static boolean useEnergy(ItemStack gadget) {
        int cost = MiningGadget.getEnergyCost(gadget);
        if (cost <= 0) {
            return true;
        }
        EnergyHandler energy = gadget.getCapability(Capabilities.Energy.ITEM, null);
        if (energy == null || energy.getAmountAsInt() < cost) {
            return false;
        }
        try (Transaction tx = Transaction.openRoot()) {
            energy.extract(cost, tx);
            tx.commit();
        }
        return true;
    }
}
