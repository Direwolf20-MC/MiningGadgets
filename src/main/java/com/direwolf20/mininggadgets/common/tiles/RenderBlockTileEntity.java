package com.direwolf20.mininggadgets.common.tiles;

import com.direwolf20.mininggadgets.client.particles.laserparticle.LaserParticleData;
import com.direwolf20.mininggadgets.client.particles.playerparticle.PlayerParticleData;
import com.direwolf20.mininggadgets.common.events.ServerTickHandler;
import com.direwolf20.mininggadgets.common.items.ModItems;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.util.VectorHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants;

import java.util.*;

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
    private boolean packetReceived = false;
    private int totalAge;


    public RenderBlockTileEntity() {
        super(RENDERBLOCK_TILE);
    }

    public void setRenderBlock(BlockState state) {
        renderBlock = state;
    }

    public BlockState getRenderBlock() {
        return renderBlock;
    }

    public void justSetDurability(int dur) {
        priorDurability = durability;
        durability = dur;
        //System.out.println("Got:"+ " Prior: " + priorDurability + " Dur: " + durability);
    }

    public void setDurability(int dur) {
        ticksSinceMine = 0;
        if (durability != 0)
            priorDurability = durability;
        durability = dur;
        if (dur <= 0) {
            removeBlock();
            if (UpgradeTools.containsUpgradeFromList(gadgetUpgrades, Upgrade.FREEZING)) {
                freeze();
            }
        }
        if (!(world.isRemote)) {
            markDirty();
            ServerTickHandler.addToList(pos, durability, world);
            //PacketHandler.sendToAll(new PacketDurabilitySync(pos, dur), world);
            //System.out.println("Sent: "+ " Prior: " + priorDurability + " Dur: " + dur);
        }
    }

    public List<BlockPos> findSources() {
        List<BlockPos> sources = new ArrayList<>();
        for (Direction side : Direction.values()) {
            BlockPos sidePos = pos.offset(side);
            if (world.getBlockState(sidePos).getBlock() == Blocks.LAVA || world.getBlockState(sidePos).getBlock() == Blocks.WATER)
                sources.add(sidePos);
        }
        return sources;
    }

    private void freeze() {
        for (Direction side : Direction.values()) {
            BlockPos sidePos = pos.offset(side);
            if (world.getBlockState(sidePos).getBlock() == Blocks.LAVA) {
                world.setBlockState(sidePos, Blocks.OBSIDIAN.getDefaultState());
            } else if (world.getBlockState(sidePos).getBlock() == Blocks.WATER) {
                world.setBlockState(sidePos, Blocks.PACKED_ICE.getDefaultState());
            }
        }
    }

    public void spawnParticle() {
        if (UpgradeTools.containsUpgradeFromList(gadgetUpgrades, Upgrade.MAGNET) && originalDurability > 0) {
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
        return super.write(tag);
    }

    private void removeBlock() {
        if (!world.isRemote) {
            PlayerEntity player = world.getPlayerByUuid(playerUUID);
            if (player == null) return;
            int silk = 0;
            int fortune = 0;
            if (!(UpgradeTools.containsUpgradeFromList(gadgetUpgrades, Upgrade.VOID_JUNK)) || renderBlock.isIn(Tags.Blocks.ORES)) {
                ItemStack tempTool = new ItemStack(ModItems.MININGGADGET);

                // If silk is in the upgrades, apply it without a tier.
                if (UpgradeTools.containsUpgradeFromList(gadgetUpgrades, Upgrade.SILK)) {
                    tempTool.addEnchantment(Enchantments.SILK_TOUCH, 1);
                    silk = 1;
                }

                // FORTUNE_1 is eval'd against the basename so this'll support all fortune upgrades
                Optional<Upgrade> upgrade = UpgradeTools.getUpgradeFromList(gadgetUpgrades, Upgrade.FORTUNE_1);
                if( upgrade.isPresent() ) {
                    fortune = upgrade.get().getTier();
                    tempTool.addEnchantment(Enchantments.FORTUNE, fortune);
                }

                List<ItemStack> blockDrops = Block.getDrops(renderBlock, (ServerWorld) world, this.pos, null, player, tempTool);
                int exp = renderBlock.getExpDrop(world, pos, fortune, silk);
                boolean magnetMode = (UpgradeTools.containsUpgradeFromList(gadgetUpgrades, Upgrade.MAGNET));
                for (ItemStack drop : blockDrops) {
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

    private void spawnFreezeParticle(PlayerEntity player, BlockPos sourcePos) {
        float randomPartSize = 0.05f + (0.125f - 0.05f) * rand.nextFloat();
        double randomTX = rand.nextDouble();
        double randomTY = rand.nextDouble();
        double randomTZ = rand.nextDouble();
        double alpha = -0.5f + (1.0f - 0.5f) * rand.nextDouble(); //rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        Vec3d playerPos = player.getPositionVec().add(0, player.getEyeHeight(), 0);
        Vec3d look = player.getLookVec(); // or getLook(partialTicks)
        BlockRayTraceResult lookAt = VectorHelper.getLookingAt(player, RayTraceContext.FluidMode.NONE);
        Vec3d lookingAt = lookAt.getHitVec();
        //The next 3 variables are directions on the screen relative to the players look direction. So right = to the right of the player, regardless of facing direction.
        Vec3d right = new Vec3d(-look.z, 0, look.x).normalize();
        Vec3d forward = look;
        Vec3d backward = look.mul(-1, 1, -1);
        Vec3d down = right.crossProduct(forward);

        //These are used to calculate where the particles are going. We want them going into the laser, so we move the destination right, down, and forward a bit.
        right = right.scale(0.65f);
        forward = forward.scale(0.85f);
        down = down.scale(-0.35);
        backward = backward.scale(0.05);

        //Take the player's eye position, and shift it to where the end of the laser is (Roughly)
        Vec3d laserPos = playerPos.add(right);
        laserPos = laserPos.add(forward);
        laserPos = laserPos.add(down);
        lookingAt = lookingAt.add(backward);
        PlayerParticleData data = PlayerParticleData.playerparticle("ice", sourcePos.getX() + randomTX, sourcePos.getY() + randomTY, sourcePos.getZ() + randomTZ, randomPartSize, 1f, 1f, 1f, 120, true);
        //Change the below laserPos to lookingAt to have it emit from the laser gun itself
        world.addParticle(data, laserPos.x, laserPos.y, laserPos.z, 0.025, 0.025f, 0.025);
    }


    @Override
    public void tick() {
        totalAge++;
        //Client and server - spawn a 'block break' particle if the player is actively mining
        if (ticksSinceMine == 0) {
            spawnParticle();
            if (UpgradeTools.containsUpgradeFromList(gadgetUpgrades, Upgrade.FREEZING)) {
                if (totalAge % 4 == 0) {
                    if (playerUUID != null) {
                        for (BlockPos sourcePos : findSources()) {
                            spawnFreezeParticle(getPlayer(), sourcePos);
                        }
                    }
                }
            }
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
}
