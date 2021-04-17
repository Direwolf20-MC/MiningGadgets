package com.direwolf20.mininggadgets.common.tiles;

import static com.direwolf20.mininggadgets.common.blocks.ModBlocks.RENDERBLOCK_TILE;

import com.direwolf20.mininggadgets.client.particles.laserparticle.LaserParticleData;
import com.direwolf20.mininggadgets.common.Config;
import com.direwolf20.mininggadgets.common.events.ServerTickHandler;
import com.direwolf20.mininggadgets.common.items.ModItems;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.util.SpecialBlockActions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class RenderBlockTileEntity extends TileEntity implements ITickableTileEntity {
    private final Random rand = new Random();
    private BlockState renderBlock;
    private int priorDurability = 9999;
    private int clientPrevDurability;
    private int clientDurability;
    private int durability;
    private UUID playerUUID;
    private int originalDurability;
    private int ticksSinceMine = 0;
    private List<Upgrade> gadgetUpgrades;
    private List<ItemStack> gadgetFilters;
    private boolean gadgetIsWhitelist;
    private boolean packetReceived = false;
    private int totalAge;
    private MiningProperties.BreakTypes breakType;
    private boolean blockAllowed;

    public RenderBlockTileEntity() {
        super(RENDERBLOCK_TILE.get());
    }

    public static boolean blockAllowed(List<ItemStack> drops, List<ItemStack> filters, boolean isWhiteList) {
        boolean blockAllowed = false;
        for (ItemStack dropStack : drops) {
            if (filters.size() == 0) {
                return true;
            }

            boolean contains = false;
            for (ItemStack filter : filters) {
                if (dropStack.sameItem(filter)) {
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

    public BlockState getRenderBlock() {
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
        int energy = stack.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);

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

    private int replaceBlockWithAlternative(World world, BlockPos pos, BlockState state, ItemStack stack, int costOfOperation, int remainingEnergy) {
        if (remainingEnergy < costOfOperation) {
            return 0;
        }

        stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> e.receiveEnergy(costOfOperation, false));

        // If the block is just water logged, remove the fluid
        BlockState blockState = world.getBlockState(pos);
        if (blockState.hasProperty(BlockStateProperties.WATERLOGGED) && blockState.getValue(BlockStateProperties.WATERLOGGED) && world.getBlockEntity(pos) == null) {
            world.setBlockAndUpdate(pos, blockState.setValue(BlockStateProperties.WATERLOGGED, false));
            return costOfOperation;
        }

        world.setBlockAndUpdate(pos, state);
        return costOfOperation;
    }

    public void spawnParticle() {
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

                LaserParticleData data = LaserParticleData.laserparticle(this.renderBlock, (float) randomPartSize, 1f, 1f, 1f, 200);
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

    public PlayerEntity getPlayer() {
        if (this.getLevel() == null) {
            return null;
        }

        return this.getLevel().getPlayerByUUID(this.playerUUID);
    }

    public void setPlayer(PlayerEntity player) {
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
    public SUpdateTileEntityPacket getUpdatePacket() {
        // Vanilla uses the type parameter to indicate which type of tile entity (command block, skull, or beacon?) is receiving the packet, but it seems like Forge has overridden this behavior
        return new SUpdateTileEntityPacket(this.worldPosition, 0, this.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        this.load(state, tag);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.load(this.getBlockState(), pkt.getTag());
    }

    public void markDirtyClient() {
        this.setChanged();
        if (this.getLevel() != null) {
            BlockState state = this.getLevel().getBlockState(this.getBlockPos());
            this.getLevel().sendBlockUpdated(this.getBlockPos(), state, state, 3);
        }
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        this.renderBlock = NBTUtil.readBlockState(tag.getCompound("renderBlock"));
        this.originalDurability = tag.getInt("originalDurability");
        this.priorDurability = tag.getInt("priorDurability");
        this.durability = tag.getInt("durability");
        this.ticksSinceMine = tag.getInt("ticksSinceMine");
        this.playerUUID = tag.getUUID("playerUUID");
        this.gadgetUpgrades = UpgradeTools.getUpgradesFromTag(tag);
        this.breakType = MiningProperties.BreakTypes.values()[tag.getByte("breakType")];
        this.gadgetFilters = MiningProperties.deserializeItemStackList(tag.getCompound("gadgetFilters"));
        this.gadgetIsWhitelist = tag.getBoolean("gadgetIsWhitelist");
        this.blockAllowed = tag.getBoolean("blockAllowed");
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        if (this.renderBlock != null) {
            tag.put("renderBlock", NBTUtil.writeBlockState(this.renderBlock));
        }
        tag.putInt("originalDurability", this.originalDurability);
        tag.putInt("priorDurability", this.priorDurability);
        tag.putInt("durability", this.durability);
        tag.putInt("ticksSinceMine", this.ticksSinceMine);
        if (this.playerUUID != null) {
            tag.putUUID("playerUUID", this.playerUUID);
        }
        tag.put("upgrades", UpgradeTools.setUpgradesNBT(this.gadgetUpgrades).getList("upgrades", Constants.NBT.TAG_COMPOUND));
        tag.putByte("breakType", (byte) this.breakType.ordinal());
        tag.put("gadgetFilters", MiningProperties.serializeItemStackList(this.getGadgetFilters()));
        tag.putBoolean("gadgetIsWhitelist", this.isGadgetIsWhitelist());
        tag.putBoolean("blockAllowed", this.blockAllowed);
        return super.save(tag);
    }

    private void removeBlock() {
        if (this.level == null || this.level.isClientSide || this.playerUUID == null) {
            return;
        }

        PlayerEntity player = this.level.getPlayerByUUID(this.playerUUID);
        if (player == null) {
            return;
        }

        int silk = 0;
        int fortune = 0;

        ItemStack tempTool = new ItemStack(ModItems.MININGGADGET.get());

        // If silk is in the upgrades, apply it without a tier.
        if (UpgradeTools.containsActiveUpgradeFromList(this.gadgetUpgrades, Upgrade.SILK)) {
            tempTool.enchant(Enchantments.SILK_TOUCH, 1);
            silk = 1;
        }

        // FORTUNE_1 is eval'd against the basename so this'll support all fortune upgrades
        if (UpgradeTools.containsActiveUpgradeFromList(this.gadgetUpgrades, Upgrade.FORTUNE_1)) {
            Optional<Upgrade> upgrade = UpgradeTools.getUpgradeFromList(this.gadgetUpgrades, Upgrade.FORTUNE_1);
            if (upgrade.isPresent()) {
                fortune = upgrade.get().getTier();
                tempTool.enchant(Enchantments.BLOCK_FORTUNE, fortune);
            }
        }

        List<ItemStack> drops = Block.getDrops(this.renderBlock, (ServerWorld) this.level, this.worldPosition, null, player, tempTool);

        if (this.blockAllowed) {
            int exp = this.renderBlock.getExpDrop(this.level, this.worldPosition, fortune, silk);
            boolean magnetMode = (UpgradeTools.containsActiveUpgradeFromList(this.gadgetUpgrades, Upgrade.MAGNET));
            for (ItemStack drop : drops) {
                if (drop != null) {
                    if (magnetMode) {
                        int wasPickedUp = ForgeEventFactory.onItemPickup(new ItemEntity(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), drop), player);
                        // 1  = someone allowed the event meaning it's handled,
                        // -1 = someone blocked the event and thus we shouldn't drop it nor insert it
                        // 0  = no body captured the event and we should handle it by hand.
                        if (wasPickedUp == 0) {
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
                    this.renderBlock.getBlock().popExperience((ServerWorld) this.level, this.worldPosition, exp);
                }
            }

            this.renderBlock.spawnAfterBreak((ServerWorld) this.level, this.worldPosition, tempTool); // Fixes silver fish basically...
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

    private void resetBlock() {
        if (this.level == null) {
            return;
        }

        if (!this.level.isClientSide) {
            if (this.renderBlock != null) {
                this.level.setBlockAndUpdate(this.worldPosition, this.renderBlock);
            } else {
                this.level.setBlockAndUpdate(this.worldPosition, Blocks.AIR.defaultBlockState());
            }
        }
    }

    @Override
    public void tick() {
        this.totalAge++;
        //Client and server - spawn a 'block break' particle if the player is actively mining
        if (this.ticksSinceMine == 0) {
            this.spawnParticle();
        }
        //Client only
        if (this.level.isClientSide) {
            //Update ticks since last mine on client side for particle renders
            if (this.playerUUID != null) {
                if (this.getPlayer() != null && !this.getPlayer().isUsingItem()) {
                    this.ticksSinceMine++;
                } else {
                    this.ticksSinceMine = 0;
                }
            }
            //The packet with new durability arrives between ticks. Update it on tick.
            if (this.packetReceived) {
                //System.out.println("PreChange: " + this.durability + ":" + this.priorDurability);
                this.priorDurability = this.durability;
                this.durability = this.clientDurability;
                //System.out.println("PostChange: " + this.durability + ":" + this.priorDurability);
                this.packetReceived = false;
            } else {
                if (this.durability != 0) {
                    this.priorDurability = this.durability;
                }

            }


        }
        //Server Only
        if (!this.level.isClientSide) {
            if (this.ticksSinceMine == 1) {
                //Immediately after player stops mining, stability the shrinking effects and notify players
                this.priorDurability = this.durability;
                ServerTickHandler.addToList(this.worldPosition, this.durability, this.level);
            }
            if (this.ticksSinceMine >= 10) {
                //After half a second, start 'regrowing' blocks that haven't been mined.
                this.priorDurability = this.durability;
                this.durability++;
                ServerTickHandler.addToList(this.worldPosition, this.durability, this.level);
            }
            if (this.durability >= this.originalDurability) {
                //Once we reach the original durability value set the block back to its original blockstate.
                this.resetBlock();
            }
            this.ticksSinceMine++;
        }
    }

    public void setBlockAllowed() {
        if (!UpgradeTools.containsActiveUpgradeFromList(this.gadgetUpgrades, Upgrade.VOID_JUNK)) {
            this.blockAllowed = true;
            return;
        }
        PlayerEntity player = this.level.getPlayerByUUID(this.playerUUID);
        if (player == null) {
            return;
        }
        int silk = 0;
        int fortune = 0;

        ItemStack tempTool = new ItemStack(ModItems.MININGGADGET.get());

        // If silk is in the upgrades, apply it without a tier.
        if (UpgradeTools.containsActiveUpgradeFromList(this.gadgetUpgrades, Upgrade.SILK)) {
            tempTool.enchant(Enchantments.SILK_TOUCH, 1);
            silk = 1;
        }

        // FORTUNE_1 is eval'd against the basename so this'll support all fortune upgrades
        if (UpgradeTools.containsActiveUpgradeFromList(this.gadgetUpgrades, Upgrade.FORTUNE_1)) {
            Optional<Upgrade> upgrade = UpgradeTools.getUpgradeFromList(this.gadgetUpgrades, Upgrade.FORTUNE_1);
            if (upgrade.isPresent()) {
                fortune = upgrade.get().getTier();
                tempTool.enchant(Enchantments.BLOCK_FORTUNE, fortune);
            }
        }

        List<ItemStack> drops = Block.getDrops(this.renderBlock, (ServerWorld) this.level, this.worldPosition, null, player, tempTool);

        this.blockAllowed = blockAllowed(drops, this.getGadgetFilters(), this.isGadgetIsWhitelist());
    }

    public boolean getBlockAllowed() {
        return this.blockAllowed;
    }

}
