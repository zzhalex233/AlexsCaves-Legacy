package com.zzhalex233.alexscaves.client.model.armor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ACArmorModel extends ModelBiped {
    private final Type type;
    private ModelRenderer frontFlap;
    private ModelRenderer backFlap;
    private ModelRenderer hazmatMask;
    private ModelRenderer rightTail1;
    private ModelRenderer rightTail2;
    private ModelRenderer leftTail1;
    private ModelRenderer leftTail2;
    private ModelRenderer cape;
    private ModelRenderer capeTail;

    private ACArmorModel(Type type) {
        super(0.5F, 0.0F, type.textureWidth, type.textureHeight);
        this.type = type;
        if (type == Type.DIVING) {
            bipedHeadwear = new ModelRenderer(this);
        }
        if (type == Type.PRIMORDIAL) {
            primordial();
        } else if (type == Type.HAZMAT) {
            hazmat();
        } else if (type == Type.DIVING) {
            diving();
        } else if (type == Type.DARKNESS) {
            darkness();
        } else if (type == Type.RAINBOUNCE) {
            rainbounce();
        } else if (type == Type.GINGERBREAD) {
            gingerbread();
        }
    }

    public static ModelBiped get(String textureName) {
        if ("primordial_armor".equals(textureName)) {
            return Holder.PRIMORDIAL;
        }
        if ("hazmat_suit".equals(textureName)) {
            return Holder.HAZMAT;
        }
        if ("diving_suit".equals(textureName)) {
            return Holder.DIVING;
        }
        if ("darkness_armor".equals(textureName)) {
            return Holder.DARKNESS;
        }
        if ("rainbounce_boots".equals(textureName)) {
            return Holder.RAINBOUNCE;
        }
        if ("gingerbread_armor".equals(textureName)) {
            return Holder.GINGERBREAD;
        }
        return null;
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        if (type == Type.PRIMORDIAL) {
            float minLeg = Math.min(bipedRightLeg.rotateAngleX, bipedLeftLeg.rotateAngleX);
            float maxLeg = Math.max(bipedRightLeg.rotateAngleX, bipedLeftLeg.rotateAngleX);
            frontFlap.rotateAngleX = minLeg - limbSwingAmount * 0.43633232F;
            frontFlap.rotationPointY = 12.0F - limbSwingAmount * 1.2F;
            backFlap.rotateAngleX = maxLeg + limbSwingAmount * 0.43633232F;
            backFlap.rotationPointY = 12.0F - limbSwingAmount * 1.2F;
        } else if (type == Type.HAZMAT) {
            hazmatMask.rotateAngleY = -(bipedHead.rotateAngleY - bipedBody.rotateAngleY) * 0.3F;
        } else if (type == Type.DARKNESS) {
            float stillAmount = 1.0F - limbSwingAmount;
            float capeWaveIdle = (float) Math.toRadians(Math.sin(ageInTicks * 0.05F) * 10.0F + 5.0F) * stillAmount;
            float capeTailWaveIdle = (float) Math.toRadians(Math.sin(ageInTicks * 0.05F - 1.0F) * 10.0F) * stillAmount;
            float capeWaveWalk = (float) Math.toRadians(Math.sin(ageInTicks * 0.5F) * 15.0F) * limbSwingAmount;
            float capeTailWaveWalk = (float) Math.toRadians(Math.sin(ageInTicks * 0.5F - 1.5F) * 10.0F) * limbSwingAmount;
            cape.rotateAngleX = -(float) Math.toRadians(70.0D) * stillAmount + capeWaveIdle + capeWaveWalk;
            capeTail.rotateAngleX = (float) Math.toRadians(25.0D) * stillAmount + capeTailWaveIdle + capeTailWaveWalk;
            rightTail1.rotateAngleX = (float) Math.toRadians(Math.sin(ageInTicks * 0.1F + 1.0F) * 5.0F + 2.5F) * stillAmount;
            leftTail1.rotateAngleX = rightTail1.rotateAngleX;
            rightTail1.rotateAngleY = -(float) Math.toRadians(Math.cos(ageInTicks * 0.1F + 2.0F) * 10.0F) + (float) Math.toRadians(30.0D) * stillAmount;
            leftTail1.rotateAngleY = -rightTail1.rotateAngleY;
            rightTail2.rotateAngleY = -(float) Math.toRadians(Math.cos(ageInTicks * 0.1F + 3.0F) * 20.0F - 10.0F);
            leftTail2.rotateAngleY = -rightTail2.rotateAngleY;
        }
    }

    private void primordial() {
        part(bipedHead, 0.0F, 0.0F, 0.0F)
                .box(100, 116, -6.0F, -13.0F, 2.0F, 12, 10, 2, 0.52F, false)
                .box(64, 116, -7.0F, -14.0F, 2.0F, 14, 11, 1, 0.52F, false)
                .box(114, 91, -2.0F, -8.0F, -7.0F, 4, 4, 3, 0.52F, false)
                .box(95, 85, -3.5F, -13.0F, -6.9F, 7, 5, 4, 0.52F, false)
                .box(120, 108, 2.0F, -10.0F, -4.0F, 2, 2, 2, 0.52F, false)
                .box(120, 112, -4.0F, -10.0F, -4.0F, 2, 2, 2, 0.52F, true);
        part(bipedBody, 0.0F, 0.0F, 0.0F).box(16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.75F, false);
        part(bipedRightArm, 0.0F, 0.0F, 0.0F).box(40, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.75F, false);
        part(bipedLeftArm, 0.0F, 0.0F, 0.0F).box(48, 49, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.75F, false);
        part(bipedRightLeg, 0.0F, 0.0F, 0.0F).box(0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.6F, false);
        part(bipedLeftLeg, 0.0F, 0.0F, 0.0F).box(0, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.6F, false);
        frontFlap = part(bipedBody, 0.0F, 12.0F, -2.0F).box(95, 104, -3.9F, -1.0F, 0.1F, 8, 7, 4, 0.75F, false).renderer;
        backFlap = part(bipedBody, 0.0F, 12.0F, 2.0F).box(56, 104, -4.1F, -1.0F, -4.1F, 8, 7, 4, 0.75F, false).renderer;
    }

    private void hazmat() {
        hazmatMask = part(bipedHead, 0.0F, -1.5F, -4.0F)
                .box(116, 100, -2.0F, -1.5F, -2.0F, 4, 5, 2, 0.4F, false).renderer;
        part(hazmatMask, 2.5F, 1.0F, 1.0F, 0.3927F, -0.7854F, 0.0F)
                .box(78, 99, -1.5F, -1.0F, -4.0F, 3, 3, 5, 0.4F, true);
        part(hazmatMask, -2.5F, 1.0F, 1.0F, 0.3927F, 0.7854F, 0.0F)
                .box(78, 99, -1.5F, -1.0F, -4.0F, 3, 3, 5, 0.4F, false);
        part(bipedBody, 0.0F, 24.0F, 0.0F).box(16, 32, -4.0F, -24.0F, -2.0F, 8, 12, 4, 0.75F, false);
        part(bipedRightArm, 0.0F, 0.0F, 0.0F)
                .box(40, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.75F, false)
                .box(42, 116, -4.0F, -4.0F, -3.0F, 5, 6, 6, 0.6F, true);
        part(bipedLeftArm, 0.0F, 0.0F, 0.0F)
                .box(48, 49, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.75F, false)
                .box(42, 116, -1.0F, -4.0F, -3.0F, 5, 6, 6, 0.6F, false);
        part(bipedRightLeg, 0.0F, 0.0F, 0.0F).box(0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.6F, false);
        part(bipedLeftLeg, 0.0F, 0.0F, 0.0F).box(0, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.6F, false);
    }

    private void diving() {
        part(bipedHead, 0.0F, 0.0F, 0.0F)
                .box(0, 32, 4.0F, -5.0F, -3.0F, 2, 4, 4, 0.5F, false)
                .box(0, 32, -6.0F, -5.0F, -3.0F, 2, 4, 4, 0.5F, true)
                .box(24, 0, -3.0F, -7.01F, -5.0F, 6, 6, 2, 0.5F, false)
                .box(14, 37, -4.5F, -1.0F, -4.5F, 9, 2, 9, 0.5F, false);
        part(bipedBody, 0.0F, 0.0F, 0.0F).box(40, 4, -2.0F, 2.0F, 2.0F, 4, 8, 4, 0.5F, false);
        part(bipedLeftArm, 1.0F, 0.0F, 0.0F).box(48, 48, -2.0F, -2.0F, -2.0F, 4, 12, 4, 0.85F, false);
        part(bipedRightArm, -1.0F, 0.0F, 0.0F).box(48, 48, -2.0F, -2.0F, -2.0F, 4, 12, 4, 0.85F, true);
        part(bipedLeftLeg, 0.0F, 0.75F, 0.0F).box(32, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.85F, false);
        part(bipedRightLeg, 0.0F, 0.75F, 0.0F).box(32, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.85F, true);
        part(bipedBody, 0.0F, 0.0F, 0.0F).box(0, 48, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.85F, false);
    }

    private void gingerbread() {
        part(bipedHead, 0.0F, 0.0F, 0.0F)
                .box(0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.52F, false)
                .box(32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 1.0F, false)
                .box(0, 64, -4.0F, -8.0F, -4.0F, 8, 8, 8, 1.25F, false);
        part(bipedHead, -0.0855F, -11.0F, 4.4918F, 0.0F, -1.5708F, 0.0F)
                .box(27, 101, -1.5F, -2.0F, -2.0F, 3, 4, 4, 0.52F, false);
        part(bipedHead, -7.5468F, -11.0F, -6.4613F, 0.0F, 1.5708F, 0.0F)
                .box(27, 101, -2.4532F, -2.0F, 5.4613F, 3, 4, 4, 0.52F, false);
        part(bipedHead, 0.0F, -12.0F, 0.0F, 0.0F, 0.0F, -0.3927F)
                .box(0, 98, -8.0F, 0.0F, -6.0F, 8, 3, 11, 0.52F, true);
        part(bipedHead, 0.0F, -12.0F, 0.0F, 0.0F, 0.0F, 0.3927F)
                .box(0, 98, 0.0F, 0.0F, -6.0F, 8, 3, 11, 0.52F, false);
        part(bipedBody, 0.0F, 0.0F, 0.0F)
                .box(16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.75F, false)
                .box(104, 0, -4.0F, 0.0F, -2.0F, 8, 12, 4, 1.1F, false)
                .box(28, 93, -2.5F, 2.0F, -4.0F, 5, 5, 2, 0.5F, false);
        part(bipedRightArm, 0.0F, 0.0F, 0.0F)
                .box(32, 64, -5.0F, -7.0F, -4.0F, 4, 10, 8, 0.5F, true)
                .box(0, 80, -3.0F, -2.0F, -2.0F, 4, 12, 4, 1.0F, true)
                .box(27, 101, -6.0F, -2.0F, -2.0F, 3, 4, 4, 0.5F, true)
                .box(40, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.75F, false);
        part(bipedLeftArm, 0.0F, 0.0F, 0.0F)
                .box(48, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.75F, false)
                .box(0, 80, -1.0F, -2.0F, -2.0F, 4, 12, 4, 1.0F, false)
                .box(32, 64, 1.0F, -7.0F, -4.0F, 4, 10, 8, 0.5F, false)
                .box(27, 101, 3.0F, -2.0F, -2.0F, 3, 4, 4, 0.5F, false);
        part(bipedRightLeg, 0.0F, 0.0F, 0.0F)
                .box(35, 112, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.9F, true)
                .box(0, 112, -2.5F, 6.0F, -2.5F, 5, 6, 5, 0.65F, true)
                .box(0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.65F, false);
        part(bipedLeftLeg, 0.0F, 0.0F, 0.0F)
                .box(0, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.65F, false)
                .box(35, 112, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.9F, false)
                .box(0, 112, -2.5F, 6.0F, -2.5F, 5, 6, 5, 0.65F, false);
    }

    private void darkness() {
        part(bipedHead, 0.0F, 0.0F, 0.0F)
                .box(68, 112, -6.0F, -2.0F, -5.5F, 12, 4, 12, 0.5F, false)
                .box(32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 1.5F, false);
        rightTail1 = part(bipedHead, 4.5F, -8.25F, 2.0F).box(104, 105, -2.5F, -2.75F, -1.0F, 5, 5, 7, 0.0F, false).renderer;
        rightTail2 = part(rightTail1, 0.0F, 0.25F, 5.5F).box(104, 108, 0.0F, -2.0F, -1.5F, 0, 4, 10, 0.0F, false).renderer;
        leftTail1 = part(bipedHead, -4.5F, -8.25F, 2.0F).box(104, 105, -2.5F, -2.75F, -1.0F, 5, 5, 7, 0.0F, true).renderer;
        leftTail2 = part(leftTail1, 0.0F, 0.25F, 5.5F).box(104, 108, 0.0F, -2.0F, -1.5F, 0, 4, 10, 0.0F, true).renderer;
        part(bipedBody, 0.0F, 0.0F, 0.0F).box(57, 123, -1.5F, 1.0F, -3.5F, 3, 3, 2, 0.0F, false);
        cape = part(bipedBody, 0.0F, -1.0F, 3.0F)
                .box(30, 111, -7.0F, 0.5F, 1.0F, 14, 0, 11, 0.0F, false)
                .box(5, 111, -5.0F, 0.0F, 0.0F, 10, 1, 16, 0.0F, false).renderer;
        capeTail = part(cape, 0.0F, 0.5F, 14.5F).box(-17, 94, -7.0F, 0.0F, -1.5F, 14, 0, 17, 0.0F, false).renderer;
    }

    private void rainbounce() {
        part(bipedRightLeg, 0.0F, 0.0F, 0.0F).box(0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.85F, false);
        part(bipedLeftLeg, 0.0F, 0.0F, 0.0F).box(0, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.85F, false);
    }

    private Part part(ModelRenderer parent, float pivotX, float pivotY, float pivotZ) {
        return part(parent, pivotX, pivotY, pivotZ, 0.0F, 0.0F, 0.0F);
    }

    private Part part(ModelRenderer parent, float pivotX, float pivotY, float pivotZ, float rotX, float rotY, float rotZ) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.setRotationPoint(pivotX, pivotY, pivotZ);
        renderer.rotateAngleX = rotX;
        renderer.rotateAngleY = rotY;
        renderer.rotateAngleZ = rotZ;
        parent.addChild(renderer);
        return new Part(renderer);
    }

    private static class Part {
        private final ModelRenderer renderer;

        private Part(ModelRenderer renderer) {
            this.renderer = renderer;
        }

        private Part box(int textureX, int textureY, float x, float y, float z, int width, int height, int depth, float scale, boolean mirror) {
            renderer.mirror = mirror;
            renderer.setTextureOffset(textureX, textureY).addBox(x, y, z, width, height, depth, scale);
            renderer.mirror = false;
            return this;
        }
    }

    private enum Type {
        PRIMORDIAL(128, 128),
        HAZMAT(128, 128),
        DIVING(64, 64),
        DARKNESS(128, 128),
        RAINBOUNCE(128, 128),
        GINGERBREAD(128, 128);

        private final int textureWidth;
        private final int textureHeight;

        Type(int textureWidth, int textureHeight) {
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
        }
    }

    private static class Holder {
        private static final ACArmorModel PRIMORDIAL = new ACArmorModel(Type.PRIMORDIAL);
        private static final ACArmorModel HAZMAT = new ACArmorModel(Type.HAZMAT);
        private static final ACArmorModel DIVING = new ACArmorModel(Type.DIVING);
        private static final ACArmorModel DARKNESS = new ACArmorModel(Type.DARKNESS);
        private static final ACArmorModel RAINBOUNCE = new ACArmorModel(Type.RAINBOUNCE);
        private static final ACArmorModel GINGERBREAD = new ACArmorModel(Type.GINGERBREAD);
    }
}
