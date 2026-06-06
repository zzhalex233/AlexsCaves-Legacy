package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.CandyCaneHookModel;
import com.zzhalex233.alexscaves.server.entity.item.CandyCaneHookEntity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public class CandyCaneHookRenderer extends Render<CandyCaneHookEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/candy_cane_hook.png");
    private final CandyCaneHookModel model = new CandyCaneHookModel();

    public CandyCaneHookRenderer(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(CandyCaneHookEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 1.5F, (float) z);
        GlStateManager.rotate(interpolate(entity.prevRotationYaw, entity.rotationYaw, partialTicks), 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-interpolate(entity.prevRotationPitch, entity.rotationPitch, partialTicks), 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, 0.0F, -0.15F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        bindEntityTexture(entity);
        model.render(entity, 0.0F, 0.0F, entity.ticksExisted + partialTicks, 0.0F, 0.0F, 0.0625F);
        GlStateManager.popMatrix();

        EntityPlayer player = entity.getPlayerOwner();
        if (player != null) {
            Vec3d hand = getHandPosition(player, entity.getHandLaunchedFrom(), partialTicks);
            Vec3d hook = entity.getPositionVector().add(0.0D, 0.25D, 0.0D);
            renderLicoriceString(hand.subtract(hook), x, y, z);
        }
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(CandyCaneHookEntity entity) {
        return TEXTURE;
    }

    private Vec3d getHandPosition(EntityPlayer player, EnumHand hand, float partialTicks) {
        float yaw = interpolate(player.prevRenderYawOffset, player.renderYawOffset, partialTicks) * ((float) Math.PI / 180.0F);
        double side = (hand == EnumHand.MAIN_HAND ? 1.0D : -1.0D) * (player.getPrimaryHand() == net.minecraft.util.EnumHandSide.RIGHT ? 1.0D : -1.0D);
        double x = interpolate(player.prevPosX, player.posX, partialTicks) - Math.cos(yaw) * side * 0.35D - Math.sin(yaw) * 0.45D;
        double y = interpolate(player.prevPosY, player.posY, partialTicks) + player.getEyeHeight() - 0.55D;
        double z = interpolate(player.prevPosZ, player.posZ, partialTicks) - Math.sin(yaw) * side * 0.35D + Math.cos(yaw) * 0.45D;
        return new Vec3d(x, y, z);
    }

    private void renderLicoriceString(Vec3d fromHookToHand, double x, double y, double z) {
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.translate(x, y + 0.25D, z);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(5, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 32; ++i) {
            float f = i / 32.0F;
            double sx = fromHookToHand.x * f;
            double sy = fromHookToHand.y * f - (1.0F - f) * f * 0.25F;
            double sz = fromHookToHand.z * f;
            int r = i % 2 == 0 ? 80 : 34;
            int b = i % 2 == 0 ? 104 : 45;
            buffer.pos(sx - 0.025D, sy, sz).color(r, 0, b, 255).endVertex();
            buffer.pos(sx + 0.025D, sy + 0.02D, sz).color(r, 0, b, 255).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private float interpolate(float prev, float current, float partialTicks) {
        return prev + (current - prev) * partialTicks;
    }

    private double interpolate(double prev, double current, float partialTicks) {
        return prev + (current - prev) * partialTicks;
    }
}
