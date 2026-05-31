package com.zzhalex233.alexscaves.server.entity.item;

import java.util.UUID;

import com.zzhalex233.alexscaves.server.entity.living.MineGuardianEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MineGuardianAnchorEntity extends Entity {
    private static final DataParameter<Integer> GUARDIAN_ID = EntityDataManager.createKey(MineGuardianAnchorEntity.class, DataSerializers.VARINT);
    private UUID guardianUUID;

    public MineGuardianAnchorEntity(World world) {
        super(world);
        setSize(0.75F, 1.35F);
    }

    public MineGuardianAnchorEntity(MineGuardianEntity guardian) {
        this(guardian.world);
        setGuardian(guardian);
        rotationYaw = rand.nextFloat() * 360.0F;
        setPosition(guardian.posX, guardian.posY + 0.5D, guardian.posZ);
    }

    @Override
    protected void entityInit() {
        dataManager.register(GUARDIAN_ID, -1);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        Entity guardian = getGuardian();
        if (!onGround) {
            motionY -= 0.08D;
        }
        move(MoverType.SELF, motionX, motionY, motionZ);
        motionX *= 0.9D;
        motionY *= 0.9D;
        motionZ *= 0.9D;
        if (!world.isRemote) {
            if (guardian == null && ticksExisted > 20) {
                setDead();
                return;
            }
            if (guardian instanceof MineGuardianEntity) {
                MineGuardianEntity mineGuardian = (MineGuardianEntity) guardian;
                dataManager.set(GUARDIAN_ID, mineGuardian.getEntityId());
                mineGuardian.setAnchor(this);
                boolean hasTarget = mineGuardian.getAttackTarget() != null && mineGuardian.getAttackTarget().isEntityAlive();
                double distance = getDistance(mineGuardian);
                int chain = mineGuardian.getMaxChainLength();
                double goal = (mineGuardian.isInWater() ? chain + Math.sin(ticksExisted * 0.1F + chain * 0.5F) * 0.25F : 5.0D) + (hasTarget ? 5.0D : 0.0D);
                if (mineGuardian.isInWater() && !hasTarget) {
                    Vec3d bob = new Vec3d(posX - Math.sin(ticksExisted * 0.025F + chain) * 0.5D, posY + goal, posZ + Math.cos(ticksExisted * 0.025F + chain) * 0.5D).subtract(mineGuardian.getPositionVector());
                    mineGuardian.addVelocity(bob.x * 0.005D, bob.y * 0.005D, bob.z * 0.005D);
                }
                if (distance > goal) {
                    double strength = Math.min(distance - goal, 1.0D) * 0.1D;
                    Vec3d pull = getChainFrom(1.0F).subtract(mineGuardian.getPositionVector());
                    if (pull.length() > 1.0D) {
                        pull = pull.normalize();
                    }
                    mineGuardian.motionX = mineGuardian.motionX * (hasTarget ? 1.0D : 0.8D) + pull.x * strength;
                    mineGuardian.motionY = mineGuardian.motionY * (hasTarget ? 1.0D : 0.8D) + pull.y * strength;
                    mineGuardian.motionZ = mineGuardian.motionZ * (hasTarget ? 1.0D : 0.8D) + pull.z * strength;
                    mineGuardian.velocityChanged = true;
                }
            }
        }
    }

    public void setGuardian(Entity entity) {
        guardianUUID = entity == null ? null : entity.getUniqueID();
        dataManager.set(GUARDIAN_ID, entity == null ? -1 : entity.getEntityId());
    }

    public Entity getGuardian() {
        if (world.isRemote) {
            int id = dataManager.get(GUARDIAN_ID);
            return id == -1 ? null : world.getEntityByID(id);
        }
        if (guardianUUID == null) {
            return null;
        }
        for (Entity entity : world.loadedEntityList) {
            if (guardianUUID.equals(entity.getUniqueID())) {
                return entity;
            }
        }
        return null;
    }

    public void linkWithGuardian(Entity guardian) {
        setGuardian(guardian);
    }

    public Vec3d getChainTo(float partialTicks) {
        Entity guardian = getGuardian();
        if (guardian instanceof MineGuardianEntity) {
            return guardian.getPositionVector().add(new Vec3d(0.0D, guardian.height * 0.5D, 0.0D));
        }
        return getPositionVector().add(new Vec3d(0.0D, 1.0D, 0.0D));
    }

    public Vec3d getChainFrom(float partialTicks) {
        return new Vec3d(prevPosX + (posX - prevPosX) * partialTicks, prevPosY + (posY - prevPosY) * partialTicks + 1.0D, prevPosZ + (posZ - prevPosZ) * partialTicks);
    }

    @Override
    public boolean canBeCollidedWith() {
        return !isDead;
    }

    @Override
    public boolean attackEntityFrom(net.minecraft.util.DamageSource source, float amount) {
        Entity guardian = getGuardian();
        return guardian != null && guardian.attackEntityFrom(source, amount);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        if (compound.hasUniqueId("GuardianUUID")) {
            guardianUUID = compound.getUniqueId("GuardianUUID");
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        if (guardianUUID != null) {
            compound.setUniqueId("GuardianUUID", guardianUUID);
        }
    }
}
