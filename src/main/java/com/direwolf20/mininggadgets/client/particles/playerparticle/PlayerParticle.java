package com.direwolf20.mininggadgets.client.particles.playerparticle;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class PlayerParticle extends TextureSheetParticle {
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
    protected final SpriteSet spriteSet;

    public static final ResourceLocation iceParticle = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/particle/snowflake1.png");
    public static final ResourceLocation iceParticle2 = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/particle/snowflake2.png");
    public static final ResourceLocation iceParticle3 = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/particle/snowflake3.png");
    public static final ResourceLocation lightParticle = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/particle/lightparticle.png");


    public PlayerParticle(ClientLevel world, double sourceX, double sourceY, double sourceZ, double targetX, double targetY, double targetZ, double xSpeed, double ySpeed, double zSpeed,
                          float size, float red, float green, float blue, boolean collide, float maxAge, String particleType, SpriteSet sprite) {
        super(world, sourceX, sourceY, sourceZ);
        xd = xSpeed;
        yd = ySpeed;
        zd = zSpeed;
        rCol = red;
        gCol = green;
        bCol = blue;
        gravity = 0;
        this.lifetime = Math.round(maxAge);

        setSize(0.001F, 0.001F);

        xo = x;
        yo = y;
        zo = z;
        this.quadSize = size;
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.sourceZ = sourceZ;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.hasPhysics = collide;
        this.particleType = particleType;
        this.setGravity(0f);
        particlePicker = rand.nextInt(3) + 1;
        this.spriteSet = sprite;
        this.setSprite(sprite.get(particlePicker, 4));
    }

    @Override
    public void render(VertexConsumer p_225606_1_, Camera p_225606_2_, float p_225606_3_) {
        super.render(p_225606_1_, p_225606_2_, p_225606_3_);
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    // [VanillaCopy] of super, without drag when onGround is true
    @Override
    public void tick() {
        //System.out.println("I exist!" + posX+":"+posY+":"+posZ +"....."+targetX+":"+targetY+":"+targetZ);
        double moveX;
        double moveY;
        double moveZ;

        //Just in case something goes weird, we remove the particle if its been around too long.
        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        //prevPos is used in the render. if you don't do this your particle rubber bands (Like lag in an MMO).
        //This is used because ticks are 20 per second, and FPS is usually 60 or higher.
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        Vec3 sourcePos = new Vec3(sourceX, sourceY, sourceZ);
        Vec3 targetPos = new Vec3(targetX, targetY, targetZ);

        //Get the current position of the particle, and figure out the vector of where it's going
        Vec3 partPos = new Vec3(this.x, this.y, this.z);
        Vec3 targetDirection = new Vec3(targetPos.x() - this.x, targetPos.y() - this.y, targetPos.z() - this.z);

        //The total distance between the particle and target
        double totalDistance = targetPos.distanceTo(partPos);
        if (totalDistance < 0.1)
            this.remove();

        double speedAdjust = 20;

        moveX = (targetX - this.x) / speedAdjust;
        moveY = (targetY - this.y) / speedAdjust;
        moveZ = (targetZ - this.z) / speedAdjust;

        BlockPos nextPos = new BlockPos(this.x + moveX, this.y + moveY, this.z + moveZ);

        if (age > 40)
            //if (world.getBlockState(nextPos).getBlock() == ModBlocks.RENDERBLOCK)
            this.hasPhysics = false;
        //Perform the ACTUAL move of the particle.
        this.move(moveX, moveY, moveZ);
    }

    public void setGravity(float value) {
        gravity = value;
    }

    public void setSpeed(float mx, float my, float mz) {
        xd = mx;
        yd = my;
        zd = mz;
    }

}
