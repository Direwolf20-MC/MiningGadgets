package com.direwolf20.mininggadgets.client.particles.laserparticle;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.tiles.RenderBlockTileEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class LaserParticle extends BreakingItemParticle {
    public static final ParticleProvider<LaserParticleData> FACTORY = (data, world, x, y, z, xSpeed, ySpeed, zSpeed) ->
                    new LaserParticle(world, x, y, z, xSpeed, ySpeed, zSpeed, data.size, data.depthTest, data.maxAgeMul, data.state);

    private BlockState blockState;
    private UUID playerUUID;
    private int speedModifier;
    private boolean voiding = false;

    private final double sourceX;
    private final double sourceY;
    private final double sourceZ;
    private final float originalSize;

    public LaserParticle(ClientLevel world, double d, double d1, double d2, double xSpeed, double ySpeed, double zSpeed,
                         float size, boolean depthTest, float maxAgeMul, BlockState blockState) {
        this(world, d, d1, d2, xSpeed, ySpeed, zSpeed, size, depthTest, maxAgeMul, new ItemStack(blockState.getBlock()));

        this.blockState = blockState;

        if (this.blockState.getBlock() == Blocks.GRASS_BLOCK) {
            rCol = 1;
            gCol = 1;
            bCol = 1;
            return;
        }

        // This isn't a perfect solution because of the above, but I'm unsure how you'd actually only apply a tint to the gray scale part
        // of the asset instead of it applying to the entire texture
        BlockColors blockColors = Minecraft.getInstance().getBlockColors();

        int color = blockColors.getColor(this.blockState, this.level, new BlockPos(d, d1, d2), 0);
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;

        rCol = f;
        gCol = f1;
        bCol = f2;
    }

    public LaserParticle(ClientLevel world, double d, double d1, double d2, double xSpeed, double ySpeed, double zSpeed,
                         float size, boolean depthTest, float maxAgeMul, ItemStack stack) {
        super(world, d, d1, d2, stack);
        // super applies wiggle to motion so set it here instead
        xd = xSpeed;
        yd = ySpeed;
        zd = zSpeed;

        gravity = 0;
        quadSize *= size;
        originalSize = quadSize;
        lifetime = Math.round(maxAgeMul);

        setSize(0.001F, 0.001F);

        xo = x;
        yo = y;
        zo = z;
        RenderBlockTileEntity te = (RenderBlockTileEntity) world.getBlockEntity(new BlockPos(this.x, this.y, this.z));
        if (te != null) {
            playerUUID = te.getPlayerUUID();
            voiding = !te.getBlockAllowed();
        }
        sourceX = d;
        sourceY = d1;
        sourceZ = d2;
        this.hasPhysics = false;
    }

    @Override
    public void render(VertexConsumer builder, Camera activeRenderInfo, float partialTicks) {
        super.render(builder, activeRenderInfo, partialTicks);
    }

    public boolean particleToPlayer(Player player) {
        boolean partToPlayer = false;
        //if (player.isHandActive()) partToPlayer = true;
        BlockPos sourcePos = new BlockPos(sourceX, sourceY, sourceZ);
        if (!(level.getBlockState(sourcePos) == this.blockState)) partToPlayer = true;
        BlockEntity te = level.getBlockEntity(sourcePos);
        if (te != null && te instanceof RenderBlockTileEntity) {
            if (((RenderBlockTileEntity) te).getTicksSinceMine() >= 5) {
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
            this.remove();
            return;
        }
        //Some calculations for the particle motion
        Player player = level.getPlayerByUUID(this.playerUUID);
        if (player == null) {
            this.remove();
            return;
        }
        Vec3 playerPos = player.position().add(0, player.getEyeHeight(), 0);
        Vec3 blockPos = new Vec3(sourceX, sourceY, sourceZ);
        Vec3 look = player.getLookAngle(); // or getLook(partialTicks)
        //The next 3 variables are directions on the screen relative to the players look direction. So right = to the right of the player, regardless of facing direction.
        Vec3 right = new Vec3(-look.z, 0, look.x).normalize();
        Vec3 forward = look;
        Vec3 down = right.cross(forward);

        //These are used to calculate where the particles are going. We want them going into the laser, so we move the destination right, down, and forward a bit.
        right = right.scale(0.65f);
        forward = forward.scale(0.85f);
        down = down.scale(-0.35);

        //Take the player's eye position, and shift it to where the end of the laser is (Roughly)
        Vec3 laserPos = playerPos.add(right);
        laserPos = laserPos.add(forward);
        laserPos = laserPos.add(down);

        //Get the current position of the particle, and figure out the vector of where it's going
        Vec3 partPos = new Vec3(this.x, this.y, this.z);
        Vec3 targetDirection = new Vec3(laserPos.x() - this.x, laserPos.y() - this.y, laserPos.z() - this.z);

        //The total distance between the laser's endpoint and the block(s) we're mining
        double totalDistance = blockPos.distanceTo(laserPos);

        //Figure out if the particles are flowing TO the player, or BACK to the blocks
        if (particleToPlayer(player)) {
            //This is like age, how many ticks the thing has been around, but we reset it when we send particles back to their source so can't use age.
            speedModifier++;
            //Basically we want it to get faster the longer its been around, up to a limit
            int speedAdjust = (30 - speedModifier) <= 0 ? 1 : (30 - speedModifier);
            //Get the distance between the laser (endpoint) and current particle position
            double distance = laserPos.distanceTo(partPos);
            //Remove the particle from the game if its really close to the laser endpoint.
            if (distance < 0.25) {
                this.remove();
            }
            //Apply the spinning effect, but only if the particle has been around for a bit, and slow the spin it gets closer to player.
            if (age > 5) {
                float spinSpeed = Mth.lerp(1 - (float) distance / (float) totalDistance, 1.1f, 0.05f);
                targetDirection = targetDirection.add(targetDirection.cross(look).scale(spinSpeed).multiply(3, 3, 3));
            }
            //Change particle size as it gets closer to player.
            this.quadSize = quadSize * Mth.lerp(1 - (float) distance / (float) totalDistance, 1.05f, 0.85f);
            //Calculate where the particle's next position should be.
            moveX = (targetDirection.x()) / speedAdjust;
            moveY = (targetDirection.y()) / speedAdjust;
            moveZ = (targetDirection.z()) / speedAdjust;
            //If the particle is less than 5 ticks old, rapidly move the particles towards the player's look position
            //This is what clumps them together early on. Comment this out if you wanna see the difference without.
            ItemStack heldItem = MiningGadget.getGadget(player);
            if (heldItem.getItem() instanceof MiningGadget && MiningProperties.getRange(heldItem) > 1) {
                if (age < 5) {
                    int compressionFactor = 7;
                    moveX = moveX * ((1 - Math.abs(look.x)) * compressionFactor);
                    moveY = moveY * ((1 - Math.abs(look.y)) * compressionFactor);
                    moveZ = moveZ * ((1 - Math.abs(look.z)) * compressionFactor);
                }
            }
        } else {
            //What to do if we are sending the particles BACK to the source block, mostly similiar to the above. Much less flair.
            speedModifier = 0;
            int speedAdjust = (20 - speedModifier) <= 0 ? 1 : (20 - speedModifier);
            double distance = blockPos.distanceTo(partPos);
            if (distance < 0.75) {
                this.remove();
            }
            moveX = (sourceX - this.x) / speedAdjust;
            moveY = (sourceY - this.y) / speedAdjust;
            moveZ = (sourceZ - this.z) / speedAdjust;

        }
        //Just in case something goes weird, we remove the particle if its been around too long.
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
        //prevPos is used in the render. if you don't do this your particle rubber bands (Like lag in an MMO).
        //This is used because ticks are 20 per second, and FPS is usually 60 or higher.
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (voiding && age > 10 && age <= 15) {
            float darkness = Mth.lerp((age - 10) / 5f, 1, 0);
            this.rCol = darkness;
            this.gCol = darkness;
            this.bCol = darkness;
        }
        if (voiding && age > 15 && age <= 25) {
            float fade = Mth.lerp(((age - 15f) / 10f), 1, 0f);
            this.quadSize = this.originalSize * fade;
        }
        /*
        if (voiding && age > 15) {
            this.particleAlpha = 0.25f;
        }
        if (voiding && age > 20) {
            this.particleAlpha = 0f;
        }*/

        //Perform the ACTUAL move of the particle.
        this.move(moveX, moveY, moveZ);
    }
}
