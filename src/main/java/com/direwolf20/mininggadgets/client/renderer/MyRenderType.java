package com.direwolf20.mininggadgets.client.renderer;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;

public final class MyRenderType {
    private static final Identifier LASER_BEAM =
            Identifier.fromNamespaceAndPath(MiningGadgets.MOD_ID, "textures/misc/laser.png");
    private static final Identifier LASER_BEAM_2 =
            Identifier.fromNamespaceAndPath(MiningGadgets.MOD_ID, "textures/misc/laser2.png");
    private static final Identifier LASER_BEAM_GLOW =
            Identifier.fromNamespaceAndPath(MiningGadgets.MOD_ID, "textures/misc/laser_glow.png");

    private MyRenderType() {
    }

    public static final RenderType LASER_MAIN_BEAM =
            RenderType.create("MiningLaserMainBeam", RenderSetup.builder(RenderPipelines.END_CRYSTAL_BEAM)
                    .withTexture("Sampler0", LASER_BEAM_2)
                    .useLightmap()
                    .setOutline(RenderSetup.OutlineProperty.NONE)
                    .createRenderSetup());

    public static final RenderType LASER_MAIN_ADDITIVE =
            RenderType.create("MiningLaserMainBeam", RenderSetup.builder(RenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE)
                    .useOverlay()
                    .affectsCrumbling()
                    .sortOnUpload()
                    .withTexture("Sampler0", LASER_BEAM_GLOW)
                    .useLightmap()
                    .setOutline(RenderSetup.OutlineProperty.NONE)
                    .createRenderSetup());

    public static final RenderType LASER_MAIN_CORE =
            RenderType.create("MiningLaserCoreBeam",  RenderSetup.builder(RenderPipelines.BEACON_BEAM_TRANSLUCENT)
                    .withTexture("Sampler0", LASER_BEAM)
                    .sortOnUpload()
                    .createRenderSetup());

    public static final RenderType BLOCK_OVERLAY =
            RenderTypes.debugQuads();

    public static final RenderType RENDER_BLOCK = RenderType.create("MiningLaserRenderBlock",
            RenderSetup.builder(RenderPipelines.TRANSLUCENT_BLOCK)
                    .withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS)
                    .useLightmap()
                    .useOverlay()
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .createRenderSetup());

}