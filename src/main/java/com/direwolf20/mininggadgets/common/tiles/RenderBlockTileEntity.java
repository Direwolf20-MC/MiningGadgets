package com.direwolf20.mininggadgets.common.tiles;

import com.direwolf20.mininggadgets.client.particles.LaserParticleData;
import com.direwolf20.mininggadgets.common.events.ServerTickHandler;
import com.direwolf20.mininggadgets.common.items.ModItems;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants;

import java.util.List;
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
        }
        if (!(world.isRemote)) {
            markDirty();
            ServerTickHandler.addToList(pos, durability, world);
            //PacketHandler.sendToAll(new PacketDurabilitySync(pos, dur), world);
            //System.out.println("Sent: "+ " Prior: " + priorDurability + " Dur: " + dur);
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
        //this.ticksSinceMine = 0;
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
                // If the upgrade exists. Apply it with it's tier
                if (UpgradeTools.containsUpgradeFromList(gadgetUpgrades, Upgrade.FORTUNE_1)) {
                    tempTool.addEnchantment(Enchantments.FORTUNE, 1);
                    fortune = 1;
                } else if (UpgradeTools.containsUpgradeFromList(gadgetUpgrades, Upgrade.FORTUNE_2)) {
                    tempTool.addEnchantment(Enchantments.FORTUNE, 2);
                    fortune = 2;
                } else if (UpgradeTools.containsUpgradeFromList(gadgetUpgrades, Upgrade.FORTUNE_3)) {
                    tempTool.addEnchantment(Enchantments.FORTUNE, 3);
                    fortune = 3;
                }
                /*UpgradeTools.getUpgradeFromList(gadgetUpgrades, Upgrade.FORTUNE_1)
                        .ifPresent(upgrade -> tempTool.addEnchantment(Enchantments.FORTUNE, upgrade.getTier()));*/

                List<ItemStack> blockDrops = Block.getDrops(renderBlock, (ServerWorld) world, this.pos, null, player, tempTool);
                int exp = renderBlock.getExpDrop(world, pos, fortune, silk);
                for (ItemStack drop : blockDrops) {
                    if (drop != null) {
                        if (UpgradeTools.containsUpgradeFromList(gadgetUpgrades, Upgrade.MAGNET)) {
                            if (!player.addItemStackToInventory(drop)) {
                                Block.spawnAsEntity(world, pos, drop);
                            }
                            if (exp > 0)
                                player.giveExperiencePoints(exp);
                        } else {
                            Block.spawnAsEntity(world, pos, drop);
                            if (exp > 0)
                                world.addEntity(new ExperienceOrbEntity(world, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, exp));
                        }
                    }
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
        //Client and server - spawn a 'block break' particle if the player is actively mining
        if (ticksSinceMine == 0) {
            spawnParticle();
        }
        //Client only
        if (world.isRemote) {
            //Update ticks since last mine on client side for particle renders
            if (!getPlayer().isHandActive()) ticksSinceMine++;
            else ticksSinceMine = 0;
            //The packet with new durability arrives between ticks. Update it on tick.
            this.durability = this.clientDurability;
            this.priorDurability = this.clientPrevDurability;
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
