package com.direwolf20.mininggadgets.common.util;

import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

/**
 * Shared "the gadget finished mining this block" logic, used by the render block and by mod integrations
 * (e.g. FTB Ultimine) so every block the gadget breaks honours its upgrades the same way.
 */
public final class GadgetHarvest {
    private GadgetHarvest() {}

    /**
     * Builds the fake tool used for loot: a gadget enchanted with silk touch / fortune according to the upgrades.
     */
    public static ItemStack createTempTool(Level level, List<Upgrade> upgrades) {
        ItemStack tempTool = new ItemStack(Registration.MININGGADGET.get());

        // If silk is in the upgrades, apply it without a tier.
        if (UpgradeTools.containsActiveUpgradeFromList(upgrades, Upgrade.SILK)) {
            HolderLookup.RegistryLookup<Enchantment> registrylookup = level.getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            tempTool.enchant(registrylookup.getOrThrow(Enchantments.SILK_TOUCH), 1);
        }

        // FORTUNE_1 is eval'd against the basename so this'll support all fortune upgrades
        if (UpgradeTools.containsActiveUpgradeFromList(upgrades, Upgrade.FORTUNE_1)) {
            Optional<Upgrade> upgrade = UpgradeTools.getUpgradeFromList(upgrades, Upgrade.FORTUNE_1);
            if (upgrade.isPresent()) {
                HolderLookup.RegistryLookup<Enchantment> registrylookup = level.getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
                tempTool.enchant(registrylookup.getOrThrow(Enchantments.FORTUNE), upgrade.get().getTier());
            }
        }

        return tempTool;
    }

    /**
     * Drops the loot / xp for {@code state} (unless {@code blockAllowed} is false, i.e. voided by the filter),
     * then removes the block and runs stats and special block actions.
     */
    public static void harvest(ServerLevel level, BlockPos pos, BlockState state, Player player, List<Upgrade> upgrades, ItemStack tempTool, boolean blockAllowed) {
        harvest(level, pos, state, player, upgrades, tempTool, blockAllowed, false);
    }

    /**
     * @param deferMagnetPickup collect magnet drops on the next tick instead of right away. Needed while another mod
     *                          intercepts spawned item entities (FTB Ultimine gathers all drops of a run and pops them at
     *                          the origin block, which would bypass the magnet).
     */
    public static void harvest(ServerLevel level, BlockPos pos, BlockState state, Player player, List<Upgrade> upgrades, ItemStack tempTool, boolean blockAllowed, boolean deferMagnetPickup) {
        List<ItemStack> drops = Block.getDrops(state, level, pos, null, player, tempTool);

        if (blockAllowed) {
            int exp = state.getExpDrop(level, pos, null, player, tempTool);
            boolean magnetMode = (UpgradeTools.containsActiveUpgradeFromList(upgrades, Upgrade.MAGNET));
            if (magnetMode) {
                List<ItemStack> toPickUp = drops.stream().filter(d -> d != null && !d.isEmpty()).toList();
                Runnable collect = () -> {
                    toPickUp.forEach(d -> pickUp(level, pos, d, player));
                    pickUpXp(level, pos, exp, player);
                };
                if (deferMagnetPickup) {
                    level.getServer().schedule(level.getServer().wrapRunnable(collect));
                } else {
                    collect.run();
                }
            } else {
                for (ItemStack drop : drops) {
                    if (drop != null) {
                        Block.popResource(level, pos, drop);
                    }
                }
                if (exp > 0) {
                    state.getBlock().popExperience(level, pos, exp);
                }
            }

            state.spawnAfterBreak(level, pos, tempTool, false); // Fixes silver fish basically...
        }

        level.removeBlockEntity(pos);
        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());

        // Add to the break stats
        player.awardStat(Stats.BLOCK_MINED.get(state.getBlock()));

        // Handle special cases
        if (SpecialBlockActions.getRegister().containsKey(state.getBlock())) {
            SpecialBlockActions.getRegister().get(state.getBlock()).accept(level, pos, state);
        }
    }

    /**
     * Magnet pickup done the vanilla way: spawn the item and let the player touch it straight away. Unlike adding it to
     * the inventory directly, this fires the item pickup events (Pre and Post, used by quest/stat mods), plays the
     * pickup animation and sound, and sends the take-item packet that pickup notification mods listen for. Whatever does
     * not fit stays on the ground as a normal item entity.
     */
    public static void pickUp(ServerLevel level, BlockPos pos, ItemStack drop, Player player) {
        if (player.isRemoved()) {
            Block.popResource(level, pos, drop);
            return;
        }
        ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop, 0, 0, 0);
        entity.setNoPickUpDelay();
        if (level.addFreshEntity(entity) && entity.isAlive()) {
            entity.playerTouch(player);
        }
    }

    /**
     * Magnet XP done the vanilla way: experience orbs at the player's feet, collected on the next player tick. This keeps
     * the XP pickup event, sound, Mending repair and pickup notifications that giving the points directly skipped.
     */
    public static void pickUpXp(ServerLevel level, BlockPos pos, int exp, Player player) {
        if (exp <= 0) {
            return;
        }
        ExperienceOrb.award(level, player.isRemoved() ? Vec3.atCenterOf(pos) : player.position(), exp);
    }
}
