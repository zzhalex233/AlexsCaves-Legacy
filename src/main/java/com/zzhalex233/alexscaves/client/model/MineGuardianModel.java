package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.MineGuardianEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MineGuardianModel extends ModelBase {
    private final ModelRenderer head;
    private final ModelRenderer eye;
    private final ModelRenderer[] spikes = new ModelRenderer[12];
    private final float[] spikeBaseY = new float[12];

    public MineGuardianModel() {
        textureWidth = 64;
        textureHeight = 64;
        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, 24.0F, 0.0F);
        head.setTextureOffset(0, 0).addBox(-6.0F, -14.0F, -8.0F, 12, 12, 16);
        head.setTextureOffset(0, 28).addBox(-8.0F, -14.0F, -6.0F, 2, 12, 12);
        head.setTextureOffset(0, 28).addBox(6.0F, -14.0F, -6.0F, 2, 12, 12);
        head.setTextureOffset(16, 40).addBox(-6.0F, -16.0F, -6.0F, 12, 2, 12);
        eye = new ModelRenderer(this, 8, 0);
        eye.setRotationPoint(0.0F, -9.0F, -8.25F);
        eye.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 1);
        head.addChild(eye);
        addSpike(0, 0.0F, 0.0F, 0.7854F, 10.25F, -4.5F, -1.0F);
        addSpike(1, 0.0F, 0.0F, -0.7854F, -12.25F, -4.5F, -1.0F);
        addSpike(2, 0.7854F, 0.0F, 0.0F, -1.0F, -4.5F, -12.25F);
        addSpike(3, -0.7854F, 0.0F, 0.0F, -1.0F, -4.5F, 10.5F);
        addSpike(4, 0.0F, 0.0F, 2.3562F, 10.25F, -27.5F, -1.0F);
        addSpike(5, 0.0F, 0.0F, -2.3562F, -12.25F, -27.5F, -1.0F);
        addSpike(6, 2.3562F, 0.0F, 0.0F, -1.0F, -28.5F, -12.25F);
        addSpike(7, -2.3562F, 0.0F, 0.0F, -1.0F, -27.5F, 10.25F);
        addSpike(8, 1.5708F, -0.7854F, 0.0F, -1.0F, -17.5F, -17.0F);
        addSpike(9, 1.5708F, 0.7854F, 0.0F, -1.0F, -17.5F, -17.0F);
        addSpike(10, 1.5708F, -2.3562F, 0.0F, -1.0F, -17.5F, -17.0F);
        addSpike(11, 1.5708F, 2.3562F, 0.0F, -1.0F, -17.5F, -17.0F);
    }

    private void addSpike(int index, float xRot, float yRot, float zRot, float x, float y, float z) {
        ModelRenderer root = new ModelRenderer(this);
        root.setRotationPoint(0.0F, -24.0F, 0.0F);
        root.rotateAngleX = xRot;
        root.rotateAngleY = yRot;
        root.rotateAngleZ = zRot;
        ModelRenderer spike = new ModelRenderer(this, 0, 0);
        spike.addBox(x, y, z, 2, 9, 2);
        root.addChild(spike);
        head.addChild(root);
        spikes[index] = spike;
        spikeBaseY[index] = spike.rotationPointY;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        head.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        MineGuardianEntity guardian = entity instanceof MineGuardianEntity ? (MineGuardianEntity) entity : null;
        float partialTicks = guardian == null ? 0.0F : MathHelper.clamp(ageInTicks - guardian.ticksExisted, 0.0F, 1.0F);
        float explode = guardian == null ? 0.0F : guardian.getExplodeProgress(partialTicks);
        float scan = guardian == null ? 0.0F : guardian.getScanProgress(partialTicks);
        head.rotateAngleX = headPitch * 0.017453292F * 0.2F;
        head.rotateAngleY = netHeadYaw * 0.017453292F * 0.2F;
        head.rotateAngleZ = MathHelper.sin(ageInTicks * 3.0F) * explode * 0.3F;
        for (int i = 0; i < spikes.length; i++) {
            spikes[i].rotationPointY = spikeBaseY[i] + walkValue(ageInTicks, 0.1F + explode, i) + 2.0F;
        }
        eye.rotationPointX = scan * MathHelper.sin(ageInTicks * 0.1F) * 3.0F;
        Entity camera = Minecraft.getMinecraft().getRenderViewEntity();
        if (guardian != null && camera != null) {
            Vec3d view = camera.getPositionEyes(0.0F);
            Vec3d eyes = guardian.getPositionEyes(0.0F);
            Vec3d forward = guardian.getLookVec();
            Vec3d side = new Vec3d(eyes.x - view.x, 0.0D, eyes.z - view.z).normalize().rotateYaw((float) Math.PI * 0.5F);
            double dot = new Vec3d(forward.x, 0.0D, forward.z).dotProduct(side);
            eye.rotationPointX += MathHelper.sqrt((float) Math.abs(dot)) * Math.signum((float) dot) * 2.0F * (1.0F - scan);
        }
    }

    private float walkValue(float ticks, float speed, int offset) {
        return MathHelper.sin(ticks * speed + offset) * 1.0F;
    }
}
