package com.direwolf20.mininggadgets.common;

import com.direwolf20.mininggadgets.client.ClientEvents;
import com.direwolf20.mininggadgets.client.ClientSetup;
import com.direwolf20.mininggadgets.client.OurKeys;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.containers.ModContainers;
import com.direwolf20.mininggadgets.common.events.ServerTickHandler;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.ModItems;
import com.direwolf20.mininggadgets.common.network.PacketHandler;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MiningGadgets.MOD_ID)
public class MiningGadgets
{
    public static final String MOD_ID = "mininggadgets";
    private static final Logger LOGGER = LogManager.getLogger();

    public static ItemGroup itemGroup = new ItemGroup(MiningGadgets.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.MININGGADGET.get());
        }
    };

    public MiningGadgets() {
        IEventBus event = FMLJavaModLoadingContext.get().getModEventBus();

        // Register all of our items, blocks, item blocks, etc
        ModItems.ITEMS.register(event);
        ModItems.UPGRADE_ITEMS.register(event);
        ModBlocks.BLOCKS.register(event);
        ModBlocks.TILES_ENTITIES.register(event);
        ModContainers.CONTAINERS.register(event);

        event.addListener(this::setup);
        event.addListener(this::setupClient);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        // Register the setup method for modloading
        event.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);

        Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MOD_ID + "-client.toml"));
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MOD_ID + "-common.toml"));
    }

    @SubscribeEvent
    public void rightClickEvent(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = MiningGadget.getGadget(event.getPlayer());
        if( stack.getItem() instanceof MiningGadget ) {
            if (this.stackIsAnnoying(event.getPlayer().getHeldItemMainhand()) ||
                    this.stackIsAnnoying(event.getPlayer().getHeldItemOffhand())) {
                event.setCanceled(true);
            }
        }
    }

    /**
     * I've tried to identity annoying offhand items that can be placed whilst mining.
     * I assume some level of logic so we assume that you'd have that item in your offhand
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

    private void setup(final FMLCommonSetupEvent event)
    {
        PacketHandler.register();
        MinecraftForge.EVENT_BUS.register(ServerTickHandler.class);
    }

    /**
     * Only run on the client making it a safe place to register client
     * components. Remember that you shouldn't reference client only
     * methods in this class as it'll crash the mod :P
     */
    private void setupClient(final FMLClientSetupEvent event) {
        // Register the container screens.
        ClientSetup.setup();
        OurKeys.register();
        MinecraftForge.EVENT_BUS.register(ClientEvents.class);
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
