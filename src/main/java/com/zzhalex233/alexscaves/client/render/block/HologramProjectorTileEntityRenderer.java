package com.zzhalex233.alexscaves.client.render.block;

import com.zzhalex233.alexscaves.client.render.entity.NotorRenderer;
import com.zzhalex233.alexscaves.server.block.entity.HologramProjectorTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class HologramProjectorTileEntityRenderer extends TileEntitySpecialRenderer<HologramProjectorTileEntity> {
    private static final float HALF_SQRT_3 = 0.8660254F;
    private static final ModelPlayer PLAYER_MODEL = new ModelPlayer(0.0F, false);
    private static final ModelPlayer SLIM_PLAYER_MODEL = new ModelPlayer(0.0F, true);

    @Override
    public void render(HologramProjectorTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        float amount = te.getSwitchAmount(partialTicks);
        if (!te.hasHologram() || amount <= 0.0F) {
            return;
        }
        Entity entity = te.getDisplayEntity();
        boolean playerRender = te.isPlayerRender();
        float ticks = te.tickCount + partialTicks;
        float bob1 = (float) (Math.sin(ticks * 0.05F + amount) * 0.1F);
        float bob2 = (float) (Math.cos(ticks * 0.05F + amount) * 0.1F);
        float length = (1.0F + bob1) * amount;
        float width = (((playerRender || entity == null) ? 0.8F : entity.width) + bob2) * amount;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.25D, z + 0.5D);
        renderBaseGlow(amount);
        renderLightCone(length, width, amount);
        if (playerRender) {
            renderPlayerHologram(te.getPlayerUUID(), length, amount, te.getRotation(partialTicks));
        } else if (entity != null) {
            renderHologramEntity(te, entity, length, amount, partialTicks);
        }
        GlStateManager.popMatrix();
    }

    private void renderBaseGlow(float amount) {
        float pad = 0.375F;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, -0.235D, 0.0D);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(-pad, 0.0D, pad).color(220, 220, 255, (int) (amount * 150.0F)).endVertex();
        buffer.pos(pad, 0.0D, pad).color(220, 220, 255, (int) (amount * 150.0F)).endVertex();
        buffer.pos(pad, 0.0D, -pad).color(220, 220, 255, (int) (amount * 150.0F)).endVertex();
        buffer.pos(-pad, 0.0D, -pad).color(220, 220, 255, (int) (amount * 150.0F)).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void renderLightCone(float length, float width, float amount) {
        float cameraY = Minecraft.getMinecraft().getRenderManager().playerViewY;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, -0.2D, 0.0D);
        GlStateManager.rotate(180.0F - cameraY, 0.0F, 1.0F, 0.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(6, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(0.0D, 0.0D, 0.0D).color(255, 255, 255, (int) (amount * 230.0F)).endVertex();
        buffer.pos(-HALF_SQRT_3 * width, length, 0.0D).color(0, 80, 255, 0).endVertex();
        buffer.pos(HALF_SQRT_3 * width, length, 0.0D).color(0, 80, 255, 0).endVertex();
        buffer.pos(-HALF_SQRT_3 * width, length, 0.0D).color(0, 80, 255, 0).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void renderHologramEntity(HologramProjectorTileEntity te, Entity entity, float length, float amount, float partialTicks) {
        float cameraY = Minecraft.getMinecraft().getRenderManager().playerViewY;
        float scale = 0.75F;
        float size = Math.max(entity.width, entity.height);
        if (size > 1.25F) {
            scale /= size / 1.25F;
        }
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.0F, amount, 1.0F);
        GlStateManager.translate(0.0D, length + 1.5F, 0.0D);
        GlStateManager.rotate(180.0F - cameraY + te.getRotation(partialTicks), 0.0F, -1.0F, 0.0F);
        GlStateManager.scale(scale, scale, scale);
        NotorRenderer.renderEntityInHologram(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks);
        GlStateManager.popMatrix();
    }

    private void renderPlayerHologram(UUID uuid, float length, float amount, float rotation) {
        Entity player = Minecraft.getMinecraft().player;
        if (uuid == null || player == null) {
            return;
        }
        float cameraY = Minecraft.getMinecraft().getRenderManager().playerViewY;
        ModelPlayer model = isSlimSkin(uuid) ? SLIM_PLAYER_MODEL : PLAYER_MODEL;
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.0F, amount, 1.0F);
        GlStateManager.translate(0.0D, length + 1.5F, 0.0D);
        GlStateManager.rotate(180.0F - cameraY + rotation, 0.0F, -1.0F, 0.0F);
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.color(0.45F, 0.85F, 1.0F, 0.7F);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        bindTexture(getPlayerSkin(uuid));
        model.setVisible(true);
        model.isChild = false;
        model.isRiding = false;
        model.isSneak = false;
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        model.render(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private ResourceLocation getPlayerSkin(UUID uuid) {
        NetworkPlayerInfo info = getPlayerInfo(uuid);
        return info == null ? DefaultPlayerSkin.getDefaultSkin(uuid) : info.getLocationSkin();
    }

    private boolean isSlimSkin(UUID uuid) {
        NetworkPlayerInfo info = getPlayerInfo(uuid);
        return "slim".equals(info == null ? DefaultPlayerSkin.getSkinType(uuid) : info.getSkinType());
    }

    private NetworkPlayerInfo getPlayerInfo(UUID uuid) {
        return Minecraft.getMinecraft().getConnection() == null ? null : Minecraft.getMinecraft().getConnection().getPlayerInfo(uuid);
    }
}
