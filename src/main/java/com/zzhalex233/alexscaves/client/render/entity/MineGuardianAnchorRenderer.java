package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.MineGuardianAnchorModel;
import com.zzhalex233.alexscaves.server.entity.item.MineGuardianAnchorEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MineGuardianAnchorRenderer extends Render<MineGuardianAnchorEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/mine_guardian_anchor.png");
    private static final ResourceLocation TEXTURE_CHAIN = new ResourceLocation("minecraft", "textures/blocks/chain.png");
    private final MineGuardianAnchorModel model = new MineGuardianAnchorModel();

    public MineGuardianAnchorRenderer(RenderManager renderManager) {
        super(renderManager);
        shadowSize = 0.35F;
    }

    @Override
    public void doRender(MineGuardianAnchorEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        bindEntityTexture(entity);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 1.35F, (float) z);
        GlStateManager.rotate(180.0F - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.rotationPitch + 180.0F, 1.0F, 0.0F, 0.0F);
        model.render(entity, 0.0F, 0.0F, entity.ticksExisted + partialTicks, 0.0F, 0.0F, 0.0625F);
        GlStateManager.popMatrix();
        Vec3d from = entity.getChainFrom(partialTicks);
        Vec3d to = entity.getChainTo(partialTicks);
        renderChain(to.subtract(from), x, y + 1.0D, z);
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    private void renderChain(Vec3d chain, double x, double y, double z) {
        double horizontal = Math.sqrt(chain.x * chain.x + chain.z * chain.z);
        float length = (float) chain.length();
        if (length <= 0.01F) {
            return;
        }
        float yaw = (float) MathHelper.atan2(chain.x, chain.z) * 180.0F / (float) Math.PI;
        float pitch = -(float) MathHelper.atan2(chain.y, horizontal) * 180.0F / (float) Math.PI;
        bindTexture(TEXTURE_CHAIN);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(pitch - 90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.disableCull();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float width = 0.1875F;
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-width, 0.0D, 0.0D).tex(0.0D, length).endVertex();
        buffer.pos(width, 0.0D, 0.0D).tex(width, length).endVertex();
        buffer.pos(width, length, 0.0D).tex(width, 0.0D).endVertex();
        buffer.pos(-width, length, 0.0D).tex(0.0D, 0.0D).endVertex();
        buffer.pos(0.0D, 0.0D, -width).tex(0.0D, length).endVertex();
        buffer.pos(0.0D, 0.0D, width).tex(width, length).endVertex();
        buffer.pos(0.0D, length, width).tex(width, 0.0D).endVertex();
        buffer.pos(0.0D, length, -width).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(MineGuardianAnchorEntity entity) {
        return TEXTURE;
    }
}
