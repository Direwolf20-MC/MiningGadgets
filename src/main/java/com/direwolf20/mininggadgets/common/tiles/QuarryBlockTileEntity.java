package com.direwolf20.mininggadgets.common.tiles;

import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.blocks.RenderBlock;
import com.direwolf20.mininggadgets.common.containers.QuarryContainer;
import com.direwolf20.mininggadgets.common.items.ModItems;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.direwolf20.mininggadgets.common.blocks.ModBlocks.QUARRY_TILE;

public class QuarryBlockTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    public ArrayList<BlockPos> adjacentStorage = new ArrayList<>();
    private boolean needScanAdjacent;
    private boolean needScanMarker;
    private BlockPos startPos = BlockPos.ZERO;
    private BlockPos endPos = BlockPos.ZERO;
    private BlockPos markerX = BlockPos.ZERO;
    private BlockPos markerZ = BlockPos.ZERO;
    private BlockPos currentPos = BlockPos.ZERO;
    private boolean lastWasAir = false;
    private boolean isDone;
    int tick;


    public QuarryBlockTileEntity() {
        super(QUARRY_TILE.get());
        needScanAdjacent = true;
    }

    public void scanAdjacentStorage() {
        adjacentStorage.clear();
        if (world == null)
            return;

        for (Direction direction : Direction.values()) {
            BlockPos adjacentPos = this.pos.offset(direction);
            TileEntity tile = world.getTileEntity(adjacentPos);

            if (tile != null)
                tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(e -> adjacentStorage.add(this.pos.offset(direction)));
        }

        needScanAdjacent = false;
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        needScanAdjacent = tag.getBoolean("needScanAdjacent");
        needScanMarker = tag.getBoolean("needScanMarker");
        startPos = NBTUtil.readBlockPos(tag.getCompound("startPos"));
        endPos = NBTUtil.readBlockPos(tag.getCompound("endPos"));
        markerX = NBTUtil.readBlockPos(tag.getCompound("markerX"));
        markerZ = NBTUtil.readBlockPos(tag.getCompound("markerZ"));
        currentPos = NBTUtil.readBlockPos(tag.getCompound("currentPos"));
        lastWasAir = tag.getBoolean("lastWasAir");
        isDone = tag.getBoolean("isDone");
        tick = tag.getInt("tick");
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag.putBoolean("needScanAdjacent", needScanAdjacent);
        tag.putBoolean("needScanMarker", needScanMarker);
        tag.put("startPos", NBTUtil.writeBlockPos(startPos));
        tag.put("endPos", NBTUtil.writeBlockPos(endPos));
        tag.put("markerX", NBTUtil.writeBlockPos(markerX));
        tag.put("markerZ", NBTUtil.writeBlockPos(markerZ));
        tag.put("currentPos", NBTUtil.writeBlockPos(currentPos));
        tag.putBoolean("lastWasAir", lastWasAir);
        tag.putBoolean("isDone", isDone);
        tag.putInt("tick", tick);
        return super.write(tag);
    }

    public void markDirtyClient() {
        markDirty();
        if (getWorld() != null) {
            BlockState state = getWorld().getBlockState(getPos());
            getWorld().notifyBlockUpdate(getPos(), state, state, 3);
        }
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

    public boolean isPowered() {
        return world.getStrongPower(this.pos) > 0;
    }

    public boolean findMarkers() {
        BlockPos thisPos = this.getPos();
        int xMarker = 0, zMarker = 0;
        for (int x = thisPos.getX() - 64; x < thisPos.getX() + 64; x++) {
            if (world.getBlockState(new BlockPos(x, thisPos.getY(), thisPos.getZ())).getBlock().equals(ModBlocks.MARKER_BLOCK.get())) {
                xMarker = x;
                markerX = new BlockPos(x, thisPos.getY(), thisPos.getZ());
                break;
            }
        }
        for (int z = thisPos.getZ() - 64; z < thisPos.getZ() + 64; z++) {
            if (world.getBlockState(new BlockPos(thisPos.getX(), thisPos.getY(), z)).getBlock().equals(ModBlocks.MARKER_BLOCK.get())) {
                zMarker = z;
                markerZ = new BlockPos(thisPos.getX(), thisPos.getY(), z);
                break;
            }
        }
        if (xMarker != 0 && zMarker != 0) {
            int startX = 0, startZ = 0, endX = 0, endZ = 0;
            if (xMarker < thisPos.getX()) {
                startX = thisPos.getX() - 1;
                endX = xMarker + 1;
            } else {
                startX = thisPos.getX() + 1;
                endX = xMarker - 1;
            }
            if (zMarker < thisPos.getZ()) {
                startZ = thisPos.getZ() - 1;
                endZ = zMarker + 1;
            } else {
                startZ = thisPos.getZ() + 1;
                endZ = zMarker - 1;
            }
            setStartPos(new BlockPos(startX, thisPos.getY(), startZ));
            setEndPos(new BlockPos(endX, thisPos.getY(), endZ));
            setCurrentPos(BlockPos.ZERO);
            isDone = false;
            System.out.println("New Coords: " + getStartPos() + ":" + getEndPos() + ":" + getCurrentPos());
            markDirtyClient();
            return true;
        } else {
            setStartPos(BlockPos.ZERO);
            setEndPos(BlockPos.ZERO);
            setCurrentPos(BlockPos.ZERO);
            markerX = BlockPos.ZERO;
            markerZ = BlockPos.ZERO;
            markDirtyClient();
            return false;
        }
    }

    public void setStartPos(BlockPos pos) {
        // We don't care about Y so we'll zero it out
        startPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ()); //this.pos.offset(Direction.NORTH).up(); //ToDo determine how we'll set the boundary
    }

    public void setEndPos(BlockPos pos) {
        endPos = new BlockPos(pos.getX(), 0, pos.getZ()); //this.pos.offset(Direction.SOUTH, 10).offset(Direction.EAST, 10).down(this.pos.getY()); //ToDo determine how we'll set the boundary
    }

    public BlockPos getCurrentPos() {
        if (currentPos == null || currentPos == BlockPos.ZERO) {
            setCurrentPos(getStartPos());
        }
        return currentPos;
    }

    public void setCurrentPos(BlockPos pos) {
        currentPos = pos;
    }

    private boolean removeBlock(BlockState state, BlockPos pos) {
        if (world == null || world.isRemote)
            return false;

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
            if (!drop.isEmpty())
                drop = insertIntoAdjacentInventory(drop);
            if (!drop.isEmpty())
                return false;
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
        return true;
    }

    public ItemStack insertIntoAdjacentInventory(ItemStack stack) {
        AtomicInteger success = new AtomicInteger(0);
        for (BlockPos pos : adjacentStorage) {
            assert world != null;
            TileEntity tile = world.getTileEntity(pos);

            if (tile != null) {
                tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((cap) -> {
                    ItemStack tempstack = ItemHandlerHelper.insertItemStacked(cap, stack, false);
                    if (tempstack.isEmpty())
                        success.getAndIncrement();
                });
            }
            if (success.get() > 0)
                return ItemStack.EMPTY;
        }
        return stack;
    }

    public void setNextPos() {
        if (currentPos == endPos) {
            isDone = true;
            return;
        }
        int x = getCurrentPos().getX();
        int y = getCurrentPos().getY();
        int z = getCurrentPos().getZ();
        if (y != 0) {
            setCurrentPos(currentPos.down());
        } else if (x != endPos.getX()) {
            setCurrentPos(new BlockPos(endPos.getX() > startPos.getX() ? x + 1 : x - 1, startPos.getY(), z));
        } else if (z != endPos.getZ()) {
            setCurrentPos(new BlockPos(startPos.getX(), startPos.getY(), endPos.getZ() > startPos.getZ() ? z + 1 : z - 1));
        } else {
            currentPos = endPos;
        }
        System.out.println(currentPos);
        markDirtyClient();
    }

    private float getHardness(int efficiency) {
        float hardness = 0;
        float toolSpeed = 8;
        if (efficiency > 0) {
            toolSpeed = toolSpeed + ((efficiency * efficiency + 1));
        }
        BlockState state = this.world.getBlockState(currentPos);
        hardness = state.getBlockHardness(world, currentPos);
        return (hardness);
    }

    public void mineCurrentPos() {
        BlockState state = world.getBlockState(getCurrentPos());
        boolean success = false;
        if (!state.getMaterial().equals(Material.AIR) && !state.getMaterial().isLiquid() && state.getBlockHardness(world, getCurrentPos()) >= 0 && (world.getTileEntity(getCurrentPos()) == null || world.getTileEntity(getCurrentPos()) instanceof RenderBlockTileEntity)) {
            if (!(state.getBlock() instanceof RenderBlock)) {
                int efficiency = 0;
                //if (UpgradeTools.containsActiveUpgrade((stack), Upgrade.EFFICIENCY_1))
                //    efficiency = UpgradeTools.getUpgradeFromGadget((stack), Upgrade.EFFICIENCY_1).get().getTier();

                float hardness = getHardness(efficiency);
                hardness = (float) Math.floor(hardness) * 1;
                if (hardness == 0) hardness = 1;
                //List<Upgrade> gadgetUpgrades = UpgradeTools.getUpgrades(stack);
                world.setBlockState(currentPos, ModBlocks.RENDER_BLOCK.get().getDefaultState());
                RenderBlockTileEntity te = (RenderBlockTileEntity) world.getTileEntity(currentPos);
                te.setRenderBlock(state);
                //te.setBreakType(MiningProperties.getBreakType(stack));
                te.setBreakType(MiningProperties.BreakTypes.SHRINK);
                //te.setGadgetUpgrades(gadgetUpgrades);
                //te.setGadgetFilters(MiningProperties.getFiltersAsList(stack));
                //te.setGadgetIsWhitelist(MiningProperties.getWhiteList(stack));
                te.setPriorDurability((int) hardness + 1);
                te.setOriginalDurability((int) hardness + 1);
                te.setDurability((int) hardness, new ItemStack(ModItems.MININGGADGET.get()));
                //te.setPlayer((PlayerEntity) player); //TODO Used for block break particles, gotta change that
                te.setBlockAllowed();
            } else {
                //if (!world.isRemote) {
                RenderBlockTileEntity te = (RenderBlockTileEntity) world.getTileEntity(currentPos);
                int durability = te.getDurability();
                System.out.println(durability);
                BlockState originalState = te.getRenderBlock();
                //System.out.println(durability);
                /*if (player.getHeldItemMainhand().getItem() instanceof MiningGadget && player.getHeldItemOffhand().getItem() instanceof MiningGadget)
                    durability = durability - 2;
                else*/
                durability = durability - 1;
                te.setDurability(durability, new ItemStack(ModItems.MININGGADGET.get()));
                if (durability <= 0) {
                    success = removeBlock(originalState, getCurrentPos());
                    //stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> e.receiveEnergy(getEnergyCost(stack) * -1, false)); //TODO: Replace with energy cost on TE
                }

                //}
            }
            lastWasAir = false;
        } else {
            lastWasAir = true;
            success = true;
        }
        if (success)
            setNextPos();
        //else
        //    isDone = true; //TODO: Decide how to reactivate a stalled miner (Short of trying every tick!). Also visual indication that its stuck.
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            if (needScanAdjacent) scanAdjacentStorage();
            if (needScanMarker) findMarkers();
            if (isPowered() && !isDone) {
                tick++;
                if (tick % 1 == 0 || lastWasAir) {
                    tick = 0;
                    mineCurrentPos();
                }
            }
        }
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new QuarryContainer(i, world, pos, playerInventory);
    }

    public BlockPos getStartPos() {
        return startPos;
    }

    public BlockPos getEndPos() {
        return endPos;
    }

    public BlockPos getMarkerX() {
        return markerX;
    }

    public BlockPos getMarkerZ() {
        return markerZ;
    }

    @Nonnull
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.up(10), getEndPos());
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Quarry");
    }
}
