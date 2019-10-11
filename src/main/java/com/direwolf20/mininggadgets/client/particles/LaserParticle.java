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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

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
    private UUID playerUUID;
    private double sourceX;
    private double sourceY;
    private double sourceZ;

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
        RenderBlockTileEntity te = (RenderBlockTileEntity) world.getTileEntity(new BlockPos(this.posX, this.posY, this.posZ));
        if (!(te == null))
            playerUUID = te.getPlayerUUID();
        sourceX = d;
        sourceY = d1;
        sourceZ = d2;
    }


    @Override
    public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
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
        float speed = 0.1f;
        double moveX;
        double moveY;
        double moveZ;

        PlayerEntity player = world.getPlayerByUuid(this.playerUUID);
        Vec3d playerPos = player.getPositionVec().add(0, player.getEyeHeight() - 0.35, 0);

        Vec3d look = player.getLookVec(); // or getLook(partialTicks)
        Vec3d right = new Vec3d(-look.z, 0, look.x).normalize();
        right = right.scale(0.25f);
        Vec3d rightPos = playerPos.add(right);
        playerPos = rightPos;


        if (particleToPlayer(player)) {
            Vec3d partPos = new Vec3d(this.posX, this.posY, this.posZ);
            double distance = playerPos.distanceTo(partPos);
            if (distance < 0.25) {
                this.setExpired();
            }
            moveX = (playerPos.getX() - this.posX) / 20;
            moveY = (playerPos.getY() - this.posY) / 20;
            moveZ = (playerPos.getZ() - this.posZ) / 20;
        } else {
            Vec3d partPos = new Vec3d(this.posX, this.posY, this.posZ);
            Vec3d sourcePos = new Vec3d(sourceX, sourceY, sourceZ);
            double distance = sourcePos.distanceTo(partPos);
            if (distance < 0.75) {
                this.setExpired();
            }
            moveX = (sourceX - this.posX) / 20;
            moveY = (sourceY - this.posY) / 20;
            moveZ = (sourceZ - this.posZ) / 20;
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
