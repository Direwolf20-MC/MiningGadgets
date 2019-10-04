package com.direwolf20.mininggadgets.common.tiles;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.world.BlockEvent;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.List;

import static com.direwolf20.mininggadgets.common.blocks.ModBlocks.RENDERBLOCK_TILE;

public class RenderBlockTileEntity extends TileEntity implements ITickableTileEntity {
    /**
     * Even though this is called "rendered", is will be used for replacement under normal conditions.
     */
    private BlockState renderBlock;
    private int priorDurability = 9999;
    private int durability;
    private PlayerEntity player;
    private int originalDurability;
    private int ticks = 0;


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
        durability = dur;
        markDirtyClient();
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
        return player;
    }

    public void setPlayer(PlayerEntity player) {
        this.player = player;
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

    private void markDirtyClient() {
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
        player = world.getPlayerByUuid(tag.getUniqueId("playerUUID"));
        markDirtyClient();
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag.put("renderBlock", NBTUtil.writeBlockState(renderBlock));
        tag.putInt("originalDurability", originalDurability);
        tag.putInt("priorDurability", priorDurability);
        tag.putInt("durability", durability);
        tag.putUniqueId("playerUUID", player.getUniqueID());
        return super.write(tag);
    }


    @Override
    public void tick() {
        if (!getWorld().isRemote) {
            if (priorDurability == 9999) {
                priorDurability = durability;
            }
            System.out.println(durability);
            if (priorDurability == durability) {
                if (durability > originalDurability) {
                    world.setBlockState(this.pos, renderBlock);
                } else {
                    durability++;
                    priorDurability = durability;
                    markDirtyClient();
                }
            } else {
                priorDurability = durability;
            }

            if (durability <= 0) {
                world.setBlockState(this.pos, Blocks.AIR.getDefaultState());
                BlockEvent.BreakEvent e = new BlockEvent.BreakEvent(world, getPos(), renderBlock, player);
                boolean cancelledBreak = MinecraftForge.EVENT_BUS.post(e);
                if (!cancelledBreak) {
                    List<ItemStack> blockDrops = renderBlock.getBlock().getDrops(renderBlock, (ServerWorld) world, pos, world.getTileEntity(pos));
                    for (ItemStack drop : blockDrops) {
                        if (drop != null) {
                            player.addItemStackToInventory(drop);
                        }
                    }
                }
            }
        }
    }
}
