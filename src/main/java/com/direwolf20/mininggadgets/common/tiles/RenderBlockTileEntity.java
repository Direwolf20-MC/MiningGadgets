package com.direwolf20.mininggadgets.common.tiles;

import com.direwolf20.mininggadgets.Config;
import com.direwolf20.mininggadgets.client.particles.laserparticle.LaserParticleData;
import com.direwolf20.mininggadgets.common.events.ServerTickHandler;
import com.direwolf20.mininggadgets.common.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.gadget.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.gadget.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.direwolf20.mininggadgets.common.blocks.ModBlocks.RENDERBLOCK_TILE;

public class RenderBlockTileEntity extends TileEntity implements ITickableTileEntity {
    private BlockState renderBlock;

    private int priorDurability = 9999;
    private int clientPrevDurability;
    private int clientDurability;
    private int durability;
    private UUID playerUUID;
    private int originalDurability;
    private Random rand = new Random();
    private int ticksSinceMine = 0;
    private List<Upgrade> gadgetUpgrades;
    private List<ItemStack> gadgetFilters;
    private boolean gadgetIsWhitelist;
    private boolean packetReceived = false;
    private int totalAge;
    private MiningProperties.BreakTypes breakType;

    public RenderBlockTileEntity() {
        super(RENDERBLOCK_TILE.get());
    }

    public void setRenderBlock(BlockState state) {
        renderBlock = state;
    }

    public BlockState getRenderBlock() {
        return renderBlock;
    }

    public MiningProperties.BreakTypes getBreakType() {
        return breakType;
    }

    public void setBreakType(MiningProperties.BreakTypes breakType) {
        this.breakType = breakType;
    }

    public void justSetDurability(int dur) {
        priorDurability = durability;
        durability = dur;
        //System.out.println("Got:"+ " Prior: " + priorDurability + " Dur: " + durability);
    }

    public void setDurability(int dur, ItemStack stack) {
        ticksSinceMine = 0;
        if (durability != 0)
            priorDurability = durability;
        durability = dur;
        if (dur <= 0) {
            removeBlock();
            if (UpgradeTools.containsActiveUpgradeFromList(gadgetUpgrades, Upgrade.FREEZING)) {
                freeze(stack);
            }
        }
        if (!(world.isRemote)) {
            markDirty();
            ServerTickHandler.addToList(pos, durability, world);
            //PacketHandler.sendToAll(new PacketDurabilitySync(pos, dur), world);
            //System.out.println("Sent: "+ " Prior: " + priorDurability + " Dur: " + dur);
        }
    }

