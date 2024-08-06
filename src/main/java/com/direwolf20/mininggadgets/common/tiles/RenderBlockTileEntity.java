package com.direwolf20.mininggadgets.common.tiles;

import com.direwolf20.mininggadgets.client.particles.laserparticle.LaserParticleData;
import com.direwolf20.mininggadgets.common.events.ServerTickHandler;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.util.SpecialBlockActions;
import com.direwolf20.mininggadgets.setup.Config;
import com.direwolf20.mininggadgets.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.*;

public class RenderBlockTileEntity extends BlockEntity {
    private final Random rand = new Random();
    private BlockState renderBlock;
    private int priorDurability = 9999;
    private int clientPrevDurability;
    private int clientDurability;
    private int durability;
    private UUID playerUUID;
    private int originalDurability;
    private int ticksSinceMine = 0;
    private List<Upgrade> gadgetUpgrades = new ArrayList<>();
    private List<ItemStack> gadgetFilters;
    private boolean gadgetIsWhitelist;
    private boolean packetReceived = false;
    private int totalAge;
    private MiningProperties.BreakTypes breakType;
    private boolean blockAllowed;

    public RenderBlockTileEntity(BlockPos pos, BlockState state) {
        super(Registration.RENDERBLOCK_TILE.get(), pos, state);
    }

    public static boolean blockAllowed(List<ItemStack> drops, List<ItemStack> filters, boolean isWhiteList) {
        boolean blockAllowed = false;
        for (ItemStack dropStack : drops) {
            if (filters.size() == 0) {
                return true;
            }

            boolean contains = false;
            for (ItemStack filter : filters) {

                if (ItemStack.matches(dropStack, filter)) {
                    contains = true;
                    break;
                }
            }

            blockAllowed = (isWhiteList && contains) || (!isWhiteList && !contains);

            if (blockAllowed) {
                break;
            }
        }

        return blockAllowed;
    }

    public static <T extends BlockEntity> void ticker(Level level, BlockPos blockPos, BlockState state, T tile) {
        if (!(tile instanceof RenderBlockTileEntity)) {
            return;
        }

        RenderBlockTileEntity entity = ((RenderBlockTileEntity) tile);
        entity.totalAge++;

        //Client and server - spawn a 'block break' particle if the player is actively mining
        if (entity.ticksSinceMine == 0) {
            entity.spawnParticle();
        }
        //Client only
        if (entity.level.isClientSide) {
            //Update ticks since last mine on client side for particle renders
            if (entity.playerUUID != null) {
                if (entity.getPlayer() != null && !entity.getPlayer().isUsingItem()) {
                    entity.ticksSinceMine++;
                } else {
                    entity.ticksSinceMine = 0;
                }
            }
            //The packet with new durability arrives between ticks. Update it on tick.
            if (entity.packetReceived) {
                //System.out.println("PreChange: " + entity.durability + ":" + entity.priorDurability);
                entity.priorDurability = entity.durability;
                entity.durability = entity.clientDurability;
                //System.out.println("PostChange: " + entity.durability + ":" + entity.priorDurability);
                entity.packetReceived = false;
            } else {
                if (entity.durability != 0) {
                    entity.priorDurability = entity.durability;
                }

            }


        }
        //Server Only
        if (!entity.level.isClientSide) {
            if (entity.ticksSinceMine == 1) {
                //Immediately after player stops mining, stability the shrinking effects and notify players
                entity.priorDurability = entity.durability;
                ServerTickHandler.addToList(blockPos, entity.durability, level);
            }
            if (entity.ticksSinceMine >= 10) {
                //After half a second, start 'regrowing' blocks that haven't been mined.
                entity.priorDurability = entity.durability;
                entity.durability++;
                ServerTickHandler.addToList(blockPos, entity.durability, level);
            }
            if (entity.durability >= entity.originalDurability) {
                //Once we reach the original durability value set the block back to its original blockstate.
                entity.resetBlock();
            }
            entity.ticksSinceMine++;
        }
    }

