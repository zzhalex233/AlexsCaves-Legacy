package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.GrottoceratopsModel;
import com.zzhalex233.alexscaves.client.model.SubterranodonModel;
import com.zzhalex233.alexscaves.client.model.TremorsaurusSpiritModel;
import com.zzhalex233.alexscaves.server.entity.item.DinosaurSpiritEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class DinosaurSpiritRenderer extends Render<DinosaurSpiritEntity> {
    private static final ResourceLocation SUBTERRANODON_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/subterranodon.png");
    private static final ResourceLocation TREMORSAURUS_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/tremorsaurus.png");
    private static final ResourceLocation GROTTOCERATOPS_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/grottoceratops.png");
    private static final SubterranodonModel SUBTERRANODON_MODEL = new SubterranodonModel();
    private static final GrottoceratopsModel GROTTOCERATOPS_MODEL = new GrottoceratopsModel();
    private static final TremorsaurusSpiritModel TREMORSAURUS_MODEL = new TremorsaurusSpiritModel();

    public DinosaurSpiritRenderer(RenderManager renderManager) {
        super(renderManager);
        shadowSize = 0.0F;
    }

    @Override
    public void doRender(DinosaurSpiritEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        float alpha = entity.getFadeIn(partialTicks);
        if (alpha <= 0.0F) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 1.5F, (float) z);
        if (entity.getDinosaurType() == DinosaurSpiritEntity.DinosaurType.GROTTOCERATOPS) {
            EntityPlayer player = entity.getUsingPlayer();
            if (player != null) {
                Vec3d playerPos = player.getPositionVector();
                Vec3d dinoPos = entity.getPositionVector();
                float face = -((float) MathHelper.atan2(playerPos.x - dinoPos.x, playerPos.z - dinoPos.z)) * (180.0F / (float) Math.PI);
                GlStateManager.rotate(-face, 0.0F, 1.0F, 0.0F);
            }
        } else {
            float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
            GlStateManager.rotate(180.0F - yaw, 0.0F, 1.0F, 0.0F);
        }
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.color(1.0F, 0.25F, 0.25F, alpha);
        bindEntityTexture(entity);
        switch (entity.getDinosaurType()) {
            case SUBTERRANODON:
                SUBTERRANODON_MODEL.render(entity, 0.0F, 0.0F, entity.ticksExisted + partialTicks, 0.0F, 0.0F, 0.0625F);
                break;
            case GROTTOCERATOPS:
                GROTTOCERATOPS_MODEL.render(entity, 0.0F, 0.0F, entity.ticksExisted + partialTicks, 0.0F, 0.0F, 0.0625F);
                break;
            case TREMORSAURUS:
                TREMORSAURUS_MODEL.render(entity, 0.0F, partialTicks, entity.ticksExisted + partialTicks, 0.0F, 0.0F, 0.0625F);
                break;
            default:
                break;
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(DinosaurSpiritEntity entity) {
        switch (entity.getDinosaurType()) {
            case GROTTOCERATOPS:
                return GROTTOCERATOPS_TEXTURE;
            case TREMORSAURUS:
                return TREMORSAURUS_TEXTURE;
            case SUBTERRANODON:
            default:
                return SUBTERRANODON_TEXTURE;
        }
    }
}