    private void freeze(ItemStack stack) {
        for (Direction side : Direction.values()) {
            BlockPos sidePos = pos.offset(side);
            IFluidState state = world.getFluidState(sidePos);
            int freezeCost = Config.UPGRADECOST_FREEZE.get() * -1;
            if (state.getFluid().isEquivalentTo(Fluids.LAVA) && state.getFluid().isSource(state)) {
                world.setBlockState(sidePos, Blocks.OBSIDIAN.getDefaultState());
                stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> e.receiveEnergy(freezeCost, false));
            } else if (state.getFluid().isEquivalentTo(Fluids.WATER) && state.getFluid().isSource(state)) {
                world.setBlockState(sidePos, Blocks.PACKED_ICE.getDefaultState());
                stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> e.receiveEnergy(freezeCost, false));
            } else if ((state.getFluid().isEquivalentTo(Fluids.WATER) || state.getFluid().isEquivalentTo(Fluids.LAVA)) && !state.getFluid().isSource(state)) {
                world.setBlockState(sidePos, Blocks.COBBLESTONE.getDefaultState());
                stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> e.receiveEnergy(freezeCost, false));
            }
        }
    }

    public void spawnParticle() {
        if (UpgradeTools.containsActiveUpgradeFromList(gadgetUpgrades, Upgrade.MAGNET) && originalDurability > 0) {
            int PartCount = 20 / originalDurability;
            if (PartCount <= 1) PartCount = 1;
            for (int i = 0; i <= PartCount; i++) {
                double randomPartSize = 0.125 + rand.nextDouble() * 0.5;
                double randomX = rand.nextDouble();
                double randomY = rand.nextDouble();
                double randomZ = rand.nextDouble();
                LaserParticleData data = LaserParticleData.laserparticle(renderBlock, (float) randomPartSize, 1F, 1F, 1F, 200);
                getWorld().addParticle(data, this.getPos().getX() + randomX, this.getPos().getY() + randomY, this.getPos().getZ() + randomZ, 0, 0.0f, 0);
            }
        }
    }

    public int getDurability() {
        return durability;
    }

    public int getOriginalDurability() {
        return originalDurability;
    }

    public void setOriginalDurability(int originalDurability) {
        this.originalDurability = originalDurability;
    }

    public PlayerEntity getPlayer() {
        if( getWorld() == null )
            return null;

        return this.getWorld().getPlayerByUuid(playerUUID);
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public void setPlayer(PlayerEntity player) {
        this.playerUUID = player.getUniqueID();
    }

    public int getTicksSinceMine() {
        return ticksSinceMine;
    }

    public void setTicksSinceMine(int ticksSinceMine) {
        this.ticksSinceMine = ticksSinceMine;
    }

    public int getPriorDurability() {
        return priorDurability;
    }

    public void setPriorDurability(int priorDurability) {
        this.priorDurability = priorDurability;
    }

    public int getClientDurability() {
        return clientDurability;
    }

    public void setClientDurability(int clientDurability) {
        if (this.durability == 0)
            this.clientPrevDurability = clientDurability;
        else
            this.clientPrevDurability = this.durability;
        this.clientDurability = clientDurability;
        packetReceived = true;
    }

    public List<Upgrade> getGadgetUpgrades() {
        return gadgetUpgrades;
    }

    public void setGadgetUpgrades(List<Upgrade> gadgetUpgrades) {
        this.gadgetUpgrades = gadgetUpgrades;
    }

    public List<ItemStack> getGadgetFilters() {
        return gadgetFilters;
    }

    public void setGadgetFilters(List<ItemStack> gadgetFilters) {
        this.gadgetFilters = gadgetFilters;
    }

    public boolean isGadgetIsWhitelist() {
        return gadgetIsWhitelist;
    }

    public void setGadgetIsWhitelist(boolean gadgetIsWhitelist) {
        this.gadgetIsWhitelist = gadgetIsWhitelist;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        // Vanilla uses the type parameter to indicate which type of tile entity (command block, skull, or beacon?) is receiving the packet, but it seems like Forge has overridden this behavior
        return new SUpdateTileEntityPacket(pos, 0, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        read(tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        read(pkt.getNbtCompound());
    }

    public void markDirtyClient() {
        markDirty();
        if (getWorld() != null) {
            BlockState state = getWorld().getBlockState(getPos());
            getWorld().notifyBlockUpdate(getPos(), state, state, 3);
        }
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        renderBlock = NBTUtil.readBlockState(tag.getCompound("renderBlock"));
        originalDurability = tag.getInt("originalDurability");
        priorDurability = tag.getInt("priorDurability");
        durability = tag.getInt("durability");
        ticksSinceMine = tag.getInt("ticksSinceMine");
        playerUUID = tag.getUniqueId("playerUUID");
        gadgetUpgrades = UpgradeTools.getUpgradesFromTag(tag);
        breakType = MiningProperties.BreakTypes.values()[tag.getByte("breakType")];
        gadgetFilters = MiningProperties.deserializeItemStackList(tag.getCompound("gadgetFilters"));
        gadgetIsWhitelist = tag.getBoolean("gadgetIsWhitelist");
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag.put("renderBlock", NBTUtil.writeBlockState(renderBlock));
        tag.putInt("originalDurability", originalDurability);
        tag.putInt("priorDurability", priorDurability);
        tag.putInt("durability", durability);
        tag.putInt("ticksSinceMine", ticksSinceMine);
        tag.putUniqueId("playerUUID", playerUUID);
        tag.put("upgrades", UpgradeTools.setUpgradesNBT(gadgetUpgrades).getList("upgrades", Constants.NBT.TAG_COMPOUND));
        tag.putByte("breakType", (byte) breakType.ordinal());
        tag.put("gadgetFilters", MiningProperties.serializeItemStackList(getGadgetFilters()));
        tag.putBoolean("gadgetIsWhitelist", isGadgetIsWhitelist());
        return super.write(tag);
    }

    private void removeBlock() {
        if (!world.isRemote) {
            PlayerEntity player = world.getPlayerByUuid(playerUUID);
            if (player == null) return;
            int silk = 0;
            int fortune = 0;

            ItemStack tempTool = new ItemStack(ModItems.MININGGADGET.get());

            // If silk is in the upgrades, apply it without a tier.
            if (UpgradeTools.containsActiveUpgradeFromList(gadgetUpgrades, Upgrade.SILK)) {
                tempTool.addEnchantment(Enchantments.SILK_TOUCH, 1);
                silk = 1;
            }

            // FORTUNE_1 is eval'd against the basename so this'll support all fortune upgrades
            if (UpgradeTools.containsActiveUpgradeFromList(gadgetUpgrades, Upgrade.FORTUNE_1)) {
                Optional<Upgrade> upgrade = UpgradeTools.getUpgradeFromList(gadgetUpgrades, Upgrade.FORTUNE_1);
                if (upgrade.isPresent()) {
                    fortune = upgrade.get().getTier();
                    tempTool.addEnchantment(Enchantments.FORTUNE, fortune);
                }
            }

            List<ItemStack> drops = Block.getDrops(renderBlock, (ServerWorld) world, this.pos, null, player, tempTool);

            // lots of calcs to figure out if we should be mining the blocks
            boolean blockAllowed;
            if( UpgradeTools.containsActiveUpgradeFromList(gadgetUpgrades, Upgrade.VOID_JUNK) )
                blockAllowed = blockAllowed(drops, getGadgetFilters(), isGadgetIsWhitelist());
            else
                blockAllowed = true;

            if ( blockAllowed ) {
                int exp = renderBlock.getExpDrop(world, pos, fortune, silk);
                boolean magnetMode = (UpgradeTools.containsActiveUpgradeFromList(gadgetUpgrades, Upgrade.MAGNET));
                for (ItemStack drop : drops) {
                    if (drop != null) {
                        if (magnetMode) {
                            if (!player.addItemStackToInventory(drop)) {
                                Block.spawnAsEntity(world, pos, drop);
                            }
                        } else {
                            Block.spawnAsEntity(world, pos, drop);
                        }
                    }
                }
                if (magnetMode) {
                    if (exp > 0)
                        player.giveExperiencePoints(exp);
                } else {
                    if (exp > 0)
                        renderBlock.getBlock().dropXpOnBlockBreak(world, pos, exp);
                    //world.addEntity(new ExperienceOrbEntity(world, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, exp));
                }
            }

            world.removeTileEntity(this.pos);
            world.setBlockState(this.pos, Blocks.AIR.getDefaultState());
        }
    }

    private void resetBlock() {
        if (!getWorld().isRemote) {
            world.setBlockState(this.pos, renderBlock);
        }
    }

    @Override
    public void tick() {
        totalAge++;
        //Client and server - spawn a 'block break' particle if the player is actively mining
        if (ticksSinceMine == 0) {
            spawnParticle();
        }
        //Client only
        if (world.isRemote) {
            //Update ticks since last mine on client side for particle renders
            if (playerUUID != null) {
                if (getPlayer() != null && !getPlayer().isHandActive()) ticksSinceMine++;
                else ticksSinceMine = 0;
            }
            //The packet with new durability arrives between ticks. Update it on tick.
            if (packetReceived) {
                //System.out.println("PreChange: " + this.durability + ":" + this.priorDurability);
                this.priorDurability = this.durability;
                this.durability = this.clientDurability;
                //System.out.println("PostChange: " + this.durability + ":" + this.priorDurability);
                packetReceived = false;
            } else {
                if (durability != 0)
                    this.priorDurability = this.durability;

            }



        }
        //Server Only
        if (!world.isRemote) {
            if (ticksSinceMine == 1) {
                //Immediately after player stops mining, stability the shrinking effects and notify players
                priorDurability = durability;
                ServerTickHandler.addToList(pos, durability, world);
            }
            if (ticksSinceMine >= 10) {
                //After half a second, start 'regrowing' blocks that haven't been mined.
                priorDurability = durability;
                durability++;
                ServerTickHandler.addToList(pos, durability, world);
            }
            if (durability >= originalDurability) {
                //Once we reach the original durability value set the block back to its original blockstate.
                resetBlock();
            }
            ticksSinceMine++;
        }
    }

    public static boolean blockAllowed(List<ItemStack> drops, List<ItemStack> filters, boolean isWhiteList) {
        boolean blockAllowed = false;
        for (ItemStack dropStack : drops) {
            if( blockAllowed )
                break;

            if( !isWhiteList && filters.size() == 0 ) {
                blockAllowed = true;
                break;
            }

            for(ItemStack stack : filters) {
                if( isWhiteList && stack.isItemEqual(dropStack)) {
                    blockAllowed = true;
                    break;
                }

                if( !isWhiteList && !stack.isItemEqual(dropStack)) {
                    blockAllowed = true;
                    break;
                }
            }
        }

        return blockAllowed;
    }
}
