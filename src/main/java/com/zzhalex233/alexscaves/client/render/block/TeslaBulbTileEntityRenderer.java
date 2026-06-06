package com.zzhalex233.alexscaves.client.render.block;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.TeslaBulbModel;
import com.zzhalex233.alexscaves.server.block.TeslaBulbBlock;
import com.zzhalex233.alexscaves.server.block.entity.TeslaBulbTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

public class TeslaBulbTileEntityRenderer extends TileEntitySpecialRenderer<TeslaBulbTileEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/tesla_bulb.png");
    private static final TeslaBulbModel MODEL = new TeslaBulbModel();

    @Override
    public void render(TeslaBulbTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        if (te.getWorld().getBlockState(te.getPos()).getValue(TeslaBulbBlock.DOWN)) {
            GlStateManager.translate(x + 0.5D, y - 0.51D, z + 0.5D);
        } else {
            GlStateManager.translate(x + 0.5D, y + 1.51D, z + 0.5D);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        }
        MODEL.setup(te.getExplodeProgress(partialTicks), te.age + partialTicks);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        MODEL.render(0.0625F);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
