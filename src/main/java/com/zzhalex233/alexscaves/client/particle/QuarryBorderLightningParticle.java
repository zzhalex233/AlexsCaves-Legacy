package com.zzhalex233.alexscaves.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class QuarryBorderLightningParticle extends Particle {
    private static final int LIFETIME = 5;
    private final Vec3d[] points;

    public QuarryBorderLightningParticle(World world, Vec3d from, Vec3d to) {
        super(world, from.x, from.y, from.z, 0.0D, 0.0D, 0.0D);
        particleMaxAge = LIFETIME;
        points = createPoints(to.subtract(from));
        particleGravity = 0.0F;
        canCollide = false;
        setSize(6.0F, 6.0F);
    }

    private Vec3d[] createPoints(Vec3d delta) {
        int sections = Math.max(2, (int) (4.0D * delta.length()));
        Vec3d[] result = new Vec3d[sections + 1];
        result[0] = Vec3d.ZERO;
        result[sections] = delta;
        for (int i = 1; i < sections; i++) {
            double progress = i / (double) sections;
            double spread = Math.sin(progress * Math.PI) * 0.12D;
            result[i] = new Vec3d(delta.x * progress + (rand.nextDouble() - 0.5D) * spread, delta.y * progress + (rand.nextDouble() - 0.5D) * spread, delta.z * progress + (rand.nextDouble() - 0.5D) * spread);
        }
        return result;
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        if (particleAge++ >= particleMaxAge) {
            setExpired();
        }
    }

    @Override
    public void renderParticle(BufferBuilder buffer, net.minecraft.entity.Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.draw();
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX - interpPosX, posY - interpPosY, posZ - interpPosZ);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GL11.glLineWidth(1.5F);
        drawBolt();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }

    private void drawBolt() {
        float fade = 1.0F - particleAge / (float) particleMaxAge;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (Vec3d point : points) {
            buffer.pos(point.x, point.y, point.z).color(0.71F, 0.76F, 0.95F, 0.3F * fade).endVertex();
        }
        tessellator.draw();
    }

    @Override
    public int getFXLayer() {
        return 3;
    }

    @Override
    public boolean shouldDisableDepth() {
        return true;
    }
}
