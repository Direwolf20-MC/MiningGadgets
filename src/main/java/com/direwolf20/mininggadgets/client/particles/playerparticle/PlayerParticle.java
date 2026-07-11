package com.direwolf20.mininggadgets.client.particles.playerparticle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class PlayerParticle extends SingleQuadParticle {
    private final double sourceX;
    private final double sourceY;
    private final double sourceZ;
    private final double targetX;
    private final double targetY;
    private final double targetZ;
    private final String particleType;
    private final Random rand = new Random();
    private final int particlePicker;
    protected final SpriteSet spriteSet;

    public PlayerParticle(ClientLevel level, double sourceX, double sourceY, double sourceZ, double targetX, double targetY, double targetZ, double xSpeed, double ySpeed, double zSpeed, float size,
            float red, float green, float blue, boolean collide, float maxAge, String particleType, SpriteSet spriteSet) {
        super(level, sourceX, sourceY, sourceZ, xSpeed, ySpeed, zSpeed, spriteSet.get(0, 4));

        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.rCol = red;
        this.gCol = green;
        this.bCol = blue;
        this.gravity = 0.0F;
        this.lifetime = Math.round(maxAge);

        this.setSize(0.001F, 0.001F);

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.quadSize = size;
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.sourceZ = sourceZ;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.hasPhysics = collide;
        this.particleType = particleType;
        this.setGravity(0.0F);

        this.particlePicker = rand.nextInt(3) + 1;
        this.spriteSet = spriteSet;
        this.setSprite(spriteSet.get(this.particlePicker, 4));
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.SINGLE_QUADS;
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    @Override
    public void tick() {
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        Vec3 targetPos = new Vec3(targetX, targetY, targetZ);
        Vec3 partPos = new Vec3(this.x, this.y, this.z);

        double totalDistance = targetPos.distanceTo(partPos);
        if (totalDistance < 0.1D) {
            this.remove();
            return;
        }

        double speedAdjust = 20.0D;

        double moveX = (targetX - this.x) / speedAdjust;
        double moveY = (targetY - this.y) / speedAdjust;
        double moveZ = (targetZ - this.z) / speedAdjust;

        if (this.age > 40) {
            this.hasPhysics = false;
        }

        this.move(moveX, moveY, moveZ);
    }

    public void setGravity(float value) {
        this.gravity = value;
    }

    public void setSpeed(float mx, float my, float mz) {
        this.xd = mx;
        this.yd = my;
        this.zd = mz;
    }
}