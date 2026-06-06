package com.zzhalex233.alexscaves.client.render.block;

import com.zzhalex233.alexscaves.server.block.entity.AbyssalAltarTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Random;

public class AbyssalAltarTileEntityRenderer extends TileEntitySpecialRenderer<AbyssalAltarTileEntity> {
    private final Random random = new Random();

    @Override
    public void render(AbyssalAltarTileEntity altar, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        ItemStack stack = altar.getDisplayStack();
        if (stack.isEmpty()) {
            return;
        }
        random.setSeed((long) Item.getIdFromItem(stack.getItem()) + stack.getMetadata());
        int count = getModelCount(stack);
        boolean gui3d = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, altar.getWorld(), null).isGui3d();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 1.02D, z + 0.5D);
        GlStateManager.rotate(altar.getItemAngle(), 0.0F, 1.0F, 0.0F);
        float slideBy = altar.getStackInSlot(0).isEmpty() ? 0.5F * (1.0F - altar.getSlideProgress(partialTicks)) : 0.5F * altar.getSlideProgress(partialTicks);
        GlStateManager.translate(0.0F, 0.0F, slideBy);
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        for (int i = 0; i < count; i++) {
            GlStateManager.pushMatrix();
            if (i > 0) {
                if (gui3d) {
                    GlStateManager.translate((random.nextFloat() * 2.0F - 1.0F) * 0.25F, (random.nextFloat() * 2.0F - 1.0F) * 0.25F, (random.nextFloat() * 2.0F - 1.0F) * 0.25F - i * 0.1F);
                } else {
                    GlStateManager.translate((random.nextFloat() * 2.0F - 1.0F) * 0.075F, (random.nextFloat() * 2.0F - 1.0F) * 0.075F, 0.0F);
                }
            }
            Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
            if (!gui3d) {
                GlStateManager.translate(0.0F, 0.0F, 0.09375F);
            }
        }
        GlStateManager.popMatrix();
    }

    private int getModelCount(ItemStack stack) {
        if (stack.getCount() > 48) {
            return 5;
        }
        if (stack.getCount() > 32) {
            return 4;
        }
        if (stack.getCount() > 16) {
            return 3;
        }
        return stack.getCount() > 1 ? 2 : 1;
    }
}
