package com.direwolf20.mininggadgets.client;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.client.renderer.RenderBlockTER;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

/**
 * Only put client code here plz.
 */
public class ClientSetup {
    public static KeyBinding gadgetMenu;

    public static void setup() {
        gadgetMenu = new KeyBinding("Open gadgets settings", GLFW.GLFW_KEY_G, MiningGadgets.MOD_ID);

        ClientRegistry.registerKeyBinding(gadgetMenu);
    }

    /**
     * Client Registry for renders
     */
    public static void registerRenderers() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(event ->
                ClientRegistry.bindTileEntitySpecialRenderer(RenderBlockTileEntity.class, new RenderBlockTER()));
    }
}
