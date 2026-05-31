package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class RelicheirusEntity extends EntityAnimal {
    private static final DataParameter<Integer> ALT_SKIN = EntityDataManager.createKey(RelicheirusEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> ATTACK_TICKS = EntityDataManager.createKey(RelicheirusEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> PUSHING_TREES_FOR = EntityDataManager.createKey(RelicheirusEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HELD_MOB_ID = EntityDataManager.createKey(RelicheirusEntity.class, DataSerializers.VARINT);
    private float prevRaiseArmsProgress;
    private float raiseArmsProgress;
    private int idleScratchCooldown;

    public RelicheirusEntity(World world) {
        super(world);
        setSize(2.2F, 3.7F);
        experienceValue = 18;
        stepHeight = 1.1F;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(ALT_SKIN, 0);
        dataManager.register(ATTACK_TICKS, 0);
        dataManager.register(PUSHING_TREES_FOR, 0);
        dataManager.register(HELD_MOB_ID, -1);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new MeleeAI());
        tasks.addTask(1, new TreeNibbleAI());
        tasks.addTask(2, new EntityAIMate(this, 1.0D));
        tasks.addTask(3, new EntityAITempt(this, 1.1D, ACBlockRegistry.TREE_STAR.item(), false));
        tasks.addTask(4, new EntityAIFollowParent(this, 1.0D));
        tasks.addTask(5, new EntityAIWander(this, 1.0D, 45));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
        tasks.addTask(7, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, RelicheirusEntity.class));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, TrilocarisEntity.class, 80, true, false, null));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(120.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(12.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.8D);
    }

    @Override
    public void onLivingUpdate() {
        prevRaiseArmsProgress = raiseArmsProgress;
        super.onLivingUpdate();
        if (getAttackTicks() > 0) {
            setAttackTicks(getAttackTicks() - 1);
        }
        if (getPushingTreesFor() > 0 && !world.isRemote) {
            setPushingTreesFor(getPushingTreesFor() - 1);
        }
        boolean arms = getAttackTicks() > 0 || getPushingTreesFor() > 0 && ticksExisted % 60 < 30;
        raiseArmsProgress = arms ? Math.min(5.0F, raiseArmsProgress + 1.0F) : Math.max(0.0F, raiseArmsProgress - 1.0F);
        if (!world.isRemote && ticksExisted % 100 == 0 && getHealth() < getMaxHealth()) {
            heal(2.0F);
        }
        if (!world.isRemote && idleScratchCooldown-- <= 0 && getAttackTarget() == null && onGround && rand.nextInt(5) == 0) {
            idleScratchCooldown = 160 + rand.nextInt(120);
            setAttackTicks(20);
            playSound(ACSoundRegistry.RELICHEIRUS_SCRATCH, 0.8F, getSoundPitch());
        }
        Entity held = getHeldMob();
        if (held instanceof TrilocarisEntity && getAttackTicks() > 0) {
            float yaw = renderYawOffset * 0.017453292F;
            held.setPosition(posX - MathHelper.sin(yaw) * 1.2D, posY + getEyeHeight() * 0.8D, posZ + MathHelper.cos(yaw) * 1.2D);
            held.fallDistance = 0.0F;
        } else if (!world.isRemote && getHeldMobId() != -1) {
            setHeldMobId(-1);
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        setAttackTicks(20);
        playSound(ACSoundRegistry.RELICHEIRUS_SCRATCH, 1.0F, getSoundPitch());
        if (entity instanceof TrilocarisEntity) {
            setHeldMobId(entity.getEntityId());
        }
        boolean attacked = entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
        if (attacked && entity instanceof EntityLivingBase) {
            float yaw = rotationYaw * 0.017453292F;
            entity.addVelocity(-MathHelper.sin(yaw) * 0.8D, 0.25D, MathHelper.cos(yaw) * 0.8D);
        }
        return attacked;
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() == ACItemRegistry.PRIMORDIAL_SOUP.item()) {
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
                if (!player.inventory.addItemStackToInventory(new ItemStack(Items.BOWL))) {
                    player.dropItem(new ItemStack(Items.BOWL), false);
                }
            }
            setPushingTreesFor(1200);
            heal(8.0F);
            return true;
        }
        return super.processInteract(player, hand);
    }

    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        RelicheirusEntity child = new RelicheirusEntity(world);
        child.setGrowingAge(-24000);
        child.setAltSkin(rand.nextInt(3));
        return child;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == ACBlockRegistry.TREE_STAR.item();
    }

    @Override
    public IEntityLivingData onInitialSpawn(net.minecraft.world.DifficultyInstance difficulty, IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        setAltSkin(rand.nextInt(3));
        return livingdata;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
        if (!isChild()) {
            playSound(ACSoundRegistry.RELICHEIRUS_STEP, 0.8F, 1.0F);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.RELICHEIRUS_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ACSoundRegistry.RELICHEIRUS_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.RELICHEIRUS_DEATH;
    }

    public int getAltSkin() {
        return dataManager.get(ALT_SKIN);
    }

    public void setAltSkin(int skin) {
        dataManager.set(ALT_SKIN, MathHelper.clamp(skin, 0, 2));
    }

    public int getAttackTicks() {
        return dataManager.get(ATTACK_TICKS);
    }

    public void setAttackTicks(int ticks) {
        dataManager.set(ATTACK_TICKS, ticks);
    }

    public int getPushingTreesFor() {
        return dataManager.get(PUSHING_TREES_FOR);
    }

    public void setPushingTreesFor(int ticks) {
        dataManager.set(PUSHING_TREES_FOR, ticks);
    }

    public int getHeldMobId() {
        return dataManager.get(HELD_MOB_ID);
    }

    public void setHeldMobId(int id) {
        dataManager.set(HELD_MOB_ID, id);
    }

    public Entity getHeldMob() {
        int id = getHeldMobId();
        return id == -1 ? null : world.getEntityByID(id);
    }

    public float getRaiseArmsProgress(float partialTicks) {
        return (prevRaiseArmsProgress + (raiseArmsProgress - prevRaiseArmsProgress) * partialTicks) * 0.2F;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("AltSkin", getAltSkin());
        compound.setInteger("PushingTreesFor", getPushingTreesFor());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setAltSkin(compound.getInteger("AltSkin"));
        setPushingTreesFor(compound.getInteger("PushingTreesFor"));
    }

    private class MeleeAI extends EntityAIBase {
        private int cooldown;

        private MeleeAI() {
            setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            EntityLivingBase target = getAttackTarget();
            return target != null && target.isEntityAlive();
        }

        @Override
        public void updateTask() {
            EntityLivingBase target = getAttackTarget();
            if (target == null) {
                return;
            }
            faceEntity(target, 30.0F, 30.0F);
            if (cooldown > 0) {
                cooldown--;
            }
            if (getDistance(target) > width + target.width + 1.5F) {
                getNavigator().tryMoveToEntityLiving(target, 1.1D);
            } else if (cooldown <= 0) {
                cooldown = 30;
                attackEntityAsMob(target);
            }
        }
    }

    private class TreeNibbleAI extends EntityAIBase {
        private BlockPos target;
        private int chewTime;

        private TreeNibbleAI() {
            setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            if (getAttackTarget() != null || rand.nextInt(getPushingTreesFor() > 0 ? 20 : 120) != 0) {
                return false;
            }
            target = findTreeBlock();
            return target != null;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return target != null && chewTime < 45;
        }

        @Override
        public void startExecuting() {
            chewTime = 0;
            setAttackTicks(30);
        }

        @Override
        public void resetTask() {
            target = null;
            chewTime = 0;
        }

        @Override
        public void updateTask() {
            chewTime++;
            if (target == null) {
                return;
            }
            getLookHelper().setLookPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 20.0F, 20.0F);
            if (getDistanceSq(target) > 16.0D) {
                getNavigator().tryMoveToXYZ(target.getX(), target.getY(), target.getZ(), 1.0D);
            } else if (chewTime == 24 && !world.isRemote) {
                Block block = world.getBlockState(target).getBlock();
                if (isEdibleTreeBlock(block, world.getBlockState(target).getMaterial())) {
                    world.destroyBlock(target, false);
                    heal(4.0F);
                    playSound(ACSoundRegistry.RELICHEIRUS_TOPPLE, 1.0F, 1.0F);
                }
            }
        }

        private BlockPos findTreeBlock() {
            BlockPos origin = getPosition();
            for (int y = 0; y <= 6; y++) {
                for (int x = -5; x <= 5; x++) {
                    for (int z = -5; z <= 5; z++) {
                        BlockPos pos = origin.add(x, y, z);
                        Block block = world.getBlockState(pos).getBlock();
                        if (isEdibleTreeBlock(block, world.getBlockState(pos).getMaterial())) {
                            return pos;
                        }
                    }
                }
            }
            return null;
        }

        private boolean isEdibleTreeBlock(Block block, Material material) {
            return material == Material.LEAVES || block == ACBlockRegistry.PEWEN_PINES.block() || block == ACBlockRegistry.TREE_STAR.block();
        }
    }
}
