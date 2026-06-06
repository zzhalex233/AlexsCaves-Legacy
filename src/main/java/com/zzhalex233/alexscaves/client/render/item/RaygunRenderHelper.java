package com.zzhalex233.alexscaves.client.render.item;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.item.RaygunItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class RaygunRenderHelper {
    private static final ResourceLocation RAY_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/raygun/raygun_ray.png");
    private static final ResourceLocation BLUE_RAY_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/raygun/raygun_blue_ray.png");

    public static void renderWorldRays(World world, float partialTicks) {
        Entity camera = Minecraft.getMinecraft().getRenderViewEntity();
        if (world == null || camera == null) {
            return;
        }
        Vec3d cameraPos = interpolate(camera, partialTicks);
        for (Entity entity : world.loadedEntityList) {
            if (entity instanceof EntityLivingBase) {
                renderRaysFor((EntityLivingBase) entity, cameraPos, partialTicks);
            }
        }
    }

    private static void renderRaysFor(EntityLivingBase living, Vec3d cameraPos, float partialTicks) {
        ItemStack active = living.getActiveItemStack();
        if (active.isEmpty() || active.getItem() != ACItemRegistry.RAYGUN.item() || RaygunItem.getUseTime(active) < 5) {
            return;
        }
        Vec3d rayPosition = RaygunItem.getLerpedRayPosition(active, partialTicks);
        if (rayPosition == null || rayPosition.equals(Vec3d.ZERO)) {
            return;
        }
        EnumHand hand = active == living.getHeldItemOffhand() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
        Vec3d gunPos = getGunPosition(living, hand, partialTicks);
        Vec3d beam = rayPosition.subtract(gunPos);
        if (beam.length() <= 0.1D) {
            return;
        }
        boolean blue = EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.GAMMA_RAY, active) > 0;
        renderRay(gunPos.subtract(cameraPos), beam, living.ticksExisted + partialTicks, blue);
    }

    private static Vec3d getGunPosition(EntityLivingBase living, EnumHand hand, float partialTicks) {
        float yaw = interpolate(living.prevRenderYawOffset, living.renderYawOffset, partialTicks) * ((float) Math.PI / 180.0F);
        boolean left = hand == EnumHand.MAIN_HAND && living.getPrimaryHand() == EnumHandSide.LEFT || hand == EnumHand.OFF_HAND && living.getPrimaryHand() == EnumHandSide.RIGHT;
        double side = left ? -1.0D : 1.0D;
        Vec3d base = interpolate(living, partialTicks);
        double x = base.x - Math.cos(yaw) * side * 0.35D - Math.sin(yaw) * 0.75D;
        double y = base.y + living.getEyeHeight() - 0.45D;
        double z = base.z - Math.sin(yaw) * side * 0.35D + Math.cos(yaw) * 0.75D;
        return new Vec3d(x, y, z);
    }

    private static void renderRay(Vec3d start, Vec3d beam, float ageInTicks, boolean blue) {
        Vec3d direction = beam.normalize();
        Vec3d side = direction.crossProduct(new Vec3d(0.0D, 1.0D, 0.0D));
        if (side.length() < 1.0E-4D) {
            side = new Vec3d(1.0D, 0.0D, 0.0D);
        }
        Vec3d up = direction.crossProduct(side).normalize();
        side = side.normalize();

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        Minecraft.getMinecraft().getTextureManager().bindTexture(blue ? BLUE_RAY_TEXTURE : RAY_TEXTURE);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        float scroll = -ageInTicks * 0.25F % 1.0F;
        addBeamQuad(buffer, start, beam, side, 0.22D, 1.15D, scroll);
        addBeamQuad(buffer, start, beam, up, 0.22D, 1.15D, scroll + 0.5F);
        tessellator.draw();

        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static void addBeamQuad(BufferBuilder buffer, Vec3d start, Vec3d beam, Vec3d axis, double startWidth, double endWidth, float scroll) {
        Vec3d nearSide = axis.scale(startWidth);
        Vec3d farSide = axis.scale(endWidth);
        Vec3d end = start.add(beam);
        float v1 = scroll + (float) beam.length();
        buffer.pos(start.x - nearSide.x, start.y - nearSide.y, start.z - nearSide.z).tex(0.5F, scroll).color(255, 255, 255, 230).endVertex();
        buffer.pos(end.x - farSide.x, end.y - farSide.y, end.z - farSide.z).tex(0.0F, v1).color(255, 255, 255, 190).endVertex();
        buffer.pos(end.x + farSide.x, end.y + farSide.y, end.z + farSide.z).tex(1.0F, v1).color(255, 255, 255, 190).endVertex();
        buffer.pos(start.x + nearSide.x, start.y + nearSide.y, start.z + nearSide.z).tex(0.5F, scroll).color(255, 255, 255, 230).endVertex();
    }

    private static Vec3d interpolate(Entity entity, float partialTicks) {
        return new Vec3d(
                entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks,
                entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks,
                entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks
        );
    }

    private static float interpolate(float prev, float current, float partialTicks) {
        return prev + (current - prev) * partialTicks;
    }
}
