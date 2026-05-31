package com.zzhalex233.alexscaves.server.entity.living;

import java.util.UUID;

import com.zzhalex233.alexscaves.server.entity.item.MagneticWeaponEntity;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;
import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class TeletorEntity extends EntityMob {
    private static final DataParameter<Integer> WEAPON_ID = EntityDataManager.createKey(TeletorEntity.class, DataSerializers.VARINT);
    private UUID weaponUUID;
    private float prevControlProgress;
    private float controlProgress;
    private int floatingTicks;

    public TeletorEntity(World world) {
        super(world);
        moveHelper = new MoveController();
        setSize(0.99F, 1.99F);
        setNoGravity(true);
        experienceValue = 6;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(WEAPON_ID, -1);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(1, new MeleeAI());
        tasks.addTask(2, new FloatAroundAI());
        tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(4, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(18.0D);
    }

    @Override
    public void onLivingUpdate() {
        prevControlProgress = controlProgress;
        super.onLivingUpdate();
        setNoGravity(true);
        Entity weapon = getWeapon();
        boolean controlling = weapon instanceof MagneticWeaponEntity && ((MagneticWeaponEntity) weapon).getTarget() instanceof EntityLivingBase;
        if (controlling && controlProgress < 5.0F) {
            controlProgress++;
        } else if (!controlling && controlProgress > 0.0F) {
            controlProgress--;
        }
        if (!world.isRemote) {
            if (weapon instanceof MagneticWeaponEntity) {
                dataManager.set(WEAPON_ID, weapon.getEntityId());
                ((MagneticWeaponEntity) weapon).setController(this);
            } else if (ticksExisted > 20) {
                spawnWeapon(new ItemStack(Items.IRON_SWORD));
            }
        }
        if (floatingTicks-- <= 0) {
            floatingTicks = 30;
            playSound(ACSoundRegistry.TELETOR_FLOAT, 0.6F, 1.0F);
        }
        motionX *= 0.98D;
        motionY *= 0.98D;
        motionZ *= 0.98D;
    }

    public float getControlProgress(float partialTick) {
        return (prevControlProgress + (controlProgress - prevControlProgress) * partialTick) * 0.2F;
    }

    public boolean areLegsCrossed(float limbSwingAmount) {
        return isEntityAlive() && limbSwingAmount <= 0.35F;
    }

    public void setWeapon(Entity entity) {
        weaponUUID = entity == null ? null : entity.getUniqueID();
        dataManager.set(WEAPON_ID, entity == null ? -1 : entity.getEntityId());
    }

    public Entity getWeapon() {
        if (world.isRemote) {
            int id = dataManager.get(WEAPON_ID);
            return id == -1 ? null : world.getEntityByID(id);
        }
        if (weaponUUID == null) {
            return null;
        }
        for (Entity entity : world.loadedEntityList) {
            if (weaponUUID.equals(entity.getUniqueID())) {
                return entity;
            }
        }
        return null;
    }

    public Vec3d getWeaponPosition() {
        return getPositionEyes(1.0F).add(0.0D, 1.4D - Math.sin(ticksExisted * 0.1F) * 0.2D, 0.0D);
    }

    public Vec3d getHelmetPosition(int side) {
        return getPositionEyes(1.0F).add(new Vec3d(side == 0 ? -0.65D : 0.65D, 1.1D, 0.0D).rotateYaw(-rotationYawHead * 0.017453292F));
    }

    private void spawnWeapon(ItemStack stack) {
        MagneticWeaponEntity weapon = new MagneticWeaponEntity(world);
        weapon.setItemStack(stack);
        Vec3d pos = getWeaponPosition();
        weapon.setPosition(pos.x, pos.y, pos.z);
        weapon.setController(this);
        setWeapon(weapon);
        world.spawnEntity(weapon);
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        if (!world.isRemote) {
            spawnWeapon(createWeaponStack());
        }
        return super.onInitialSpawn(difficulty, livingdata);
    }

    private ItemStack createWeaponStack() {
        int choice = rand.nextInt(4);
        return new ItemStack(choice == 0 ? Items.IRON_AXE : choice == 1 ? Items.STONE_SWORD : choice == 2 ? Items.GOLDEN_SWORD : Items.IRON_SWORD);
    }

    @Override
    public void setDead() {
        Entity weapon = getWeapon();
        if (!world.isRemote && weapon instanceof MagneticWeaponEntity && getHealth() <= 0.0F) {
            ItemStack stack = ((MagneticWeaponEntity) weapon).getItemStack();
            if (!stack.isEmpty() && rand.nextFloat() < 0.085F) {
                entityDropItem(stack.copy(), 0.0F);
            }
            weapon.setDead();
        }
        super.setDead();
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        int neodymium = 1 + rand.nextInt(2) + rand.nextInt(1 + lootingModifier);
        entityDropItem(new ItemStack(rand.nextBoolean() ? ACItemRegistry.RAW_AZURE_NEODYMIUM.item() : ACItemRegistry.RAW_SCARLET_NEODYMIUM.item(), neodymium), 0.0F);
        int cores = rand.nextInt(2) + (lootingModifier > 0 && rand.nextInt(3) < lootingModifier ? 1 : 0);
        if (cores > 0) {
            entityDropItem(new ItemStack(ACItemRegistry.TELECORE.item(), cores), 0.0F);
        }
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, net.minecraft.block.state.IBlockState state, BlockPos pos) {
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
    }

    @Override
    public boolean isPotionApplicable(PotionEffect effect) {
        return super.isPotionApplicable(effect) && effect.getPotion() != ACEffectRegistry.MAGNETIZING;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.TELETOR_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ACSoundRegistry.TELETOR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.TELETOR_DEATH;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (weaponUUID != null) {
            compound.setUniqueId("WeaponUUID", weaponUUID);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasUniqueId("WeaponUUID")) {
            weaponUUID = compound.getUniqueId("WeaponUUID");
        }
    }

    private class MeleeAI extends EntityAIBase {
        private int executionTime;
        private BlockPos strafeOrigin;

        private MeleeAI() {
            setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            EntityLivingBase target = getAttackTarget();
            return target != null && target.isEntityAlive() && getWeapon() != null;
        }

        @Override
        public void startExecuting() {
            executionTime = 0;
            strafeOrigin = null;
        }

        @Override
        public void updateTask() {
            executionTime++;
            EntityLivingBase target = getAttackTarget();
            if (target == null) {
                return;
            }
            double dist = getDistance(target);
            getLookHelper().setLookPositionWithEntity(target, 180.0F, 90.0F);
            if (dist < 2.0D) {
                strafeOrigin = getPosition().add(rand.nextInt(16) - 8, rand.nextInt(8), rand.nextInt(16) - 8);
            }
            if (dist < 16.0D) {
                getNavigator().clearPath();
                float targetYaw = (float) (-MathHelper.atan2(target.posX - posX, target.posZ - posZ) * 57.2957763671875D);
                float angle = executionTime * 0.1F;
                Vec3d strafe = new Vec3d(Math.sin(angle) * 5.0D, Math.cos(angle) * 2.0D, 0.0D).rotateYaw(-targetYaw * 0.017453292F);
                if (strafeOrigin == null) {
                    strafeOrigin = getPosition();
                }
                Vec3d moveTo = new Vec3d(strafeOrigin).add(0.5D, 0.5D, 0.5D).add(strafe);
                moveHelper.setMoveTo(moveTo.x, moveTo.y, moveTo.z, 1.0D);
                rotationYaw = targetYaw;
                renderYawOffset = rotationYaw;
                rotationYawHead = rotationYaw;
            } else {
                strafeOrigin = null;
                getNavigator().tryMoveToEntityLiving(target, 1.0D);
            }
        }
    }

    private class FloatAroundAI extends EntityAIBase {
        private double x;
        private double y;
        private double z;

        private FloatAroundAI() {
            setMutexBits(1);
        }

        @Override
        public boolean shouldExecute() {
            return getAttackTarget() == null && (rand.nextInt(45) == 0 || getNavigator().noPath());
        }

        @Override
        public void startExecuting() {
            BlockPos pos = getPosition().add(rand.nextInt(13) - 6, rand.nextInt(7) - 2, rand.nextInt(13) - 6);
            x = pos.getX() + 0.5D;
            y = pos.getY() + 0.5D;
            z = pos.getZ() + 0.5D;
            moveHelper.setMoveTo(x, y, z, 1.0D);
        }

        @Override
        public boolean shouldContinueExecuting() {
            return getAttackTarget() == null && getDistanceSq(x, y, z) > 2.0D;
        }

        @Override
        public void updateTask() {
            moveHelper.setMoveTo(x, y, z, 1.0D);
        }
    }

    private class MoveController extends EntityMoveHelper {
        private MoveController() {
            super(TeletorEntity.this);
        }

        @Override
        public void onUpdateMoveHelper() {
            if (action == EntityMoveHelper.Action.MOVE_TO) {
                double dx = posX - TeletorEntity.this.posX;
                double dy = posY - TeletorEntity.this.posY;
                double dz = posZ - TeletorEntity.this.posZ;
                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (dist < width * 0.3F) {
                    action = EntityMoveHelper.Action.WAIT;
                    return;
                }
                motionX += dx / dist * speed * 0.025D;
                motionY += dy / dist * speed * 0.025D;
                motionZ += dz / dist * speed * 0.025D;
                if (getAttackTarget() == null) {
                    rotationYaw = -((float) MathHelper.atan2(motionX, motionZ)) * 57.295776F;
                    renderYawOffset = rotationYaw;
                }
            }
        }
    }
}
