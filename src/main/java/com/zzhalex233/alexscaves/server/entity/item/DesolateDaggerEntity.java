package com.zzhalex233.alexscaves.server.entity.item;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DesolateDaggerEntity extends Entity {
    private static final DataParameter<Integer> TARGET_ID = EntityDataManager.createKey(DesolateDaggerEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> OWNER_ID = EntityDataManager.createKey(DesolateDaggerEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Float> STAB = EntityDataManager.createKey(DesolateDaggerEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<ItemStack> STACK = EntityDataManager.createKey(DesolateDaggerEntity.class, DataSerializers.ITEM_STACK);
    private float prevStab;
    private float orbitOffset;
    public int orbitFor = 20;
    private boolean playedSummon;

    public DesolateDaggerEntity(World world) {
        super(world);
        setSize(0.35F, 0.35F);
        noClip = true;
        orbitFor = 20 + rand.nextInt(10);
    }

    @Override
    protected void entityInit() {
        dataManager.register(TARGET_ID, -1);
        dataManager.register(OWNER_ID, -1);
        dataManager.register(STAB, 0.0F);
        dataManager.register(STACK, new ItemStack(Items.IRON_SWORD));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        prevStab = getStab();
        Entity target = getTarget();
        if (world.isRemote) {
            world.spawnParticle(EnumParticleTypes.REDSTONE, posX + (rand.nextDouble() - 0.5D) * 0.75D, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5D) * 0.75D, 0.8D, 0.0D, 0.0D);
        }
        if (!playedSummon) {
            playedSummon = true;
            playSound(ACSoundRegistry.DESOLATE_DAGGER_SUMMON, 0.75F, 1.0F);
        }
        if (target == null || !target.isEntityAlive()) {
            if (ticksExisted > 3) {
                setDead();
            }
            return;
        }
        float invStab = 1.0F - getStab();
        if (orbitOffset == 0.0F) {
            orbitOffset = rand.nextInt(360);
        }
        Vec3d targetPos = target.getPositionVector();
        Vec3d center = new Vec3d(targetPos.x, targetPos.y + target.height * 0.35D, targetPos.z);
        Vec3d orbit = new Vec3d(0.0D, (target.height + 0.4D) * invStab, (target.width + 0.5D) * invStab).rotateYaw((float) Math.toRadians(orbitOffset + ticksExisted * 9.0F));
        Vec3d desired = center.add(orbit);
        motionX = desired.x - posX;
        motionY = desired.y - posY;
        motionZ = desired.z - posZ;
        if (!world.isRemote) {
            if (orbitFor > 0) {
                orbitFor--;
            } else {
                setStab(Math.min(1.0F, getStab() + 0.2F));
            }
            if (getStab() >= 1.0F) {
                Entity owner = getOwner();
                DamageSource source = owner instanceof EntityLivingBase ? DamageSource.causeMobDamage((EntityLivingBase) owner).setMagicDamage() : DamageSource.MAGIC;
                if (target.attackEntityFrom(source, 2.0F)) {
                    world.playSound(null, posX, posY, posZ, ACSoundRegistry.DESOLATE_DAGGER_HIT, SoundCategory.PLAYERS, 0.8F, 1.0F);
                }
                setDead();
            }
        }
        faceTarget(target);
        move(MoverType.SELF, motionX, motionY, motionZ);
        motionX *= 0.9D;
        motionY *= 0.9D;
        motionZ *= 0.9D;
    }

    private void faceTarget(Entity target) {
        double dx = target.posX - posX;
        double dy = target.getEntityBoundingBox().minY + target.height * 0.5D - posY;
        double dz = target.posZ - posZ;
        double horizontal = MathHelper.sqrt(dx * dx + dz * dz);
        rotationYaw = (float) (MathHelper.atan2(dx, dz) * -57.2957763671875D);
        rotationPitch = (float) (MathHelper.atan2(dy, horizontal) * -57.2957763671875D);
    }

    public void setTargetId(int id) {
        dataManager.set(TARGET_ID, id);
    }

    public void setOwnerId(int id) {
        dataManager.set(OWNER_ID, id);
    }

    public void setItemStack(ItemStack stack) {
        dataManager.set(STACK, stack.copy());
    }

    public ItemStack getItemStack() {
        return dataManager.get(STACK);
    }

    public float getStab(float partialTicks) {
        return prevStab + (getStab() - prevStab) * partialTicks;
    }

    public float getStab() {
        return dataManager.get(STAB);
    }

    private void setStab(float stab) {
        dataManager.set(STAB, stab);
    }

    private Entity getTarget() {
        int id = dataManager.get(TARGET_ID);
        return id < 0 ? null : world.getEntityByID(id);
    }

    private Entity getOwner() {
        int id = dataManager.get(OWNER_ID);
        return id < 0 ? null : world.getEntityByID(id);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        orbitFor = compound.getInteger("OrbitFor");
        setTargetId(compound.getInteger("TargetId"));
        setOwnerId(compound.getInteger("OwnerId"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("OrbitFor", orbitFor);
        compound.setInteger("TargetId", dataManager.get(TARGET_ID));
        compound.setInteger("OwnerId", dataManager.get(OWNER_ID));
    }
}
