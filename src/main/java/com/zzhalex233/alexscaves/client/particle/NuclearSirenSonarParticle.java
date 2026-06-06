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
public class NuclearSirenSonarParticle extends Particle {
    private static final ResourceLocation SONAR = new ResourceLocation(AlexsCaves.MODID, "particle/sonar");
    private final Vec3d direction;

    private NuclearSirenSonarParticle(World world, double x, double y, double z, Vec3d direction) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.direction = direction.length() < 1.0E-4D ? new Vec3d(0.0D, 0.0D, 1.0D) : direction.normalize();
        this.particleMaxAge = 8;
        this.particleScale = 0.4F;
        this.particleRed = 1.0F;
        this.particleGreen = 1.0F;
        this.particleBlue = 1.0F;
        this.canCollide = false;
        setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(SONAR.toString()));
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        float life = particleAge / (float) particleMaxAge;
        double speed = 0.055D * (1.0D - 0.1D * life);
        motionX += direction.x * speed;
        motionY += direction.y * speed;
        motionZ += direction.z * speed;
        if (particleAge > particleMaxAge / 2) {
            particleAlpha = 1.0F - ((particleAge - particleMaxAge / 2.0F) / (particleMaxAge / 2.0F));
        }
        particleRed += (0.0F - particleRed) * 0.1F;
        particleGreen += (0.933F - particleGreen) * 0.1F;
        particleBlue += (0.0F - particleBlue) * 0.1F;
        if (particleAge++ >= particleMaxAge) {
            setExpired();
            return;
        }
        move(motionX, motionY, motionZ);
        double friction = 1.0D - 0.65D * life;
        motionX *= friction;
        motionY *= friction;
        motionZ *= friction;
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
        Minecraft.getMinecraft().getTextureManager().bindTexture(net.minecraft.client.renderer.texture.TextureMap.LOCATION_BLOCKS_TEXTURE);
        drawSignal(partialTicks);
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }

    private void drawSignal(float partialTicks) {
        TextureAtlasSprite sprite = particleTexture;
        double x = prevPosX + (posX - prevPosX) * partialTicks - interpPosX;
        double y = prevPosY + (posY - prevPosY) * partialTicks - interpPosY;
        double z = prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ;
        float size = particleScale * Math.min((particleAge + partialTicks) / (float) particleMaxAge, 1.0F) * 2.0F;
        Vec3d side = new Vec3d(direction.z, 0.0D, -direction.x);
        if (side.length() < 1.0E-4D) {
            side = new Vec3d(1.0D, 0.0D, 0.0D);
        }
        side = side.normalize().scale(size);
        Vec3d up = direction.crossProduct(side).normalize().scale(size);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        addQuad(builder, x, y, z, side, up, sprite);
        addQuad(builder, x, y, z, side.scale(-1.0D), up, sprite);
        tessellator.draw();
    }

    private void addQuad(BufferBuilder builder, double x, double y, double z, Vec3d side, Vec3d up, TextureAtlasSprite sprite) {
        int light = 240;
        float alpha = particleAlpha;
        builder.pos(x - side.x - up.x, y - side.y - up.y, z - side.z - up.z).tex(sprite.getMaxU(), sprite.getMaxV()).color(particleRed, particleGreen, particleBlue, alpha).lightmap(light, light).endVertex();
        builder.pos(x - side.x + up.x, y - side.y + up.y, z - side.z + up.z).tex(sprite.getMaxU(), sprite.getMinV()).color(particleRed, particleGreen, particleBlue, alpha).lightmap(light, light).endVertex();
        builder.pos(x + side.x + up.x, y + side.y + up.y, z + side.z + up.z).tex(sprite.getMinU(), sprite.getMinV()).color(particleRed, particleGreen, particleBlue, alpha).lightmap(light, light).endVertex();
        builder.pos(x + side.x - up.x, y + side.y - up.y, z + side.z - up.z).tex(sprite.getMinU(), sprite.getMaxV()).color(particleRed, particleGreen, particleBlue, alpha).lightmap(light, light).endVertex();
    }

    @Override
    public int getFXLayer() {
        return 3;
    }

    @Override
    public boolean shouldDisableDepth() {
        return true;
    }

    public static void spawn(World world, Vec3d pos, Vec3d direction) {
        Minecraft.getMinecraft().effectRenderer.addEffect(new NuclearSirenSonarParticle(world, pos.x, pos.y, pos.z, direction));
    }
}
