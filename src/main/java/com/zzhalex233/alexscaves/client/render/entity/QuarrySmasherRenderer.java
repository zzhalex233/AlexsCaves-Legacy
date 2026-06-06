package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.BoundroidModel;
import com.zzhalex233.alexscaves.client.model.QuarrySmasherModel;
import com.zzhalex233.alexscaves.server.block.entity.QuarryTileEntity;
import com.zzhalex233.alexscaves.server.entity.item.QuarrySmasherEntity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class QuarrySmasherRenderer extends Render<QuarrySmasherEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/quarry_smasher.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation(AlexsCaves.MODID, "textures/entity/quarry_smasher_glow.png");
    private static final ResourceLocation TEXTURE_BOUNDROID = new ResourceLocation(AlexsCaves.MODID, "textures/entity/boundroid_quarry.png");
    private static final ResourceLocation TEXTURE_CHAIN = new ResourceLocation("minecraft", "textures/blocks/iron_bars.png");
    private final QuarrySmasherModel smasherModel = new QuarrySmasherModel();
    private final BoundroidModel boundroidModel = new BoundroidModel();

    public QuarrySmasherRenderer(RenderManager renderManager) {
        super(renderManager);
        shadowSize = 0.8F;
    }

    @Override
    public void doRender(QuarrySmasherEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        float ageInTicks = entity.ticksExisted + partialTicks;
        renderMiningArea(entity, x, y, z);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 1.5F, (float) z);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        bindTexture(TEXTURE);
        smasherModel.render(entity, 0.0F, 0.0F, ageInTicks, 0.0F, 0.0F, 0.0625F);
        float active = 1.0F - entity.getInactiveProgress(partialTicks);
        if (active > 0.0F) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, active);
            bindTexture(TEXTURE_GLOW);
            smasherModel.render(entity, 0.0F, 0.0F, ageInTicks, 0.0F, 0.0F, 0.0625F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableBlend();
        }
        GlStateManager.popMatrix();

        Vec3d headOffset = entity.getHeadPosition(partialTicks).subtract(entity.getPositionVector());
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) (x + headOffset.x), (float) (y + headOffset.y + 1.5D), (float) (z + headOffset.z));
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        bindTexture(TEXTURE_BOUNDROID);
        boundroidModel.renderForQuarry(ageInTicks, Math.max(entity.getHeadGroundProgress(partialTicks), entity.getInactiveProgress(partialTicks)), 0.0625F);
        GlStateManager.popMatrix();

        renderChain(headOffset.add(0.0D, 0.5D, 0.0D), x, y - 0.25D, z);
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    private void renderMiningArea(QuarrySmasherEntity entity, double x, double y, double z) {
        BlockPos quarryPos = entity.getQuarryPos();
        if (quarryPos == null) {
            return;
        }
        TileEntity tile = entity.world.getTileEntity(quarryPos);
        AxisAlignedBB area = tile instanceof QuarryTileEntity ? ((QuarryTileEntity) tile).getMiningBox() : null;
        if (area == null) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(x - entity.posX, y - entity.posY, z - entity.posZ);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(2.0F);
        RenderGlobal.drawSelectionBoundingBox(area.grow(-0.001D), 0.7F, 0.78F, 1.0F, 0.45F);
        GL11.glLineWidth(1.0F);
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
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
    protected ResourceLocation getEntityTexture(QuarrySmasherEntity entity) {
        return TEXTURE;
    }
}
