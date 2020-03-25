package com.direwolf20.mininggadgets.client;

import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;

public class ModRenders {
    public static void register() {
        RenderType rendertype1 = RenderType.getCutout();
        RenderTypeLookup.setRenderLayer(ModBlocks.MARKER_BLOCK.get(), rendertype1);
    }
}
