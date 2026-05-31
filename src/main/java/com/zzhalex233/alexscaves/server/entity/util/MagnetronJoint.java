package com.zzhalex233.alexscaves.server.entity.util;

import com.zzhalex233.alexscaves.server.entity.living.MagnetronEntity;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public enum MagnetronJoint {
    SHOULDER(new Vec3d(1.0D, 0.5D, 0.0D)),
    ELBOW(new Vec3d(1.5D, -0.5D, 0.2D)),
    HAND(new Vec3d(1.5D, -1.5D, 0.4D)),
    KNEE(new Vec3d(0.5D, -1.5D, 0.0D)),
    FOOT(new Vec3d(0.5D, -3.0D, 0.0D));

    private final Vec3d basePosition;

    MagnetronJoint(Vec3d basePosition) {
        this.basePosition = basePosition;
    }

    public Vec3d getTargetPosition(MagnetronEntity entity, boolean left) {
        Vec3d base = left ? new Vec3d(-basePosition.x, basePosition.y, basePosition.z) : basePosition;
        float poseProgress = entity.getAttackPoseProgress(1.0F);
        base = base.add(animateForPose(left, entity.getAttackPose(), poseProgress));
        float yaw = -entity.renderYawOffset * 0.017453292F;
        double sin = MathHelper.sin(yaw);
        double cos = MathHelper.cos(yaw);
        return new Vec3d(base.x * cos + base.z * sin, base.y, base.z * cos - base.x * sin);
    }

    private Vec3d animateForPose(boolean left, MagnetronEntity.AttackPose pose, float progress) {
        Vec3d add = Vec3d.ZERO;
        if (pose == MagnetronEntity.AttackPose.RIGHT_PUNCH) {
            if (this == SHOULDER) add = left ? new Vec3d(-0.15D, 0.0D, -0.5D) : new Vec3d(0.0D, -0.5D, 0.5D);
            if (this == ELBOW) add = left ? new Vec3d(-0.5D, 0.0D, -0.7D) : new Vec3d(-1.0D, -0.1D, 1.5D);
            if (this == HAND) add = left ? new Vec3d(-1.0D, 0.0D, -0.9D) : new Vec3d(-2.0D, 0.5D, 3.0D);
        } else if (pose == MagnetronEntity.AttackPose.LEFT_PUNCH) {
            if (this == SHOULDER) add = left ? new Vec3d(0.0D, -0.5D, 0.5D) : new Vec3d(0.15D, 0.0D, -0.5D);
            if (this == ELBOW) add = left ? new Vec3d(1.0D, -0.1D, 1.5D) : new Vec3d(0.5D, 0.0D, -0.7D);
            if (this == HAND) add = left ? new Vec3d(2.0D, 0.5D, 3.0D) : new Vec3d(1.0D, 0.0D, -0.9D);
        } else if (pose == MagnetronEntity.AttackPose.SLAM) {
            float f = left ? 1.0F : -1.0F;
            if (this == SHOULDER) add = new Vec3d(0.3D * f, -0.5D, -1.0D);
            if (this == ELBOW) add = new Vec3d(0.5D * f, -1.0D, -1.0D);
            if (this == HAND) add = new Vec3d(1.0D * f, -2.0D, -1.5D);
        }
        return add.scale(progress);
    }
}
