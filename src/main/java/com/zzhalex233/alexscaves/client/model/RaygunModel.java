package com.zzhalex233.alexscaves.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class RaygunModel extends ModelBase {
    private final ModelRenderer root;

    public RaygunModel() {
        textureWidth = 64;
        textureHeight = 64;

        root = new ModelRenderer(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);

        ModelRenderer handle = new ModelRenderer(this);
        handle.setRotationPoint(0.0F, -3.0F, 6.5F);
        root.addChild(handle);
        handle.setTextureOffset(14, 0).addBox(-1.0F, -3.0F, -0.5F, 2, 6, 3, 0.0F);
        handle.setTextureOffset(0, 0).addBox(-1.0F, -3.0F, -0.5F, 2, 6, 3, 0.25F);

        ModelRenderer trigger = new ModelRenderer(this);
        trigger.setRotationPoint(0.0F, -1.5F, -3.5F);
        handle.addChild(trigger);
        trigger.setTextureOffset(0, 8).addBox(0.0F, -2.5F, -1.0F, 0, 5, 4, 0.0F);

        ModelRenderer main = new ModelRenderer(this);
        main.setRotationPoint(0.0F, 3.0F, -2.5F);
        handle.addChild(main);
        main.setTextureOffset(18, 6).addBox(-3.0F, -12.0F, -2.0F, 6, 6, 6, 0.25F);
        main.setTextureOffset(0, 0).addBox(0.0F, -15.0F, -8.0F, 0, 5, 18, 0.0F);
        main.setTextureOffset(24, 23).addBox(-3.0F, -12.0F, -2.0F, 6, 6, 6, 0.0F);

        ModelRenderer barrel = new ModelRenderer(this);
        barrel.setRotationPoint(0.0F, -9.0F, -6.0F);
        main.addChild(barrel);
        barrel.setTextureOffset(20, 35).addBox(-2.0F, -2.0F, -2.0F, 4, 4, 6, 0.0F);
        barrel.setTextureOffset(0, 35).addBox(-2.0F, -2.0F, -2.0F, 4, 4, 6, 0.25F);

        ModelRenderer nose = new ModelRenderer(this);
        nose.setRotationPoint(0.0F, 9.0F, 6.0F);
        barrel.addChild(nose);
        nose.setTextureOffset(36, 14).addBox(-0.5F, -9.5F, -14.0F, 1, 1, 6, 0.0F);

        ModelRenderer ring = new ModelRenderer(this);
        ring.setRotationPoint(0.0F, -9.0F, -14.0F);
        nose.addChild(ring);
        ring.setTextureOffset(42, 7).addBox(-3.5F, -3.5F, 1.0F, 7, 7, 0, 0.0F);

        ModelRenderer ring2 = new ModelRenderer(this);
        ring2.setRotationPoint(0.0F, -9.0F, -13.0F);
        nose.addChild(ring2);
        ring2.setTextureOffset(42, 7).addBox(-3.5F, -3.5F, 1.0F, 7, 7, 0, 0.0F);

        ModelRenderer ring3 = new ModelRenderer(this);
        ring3.setRotationPoint(0.0F, -9.0F, -12.0F);
        nose.addChild(ring3);
        ring3.setTextureOffset(42, 7).addBox(-3.5F, -3.5F, 1.0F, 7, 7, 0, 0.0F);

        ModelRenderer ball = new ModelRenderer(this);
        ball.setRotationPoint(0.0F, -9.0F, -16.0F);
        nose.addChild(ball);
        ball.setTextureOffset(8, 12).addBox(-1.0F, -1.0F, 0.0F, 2, 2, 2, 0.0F);

        ModelRenderer grip = new ModelRenderer(this);
        grip.setRotationPoint(0.0F, -3.0F, -8.0F);
        handle.addChild(grip);
        grip.setTextureOffset(48, 37).addBox(-1.0F, -1.0F, -2.0F, 2, 2, 6, 0.25F);
        grip.setTextureOffset(48, 27).addBox(-1.0F, -1.0F, -2.0F, 2, 2, 6, 0.0F);
    }

    public void setupAnim(float useAmount, float ageInTicks) {
        root.rotateAngleX = 0.0F;
        root.rotateAngleY = 0.0F;
        root.rotateAngleZ = 0.0F;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        root.render(scale);
    }
}
