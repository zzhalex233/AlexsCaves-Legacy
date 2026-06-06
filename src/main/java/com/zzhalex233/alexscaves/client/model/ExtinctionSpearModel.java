package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.item.ExtinctionSpearEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ExtinctionSpearModel extends ModelBase {
    private final ModelRenderer spear;
    private final ModelRenderer spear2;

    public ExtinctionSpearModel() {
        textureWidth = 32;
        textureHeight = 32;
        spear = new ModelRenderer(this);
        spear.setRotationPoint(0.0F, 15.0F, 0.0F);
        spear.setTextureOffset(0, 0).addBox(-0.5F, -20.0F, -0.5F, 1, 29, 1, 0.0F);
        spear.setTextureOffset(12, 0).addBox(-1.0F, -18.0F, -1.0F, 2, 2, 2, 0.0F);
        spear.setTextureOffset(4, 0).addBox(-1.0F, -15.0F, -1.0F, 2, 2, 2, 0.0F);
        spear.setTextureOffset(4, 19).addBox(-0.5F, -5.0F, -0.5F, 1, 12, 1, 0.25F);
        spear.setTextureOffset(8, 10).addBox(0.0F, -18.0F, -5.5F, 0, 2, 5, 0.0F);
        spear.setTextureOffset(14, 1).addBox(0.0F, -15.0F, -3.5F, 0, 2, 3, 0.0F);
        spear.setTextureOffset(4, 0).addBox(0.0F, -18.0F, 0.5F, 0, 2, 5, 0.0F);
        spear.setTextureOffset(8, 14).addBox(0.0F, -15.0F, 0.5F, 0, 2, 3, 0.0F);
        spear.setTextureOffset(18, 9).addBox(0.0F, -35.0F, -3.5F, 0, 16, 7, 0.0F);

        spear2 = new ModelRenderer(this);
        spear2.setRotationPoint(0.0F, -23.0F, 0.0F);
        spear2.rotateAngleY = -1.5708F;
        spear2.setTextureOffset(18, 9).addBox(0.0F, -12.0F, -3.5F, 0, 16, 7, 0.0F);
        spear.addChild(spear2);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        spear.rotateAngleY = entity instanceof ExtinctionSpearEntity && ((ExtinctionSpearEntity) entity).isWiggling() ? MathHelper.sin(ageInTicks * 2.0F - 1.5F) * 0.2F : 0.0F;
        spear.render(scale);
    }
}
