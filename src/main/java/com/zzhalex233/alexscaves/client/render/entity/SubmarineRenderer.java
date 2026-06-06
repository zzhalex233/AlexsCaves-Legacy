package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.SubmarineModel;
import com.zzhalex233.alexscaves.server.entity.item.SubmarineEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class SubmarineRenderer extends Render<SubmarineEntity> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[] {
            new ResourceLocation(AlexsCaves.MODID, "textures/entity/submarine/submarine.png"),
            new ResourceLocation(AlexsCaves.MODID, "textures/entity/submarine/submarine_exposed.png"),
            new ResourceLocation(AlexsCaves.MODID, "textures/entity/submarine/submarine_weathered.png"),
            new ResourceLocation(AlexsCaves.MODID, "textures/entity/submarine/submarine_oxidized.png")
    };
    private static final ResourceLocation[] DAMAGE_TEXTURES = new ResourceLocation[] {
            new ResourceLocation(AlexsCaves.MODID, "textures/entity/submarine/submarine_new.png"),
            new ResourceLocation(AlexsCaves.MODID, "textures/entity/submarine/submarine_low.png"),
            new ResourceLocation(AlexsCaves.MODID, "textures/entity/submarine/submarine_medium.png"),
            new ResourceLocation(AlexsCaves.MODID, "textures/entity/submarine/submarine_high.png"),
            new ResourceLocation(AlexsCaves.MODID, "textures/entity/submarine/submarine_critical.png")
    };
    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/submarine/submarine_glow.png");
    private static final ResourceLocation BUTTONS_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/submarine/submarine_buttons.png");
    private final SubmarineModel model = new SubmarineModel();

    public SubmarineRenderer(RenderManager renderManager) {
        super(renderManager);
        shadowSize = 1.0F;
    }

    @Override
    public void doRender(SubmarineEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        float ageInTicks = entity.ticksExisted + partialTicks;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 1.5F, (float) z);
        GlStateManager.rotate(180.0F - interpolateRotation(entity.prevRotationYaw, entity.rotationYaw, partialTicks), 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.rotate(-(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks), 1.0F, 0.0F, 0.0F);
        if (entity.getWaterHeight() > 0.0F && entity.getWaterHeight() < 1.6F) {
            GlStateManager.rotate((float) (Math.sin(ageInTicks * 0.1F) * 0.5F), 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate((float) (Math.sin(ageInTicks * 0.1F + 1.3F) * 0.5F), 1.0F, 0.0F, 0.0F);
        }
        GlStateManager.disableCull();
        bindEntityTexture(entity);
        model.render(entity, 0.0F, 0.0F, ageInTicks, 0.0F, 0.0F, 0.0625F);
        renderOverlay(DAMAGE_TEXTURES[MathHelper.clamp(entity.getDamageLevel(), 0, 4)], 1.0F, entity, ageInTicks);
        if (entity.getDamageLevel() <= 3) {
            renderOverlay(BUTTONS_TEXTURE, entity.getSonarFlashAmount(partialTicks), entity, ageInTicks);
            if (entity.areLightsOn() && entity.isBeingRidden()) {
                renderOverlay(GLOW_TEXTURE, 1.0F, entity, ageInTicks);
            }
        }
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    private void renderOverlay(ResourceLocation texture, float alpha, SubmarineEntity entity, float ageInTicks) {
        if (alpha <= 0.0F) {
            return;
        }
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        bindTexture(texture);
        model.render(entity, 0.0F, 0.0F, ageInTicks, 0.0F, 0.0F, 0.0625F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
    }

    private static float interpolateRotation(float prevYaw, float yaw, float partialTicks) {
        float delta = yaw - prevYaw;
        while (delta < -180.0F) {
            delta += 360.0F;
        }
        while (delta >= 180.0F) {
            delta -= 360.0F;
        }
        return prevYaw + partialTicks * delta;
    }

    @Override
    protected ResourceLocation getEntityTexture(SubmarineEntity entity) {
        return TEXTURES[MathHelper.clamp(entity.getOxidizationLevel(), 0, 3)];
    }
}
