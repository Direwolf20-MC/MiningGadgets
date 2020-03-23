package com.direwolf20.mininggadgets.common.tiles;

import com.direwolf20.mininggadgets.common.items.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

import static com.direwolf20.mininggadgets.common.blocks.ModBlocks.QUARRY_TILE;

public class QuarryBlockTileEntity extends TileEntity implements ITickableTileEntity {

    public ArrayList<BlockPos> adjacentStorage = new ArrayList<>();
    public boolean needScanAdjacent;
    private BlockPos startPos;
    private BlockPos endPos;
    private BlockPos currentPos;
    private boolean lastWasAir = false;
    int tick;

    public QuarryBlockTileEntity() {
        super(QUARRY_TILE.get());
        needScanAdjacent = true;
    }

    public void scanAdjacentStorage() {
        adjacentStorage.clear();
        if( world == null )
            return;

        for (Direction direction : Direction.values()) {
            BlockPos adjacentPos = this.pos.offset(direction);
            TileEntity tile = world.getTileEntity(adjacentPos);

            if( tile != null )
                tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(e -> adjacentStorage.add(this.pos.offset(direction)));
        }

        needScanAdjacent = false;
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        return super.write(tag);
    }

    public boolean isPowered() {
        return world.getStrongPower(this.pos) > 0;
    }

    public void setStartPos() {
        startPos = this.pos.offset(Direction.NORTH).up(); //ToDo determine how we'll set the boundary
    }

    public void setEndPos() {
        endPos = this.pos.offset(Direction.SOUTH, 10).offset(Direction.EAST, 10).down(this.pos.getY()); //ToDo determine how we'll set the boundary
    }

    public BlockPos getCurrentPos() {
        if (currentPos == null) {
            setCurrentPos(startPos);
        }
        return currentPos;
    }

    public void setCurrentPos(BlockPos pos) {
        currentPos = pos;
    }

    private void removeBlock(BlockState state, BlockPos pos) {
        if (world == null || world.isRemote)
            return;

// apparent this isn't something you can do... We need a way of setting the placer as the player
// Don't mine if we cant
//        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, state, fakePlayer);
//        MinecraftForge.EVENT_BUS.post(event);
//        if( event.isCanceled() )
//            return;

        ItemStack tempTool = new ItemStack(ModItems.MININGGADGET.get());
        tempTool.addEnchantment(Enchantments.FORTUNE, 3);

        LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld) world)).withRandom(world.rand).withParameter(LootParameters.POSITION, pos).withParameter(LootParameters.TOOL, tempTool);
        List<ItemStack> drops = state.getDrops(lootcontext$builder);

        for (ItemStack drop : drops) {
            if( !drop.isEmpty() )
                System.out.println(insertIntoAdjacentInventory(drop));
        }
        //   }
        //   if (magnetMode) {
        //       if (exp > 0)
        //           player.giveExperiencePoints(exp);
        //   } else {
        //       if (exp > 0)
        //           renderBlock.getBlock().dropXpOnBlockBreak(world, pos, exp);
        //world.addEntity(new ExperienceOrbEntity(world, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, exp));
        //}
//}

        world.removeTileEntity(pos);
        world.setBlockState(pos, Blocks.AIR.getDefaultState());
    }

    public ItemStack insertIntoAdjacentInventory(ItemStack stack) {
        boolean success = false;
        for (BlockPos pos : adjacentStorage) {
            assert world != null;
            TileEntity tile = world.getTileEntity(pos);

            if (tile != null) {
                tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((cap) -> {
                    ItemHandlerHelper.insertItemStacked(cap, stack, false);
                });
                if (stack.isEmpty())
                    return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    public void setNextPos() {
        int x = getCurrentPos().getX();
        int y = getCurrentPos().getY();
        int z = getCurrentPos().getZ();
        if (y != 0) {
            setCurrentPos(currentPos.down());
        } else if (x < endPos.getX()) {
            setCurrentPos(new BlockPos(x + 1, startPos.getY(), z));
        } else if (z < endPos.getZ()) {
            setCurrentPos(new BlockPos(startPos.getX(), startPos.getY(), z + 1));
        } else {
            currentPos = endPos;
        }
        System.out.println(currentPos);
    }

    public void mineCurrentPos() {
        BlockState state = world.getBlockState(getCurrentPos());
        if (!state.getMaterial().equals(Material.AIR) && state.getBlockHardness(world, getCurrentPos()) >= 0) {
            removeBlock(state, getCurrentPos());
            lastWasAir = false;
        } else {
            lastWasAir = true;
        }
        setNextPos();
    }


    @Override
    public void tick() {
        if (!world.isRemote) {
            if (needScanAdjacent) scanAdjacentStorage();
            if (isPowered()) {
                if (startPos == null) setStartPos();
                if (endPos == null) setEndPos();
                tick++;
                if (tick % 20 == 0 || lastWasAir) {
                    tick = 0;
                    mineCurrentPos();
                }
            }
        }
    }
}
