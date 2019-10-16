package com.direwolf20.mininggadgets.common.tiles;

import com.direwolf20.mininggadgets.client.particles.LaserParticleData;
import com.direwolf20.mininggadgets.common.items.ModItems;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
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

    public void setDurability(int dur) {
        priorDurability = durability;
        durability = dur;
        if (dur <= 0) {
            removeBlock();
        }
        ticksSinceMine = 0;
        if (UpgradeTools.containsUpgradeFromList(gadgetUpgrades, Upgrade.MAGNET)) {
            if (durability % 1 == 0) {
                double randomPartSize = 0.125 + rand.nextDouble() * 0.5;
                double randomX = rand.nextDouble();
                double randomY = rand.nextDouble();
                double randomZ = rand.nextDouble();
                LaserParticleData data = LaserParticleData.laserparticle(renderBlock, (float) randomPartSize, 1F, 1F, 1F, 200);
                getWorld().addParticle(data, this.getPos().getX() + randomX, this.getPos().getY() + randomY, this.getPos().getZ() + randomZ, 0, 0.0f, 0);
            }
        }
        markDirty();
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
        this.clientDurability = clientDurability;
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
        if (!getWorld().isRemote) {
            PlayerEntity player = world.getPlayerByUuid(playerUUID);
            if (player == null) return;

            if (!(UpgradeTools.containsUpgradeFromList(gadgetUpgrades, Upgrade.VOID_JUNK)) || renderBlock.isIn(Tags.Blocks.ORES)) {
                ItemStack tempTool = new ItemStack(ModItems.MININGGADGET);

                // If silk is in the upgrades, apply it without a tier.
                if (UpgradeTools.containsUpgradeFromList(gadgetUpgrades, Upgrade.SILK))
                    tempTool.addEnchantment(Enchantments.SILK_TOUCH, 1);

                // If the upgrade exists. Apply it with it's tier
                UpgradeTools.getUpgradeFromList(gadgetUpgrades, Upgrade.FORTUNE_1)
                        .ifPresent( upgrade -> tempTool.addEnchantment(Enchantments.FORTUNE, upgrade.getTier()));

                List<ItemStack> blockDrops = Block.getDrops(renderBlock, (ServerWorld) world, this.pos, null, player, tempTool);
                //List<ItemStack> blockDrops = renderBlock.getBlock().getDrops(renderBlock, (ServerWorld) world, pos, world.getTileEntity(pos));
                for (ItemStack drop : blockDrops) {
                    if (drop != null) {
                        if (UpgradeTools.containsUpgradeFromList(gadgetUpgrades, Upgrade.MAGNET)) {
                            if (!player.addItemStackToInventory(drop)) {
                                Block.spawnAsEntity(world, pos, drop);
                            }
                        } else {
                            Block.spawnAsEntity(world, pos, drop);
                        }
                    }
                }
            }
            player.giveExperiencePoints(renderBlock.getExpDrop(world, pos, 0, 0));
            world.removeTileEntity(this.pos);
            world.setBlockState(this.pos, Blocks.AIR.getDefaultState());
            //markDirtyClient();
        }
    }

    private void resetBlock() {
        if (!getWorld().isRemote) {
            world.setBlockState(this.pos, renderBlock);
            //markDirtyClient();
        }
    }


    @Override
    public void tick() {
        if (ticksSinceMine == 1) {
            priorDurability = durability;
            //markDirtyClient();
        }
        ticksSinceMine++;
        if (ticksSinceMine >= 10) {
            priorDurability = durability;
            durability++;
        } else {

        }
        if (durability >= originalDurability) {
            resetBlock();
        }
    }
}
