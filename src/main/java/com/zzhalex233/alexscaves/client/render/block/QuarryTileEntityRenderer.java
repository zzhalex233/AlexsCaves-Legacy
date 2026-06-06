package com.zzhalex233.alexscaves.client.render.block;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.QuarryGrinderModel;
import com.zzhalex233.alexscaves.server.block.QuarryBlock;
import com.zzhalex233.alexscaves.server.block.entity.QuarryTileEntity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class QuarryTileEntityRenderer extends TileEntitySpecialRenderer<QuarryTileEntity> {
    private static final QuarryGrinderModel MODEL = new QuarryGrinderModel();
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/quarry_grinder.png");

    @Override
    public void render(QuarryTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        IBlockState state = te.getWorld().getBlockState(te.getPos());
        if (!(state.getBlock() instanceof QuarryBlock)) {
            return;
        }
        EnumFacing facing = state.getValue(QuarryBlock.FACING);
        GlStateManager.pushMatrix();
        if (facing == EnumFacing.NORTH) {
            GlStateManager.translate(x + 0.5D, y + 0.5D, z - 0.5D);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        } else if (facing == EnumFacing.EAST) {
            GlStateManager.translate(x + 1.5D, y + 0.5D, z + 0.5D);
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        } else if (facing == EnumFacing.SOUTH) {
            GlStateManager.translate(x + 0.5D, y + 0.5D, z + 1.5D);
        } else {
            GlStateManager.translate(x - 0.5D, y + 0.5D, z + 0.5D);
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        }
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        MODEL.setup(te.getGrindRotation(partialTicks));
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        MODEL.render(0.0625F);
        GlStateManager.popMatrix();
    }
}
