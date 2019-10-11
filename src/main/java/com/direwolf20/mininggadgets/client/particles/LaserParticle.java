package com.direwolf20.mininggadgets.client.particles;

import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.BreakingParticle;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public class LaserParticle extends BreakingParticle {
    private static final ResourceLocation vanillaParticles = new ResourceLocation("textures/particle/particles.png");

    //protected float particleScale = (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
    // Queue values
    private float f;
    private float f1;
    private float f2;
    private float f3;
    private float f4;
    private float f5;
    private BlockState blockState;
    private UUID playerUUID;
    private double sourceX;
    private double sourceY;
    private double sourceZ;
    private int speedModifier;

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
        setSize(0.001F, 0.001F);

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        RenderBlockTileEntity te = (RenderBlockTileEntity) world.getTileEntity(new BlockPos(this.posX, this.posY, this.posZ));
        if (te != null)
            playerUUID = te.getPlayerUUID();
        sourceX = d;
        sourceY = d1;
        sourceZ = d2;
        this.canCollide = false;
    }


    @Override
    public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        /*float f = this.getScale(partialTicks);
        float f1 = this.getMinU();
        float f2 = this.getMaxU();
        float f3 = this.getMinV();
        float f4 = this.getMaxV();
        float f5 = (float)(MathHelper.lerp((double)partialTicks, this.prevPosX, this.posX) - interpPosX);
        float f6 = (float)(MathHelper.lerp((double)partialTicks, this.prevPosY, this.posY) - interpPosY);
        float f7 = (float)(MathHelper.lerp((double)partialTicks, this.prevPosZ, this.posZ) - interpPosZ);
        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & '\uffff';
        int k = i & '\uffff';
        Vec3d[] avec3d = new Vec3d[]{new Vec3d((double)(-rotationX * f - rotationXY * f), (double)(-rotationZ * f), (double)(-rotationYZ * f - rotationXZ * f)), new Vec3d((double)(-rotationX * f + rotationXY * f), (double)(rotationZ * f), (double)(-rotationYZ * f + rotationXZ * f)), new Vec3d((double)(rotationX * f + rotationXY * f), (double)(rotationZ * f), (double)(rotationYZ * f + rotationXZ * f)), new Vec3d((double)(rotationX * f - rotationXY * f), (double)(-rotationZ * f), (double)(rotationYZ * f - rotationXZ * f))};
        if (this.particleAngle != 0.0F) {
            float f8 = MathHelper.lerp(partialTicks, this.prevParticleAngle, this.particleAngle);
            float f9 = MathHelper.cos(f8 * 0.5F);
            float f10 = (float)((double)MathHelper.sin(f8 * 0.5F) * entityIn.getLookDirection().x);
            float f11 = (float)((double)MathHelper.sin(f8 * 0.5F) * entityIn.getLookDirection().y);
            float f12 = (float)((double)MathHelper.sin(f8 * 0.5F) * entityIn.getLookDirection().z);
            Vec3d vec3d = new Vec3d((double)f10, (double)f11, (double)f12);

            for(int l = 0; l < 4; ++l) {
                avec3d[l] = vec3d.scale(2.0D * avec3d[l].dotProduct(vec3d)).add(avec3d[l].scale((double)(f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(avec3d[l]).scale((double)(2.0F * f9)));
            }
        }

        buffer.pos((double)f5 + avec3d[0].x, (double)f6 + avec3d[0].y, (double)f7 + avec3d[0].z).tex((double)f2, (double)f4).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double)f5 + avec3d[1].x, (double)f6 + avec3d[1].y, (double)f7 + avec3d[1].z).tex((double)f2, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double)f5 + avec3d[2].x, (double)f6 + avec3d[2].y, (double)f7 + avec3d[2].z).tex((double)f1, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double)f5 + avec3d[3].x, (double)f6 + avec3d[3].y, (double)f7 + avec3d[3].z).tex((double)f1, (double)f4).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
*/
    }

    public boolean particleToPlayer(PlayerEntity player) {
        boolean partToPlayer = false;
        //if (player.isHandActive()) partToPlayer = true;
        BlockPos sourcePos = new BlockPos(sourceX, sourceY, sourceZ);
        if (!(world.getBlockState(sourcePos) == this.blockState)) partToPlayer = true;
        TileEntity te = world.getTileEntity(sourcePos);
        if (te != null && te instanceof RenderBlockTileEntity) {
            if (((RenderBlockTileEntity) te).getTicksSinceMine() >= 10) {
                partToPlayer = false;
            }
        }
        //if (world.getBlockState(sourcePos).getBlock() instanceof RenderBlock) partToPlayer = true;
        return partToPlayer;
    }

    // [VanillaCopy] of super, without drag when onGround is true
    @Override
    public void tick() {
        double moveX;
        double moveY;
        double moveZ;
        //this.particleScale = 0.5f;
        //double getPartScale = this.getScale(0);

        if (this.playerUUID == null) {
            this.setExpired();
            return;
        }
        PlayerEntity player = world.getPlayerByUuid(this.playerUUID);
        Vec3d playerPos = player.getPositionVec().add(0, player.getEyeHeight(), 0);
        Vec3d blockPos = new Vec3d(sourceX, sourceY, sourceZ);
        Vec3d look = player.getLookVec(); // or getLook(partialTicks)
        Vec3d right = new Vec3d(-look.z, 0, look.x).normalize();
        Vec3d forward = look;
        Vec3d down = right.crossProduct(forward);
        right = right.scale(0.65f);
        forward = forward.scale(1f);
        down = down.scale(-0.35);
        Vec3d rightPos = playerPos.add(right);
        rightPos = rightPos.add(forward);
        rightPos = rightPos.add(down);
        playerPos = rightPos;
        double totalDistance = blockPos.distanceTo(playerPos);

        if (particleToPlayer(player)) {
            speedModifier++;
            int speedAdjust = (20 - speedModifier) <= 0 ? 1 : (20 - speedModifier);
            Vec3d partPos = new Vec3d(this.posX, this.posY, this.posZ);
            double distance = playerPos.distanceTo(partPos);
            if (distance < 0.25) {
                this.setExpired();
            }
            this.particleScale = particleScale * MathHelper.lerp(1 - (float) distance / (float) totalDistance, 1.2f, 0.55f);
            //this.particleScale = MathHelper.lerp(1- (float) distance/(float)totalDistance, 0.09f, 0.005f);
            moveX = (playerPos.getX() - this.posX) / speedAdjust;
            moveY = (playerPos.getY() - this.posY) / speedAdjust;
            moveZ = (playerPos.getZ() - this.posZ) / speedAdjust;
        } else {
            speedModifier = 0;
            int speedAdjust = (20 - speedModifier) <= 0 ? 1 : (20 - speedModifier);
            Vec3d partPos = new Vec3d(this.posX, this.posY, this.posZ);
            Vec3d sourcePos = new Vec3d(sourceX, sourceY, sourceZ);
            double distance = sourcePos.distanceTo(partPos);
            if (distance < 0.75) {
                this.setExpired();
            }
            moveX = (sourceX - this.posX) / speedAdjust;
            moveY = (sourceY - this.posY) / speedAdjust;
            moveZ = (sourceZ - this.posZ) / speedAdjust;

        }
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.move(moveX, moveY, moveZ);
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
