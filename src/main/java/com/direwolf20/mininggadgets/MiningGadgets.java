package com.direwolf20.mininggadgets;

import com.direwolf20.mininggadgets.client.ClientSetup;
import com.direwolf20.mininggadgets.client.events.EventModelBake;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.containers.ModContainers;
import com.direwolf20.mininggadgets.common.events.ServerTickHandler;
import com.direwolf20.mininggadgets.common.items.ModItems;
import com.direwolf20.mininggadgets.common.network.PacketHandler;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
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
        EventModelBake.addListeners(event);
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
    }

    public static Logger getLogger() {
        return LOGGER;
    }

}
