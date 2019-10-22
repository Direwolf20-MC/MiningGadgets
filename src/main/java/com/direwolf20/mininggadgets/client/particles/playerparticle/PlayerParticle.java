package com.direwolf20.mininggadgets.client.particles.playerparticle;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.common.blocks.ModBlocks;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class PlayerParticle extends Particle {
    private double sourceX;
    private double sourceY;
    private double sourceZ;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int speedModifier;
    private String particleType;
    private Random rand = new Random();
    private int particlePicker;
    private float particleScale;

    public static final ResourceLocation iceParticle = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/particle/snowflake1.png");
    public static final ResourceLocation iceParticle2 = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/particle/snowflake2.png");
    public static final ResourceLocation iceParticle3 = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/particle/snowflake3.png");
    public static final ResourceLocation lightParticle = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/particle/lightparticle.png");

    public PlayerParticle(World world, double sourceX, double sourceY, double sourceZ, double targetX, double targetY, double targetZ, double xSpeed, double ySpeed, double zSpeed,
                          float size, float red, float green, float blue, boolean collide, float maxAge, String particleType) {
        super(world, sourceX, sourceY, sourceZ);
        motionX = xSpeed;
        motionY = ySpeed;
        motionZ = zSpeed;
        particleRed = red;
        particleGreen = green;
        particleBlue = blue;
        particleGravity = 0;
        this.maxAge = Math.round(maxAge);

        setSize(0.001F, 0.001F);

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        this.particleScale = size;
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.sourceZ = sourceZ;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.canCollide = collide;
        this.particleType = particleType;
        this.setGravity(0f);
        particlePicker = rand.nextInt(3);
    }

    @Override
    public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        //particleScale = 1f;
        //Minecraft.getInstance().getTextureManager().bindTexture(particleType == "ice" ? iceParticle : lightParticle);
        ResourceLocation particleResource = lightParticle;
        if (particleType == "ice") {
            System.out.println(particlePicker);
            switch (particlePicker) {
                case 0:
                    particleResource = iceParticle;
                    break;
                case 1:
                    particleResource = iceParticle2;
                    break;
                case 2:
                    particleResource = iceParticle3;
                    break;
            }
        }
        System.out.println(particleResource);
        Minecraft.getInstance().getTextureManager().bindTexture(particleResource);
        float f10 = 0.5F * particleScale;
        float f11 = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
        float f12 = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
        float f13 = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);
        int combined = 15 << 20 | 15 << 4;
        int k3 = combined >> 16 & 0xFFFF;
        int l3 = combined & 0xFFFF;
        buffer.pos(f11 - rotationX * f10 - rotationXY * f10, f12 - rotationZ * f10, f13 - rotationYZ * f10 - rotationXZ * f10).tex(0, 1).lightmap(k3, l3).color(particleRed, particleGreen, particleBlue, 0.5F).endVertex();
        buffer.pos(f11 - rotationX * f10 + rotationXY * f10, f12 + rotationZ * f10, f13 - rotationYZ * f10 + rotationXZ * f10).tex(1, 1).lightmap(k3, l3).color(particleRed, particleGreen, particleBlue, 0.5F).endVertex();
        buffer.pos(f11 + rotationX * f10 + rotationXY * f10, f12 + rotationZ * f10, f13 + rotationYZ * f10 + rotationXZ * f10).tex(1, 0).lightmap(k3, l3).color(particleRed, particleGreen, particleBlue, 0.5F).endVertex();
        buffer.pos(f11 + rotationX * f10 - rotationXY * f10, f12 - rotationZ * f10, f13 + rotationYZ * f10 - rotationXZ * f10).tex(0, 0).lightmap(k3, l3).color(particleRed, particleGreen, particleBlue, 0.5F).endVertex();

    }

    private static final IParticleRenderType NORMAL_RENDER = new IParticleRenderType() {
        @Override
        public void beginRender(BufferBuilder bufferBuilder, TextureManager textureManager) {
            beginRenderCommon(bufferBuilder, textureManager);
        }

        @Override
        public void finishRender(Tessellator tessellator) {
            tessellator.draw();
            endRenderCommon();
        }

        @Override
        public String toString() {
            return "mininggadgets:playerparticle";
        }
    };

    private static void beginRenderCommon(BufferBuilder bufferBuilder, TextureManager textureManager) {
        GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
        GlStateManager.disableLighting();

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.75F);

        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
    }

    private static void endRenderCommon() {
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GL11.glPopAttrib();
    }

    public IParticleRenderType getRenderType() {
        return NORMAL_RENDER;
    }

    // [VanillaCopy] of super, without drag when onGround is true
    @Override
    public void tick() {
        //System.out.println("I exist!" + posX+":"+posY+":"+posZ +"....."+targetX+":"+targetY+":"+targetZ);
        double moveX;
        double moveY;
        double moveZ;

        //Just in case something goes weird, we remove the particle if its been around too long.
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        }


        //prevPos is used in the render. if you don't do this your particle rubber bands (Like lag in an MMO).
        //This is used because ticks are 20 per second, and FPS is usually 60 or higher.
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        Vec3d sourcePos = new Vec3d(sourceX, sourceY, sourceZ);
        Vec3d targetPos = new Vec3d(targetX, targetY, targetZ);

        //Get the current position of the particle, and figure out the vector of where it's going
        Vec3d partPos = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d targetDirection = new Vec3d(targetPos.getX() - this.posX, targetPos.getY() - this.posY, targetPos.getZ() - this.posZ);

        //The total distance between the particle and target
        double totalDistance = targetPos.distanceTo(partPos);
        if (totalDistance < 0.1)
            this.setExpired();

        double speedAdjust = 20;

        moveX = (targetX - this.posX) / speedAdjust;
        moveY = (targetY - this.posY) / speedAdjust;
        moveZ = (targetZ - this.posZ) / speedAdjust;

        BlockPos nextPos = new BlockPos(this.posX + moveX, this.posY + moveY, this.posZ + moveZ);

        if (world.getBlockState(nextPos).getBlock() == ModBlocks.RENDERBLOCK)
            this.canCollide = false;
        //Perform the ACTUAL move of the particle.
        this.move(moveX, moveY, moveZ);
    }

    public void setGravity(float value) {
        particleGravity = value;
    }

    public void setSpeed(float mx, float my, float mz) {
        motionX = mx;
        motionY = my;
        motionZ = mz;
    }

    public static IParticleFactory<PlayerParticleData> FACTORY =
            (data, world, x, y, z, xSpeed, ySpeed, zSpeed) ->
                    new PlayerParticle(world, x, y, z, data.targetX, data.targetY, data.targetZ, xSpeed, ySpeed, zSpeed, data.size, data.r, data.g, data.b, data.depthTest, data.maxAgeMul, data.partType);

}
