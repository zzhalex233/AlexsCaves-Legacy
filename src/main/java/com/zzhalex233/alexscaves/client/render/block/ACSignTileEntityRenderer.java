package com.zzhalex233.alexscaves.client.render.block;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.block.CaveStandingSignBlock;
import com.zzhalex233.alexscaves.server.block.CaveWallSignBlock;
import com.zzhalex233.alexscaves.server.block.entity.ACSignTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ACSignTileEntityRenderer extends TileEntitySpecialRenderer<ACSignTileEntity> {
    private final ModelSign model = new ModelSign();

    @Override
    public void render(ACSignTileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        Block block = tile.getBlockType();
        if (!(block instanceof CaveStandingSignBlock || block instanceof CaveWallSignBlock)) {
            return;
        }
        GlStateManager.pushMatrix();
        if (block instanceof CaveStandingSignBlock) {
            GlStateManager.translate((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
            float rotation = 0.0F;
            IBlockState state = tile.getWorld().getBlockState(tile.getPos());
            if (state.getBlock() instanceof BlockStandingSign) {
                rotation = state.getValue(BlockStandingSign.ROTATION) * 360 / 16.0F;
            }
            GlStateManager.rotate(-rotation, 0.0F, 1.0F, 0.0F);
            model.signStick.showModel = true;
        } else {
            IBlockState state = tile.getWorld().getBlockState(tile.getPos());
            float rotation = -state.getValue(BlockWallSign.FACING).getHorizontalIndex() * 90.0F;
            GlStateManager.translate((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
            GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
            model.signStick.showModel = false;
        }
        GlStateManager.scale(0.6666667F, -0.6666667F, -0.6666667F);
        bindTexture(texture(block));
        model.renderSign();
        renderText(tile);
        GlStateManager.popMatrix();
    }

    private void renderText(ACSignTileEntity tile) {
        FontRenderer font = getFontRenderer();
        GlStateManager.translate(0.0F, 0.33333334F, 0.046666667F);
        GlStateManager.scale(0.010416667F, -0.010416667F, 0.010416667F);
        GlStateManager.glNormal3f(0.0F, 0.0F, -0.010416667F);
        GlStateManager.depthMask(false);
        int color = 0;
        for (int i = 0; i < tile.signText.length; i++) {
            ITextComponent text = tile.signText[i];
            String line = text == null ? "" : text.getFormattedText();
            if (i == tile.lineBeingEdited) {
                line = "> " + line + " <";
            }
            font.drawString(line, -font.getStringWidth(line) / 2, i * 10 - tile.signText.length * 5, color);
        }
        GlStateManager.depthMask(true);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private ResourceLocation texture(Block block) {
        String wood = block instanceof CaveWallSignBlock ? ((CaveWallSignBlock) block).getWoodName() : ((CaveStandingSignBlock) block).getWoodName();
        return new ResourceLocation(AlexsCaves.MODID, "textures/entity/signs/" + wood + ".png");
    }
}
