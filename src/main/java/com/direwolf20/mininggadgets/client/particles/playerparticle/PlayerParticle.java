package com.direwolf20.mininggadgets.client.particles.playerparticle;

import com.direwolf20.mininggadgets.MiningGadgets;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class PlayerParticle extends SpriteTexturedParticle {
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
    protected final IAnimatedSprite spriteSet;

    public static final ResourceLocation iceParticle = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/particle/snowflake1.png");
    public static final ResourceLocation iceParticle2 = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/particle/snowflake2.png");
    public static final ResourceLocation iceParticle3 = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/particle/snowflake3.png");
    public static final ResourceLocation lightParticle = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/particle/lightparticle.png");


    public PlayerParticle(World world, double sourceX, double sourceY, double sourceZ, double targetX, double targetY, double targetZ, double xSpeed, double ySpeed, double zSpeed,
                          float size, float red, float green, float blue, boolean collide, float maxAge, String particleType, IAnimatedSprite sprite) {
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
        particlePicker = rand.nextInt(3) + 1;
        this.spriteSet = sprite;
        this.setSprite(sprite.get(particlePicker, 4));
    }

    /*@Override
    public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }*/

    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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

        if (age > 40)
            //if (world.getBlockState(nextPos).getBlock() == ModBlocks.RENDERBLOCK)
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

}