    public BlockState getRenderBlock() {
        if (this.renderBlock == null)
            return Blocks.COBBLESTONE.defaultBlockState();
        return this.renderBlock;
    }

    public void setRenderBlock(BlockState state) {
        this.renderBlock = state;
    }

    public MiningProperties.BreakTypes getBreakType() {
        return this.breakType;
    }

    public void setBreakType(MiningProperties.BreakTypes breakType) {
        this.breakType = breakType;
    }

    public void justSetDurability(int durability) {
        this.priorDurability = this.durability;
        this.durability = durability;
        //System.out.println("Got:"+ " Prior: " + priorDurability + " Dur: " + durability);
    }

    public void setDurability(int dur, ItemStack stack) {
        this.ticksSinceMine = 0;
        if (this.durability != 0) {
            this.priorDurability = this.durability;
        }
        this.durability = dur;
        if (dur <= 0) {
            this.removeBlock();
            if (UpgradeTools.containsActiveUpgradeFromList(this.gadgetUpgrades, Upgrade.FREEZING)) {
                this.freeze(stack);
            }
        }
        if (!(this.level.isClientSide)) {
            this.setChanged();
            ServerTickHandler.addToList(this.worldPosition, this.durability, this.level);
            //PacketHandler.sendToAll(new PacketDurabilitySync(pos, dur), world);
            //System.out.println("Sent: "+ " Prior: " + priorDurability + " Dur: " + dur);
        }
    }

    private void freeze(ItemStack stack) {
        int freezeCost = Config.UPGRADECOST_FREEZE.get() * -1;
        var cap = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (cap == null) return;
        int energy = cap.getEnergyStored();

        if (energy == 0) {
            return;
        }

        for (Direction side : Direction.values()) {
            BlockPos sidePos = this.worldPosition.relative(side);
            FluidState state = this.level.getFluidState(sidePos);

            if (state.getType().isSame(Fluids.LAVA) && state.getType().isSource(state)) {
                energy -= this.replaceBlockWithAlternative(this.level, sidePos, Blocks.OBSIDIAN.defaultBlockState(), stack, freezeCost, energy);
            } else if (state.getType().isSame(Fluids.WATER) && state.getType().isSource(state)) {
                energy -= this.replaceBlockWithAlternative(this.level, sidePos, Blocks.PACKED_ICE.defaultBlockState(), stack, freezeCost, energy);
            } else if ((state.getType().isSame(Fluids.WATER) || state.getType().isSame(Fluids.LAVA)) && !state.getType().isSource(state)) {
                energy -= this.replaceBlockWithAlternative(this.level, sidePos, Blocks.COBBLESTONE.defaultBlockState(), stack, freezeCost, energy);
            }
        }
    }

    private int replaceBlockWithAlternative(Level world, BlockPos pos, BlockState state, ItemStack stack, int costOfOperation, int remainingEnergy) {
        if (remainingEnergy < costOfOperation) {
            return 0;
        }
        var cap = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (cap == null) return 0;
        cap.receiveEnergy(costOfOperation, false);

        // If the block is just water logged, remove the fluid
        BlockState blockState = world.getBlockState(pos);
        // Chests have a tile entity, and are then converted to ice below, we need them to lose waterlogged
        if (blockState.hasProperty(BlockStateProperties.WATERLOGGED) && blockState.getValue(BlockStateProperties.WATERLOGGED)) {
            world.setBlockAndUpdate(pos, blockState.setValue(BlockStateProperties.WATERLOGGED, false));
            return costOfOperation;
        }

        if (world.getBlockEntity(pos) == null)
        {
            world.setBlockAndUpdate(pos, state);
            return costOfOperation;
        }

        // Block is a block entity, we don't replace it, costs zero
        return 0;
    }

