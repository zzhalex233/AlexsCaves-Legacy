package com.zzhalex233.alexscaves.server.entity.ai;

import com.zzhalex233.alexscaves.server.entity.living.CaniacEntity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.Vec3d;

public class CaniacMeleeAI extends EntityAIBase {
    private final CaniacEntity caniac;
    private int chaseTime;

    public CaniacMeleeAI(CaniacEntity caniac) {
        this.caniac = caniac;
        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase target = caniac.getAttackTarget();
        return target != null && target.isEntityAlive();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return shouldExecute();
    }

    @Override
    public void resetTask() {
        chaseTime = 0;
        caniac.setRunning(false);
    }

    @Override
    public void updateTask() {
        EntityLivingBase target = caniac.getAttackTarget();
        if (target == null || !target.isEntityAlive()) {
            return;
        }

        caniac.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
        double distance = caniac.getDistance(target);
        double attackDistance = caniac.width + target.width;
        if (caniac.isLunging()) {
            caniac.getNavigator().clearPath();
            updateLunge(target, distance);
            return;
        }

        chaseTime++;
        caniac.setRunning(distance > attackDistance);
        if (distance > attackDistance) {
            caniac.getNavigator().tryMoveToEntityLiving(target, 1.0D);
            if (distance < 12.0D && distance > 4.0D && caniac.getRNG().nextInt(15) == 0) {
                caniac.startLunge();
            }
        } else {
            caniac.getNavigator().clearPath();
            caniac.setRunning(false);
            caniac.attackEntityAsMob(target);
        }
    }

    private void updateLunge(EntityLivingBase target, double distance) {
        int tick = CaniacEntity.LUNGE_DURATION - caniac.getLungeTicks();
        if (tick == 10) {
            Vec3d delta = target.getPositionVector().subtract(caniac.getPositionVector()).normalize();
            caniac.addVelocity(delta.x * 1.3D, 0.35D, delta.z * 1.3D);
            caniac.velocityChanged = true;
        } else if (tick > 15 && distance < 10.0D) {
            Vec3d pull = caniac.getPositionVector().subtract(target.getPositionVector());
            target.motionX = target.motionX * 0.3D + pull.x * 0.1D;
            target.motionZ = target.motionZ * 0.3D + pull.z * 0.1D;
            target.velocityChanged = true;
            if (tick > 19 && tick <= 22 && distance < 3.5D && caniac.canEntityBeSeen(target)) {
                target.attackEntityFrom(net.minecraft.util.DamageSource.causeMobDamage(caniac), 3.0F);
            }
        }
    }
}
