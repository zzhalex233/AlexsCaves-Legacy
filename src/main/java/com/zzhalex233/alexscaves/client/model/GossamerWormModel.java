package com.zzhalex233.alexscaves.client.model;

import java.util.ArrayList;
import java.util.List;

import com.zzhalex233.alexscaves.server.entity.living.GossamerWormEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class GossamerWormModel extends ModelBase {
    private final ModelRenderer head;
    private final ModelRenderer antennae;
    private final ModelRenderer antennae2;
    private final ModelRenderer[] segments = new ModelRenderer[9];
    private final List<ModelRenderer> flippers = new ArrayList<>();

    public GossamerWormModel() {
        textureWidth = 128;
        textureHeight = 128;

        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, 23.0F, -5.75F);
        box(head, 0, 0, -4.0F, -1.0F, -5.25F, 8, 2, 5, false);
        box(head, 0, 44, -8.0F, 0.0F, -13.25F, 16, 0, 8, false);

        antennae = child(head, 4.0F, 0.0F, -4.25F);
        box(antennae, 0, 0, -2.0F, 0.0F, -1.0F, 21, 0, 30, false);
        antennae2 = child(head, -4.0F, 0.0F, -4.25F);
        box(antennae2, 0, 0, -19.0F, 0.0F, -1.0F, 21, 0, 30, true);

        segments[0] = child(head, 0.0F, 0.0F, -0.75F);
        box(segments[0], 58, 70, -2.0F, -1.0F, 0.5F, 4, 2, 14, false);
        addWideFlippers(segments[0], 2.0F, 4.5F, 74, 68, -2.0F, 4.5F, 68, 42);
        addWideFlippers(segments[0], 2.0F, 11.5F, 68, 48, -2.0F, 11.5F, 66, 24);

        segments[1] = segmentChild(segments[0], 4, 2, 58, 70);
        addWideFlippers(segments[1], 2.0F, 4.5F, 66, 18, -2.0F, 4.5F, 66, 6);
        addWideFlippers(segments[1], 2.0F, 11.5F, 66, 18, -2.0F, 11.5F, 66, 6);

        segments[2] = segmentChild(segments[1], 4, 2, 58, 70);
        addWideFlippers(segments[2], 2.0F, 4.5F, 66, 18, -2.0F, 4.5F, 66, 6);
        addWideFlippers(segments[2], 2.0F, 11.5F, 66, 18, -2.0F, 11.5F, 66, 6);

        segments[3] = segmentChild(segments[2], 4, 2, 58, 70);
        addWideFlippers(segments[3], 2.0F, 4.5F, 68, 48, -2.0F, 4.5F, 66, 24);
        addWideFlippers(segments[3], 2.0F, 11.5F, 68, 48, -2.0F, 11.5F, 66, 24);

        segments[4] = segmentChild(segments[3], 2, 1, 88, 86);
        addThinFlippers(segments[4], 1.0F, 4.5F, 74, 68, -1.0F, 4.5F, 69, 42);
        addThinFlippers(segments[4], 1.0F, 11.5F, 74, 68, -1.0F, 11.5F, 69, 42);

        segments[5] = segmentChild(segments[4], 2, 1, 88, 86);
        addThinFlippers(segments[5], 1.0F, 3.0F, 32, 86, -1.0F, 3.5F, 84, 54);
        addThinFlippers(segments[5], 1.0F, 10.0F, 88, 80, -1.0F, 10.0F, 64, 86);

        segments[6] = segmentChild(segments[5], 2, 1, 88, 86);
        addThinFlippers(segments[6], 1.0F, 3.5F, 82, 30, -1.0F, 3.5F, 74, 74);
        addThinFlippers(segments[6], 1.0F, 10.0F, 0, 84, -1.0F, 10.0F, 82, 36);

        segments[7] = child(segments[6], 0.0F, 0.0F, 14.0F);
        box(segments[7], 20, 30, -5.0F, 0.0F, 0.5F, 10, 0, 14, false);
        segments[8] = child(segments[7], 0.0F, 0.0F, 14.0F);
        box(segments[8], 0, 30, -5.0F, 0.0F, 0.5F, 10, 0, 14, false);
    }

    private ModelRenderer segmentChild(ModelRenderer parent, int width, int height, int textureX, int textureY) {
        ModelRenderer segment = child(parent, 0.0F, 0.0F, 14.0F);
        box(segment, textureX, textureY, -width * 0.5F, -height * 0.5F, 0.5F, width, height, 14, false);
        return segment;
    }

    private void addWideFlippers(ModelRenderer parent, float rightX, float rightZ, int rightU, int rightV, float leftX, float leftZ, int leftU, int leftV) {
        addFlipper(parent, rightX, rightZ, rightU, rightV, -0.0F, 17);
        addFlipper(parent, leftX, leftZ, leftU, leftV, -17.0F, 17);
    }

    private void addThinFlippers(ModelRenderer parent, float rightX, float rightZ, int rightU, int rightV, float leftX, float leftZ, int leftU, int leftV) {
        addFlipper(parent, rightX, rightZ, rightU, rightV, -0.25F, 16);
        addFlipper(parent, leftX, leftZ, leftU, leftV, -15.75F, 16);
    }

    private void addFlipper(ModelRenderer parent, float x, float z, int textureX, int textureY, float boxX, int width) {
        ModelRenderer flipper = child(parent, x, 0.0F, z);
        box(flipper, textureX, textureY, boxX, 0.0F, -3.0F, width, 0, 6, boxX < -1.0F);
        flippers.add(flipper);
    }

    private ModelRenderer child(ModelRenderer parent, float x, float y, float z) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.setRotationPoint(x, y, z);
        parent.addChild(renderer);
        return renderer;
    }

    private void box(ModelRenderer renderer, int textureX, int textureY, float x, float y, float z, int width, int height, int depth, boolean mirror) {
        renderer.mirror = mirror;
        renderer.setTextureOffset(textureX, textureY).addBox(x, y, z, width, height, depth, 0.0F);
        renderer.mirror = false;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        head.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetRotations();
        float partialTicks = ageInTicks - entity.ticksExisted;
        float squish = entity instanceof GossamerWormEntity ? ((GossamerWormEntity) entity).getSquishProgress(partialTicks) : 0.0F;
        float fishPitch = entity instanceof GossamerWormEntity ? ((GossamerWormEntity) entity).getFishPitch(partialTicks) : 0.0F;
        float swimAmount = 1.0F - squish;

        head.rotationPointY = 23.0F + squish * 2.0F + MathHelper.sin(ageInTicks * 0.1F) * 0.15F;
        head.rotateAngleX = fishPitch * 0.017453292F + MathHelper.sin(ageInTicks * 0.1F) * 0.15F;
        antennae.rotateAngleY = MathHelper.sin(ageInTicks * 0.1F + 1.0F) * 0.15F - 0.1F;
        antennae2.rotateAngleY = -MathHelper.sin(ageInTicks * 0.1F + 1.0F) * 0.15F + 0.1F;

        for (int i = 0; i < segments.length; i++) {
            ModelRenderer segment = segments[i];
            segment.rotateAngleX += MathHelper.sin(ageInTicks * 0.1F + i) * 0.05F;
            segment.rotateAngleY += MathHelper.sin(ageInTicks * 0.22F + i) * 0.22F * swimAmount;
        }
        for (int i = 0; i < flippers.size(); i++) {
            ModelRenderer flipper = flippers.get(i);
            float wave = MathHelper.sin(ageInTicks * 0.5F + i * 0.5F) * swimAmount;
            flipper.rotateAngleY += wave * 0.5F;
            flipper.rotateAngleZ += wave * 0.25F;
        }

        if (entity instanceof GossamerWormEntity) {
            GossamerWormEntity worm = (GossamerWormEntity) entity;
            float bodyYaw = worm.renderYawOffset;
            for (int i = 1; i < 7; i++) {
                float tailYaw = MathHelper.wrapDegrees(worm.getTrailTransformation(i * 5, 0, partialTicks) - bodyYaw);
                float tailPitch = worm.getTrailTransformation(i * 5, 1, partialTicks) - fishPitch;
                segments[i].rotateAngleY += tailYaw * 0.017453292F * 0.25F;
                segments[i].rotateAngleX += tailPitch * 0.017453292F * 0.25F;
            }
        }
    }

    private void resetRotations() {
        head.rotateAngleX = 0.0F;
        head.rotateAngleY = 0.0F;
        head.rotateAngleZ = 0.0F;
        antennae.rotateAngleX = 0.0F;
        antennae.rotateAngleY = 0.0F;
        antennae.rotateAngleZ = 0.0F;
        antennae2.rotateAngleX = 0.0F;
        antennae2.rotateAngleY = 0.0F;
        antennae2.rotateAngleZ = 0.0F;
        for (ModelRenderer segment : segments) {
            segment.rotateAngleX = 0.0F;
            segment.rotateAngleY = 0.0F;
            segment.rotateAngleZ = 0.0F;
        }
        for (ModelRenderer flipper : flippers) {
            flipper.rotateAngleX = 0.0F;
            flipper.rotateAngleY = 0.0F;
            flipper.rotateAngleZ = 0.0F;
        }
    }
}
