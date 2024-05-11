package com.direwolf20.mininggadgets.common.tiles;

import com.direwolf20.mininggadgets.common.containers.ModificationTableContainer;
import com.direwolf20.mininggadgets.common.containers.ModificationTableHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import static com.direwolf20.mininggadgets.setup.Registration.MODIFICATIONTABLE_TILE;

public class ModificationTableTileEntity extends BlockEntity implements MenuProvider {
    public final ModificationTableHandler handler = new ModificationTableHandler(2, this);

    public ModificationTableTileEntity(BlockPos pos, BlockState state) {
        super(MODIFICATIONTABLE_TILE.get(), pos, state);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, provider);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        this.loadAdditional(tag, lookupProvider);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        if (tag.contains("Inventory"))
            handler.deserializeNBT(provider, tag.getCompound("Inventory"));
        super.loadAdditional(tag, provider);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("Inventory", handler.serializeNBT(provider));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("buildinggadgets2.screen.modificationtable");
    }


    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
        return new ModificationTableContainer(i, level, worldPosition, playerInventory);
    }

    public ModificationTableContainer getContainer(Player playerIn) {
        return new ModificationTableContainer(0, playerIn.level(), this.worldPosition, playerIn.getInventory());
    }
}
