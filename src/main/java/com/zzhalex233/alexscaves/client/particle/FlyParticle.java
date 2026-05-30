package com.zzhalex233.alexscaves.client.particle;

import java.util.Random;

import com.zzhalex233.alexscaves.AlexsCaves;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FlyParticle extends Particle {
    private static final ResourceLocation FLY_0 = new ResourceLocation(AlexsCaves.MODID, "particle/fly_0");
    private static final ResourceLocation FLY_1 = new ResourceLocation(AlexsCaves.MODID, "particle/fly_1");
    private final double orbitX;
    private final double orbitY;
    private final double orbitZ;
    private final Vec3d orbitOffset;
    private final boolean reverseOrbit;
    private final float orbitSpeed;

    private FlyParticle(World world, double x, double y, double z, double orbitX, double orbitY, double orbitZ) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        particleScale *= 1.0F + rand.nextFloat() * 0.3F;
        particleMaxAge = rand.nextInt(10) + 40;
        canCollide = true;
        this.orbitX = orbitX;
        this.orbitY = orbitY;
        this.orbitZ = orbitZ;
        this.orbitOffset = new Vec3d((0.5F - rand.nextFloat()) * 2.0F, 0.0D, (0.5F - rand.nextFloat()) * 2.0F);
        this.reverseOrbit = rand.nextBoolean();
        this.orbitSpeed = 3.0F + rand.nextFloat() * 3.0F;
        setParticleTexture(texture(FLY_0));
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        setParticleTexture(texture(particleAge % 4 >= 2 ? FLY_1 : FLY_0));
        if (particleAge++ >= particleMaxAge) {
            setExpired();
            return;
        }
        Vec3d target = getOrbitPosition(particleAge);
        Vec3d movement = target.subtract(posX, posY, posZ).normalize().scale(0.1D);
        motionX = movement.x + rand.nextGaussian() * 0.015D;
        motionY += movement.y + rand.nextGaussian() * 0.015D;
        if (onGround) {
            motionY += 0.3D;
        }
        motionZ += movement.z + rand.nextGaussian() * 0.015D;
        move(motionX, motionY, motionZ);
        motionX *= 0.8D;
        motionY *= 0.8D;
        motionZ *= 0.8D;
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    private Vec3d getOrbitPosition(float angle) {
        float radians = angle * (reverseOrbit ? -orbitSpeed : orbitSpeed) * ((float) Math.PI / 180.0F);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double x = orbitOffset.x * cos + orbitOffset.z * sin;
        double z = orbitOffset.z * cos - orbitOffset.x * sin;
        return new Vec3d(orbitX + x, orbitY + orbitOffset.y, orbitZ + z);
    }

    private TextureAtlasSprite texture(ResourceLocation location) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
    }

    public static void spawn(World world, BlockPos pos, Random rand) {
        double x = pos.getX() + 0.5D + rand.nextFloat() - 0.5D;
        double y = pos.getY() + 1.2D + rand.nextFloat() * 0.5D;
        double z = pos.getZ() + 0.5D + rand.nextFloat() - 0.5D;
        Minecraft.getMinecraft().effectRenderer.addEffect(new FlyParticle(world, x, y, z, x, y, z));
    }
}
