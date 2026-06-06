package com.zzhalex233.alexscaves.client.render.item;

import com.zzhalex233.alexscaves.client.render.block.SirenLightTileEntityRenderer;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;

public class SirenLightItemStackRenderer extends TileEntityItemStackRenderer {
    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        SirenLightTileEntityRenderer.renderItem(0.75F);
    }
}
