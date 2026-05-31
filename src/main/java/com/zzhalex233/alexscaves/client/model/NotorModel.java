package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.NotorEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class NotorModel extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer propeller;
    private final ModelRenderer blades;
    private final ModelRenderer larm;
    private final ModelRenderer rarm;
    private final ModelRenderer lleg;
    private final ModelRenderer rleg;

    public NotorModel() {
        textureWidth = 64;
        textureHeight = 64;
        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, 20.0F, 0.0F);
        body.setTextureOffset(0, 4).addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F);
        propeller = new ModelRenderer(this);
        propeller.setRotationPoint(0.0F, -2.0F, 0.0F);
        body.addChild(propeller);
        propeller.setTextureOffset(0, 0).addBox(-0.5F, -2.0F, 0.0F, 1, 3, 0, 0.0F);
        ModelRenderer bladePost = new ModelRenderer(this);
        bladePost.setRotationPoint(0.0F, -1.0F, 0.0F);
        bladePost.rotateAngleY = -1.5708F;
        propeller.addChild(bladePost);
        bladePost.setTextureOffset(0, 0).addBox(-0.5F, -1.0F, 0.0F, 1, 3, 0, 0.0F);
        blades = new ModelRenderer(this);
        blades.setRotationPoint(0.0F, -1.0F, 0.0F);
        propeller.addChild(blades);
        blades.setTextureOffset(0, 0).addBox(-9.0F, 0.0F, -2.0F, 18, 0, 4, 0.0F);
        larm = limb(-1.5F, 2.0F, 2.0F);
        rarm = limb(1.5F, 2.0F, 2.0F);
        lleg = limb(-1.5F, 2.0F, -2.0F);
        rleg = limb(1.5F, 2.0F, -2.0F);
    }

    private ModelRenderer limb(float x, float y, float z) {
        ModelRenderer limb = new ModelRenderer(this);
        limb.setRotationPoint(x, y, z);
        body.addChild(limb);
        limb.setTextureOffset(4, 12).addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0, 0.0F);
        return limb;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        body.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        float land = entity instanceof NotorEntity ? ((NotorEntity) entity).getGroundProgress(ageInTicks - entity.ticksExisted) : 0.0F;
        float fly = 1.0F - land;
        float bob = MathHelper.sin(ageInTicks * 0.2F) * fly;
        body.rotationPointY = 20.0F + bob * 1.2F + Math.abs(MathHelper.sin(ageInTicks * 0.1F)) * land;
        propeller.rotateAngleY = entity instanceof NotorEntity ? (float) Math.toRadians(((NotorEntity) entity).getPropellerAngle(ageInTicks - entity.ticksExisted)) : 0.0F;
        larm.rotateAngleX = MathHelper.sin(ageInTicks * 0.2F) * 0.25F + 0.2F * fly;
        rarm.rotateAngleX = MathHelper.sin(ageInTicks * 0.2F) * 0.25F + 0.2F * fly;
        lleg.rotateAngleX = MathHelper.sin(ageInTicks * 0.2F + 2.0F) * 0.25F + 0.2F * fly;
        rleg.rotateAngleX = MathHelper.sin(ageInTicks * 0.2F + 2.0F) * 0.25F + 0.2F * fly;
        body.rotateAngleX = MathHelper.sin(limbSwing * 0.2F + 2.0F) * limbSwingAmount * fly * 0.1F;
        body.rotateAngleZ = MathHelper.sin(ageInTicks * 0.4F - 1.0F) * fly * 0.05F;
    }

    public Vec3d getChainPosition() {
        return new Vec3d(0.0D, 1.5D, 0.0D);
    }
}
