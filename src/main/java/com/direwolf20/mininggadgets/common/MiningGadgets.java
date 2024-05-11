package com.direwolf20.mininggadgets.common;

import com.direwolf20.mininggadgets.common.capabilities.EnergyStorageItemstack;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.network.PacketHandler;
import com.direwolf20.mininggadgets.common.tiles.ModificationTableTileEntity;
import com.direwolf20.mininggadgets.setup.ClientSetup;
import com.direwolf20.mininggadgets.setup.Config;
import com.direwolf20.mininggadgets.setup.ModSetup;
import com.direwolf20.mininggadgets.setup.Registration;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MiningGadgets.MOD_ID)
public class MiningGadgets
{
    public static final String MOD_ID = "mininggadgets";
    private static final Logger LOGGER = LogManager.getLogger();

    public MiningGadgets(IEventBus event, ModContainer container) {
        // Register all of our items, blocks, item blocks, etc
        Registration.init(event);
        Config.register(container);

        event.addListener(ModSetup::init);
        ModSetup.TABS.register(event);
        NeoForge.EVENT_BUS.register(this);
        event.addListener(PacketHandler::registerNetworking);
        event.addListener(this::registerCapabilities);

        if (FMLLoader.getDist().isClient()) {
            event.addListener(ClientSetup::init);
        }
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.EnergyStorage.ITEM, (itemStack, context) -> new EnergyStorageItemstack(((MiningGadget) itemStack.getItem()).getEnergyMax(), itemStack),
                Registration.MININGGADGET.get(),
                Registration.MININGGADGET_FANCY.get(),
                Registration.MININGGADGET_SIMPLE.get()
        );
        event.registerBlock(Capabilities.ItemHandler.BLOCK,
                (level, pos, state, be, side) -> ((ModificationTableTileEntity) be).handler,
                // blocks to register for
                Registration.MODIFICATION_TABLE.get());
    }

    @SubscribeEvent
    public void rightClickEvent(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = MiningGadget.getGadget(event.getEntity());
        if (stack.getItem() instanceof MiningGadget) {
            if (this.stackIsAnnoying(event.getEntity().getMainHandItem())
                    || this.stackIsAnnoying(event.getEntity().getOffhandItem())
                    || event.getLevel().getBlockState(event.getPos()).getBlock() instanceof RedStoneOreBlock) {
                event.setCanceled(true);
            }
        }
    }

    /**
     * I've tried to identity annoying offhand items that can be placed whilst mining.
     * I assume some level of logic, so we assume that you'd have that item in your offhand
     * whilst using the gadget.
     */
    private boolean stackIsAnnoying(ItemStack stack) {
        // This should never happen but I like casting safety
        if (!(stack.getItem() instanceof BlockItem))
            return false;

        Block block = ((BlockItem) stack.getItem()).getBlock();
        return block instanceof TorchBlock || block instanceof LanternBlock || block.equals(Blocks.GLOWSTONE)
                || block instanceof RedstoneLampBlock || block instanceof EndRodBlock;
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
