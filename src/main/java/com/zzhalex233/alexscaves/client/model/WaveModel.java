package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.item.WaveEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class WaveModel extends ModelBase {
    private final ModelRenderer main;
    private final ModelRenderer top;

    public WaveModel() {
        textureWidth = 128;
        textureHeight = 128;

        main = new ModelRenderer(this);
        main.setRotationPoint(0.0F, 24.0F, 0.0F);
        main.setTextureOffset(0, 28).addBox(-13.0F, -7.2919F, 0.9497F, 26, 6, 12, -0.01F);

        top = new ModelRenderer(this);
        top.setRotationPoint(0.0F, -2.0F, -4.0F);
        top.rotateAngleX = 0.7854F;
        top.setTextureOffset(0, 0).addBox(-13.0F, -7.0F, 3.0F, 26, 11, 17);
        main.addChild(top);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entity instanceof WaveEntity) {
            WaveEntity wave = (WaveEntity) entity;
            float wobble = (MathHelper.sin(ageInTicks * 0.1F) + 1.0F) * 0.2F;
            float slam = wave.getSlamAmount(ageInTicks - wave.activeWaveTicks) * 1.4F;
            top.rotateAngleX = 0.7854F + slam * (float) Math.toRadians(100.0D) + wobble;
            top.rotationPointY = -2.0F - slam + wobble * 8.0F;
            top.rotationPointZ = -4.0F + slam * 13.0F + wobble * 2.0F;
        }
        main.render(scale);
    }
}