    public void spawnParticle() {
        if (this.renderBlock == null || this.renderBlock.isAir())
            return;
        if (UpgradeTools.containsActiveUpgradeFromList(this.gadgetUpgrades, Upgrade.MAGNET) && this.originalDurability > 0) {
            int PartCount = 20 / this.originalDurability;
            if (PartCount <= 1) {
                PartCount = 1;
            }
            for (int i = 0; i <= PartCount; i++) {
                double randomPartSize = 0.125 + this.rand.nextDouble() * 0.5;
                double randomX = this.rand.nextDouble();
                double randomY = this.rand.nextDouble();
                double randomZ = this.rand.nextDouble();

                LaserParticleData data = LaserParticleData.laserparticle(this.renderBlock, (float) randomPartSize, 200);
                this.getLevel().addParticle(data, this.getBlockPos().getX() + randomX, this.getBlockPos().getY() + randomY, this.getBlockPos().getZ() + randomZ, 0, 0.0f, 0);
            }
        }
    }

    public int getDurability() {
        return this.durability;
    }

    public int getOriginalDurability() {
        return this.originalDurability;
    }

    public void setOriginalDurability(int originalDurability) {
        this.originalDurability = originalDurability;
    }

    public Player getPlayer() {
        if (this.getLevel() == null) {
            return null;
        }

        return this.getLevel().getPlayerByUUID(this.playerUUID);
    }

    public void setPlayer(Player player) {
        this.playerUUID = player.getUUID();
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public int getTicksSinceMine() {
        return this.ticksSinceMine;
    }

    public void setTicksSinceMine(int ticksSinceMine) {
        this.ticksSinceMine = ticksSinceMine;
    }

    public int getPriorDurability() {
        return this.priorDurability;
    }

    public void setPriorDurability(int priorDurability) {
        this.priorDurability = priorDurability;
    }

    public int getClientDurability() {
        return this.clientDurability;
    }

    public void setClientDurability(int clientDurability) {
        if (this.durability == 0) {
            this.clientPrevDurability = clientDurability;
        } else {
            this.clientPrevDurability = this.durability;
        }
        this.clientDurability = clientDurability;
        this.packetReceived = true;
    }

    public List<Upgrade> getGadgetUpgrades() {
        return this.gadgetUpgrades;
    }

    public void setGadgetUpgrades(List<Upgrade> gadgetUpgrades) {
        this.gadgetUpgrades = gadgetUpgrades;
    }

    public List<ItemStack> getGadgetFilters() {
        return this.gadgetFilters;
    }

    public void setGadgetFilters(List<ItemStack> gadgetFilters) {
        this.gadgetFilters = gadgetFilters;
    }

    public boolean isGadgetIsWhitelist() {
        return this.gadgetIsWhitelist;
    }

    public void setGadgetIsWhitelist(boolean gadgetIsWhitelist) {
        this.gadgetIsWhitelist = gadgetIsWhitelist;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        // Vanilla uses the type parameter to indicate which type of tile entity (command block, skull, or beacon?) is receiving the packet, but it seems like Forge has overridden this behavior
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        this.loadAdditional(tag, lookupProvider);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, provider);
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
    }

