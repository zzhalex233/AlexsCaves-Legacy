package com.zzhalex233.alexscaves.server.entity.item;

import java.util.UUID;

import com.google.common.collect.Multimap;
import com.zzhalex233.alexscaves.server.entity.living.TeletorEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MagneticWeaponEntity extends Entity {
    private static final DataParameter<ItemStack> ITEMSTACK = EntityDataManager.createKey(MagneticWeaponEntity.class, DataSerializers.ITEM_STACK);
    private static final DataParameter<Integer> CONTROLLER_ID = EntityDataManager.createKey(MagneticWeaponEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TARGET_ID = EntityDataManager.createKey(MagneticWeaponEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> IDLING = EntityDataManager.createKey(MagneticWeaponEntity.class, DataSerializers.BOOLEAN);
    private float prevStrikeProgress;
    private float strikeProgress;
    private UUID controllerUUID;
    private boolean comingBack;

    public MagneticWeaponEntity(World world) {
        super(world);
        setSize(0.5F, 0.5F);
        noClip = true;
    }

    @Override
    protected void entityInit() {
        dataManager.register(ITEMSTACK, new ItemStack(Items.IRON_SWORD));
        dataManager.register(CONTROLLER_ID, -1);
        dataManager.register(TARGET_ID, -1);
        dataManager.register(IDLING, true);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        prevStrikeProgress = strikeProgress;
        Entity controller = getController();
        Entity target = getTarget();
        if (!world.isRemote) {
            if (controller == null && ticksExisted > 20 || getItemStack().isEmpty()) {
                setDead();
                return;
            }
            if (controller instanceof TeletorEntity) {
                TeletorEntity teletor = (TeletorEntity) controller;
                dataManager.set(CONTROLLER_ID, teletor.getEntityId());
                teletor.setWeapon(this);
                EntityLivingBase attackTarget = teletor.getAttackTarget();
                dataManager.set(TARGET_ID, attackTarget != null && attackTarget.isEntityAlive() ? attackTarget.getEntityId() : -1);
                target = getTarget();
            }
        }
        if (controller instanceof TeletorEntity) {
            tickTeletorWeapon((TeletorEntity) controller, target);
        } else if (!world.isRemote && ticksExisted > 20) {
            setDead();
        }
        move(MoverType.SELF, motionX, motionY, motionZ);
        motionX *= 0.9D;
        motionY *= 0.9D;
        motionZ *= 0.9D;
    }

    private void tickTeletorWeapon(TeletorEntity teletor, Entity target) {
        boolean attacking = !comingBack && target instanceof EntityLivingBase && target.isEntityAlive();
        Vec3d moveTo = attacking ? target.getPositionVector().add(0.0D, target.height * 0.55D, 0.0D) : teletor.getWeaponPosition();
        Vec3d want = moveTo.subtract(getPositionVector());
        dataManager.set(IDLING, !attacking);
        if (attacking) {
            if (want.length() < target.width + 1.0F) {
                if (strikeProgress < 1.0F) {
                    strikeProgress = Math.min(1.0F, strikeProgress + 0.35F);
                } else if (!world.isRemote) {
                    hurtEntity(teletor, target);
                    comingBack = true;
                    strikeProgress = 0.0F;
                }
            } else if (want.length() > 32.0D) {
                comingBack = true;
            }
        } else if (strikeProgress > 0.0F) {
            strikeProgress = Math.max(0.0F, strikeProgress - 0.1F);
        }
        directMovementTowards(moveTo, 0.1F);
        if (getDistance(teletor) < 2.5F && posY > teletor.posY) {
            comingBack = false;
        }
    }

    private void directMovementTowards(Vec3d moveTo, float speed) {
        Vec3d want = moveTo.subtract(getPositionVector());
        if (want.length() > 1.0D) {
            want = want.normalize();
        }
        float targetPitch = (float) (-(MathHelper.atan2(want.y, Math.sqrt(want.x * want.x + want.z * want.z)) * 57.2957763671875D));
        float targetYaw = (float) (-MathHelper.atan2(want.x, want.z) * 57.2957763671875D);
        if (isIdling()) {
            targetPitch = rotationPitch;
            targetYaw = rotationYaw + 5.0F;
        }
        rotationPitch = approachDegrees(rotationPitch, targetPitch, 5.0F);
        rotationYaw = approachDegrees(rotationYaw, targetYaw, 5.0F);
        motionX += want.x * speed;
        motionY += want.y * speed;
        motionZ += want.z * speed;
    }

    private float approachDegrees(float current, float target, float max) {
        float delta = MathHelper.wrapDegrees(target - current);
        return current + MathHelper.clamp(delta, -max, max);
    }

    private void hurtEntity(EntityLivingBase holder, Entity target) {
        ItemStack stack = getItemStack();
        float damage = (float) holder.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() + (float) getDamageForItem(stack);
        if (target.attackEntityFrom(DamageSource.causeMobDamage(holder), Math.max(2.0F, damage))) {
            if (stack.isItemStackDamageable()) {
                stack.damageItem(1, holder);
                setItemStack(stack);
            }
        }
    }

    private double getDamageForItem(ItemStack stack) {
        Multimap<String, AttributeModifier> modifiers = stack.getAttributeModifiers(net.minecraft.inventory.EntityEquipmentSlot.MAINHAND);
        double damage = 0.0D;
        for (AttributeModifier modifier : modifiers.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) {
            damage += modifier.getAmount();
        }
        return damage;
    }

    public void setItemStack(ItemStack stack) {
        dataManager.set(ITEMSTACK, stack.copy());
    }

    public ItemStack getItemStack() {
        return dataManager.get(ITEMSTACK);
    }

    public void setController(Entity entity) {
        controllerUUID = entity == null ? null : entity.getUniqueID();
        dataManager.set(CONTROLLER_ID, entity == null ? -1 : entity.getEntityId());
    }

    public Entity getController() {
        if (world.isRemote) {
            int id = dataManager.get(CONTROLLER_ID);
            return id == -1 ? null : world.getEntityByID(id);
        }
        if (controllerUUID == null) {
            return null;
        }
        for (Entity entity : world.loadedEntityList) {
            if (controllerUUID.equals(entity.getUniqueID())) {
                return entity;
            }
        }
        return null;
    }

    public Entity getTarget() {
        int id = dataManager.get(TARGET_ID);
        return id == -1 ? null : world.getEntityByID(id);
    }

    public boolean isIdling() {
        return dataManager.get(IDLING);
    }

    public float getStrikeProgress(float partialTick) {
        return prevStrikeProgress + (strikeProgress - prevStrikeProgress) * partialTick;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("WeaponStack")) {
            setItemStack(new ItemStack(compound.getCompoundTag("WeaponStack")));
        }
        if (compound.hasUniqueId("ControllerUUID")) {
            controllerUUID = compound.getUniqueId("ControllerUUID");
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        if (!getItemStack().isEmpty()) {
            NBTTagCompound stackTag = new NBTTagCompound();
            getItemStack().writeToNBT(stackTag);
            compound.setTag("WeaponStack", stackTag);
        }
        if (controllerUUID != null) {
            compound.setUniqueId("ControllerUUID", controllerUUID);
        }
    }
}
