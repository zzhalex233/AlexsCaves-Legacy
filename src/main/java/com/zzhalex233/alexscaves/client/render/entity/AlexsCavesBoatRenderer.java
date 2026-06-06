package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.ACBoatModel;
import com.zzhalex233.alexscaves.client.model.PewenBoatModel;
import com.zzhalex233.alexscaves.client.model.ThornwoodBoatModel;
import com.zzhalex233.alexscaves.server.entity.item.AlexsCavesBoatEntity;
import com.zzhalex233.alexscaves.server.entity.util.ACBoatType;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class AlexsCavesBoatRenderer extends Render<AlexsCavesBoatEntity> {
    private static final ResourceLocation PEWEN_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/boat/pewen_boat.png");
    private static final ResourceLocation THORNWOOD_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/boat/thornwood_boat.png");
    private final ACBoatModel pewenModel = new PewenBoatModel();
    private final ACBoatModel thornwoodModel = new ThornwoodBoatModel();

    public AlexsCavesBoatRenderer(RenderManager renderManager) {
        super(renderManager);
        shadowSize = 0.8F;
    }

    @Override
    public void doRender(AlexsCavesBoatEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        setupTranslation(x, y, z);
        setupRotation(entity, entityYaw, partialTicks);
        bindEntityTexture(entity);
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        getModel(entity).render(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    protected void setupTranslation(double x, double y, double z) {
        GlStateManager.translate((float) x, (float) y + 1.375F, (float) z);
    }

    protected void setupRotation(AlexsCavesBoatEntity entity, float entityYaw, float partialTicks) {
        GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
        float timeSinceHit = entity.getTimeSinceHit() - partialTicks;
        float damageTaken = entity.getDamageTaken() - partialTicks;
        if (damageTaken < 0.0F) {
            damageTaken = 0.0F;
        }
        if (timeSinceHit > 0.0F) {
            GlStateManager.rotate(MathHelper.sin(timeSinceHit) * timeSinceHit * damageTaken / 10.0F * entity.getForwardDirection(), 1.0F, 0.0F, 0.0F);
        }
    }

    private ACBoatModel getModel(AlexsCavesBoatEntity entity) {
        return entity.getACBoatType() == ACBoatType.THORNWOOD ? thornwoodModel : pewenModel;
    }

    @Override
    protected ResourceLocation getEntityTexture(AlexsCavesBoatEntity entity) {
        return entity.getACBoatType() == ACBoatType.THORNWOOD ? THORNWOOD_TEXTURE : PEWEN_TEXTURE;
    }
}
