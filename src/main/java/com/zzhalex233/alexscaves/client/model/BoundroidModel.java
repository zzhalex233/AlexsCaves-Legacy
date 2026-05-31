package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.BoundroidEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class BoundroidModel extends ModelBase {
    private final ModelRenderer head;
    private final ModelRenderer bump1;
    private final ModelRenderer bump2;

    public BoundroidModel() {
        textureWidth = 128;
        textureHeight = 128;
        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, 13.0F, 0.0F);
        head.setTextureOffset(0, 27).addBox(-6.0F, 1.0F, -6.0F, 12, 5, 12, 0.0F);
        head.setTextureOffset(0, 0).addBox(-11.0F, 6.0F, -11.0F, 22, 5, 22, 0.0F);
        bump1 = new ModelRenderer(this);
        bump1.setRotationPoint(0.0F, -1.5F, 0.0F);
        bump1.rotateAngleY = -0.7854F;
        head.addChild(bump1);
        bump1.setTextureOffset(0, 0).addBox(-4.0F, -2.5F, 0.0F, 8, 5, 0, 0.0F);
        bump2 = new ModelRenderer(this);
        bump2.setRotationPoint(0.0F, -1.5F, 0.0F);
        bump2.rotateAngleY = 0.7854F;
        head.addChild(bump2);
        bump2.setTextureOffset(0, 0).addBox(-4.0F, -2.5F, 0.0F, 8, 5, 0, 0.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        head.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        float ground = entity instanceof BoundroidEntity ? ((BoundroidEntity) entity).getGroundProgress(ageInTicks - entity.ticksExisted) : 1.0F;
        float offGround = 1.0F - ground;
        head.rotateAngleY = netHeadYaw * 0.017453292F;
        head.rotateAngleX = MathHelper.sin(ageInTicks * 0.15F - 1.0F) * 0.2F * offGround;
        head.rotateAngleZ = MathHelper.sin(ageInTicks * 0.15F + 1.0F) * 0.2F * offGround;
    }

    public void showChains(boolean show) {
        bump1.showModel = show;
        bump2.showModel = show;
    }
}
