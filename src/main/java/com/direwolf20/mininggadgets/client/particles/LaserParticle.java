package com.direwolf20.mininggadgets.client.particles;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.BreakingParticle;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class LaserParticle extends BreakingParticle {
    private static final ResourceLocation vanillaParticles = new ResourceLocation("textures/particle/particles.png");

    protected float particleScale = (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
    // Queue values
    private float f;
    private float f1;
    private float f2;
    private float f3;
    private float f4;
    private float f5;
    private BlockState blockState;

    public LaserParticle(World world, double d, double d1, double d2, double xSpeed, double ySpeed, double zSpeed,
                         float size, float red, float green, float blue, boolean depthTest, float maxAgeMul, BlockState blockState) {
        this(world, d, d1, d2, xSpeed, ySpeed, zSpeed, size, red, green, blue, depthTest, maxAgeMul);
        this.blockState = blockState;
        this.setSprite(Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getTexture(blockState));
    }

    public LaserParticle(World world, double d, double d1, double d2, double xSpeed, double ySpeed, double zSpeed,
                         float size, float red, float green, float blue, boolean depthTest, float maxAgeMul) {
        super(world, d, d1, d2, ItemStack.EMPTY);
        // super applies wiggle to motion so set it here instead
        motionX = xSpeed;
        motionY = ySpeed;
        motionZ = zSpeed;
        particleRed = red;
        particleGreen = green;
        particleBlue = blue;
        particleGravity = 0;
        particleScale *= size;
        moteParticleScale = particleScale;
        maxAge = Math.round(maxAgeMul);
        this.depthTest = depthTest;

        moteHalfLife = maxAge / 2;
        setSize(0.1F, 0.1F);

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
    }


    @Override
    public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        //BlockState renderState = Blocks.COBBLESTONE.getDefaultState();
        //float scale = 0.125f;

        /*GlStateManager.pushMatrix();
        //GlStateManager.enableBlend();
        //This blend function allows you to use a constant alpha, which is defined later
        //GlStateManager.blendFunc(GL14.GL_CONSTANT_ALPHA, GL14.GL_ONE_MINUS_CONSTANT_ALPHA);
        Vec3d playerPos = new Vec3d(TileEntityRendererDispatcher.staticPlayerX, TileEntityRendererDispatcher.staticPlayerY, TileEntityRendererDispatcher.staticPlayerZ);

        //BlockPos pos = entityIn.getBlockPos();
        GlStateManager.translated(-playerPos.x, -playerPos.y, -playerPos.z);
        GlStateManager.translated(this.posX, this.posY, this.posZ);
        GlStateManager.scalef(scale,scale,scale);
        GlStateManager.translated(-0.5f, -0.5f, -0.5f);
        GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
        GL14.glBlendColor(1F, 1F, 1F, 1f); //Set the alpha of the blocks we are rendering
        try {
            blockrendererdispatcher.renderBlockBrightness(renderState, 1.0f);
        } catch (Throwable t) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            try {
                // If the buffer is already not drawing then it'll throw
                // and IllegalStateException... Very rare
                bufferBuilder.finishDrawing();
            } catch (IllegalStateException ex) {

            }
        }
        //Disable blend
        //GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        //GlStateManager.disableBlend();
        GlStateManager.popMatrix();*/
    }

    /*@Nonnull
    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.CUSTOM;
    }*/

    // [VanillaCopy] of super, without drag when onGround is true
    @Override
    public void tick() {
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.move(motionX, motionY, motionZ);
        /*if (this.age++ >= this.maxAge)
        {
            this.setExpired();
        }

        this.motionY -= 0.04D * (double)this.particleGravity;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;*/
    }

    public void setGravity(float value) {
        particleGravity = value;
    }

    public void setSpeed(float mx, float my, float mz) {
        motionX = mx;
        motionY = my;
        motionZ = mz;
    }

    public static IParticleFactory<LaserParticleData> FACTORY =
            (data, world, x, y, z, xSpeed, ySpeed, zSpeed) ->
                    new LaserParticle(world, x, y, z, xSpeed, ySpeed, zSpeed, data.size, data.r, data.g, data.b, data.depthTest, data.maxAgeMul, data.state);

    private boolean depthTest;
    private final float moteParticleScale;
    private final int moteHalfLife;
}
