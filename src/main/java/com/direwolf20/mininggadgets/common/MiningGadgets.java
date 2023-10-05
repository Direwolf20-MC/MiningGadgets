package com.direwolf20.mininggadgets.common;

import com.direwolf20.mininggadgets.client.ClientEvents;
import com.direwolf20.mininggadgets.client.ClientSetup;
import com.direwolf20.mininggadgets.client.particles.ModParticles;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.direwolf20.mininggadgets.common.containers.ModContainers;
import com.direwolf20.mininggadgets.common.events.ServerTickHandler;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.ModItems;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeBatteryLevels;
import com.direwolf20.mininggadgets.common.network.PacketHandler;
import com.direwolf20.mininggadgets.common.sounds.OurSounds;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.minecraftforge.common.MinecraftForge;
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
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MiningGadgets.MOD_ID)
public class MiningGadgets
{
    public static final String MOD_ID = "mininggadgets";
    private static final Logger LOGGER = LogManager.getLogger();

    // Borrowed from FTB Ulitmine by LatvianModder
    public static final TagKey<Block> EXCLUDED_BLOCKS = TagKey.create(Registries.BLOCK, new ResourceLocation(MOD_ID, "excluded_blocks"));

    public MiningGadgets() {
        IEventBus event = FMLJavaModLoadingContext.get().getModEventBus();

        // Register all of our items, blocks, item blocks, etc
        ModItems.ITEMS.register(event);
        ModItems.UPGRADE_ITEMS.register(event);
        ModBlocks.BLOCKS.register(event);
        ModBlocks.TILES_ENTITIES.register(event);
        ModContainers.CONTAINERS.register(event);
        ModParticles.PARTICLE_TYPES.register(event);
        OurSounds.SOUND_REGISTRY.register(event);

        event.addListener(this::setup);
        event.addListener(this::setupClient);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        // Register the setup method for modloading
        event.addListener(this::setup);
        event.addListener(this::buildContents);
        MinecraftForge.EVENT_BUS.register(this);

        Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MOD_ID + "-client.toml"));
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MOD_ID + "-common.toml"));
    }

    public void buildContents(RegisterEvent event) {
        ResourceKey<CreativeModeTab> TAB = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(MOD_ID, "creative_tab"));
        event.register(Registries.CREATIVE_MODE_TAB, creativeModeTabRegisterHelper ->
        {
            creativeModeTabRegisterHelper.register(TAB, CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.MININGGADGET_FANCY.get()))
                    .title(Component.translatable("itemGroup." + MOD_ID))
                    .displayItems((params, output) -> {
                        ModItems.ITEMS.getEntries()
                                .stream().filter(e -> e != ModItems.MINERS_LIGHT_ITEM)
                                .forEach(e -> {
                                    // Normal
                                    Item item = e.get();
                                    output.accept(item);

                                    // Charged
                                    if (item instanceof MiningGadget) {
                                        ItemStack stack = new ItemStack(item);
                                        stack.getOrCreateTag().putInt("energy", UpgradeBatteryLevels.BATTERY.getPower());
                                        output.accept(stack);
                                    }
                                });

                        ModItems.UPGRADE_ITEMS.getEntries().forEach(e -> output.accept(e.get()));
                    })
                    .build());
        });
    }

    @SubscribeEvent
    public void rightClickEvent(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = MiningGadget.getGadget(event.getEntity());
        if( stack.getItem() instanceof MiningGadget ) {
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
        MinecraftForge.EVENT_BUS.register(ClientEvents.class);
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