    public void markDirtyClient() {
        this.setChanged();
        if (this.getLevel() != null) {
            BlockState state = this.getLevel().getBlockState(this.getBlockPos());
            this.getLevel().sendBlockUpdated(this.getBlockPos(), state, state, 3);
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.renderBlock = NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), tag.getCompound("renderBlock"));
        this.originalDurability = tag.getInt("originalDurability");
        this.priorDurability = tag.getInt("priorDurability");
        this.durability = tag.getInt("durability");
        this.ticksSinceMine = tag.getInt("ticksSinceMine");
        if (tag.contains("playerUUID")) {
            this.playerUUID = tag.getUUID("playerUUID");
        }
        this.gadgetUpgrades = UpgradeTools.getUpgradesFromTag(tag);
        this.breakType = MiningProperties.BreakTypes.values()[tag.getByte("breakType")];
        this.gadgetFilters = MiningProperties.deserializeItemStackList(tag.getCompound("gadgetFilters"), provider);
        this.gadgetIsWhitelist = tag.getBoolean("gadgetIsWhitelist");
        this.blockAllowed = tag.getBoolean("blockAllowed");
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (this.renderBlock != null) {
            tag.put("renderBlock", NbtUtils.writeBlockState(this.renderBlock));
        }
        tag.putInt("originalDurability", this.originalDurability);
        tag.putInt("priorDurability", this.priorDurability);
        tag.putInt("durability", this.durability);
        tag.putInt("ticksSinceMine", this.ticksSinceMine);
        if (this.playerUUID != null) {
            tag.putUUID("playerUUID", this.playerUUID);
        }
        tag.put("upgrades", UpgradeTools.setUpgradesNBT(this.gadgetUpgrades).getList("upgrades", Tag.TAG_COMPOUND));
        tag.putByte("breakType", (byte) this.breakType.ordinal());
        tag.put("gadgetFilters", MiningProperties.serializeItemStackList(this.getGadgetFilters(), provider));
        tag.putBoolean("gadgetIsWhitelist", this.isGadgetIsWhitelist());
        tag.putBoolean("blockAllowed", this.blockAllowed);
    }

    private void removeBlock() {
        if (this.level == null || this.level.isClientSide || this.playerUUID == null) {
            return;
        }

        Player player = this.level.getPlayerByUUID(this.playerUUID);
        if (player == null) {
            return;
        }

        int silk = 0;
        int fortune = 0;

        ItemStack tempTool = new ItemStack(Registration.MININGGADGET.get());

        // If silk is in the upgrades, apply it without a tier.
        if (UpgradeTools.containsActiveUpgradeFromList(this.gadgetUpgrades, Upgrade.SILK)) {
            HolderLookup.RegistryLookup<Enchantment> registrylookup = level.getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            tempTool.enchant(registrylookup.getOrThrow(Enchantments.SILK_TOUCH), 1);
            silk = 1;
        }

        // FORTUNE_1 is eval'd against the basename so this'll support all fortune upgrades
        if (UpgradeTools.containsActiveUpgradeFromList(this.gadgetUpgrades, Upgrade.FORTUNE_1)) {
            Optional<Upgrade> upgrade = UpgradeTools.getUpgradeFromList(this.gadgetUpgrades, Upgrade.FORTUNE_1);
            if (upgrade.isPresent()) {
                fortune = upgrade.get().getTier();
                HolderLookup.RegistryLookup<Enchantment> registrylookup = level.getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
                tempTool.enchant(registrylookup.getOrThrow(Enchantments.FORTUNE), fortune);
            }
        }

        // Fire an event for other mods that we've just broken the block
        BlockEvent.BreakEvent breakEvent = fixForgeEventBreakBlock(this.renderBlock, player, level, worldPosition, tempTool);
        NeoForge.EVENT_BUS.post(breakEvent);
        // Someone cancelled out break event
        if (breakEvent.isCanceled()) {
            return;
        }


        List<ItemStack> drops = Block.getDrops(this.renderBlock, (ServerLevel) this.level, this.worldPosition, null, player, tempTool);

        if (this.blockAllowed) {
            int exp = this.renderBlock.getExpDrop(this.level, this.worldPosition, null, player, tempTool);
            boolean magnetMode = (UpgradeTools.containsActiveUpgradeFromList(this.gadgetUpgrades, Upgrade.MAGNET));
            for (ItemStack drop : drops) {
                if (drop != null) {
                    if (magnetMode) {
                        ItemEntityPickupEvent.Pre wasPickedUp = EventHooks.fireItemPickupPre(new ItemEntity(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), drop), player);
                        if (wasPickedUp.canPickup() == TriState.DEFAULT) {
                            if (!player.addItem(drop)) {
                                Block.popResource(this.level, this.worldPosition, drop);
                            }
                        }
                    } else {
                        Block.popResource(this.level, this.worldPosition, drop);
                    }
                }
            }
            if (magnetMode) {
                if (exp > 0) {
                    player.giveExperiencePoints(exp);
                }
            } else {
                if (exp > 0) {
                    this.renderBlock.getBlock().popExperience((ServerLevel) this.level, this.worldPosition, exp);
                }
            }

            this.renderBlock.spawnAfterBreak((ServerLevel) this.level, this.worldPosition, tempTool, false); // Fixes silver fish basically...
        }

        //        BlockState underState = world.getBlockState(this.pos.down());
        this.level.removeBlockEntity(this.worldPosition);
        this.level.setBlockAndUpdate(this.worldPosition, Blocks.AIR.defaultBlockState());

        //        if (UpgradeTools.containsActiveUpgradeFromList(gadgetUpgrades, Upgrade.PAVER)) {
        //            if (this.pos.getY() <= player.getPosY() && underState == Blocks.AIR.getDefaultState()) {
        //                world.setBlockState(this.pos.down(), Blocks.COBBLESTONE.getDefaultState());
        //            }
        //        }

        // Add to the break stats
        player.awardStat(Stats.BLOCK_MINED.get(this.renderBlock.getBlock()));

        // Handle special cases
        if (SpecialBlockActions.getRegister().containsKey(this.renderBlock.getBlock())) {
            SpecialBlockActions.getRegister().get(this.renderBlock.getBlock()).accept(this.level, this.worldPosition, this.renderBlock);
        }
    }

    private static BlockEvent.BreakEvent fixForgeEventBreakBlock(BlockState state, Player player, Level world, BlockPos pos, ItemStack tool) {
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, state, player);
        return event;
    }

    private void resetBlock() {
        if (this.level == null) {
            return;
        }

        if (!this.level.isClientSide) {
            this.level.setBlockAndUpdate(this.worldPosition, Objects.requireNonNullElseGet(this.renderBlock, Blocks.AIR::defaultBlockState));
        }
    }

    public void setBlockAllowed() {
        if (!UpgradeTools.containsActiveUpgradeFromList(this.gadgetUpgrades, Upgrade.VOID_JUNK)) {
            this.blockAllowed = true;
            return;
        }
        Player player = this.level.getPlayerByUUID(this.playerUUID);
        if (player == null) {
            return;
        }
        int silk = 0;
        int fortune = 0;

        ItemStack tempTool = new ItemStack(Registration.MININGGADGET.get());

        // If silk is in the upgrades, apply it without a tier.
        if (UpgradeTools.containsActiveUpgradeFromList(this.gadgetUpgrades, Upgrade.SILK)) {
            HolderLookup.RegistryLookup<Enchantment> registrylookup = level.getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            tempTool.enchant(registrylookup.getOrThrow(Enchantments.SILK_TOUCH), 1);
            silk = 1;
        }

        // FORTUNE_1 is eval'd against the basename so this'll support all fortune upgrades
        if (UpgradeTools.containsActiveUpgradeFromList(this.gadgetUpgrades, Upgrade.FORTUNE_1)) {
            Optional<Upgrade> upgrade = UpgradeTools.getUpgradeFromList(this.gadgetUpgrades, Upgrade.FORTUNE_1);
            if (upgrade.isPresent()) {
                fortune = upgrade.get().getTier();
                HolderLookup.RegistryLookup<Enchantment> registrylookup = level.getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
                tempTool.enchant(registrylookup.getOrThrow(Enchantments.FORTUNE), fortune);
            }
        }

        List<ItemStack> drops = Block.getDrops(this.renderBlock, (ServerLevel) this.level, this.worldPosition, null, player, tempTool);

        this.blockAllowed = blockAllowed(drops, this.getGadgetFilters(), this.isGadgetIsWhitelist());
    }

    public boolean getBlockAllowed() {
        return this.blockAllowed;
    }

}
