package com.zzhalex233.alexscaves.client.particle;

import com.zzhalex233.alexscaves.AlexsCaves;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AmberMonolithParticle extends Particle {
    private static final ResourceLocation SUNDROP = new ResourceLocation(AlexsCaves.MODID, "particle/sundrop");
    private final Vec3d target;
    private final float initialDistance;

    private AmberMonolithParticle(World world, Vec3d pos, Vec3d target) {
        super(world, pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
        this.target = target;
        this.initialDistance = Math.max(0.1F, (float) target.subtract(pos).length());
        this.particleRed = 1.0F;
        this.particleGreen = 0.69F;
        this.particleBlue = 0.12F;
        this.particleAlpha = 0.35F;
        this.particleScale *= 1.125F;
        this.particleMaxAge = (int) (20.0D / (rand.nextDouble() * 0.8D + 0.2D));
        this.particleGravity = 0.0F;
        this.canCollide = false;
        setParticleTexture(texture(SUNDROP));
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        Vec3d to = target.subtract(posX, posY, posZ);
        if (particleAge++ >= particleMaxAge || to.length() <= 1.0D) {
            setExpired();
            return;
        }
        Vec3d direction = to.length() > 1.0D ? to.normalize() : to;
        motionX += direction.x * 0.05D;
        motionY += direction.y * 0.05D;
        motionZ += direction.z * 0.05D;
        particleAlpha = Math.min(1.0F, particleAlpha + 0.05F);
        particleRed = Math.min(1.0F, particleRed + 0.03F);
        particleGreen = Math.min(1.0F, particleGreen + 0.03F);
        particleBlue = Math.min(1.0F, particleBlue + 0.03F);
        move(motionX, motionY, motionZ);
        motionX *= 0.96D;
        motionY *= 0.96D;
        motionZ *= 0.96D;
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        float glowBy = Math.max(0.0F, Math.min(1.0F, (float) new Vec3d(target.x - posX, target.y - posY, target.z - posZ).length() / initialDistance));
        int light = super.getBrightnessForRender(partialTicks);
        int low = light & 255;
        int high = light >> 16 & 255;
        low = Math.min(240, low + (int) (glowBy * 240.0F));
        return low | high << 16;
    }

    private static TextureAtlasSprite texture(ResourceLocation location) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
    }

    public static void spawn(World world, Vec3d from, Vec3d to) {
        Vec3d distance = to.subtract(from);
        double length = distance.length();
        if (length <= 0.0D) {
            return;
        }
        Vec3d direction = distance.normalize();
        int maxDist = Math.max(1, (int) (length * 1.5D));
        for (int i = 0; i < maxDist; i++) {
            double progress = length * (i / (double) maxDist);
            Vec3d pos = from.add(direction.scale(progress)).add(world.rand.nextFloat() - 0.5F, world.rand.nextFloat() - 0.5F, world.rand.nextFloat() - 0.5F);
            Minecraft.getMinecraft().effectRenderer.addEffect(new AmberMonolithParticle(world, pos, to));
        }
        for (int i = 0; i < 5; i++) {
            Minecraft.getMinecraft().effectRenderer.addEffect(new AmberBurstParticle(world, to));
        }
    }

    private static class AmberBurstParticle extends Particle {
        private AmberBurstParticle(World world, Vec3d pos) {
            super(world, pos.x, pos.y, pos.z, (world.rand.nextDouble() - 0.5D) * 0.12D, world.rand.nextDouble() * 0.08D, (world.rand.nextDouble() - 0.5D) * 0.12D);
            particleRed = 1.0F;
            particleGreen = 0.85F;
            particleBlue = 0.12F;
            particleAlpha = 0.9F;
            particleScale = 0.8F + world.rand.nextFloat() * 0.3F;
            particleMaxAge = 15 + world.rand.nextInt(10);
            particleGravity = 0.0F;
            canCollide = false;
            setParticleTexture(texture(SUNDROP));
        }

        @Override
        public void onUpdate() {
            prevPosX = posX;
            prevPosY = posY;
            prevPosZ = posZ;
            if (particleAge++ >= particleMaxAge) {
                setExpired();
                return;
            }
            particleRed *= 0.98F;
            particleGreen *= 0.97F;
            particleBlue *= 0.95F;
            particleAlpha = 1.0F - particleAge / (float) particleMaxAge;
            move(motionX, motionY, motionZ);
            motionX *= 0.96D;
            motionY *= 0.96D;
            motionZ *= 0.96D;
        }

        @Override
        public int getFXLayer() {
            return 1;
        }

        @Override
        public int getBrightnessForRender(float partialTicks) {
            return 0xF000F0;
        }
    }
}
