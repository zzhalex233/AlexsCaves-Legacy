package com.zzhalex233.alexscaves.client.particle;

import com.zzhalex233.alexscaves.AlexsCaves;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class MagneticTrailParticle extends Particle {
    private static final ResourceLocation TRAIL = new ResourceLocation(AlexsCaves.MODID, "particle/trail");
    private static final ResourceLocation TRAIL_MIRRORED = new ResourceLocation(AlexsCaves.MODID, "particle/trail_mirrored");
    private static final int SAMPLE_COUNT = 20;
    private final Vec3d[] trail = new Vec3d[SAMPLE_COUNT];
    private final boolean flow;
    private final double originX;
    private final double originY;
    private final double originZ;
    private final double targetX;
    private final double targetY;
    private final double targetZ;
    private final Vec3d orbitOffset;
    private final boolean reverseOrbit;
    private final int orbitAxis;
    private final float orbitSpeed;
    private final float red;
    private final float green;
    private final float blue;

    private MagneticTrailParticle(World world, double x, double y, double z, double targetX, double targetY, double targetZ, boolean azure, boolean flow) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.flow = flow;
        this.originX = x;
        this.originY = y;
        this.originZ = z;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.orbitOffset = new Vec3d(rand.nextDouble() - 0.5D, rand.nextDouble() - 0.5D, rand.nextDouble() - 0.5D);
        this.reverseOrbit = rand.nextBoolean();
        this.orbitAxis = rand.nextInt(3);
        this.orbitSpeed = 1.0F + rand.nextFloat() * 3.0F;
        this.red = azure ? 0.2F + rand.nextFloat() * 0.05F : 0.9F + rand.nextFloat() * 0.1F;
        this.green = 0.2F + rand.nextFloat() * 0.05F;
        this.blue = azure ? 0.9F + rand.nextFloat() * 0.1F : 0.2F + rand.nextFloat() * 0.05F;
        this.particleMaxAge = flow ? 40 + rand.nextInt(24) : 50 + rand.nextInt(30);
        this.particleGravity = 0.0F;
        this.canCollide = false;
        setParticleTexture(texture(flow ? TRAIL_MIRRORED : TRAIL));
        Vec3d current = new Vec3d(x, y, z);
        for (int i = 0; i < trail.length; i++) {
            trail[i] = current;
        }
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        for (int i = trail.length - 1; i > 0; i--) {
            trail[i] = trail[i - 1];
        }
        trail[0] = new Vec3d(posX, posY, posZ);
        if (particleAge++ >= particleMaxAge) {
            setExpired();
            return;
        }
        Vec3d target = flow ? getFlowTarget() : getOrbitPosition(particleAge);
        Vec3d distance = target.subtract(posX, posY, posZ);
        if (distance.length() > 1.0E-4D) {
            Vec3d movement = distance.normalize().scale(flow ? 0.02D : orbitSpeed * 0.01D);
            motionX += movement.x;
            motionY += movement.y;
            motionZ += movement.z;
        }
        move(motionX, motionY, motionZ);
        motionX *= 0.96D;
        motionY *= 0.96D;
        motionZ *= 0.96D;
    }

    private Vec3d getFlowTarget() {
        Vec3d toTarget = new Vec3d(targetX - posX, targetY - posY, targetZ - posZ);
        Vec3d toOrigin = new Vec3d(originX - posX, originY - posY, originZ - posZ);
        return toTarget.length() < 1.0D ? new Vec3d(originX, originY, originZ) : toOrigin.length() < 1.0D ? new Vec3d(targetX, targetY, targetZ) : new Vec3d(targetX, targetY, targetZ);
    }

    private Vec3d getOrbitPosition(float angle) {
        float rot = angle * (reverseOrbit ? -orbitSpeed : orbitSpeed) * ((float) Math.PI / 180.0F);
        Vec3d add = orbitOffset;
        double cos = Math.cos(rot);
        double sin = Math.sin(rot);
        switch (orbitAxis) {
            case 0:
                add = new Vec3d(add.x, add.y * cos - add.z * sin, add.y * sin + add.z * cos);
                break;
            case 1:
                add = new Vec3d(add.x * cos + add.z * sin, add.y, add.z * cos - add.x * sin);
                break;
            default:
                add = new Vec3d(add.x * cos - add.y * sin, add.x * sin + add.y * cos, add.z);
                break;
        }
        return new Vec3d(targetX + add.x, targetY + add.y, targetZ + add.z);
    }

    @Override
    public void renderParticle(BufferBuilder buffer, net.minecraft.entity.Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.draw();
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMapHolder.PARTICLES);
        drawTrail(partialTicks);
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }

    private void drawTrail(float partialTicks) {
        TextureAtlasSprite sprite = particleTexture;
        float fade = 1.0F - (particleAge + partialTicks) / (float) particleMaxAge;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        for (int i = 0; i < trail.length - 1; i += 2) {
            Vec3d from = trail[i];
            Vec3d to = trail[i + 1];
            if (from == null || to == null || from.squareDistanceTo(to) < 1.0E-6D) {
                continue;
            }
            Vec3d camera = new Vec3d(interpPosX, interpPosY, interpPosZ);
            Vec3d side = to.subtract(from).crossProduct(camera.subtract(from));
            if (side.length() < 1.0E-4D) {
                side = new Vec3d(0.0D, 1.0D, 0.0D);
            }
            side = side.normalize().scale((flow ? 0.15D : 0.125D) * (1.0D - i / (double) trail.length));
            float alpha = (flow ? 0.5F : 0.8F) * fade * (1.0F - i / (float) trail.length);
            addQuad(builder, from.subtract(camera), to.subtract(camera), side, sprite, alpha);
        }
        tessellator.draw();
    }

    private void addQuad(BufferBuilder builder, Vec3d from, Vec3d to, Vec3d side, TextureAtlasSprite sprite, float alpha) {
        int light = 240;
        builder.pos(from.x - side.x, from.y - side.y, from.z - side.z).tex(sprite.getMinU(), sprite.getMinV()).color(red, green, blue, alpha).lightmap(light, light).endVertex();
        builder.pos(to.x - side.x, to.y - side.y, to.z - side.z).tex(sprite.getMaxU(), sprite.getMinV()).color(red, green, blue, alpha).lightmap(light, light).endVertex();
        builder.pos(to.x + side.x, to.y + side.y, to.z + side.z).tex(sprite.getMaxU(), sprite.getMaxV()).color(red, green, blue, alpha).lightmap(light, light).endVertex();
        builder.pos(from.x + side.x, from.y + side.y, from.z + side.z).tex(sprite.getMinU(), sprite.getMaxV()).color(red, green, blue, alpha).lightmap(light, light).endVertex();
    }

    @Override
    public int getFXLayer() {
        return 3;
    }

    @Override
    public boolean shouldDisableDepth() {
        return true;
    }

    private TextureAtlasSprite texture(ResourceLocation location) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
    }

    public static void spawnFlow(World world, Vec3d from, Vec3d to, boolean azure) {
        Minecraft.getMinecraft().effectRenderer.addEffect(new MagneticTrailParticle(world, from.x, from.y, from.z, to.x, to.y, to.z, azure, true));
    }

    public static void spawnOrbit(World world, Vec3d center, boolean azure) {
        Minecraft.getMinecraft().effectRenderer.addEffect(new MagneticTrailParticle(world, center.x, center.y, center.z, center.x, center.y, center.z, azure, false));
    }

    private static class TextureMapHolder {
        private static final ResourceLocation PARTICLES = net.minecraft.client.renderer.texture.TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
