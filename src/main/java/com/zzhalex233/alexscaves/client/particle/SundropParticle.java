package com.zzhalex233.alexscaves.client.particle;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SundropParticle extends Particle {
    private static final ResourceLocation SUNDROP = new ResourceLocation(AlexsCaves.MODID, "particle/sundrop");
    private static final Set<BlockPos> ACTIVE_POSITIONS = new HashSet<>();
    private final BlockPos blockPos;
    private final float initialScale;

    private SundropParticle(World world, double x, double y, double z, BlockPos blockPos) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.blockPos = blockPos;
        this.initialScale = 1.1F + rand.nextFloat() * 0.4F;
        this.particleScale = initialScale;
        this.particleMaxAge = 80 + rand.nextInt(40);
        this.particleAlpha = 0.0F;
        this.particleGravity = 0.0F;
        this.canCollide = false;
        setSize(2.5F, 2.5F);
        setParticleTexture(texture(SUNDROP));
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        float progress = particleAge / (float) particleMaxAge;
        float flicker = 0.5F + ((float) Math.sin(particleAge * 0.2F) + 1.0F) * 0.25F;
        particleAlpha = flicker * (float) Math.sin(Math.sqrt(progress) * Math.PI);
        particleScale = initialScale + flicker * 0.1F;
        if (particleAge++ >= particleMaxAge || world.getBlockState(blockPos).getBlock() != ACBlockRegistry.SUNDROP.block()) {
            setExpired();
            return;
        }
        move(motionX, motionY, motionZ);
        motionX *= 0.99D;
        motionY *= 0.99D;
        motionZ *= 0.99D;
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        return 0xF000F0;
    }

    @Override
    public void setExpired() {
        super.setExpired();
        ACTIVE_POSITIONS.remove(blockPos);
    }

    private TextureAtlasSprite texture(ResourceLocation location) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
    }

    public static void spawn(World world, BlockPos pos, Random rand) {
        BlockPos blockPos = pos.toImmutable();
        if (!ACTIVE_POSITIONS.add(blockPos)) {
            return;
        }
        Minecraft.getMinecraft().effectRenderer.addEffect(new SundropParticle(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, blockPos));
    }
}
