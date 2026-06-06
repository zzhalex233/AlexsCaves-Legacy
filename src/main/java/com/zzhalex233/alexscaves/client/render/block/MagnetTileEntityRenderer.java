package com.zzhalex233.alexscaves.client.render.block;

import com.zzhalex233.alexscaves.server.block.MagnetBlock;
import com.zzhalex233.alexscaves.server.block.entity.MagnetTileEntity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

public class MagnetTileEntityRenderer extends TileEntitySpecialRenderer<MagnetTileEntity> {
    @Override
    public void render(MagnetTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        IBlockState state = te.getWorld().getBlockState(te.getPos());
        if (!(state.getBlock() instanceof MagnetBlock)) {
            return;
        }
        float ageInTicks = (te.age + partialTicks) * 3.0F;
        if (state.getValue(MagnetBlock.POWERED)) {
            float twitch = 0.01F;
            IBlockState copy = state.withProperty(MagnetBlock.POWERED, false);
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + Math.sin(ageInTicks) * twitch, y + Math.cos(ageInTicks - Math.PI / 2.0D) * twitch, z - Math.cos(ageInTicks) * twitch);
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(copy, 1.0F);
            GlStateManager.popMatrix();
        }
        renderRange(te, x, y, z, partialTicks, ageInTicks);
    }

    private void renderRange(MagnetTileEntity te, double x, double y, double z, float partialTicks, float ageInTicks) {
        float visibility = te.getRangeVisuality(partialTicks);
        if (visibility <= 0.0F) {
            return;
        }
        EntityPlayer player = Minecraft.getMinecraft().player;
        float red = te.isAzure() ? 0.2F : 1.0F;
        float green = 0.2F;
        float blue = te.isAzure() ? 1.0F : 0.2F;
        float advance = (float) (Math.sin(ageInTicks * 0.04F) * 0.5F + 0.5F);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x - te.getPos().getX(), y - te.getPos().getY(), z - te.getPos().getZ());
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(2.0F);
        if (player != null) {
            AxisAlignedBB preview = null;
            if (te.isExtenderItem(player.getHeldItemMainhand()) || te.isExtenderItem(player.getHeldItemOffhand())) {
                preview = te.getRangeBB(te.getEffectiveRange() + advance, false).grow(-0.002D);
            } else if (te.isRetracterItem(player.getHeldItemMainhand()) || te.isRetracterItem(player.getHeldItemOffhand())) {
                preview = te.getRangeBB(te.getEffectiveRange() - advance, false).grow(-0.002D);
            }
            if (preview != null && (te.canAddRange() || te.canRemoveRange())) {
                RenderGlobal.drawSelectionBoundingBox(preview, red, green, blue, 0.3F * visibility);
            }
        }
        RenderGlobal.drawSelectionBoundingBox(te.getRangeBB(te.getEffectiveRange(), false).grow(-0.001D), red, green, blue, 0.6F * visibility);
        GL11.glLineWidth(1.0F);
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }
}
