package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.item.DinosaurSpiritEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class TremorsaurusSpiritModel extends ModelBase {
    private final ModelRenderer root;
    private final ModelRenderer neck;
    private final ModelRenderer head;
    private final ModelRenderer jaw;

    public TremorsaurusSpiritModel() {
        textureWidth = 256;
        textureHeight = 256;
        root = new ModelRenderer(this);
        root.setRotationPoint(0.0F, 18.0F, 0.0F);
        neck = new ModelRenderer(this);
        neck.setRotationPoint(0.0F, -8.0F, 0.0F);
        neck.setTextureOffset(0, 76).addBox(-5.0F, -5.0F, -16.0F, 10, 10, 18, 0.0F);
        root.addChild(neck);
        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, -1.0F, -16.0F);
        head.setTextureOffset(0, 0).addBox(-8.0F, -8.0F, -16.0F, 16, 12, 18, 0.0F);
        head.setTextureOffset(74, 0).addBox(-6.0F, -1.0F, -28.0F, 12, 7, 14, 0.0F);
        neck.addChild(head);
        jaw = new ModelRenderer(this);
        jaw.setRotationPoint(0.0F, 5.0F, -13.0F);
        jaw.setTextureOffset(0, 32).addBox(-6.0F, 0.0F, -14.0F, 12, 4, 16, 0.0F);
        head.addChild(jaw);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        float bite = entity instanceof DinosaurSpiritEntity ? ((DinosaurSpiritEntity) entity).getAbilityProgress(limbSwingAmount) : 0.0F;
        neck.rotateAngleX = -0.15F;
        head.rotateAngleX = 0.15F + bite * 0.25F;
        jaw.rotateAngleX = bite * 0.75F;
        root.render(scale);
    }
}
