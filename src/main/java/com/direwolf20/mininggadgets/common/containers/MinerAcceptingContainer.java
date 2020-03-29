package com.direwolf20.mininggadgets.common.containers;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * I use this twice and I don't want to rewrite the base logic, It's a container, but.. It accepts a Mining Gadget!
 */
public abstract class MinerAcceptingContainer extends Container {
    private List<Upgrade> upgradesCache = new ArrayList<>();

    protected MinerAcceptingContainer(@Nullable ContainerType<?> type, int id) {
        super(type, id);
    }

    public void setupMinerSlot(IItemHandler handler, int index, int x, int y) {
        addSlot(new WatchedSlot(handler, index, x, y, this::updateUpgradeCache));
    }

    protected void updateUpgradeCache(int index) {
        ItemStack stack = this.getSlot(index).getStack();
        if( (stack.isEmpty() && !upgradesCache.isEmpty()) || !MiningGadget.is(stack) ) {
            upgradesCache.clear();
            return;
        }

        // Purge and set cache
        upgradesCache.clear();
        upgradesCache = UpgradeTools.getUpgrades(stack);
    }

    public List<Upgrade> getUpgradesCache() {
        return upgradesCache;
    }

    private static class WatchedSlot extends SlotItemHandler {
        private Consumer<Integer> onPress;

        public WatchedSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Consumer<Integer> onPress) {
            super(itemHandler, index, xPosition, yPosition);
            this.onPress = onPress;
        }

        @Override
        public void onSlotChanged() {
            super.onSlotChanged();

            this.onPress.accept(this.getSlotIndex());
        }
    }
}
