package com.zzhalex233.alexscaves.server.entity.living;

import javax.annotation.Nullable;

import com.zzhalex233.alexscaves.server.entity.ACEntityRegistry;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;
import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIOcelotAttack;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class RaycatEntity extends EntityTameable {
    private static final DataParameter<Integer> ABSORB_TARGET_ID = EntityDataManager.createKey(RaycatEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> COMMAND = EntityDataManager.createKey(RaycatEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> LAY_TIME = EntityDataManager.createKey(RaycatEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Float> ABSORB_AMOUNT = EntityDataManager.createKey(RaycatEntity.class, DataSerializers.FLOAT);
    private final EntityAISit aiSit = new EntityAISit(this);
    private float sitProgress;
    private float prevSitProgress;
    private float layProgress;
    private float prevLayProgress;
    private float prevAbsorbAmount;
    private int absorbCooldown = 300;

    public RaycatEntity(World world) {
        super(world);
        setSize(0.6F, 0.85F);
        absorbCooldown += rand.nextInt(300);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(ABSORB_TARGET_ID, -1);
        dataManager.register(COMMAND, 0);
        dataManager.register(LAY_TIME, 0);
        dataManager.register(ABSORB_AMOUNT, 0.0F);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, aiSit);
        tasks.addTask(2, new EntityAITempt(this, 1.0D, Items.FISH, false));
        tasks.addTask(3, new RaycatFollowOwnerAI(this, 1.2D, 5.0F, 2.0F));
        tasks.addTask(4, new EntityAILeapAtTarget(this, 0.3F));
        tasks.addTask(5, new EntityAIOcelotAttack(this));
        tasks.addTask(6, new EntityAIMate(this, 0.8D));
        tasks.addTask(7, new EntityAIWanderAvoidWater(this, 0.8D));
        tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 15.0F));
        tasks.addTask(9, new EntityAILookIdle(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(24.0D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        prevSitProgress = sitProgress;
        prevLayProgress = layProgress;
        prevAbsorbAmount = getAbsorbAmount();
        if (isSitting() && sitProgress < 5.0F) {
            sitProgress++;
        }
        if (!isSitting() && sitProgress > 0.0F) {
            sitProgress--;
        }
        if (getLayTime() > 0) {
            setLayTime(getLayTime() - 1);
            if (layProgress < 5.0F) {
                layProgress++;
            }
        } else if (layProgress > 0.0F) {
            layProgress--;
        }
        if (isPotionActive(ACEffectRegistry.IRRADIATED) && ticksExisted % 10 == 0) {
            heal(1.0F);
        }
        updateAbsorbing();
    }

    private void updateAbsorbing() {
        Entity absorbTarget = getAbsorbTarget();
        if (absorbCooldown > 0) {
            absorbCooldown--;
            return;
        }
        if (absorbTarget == null) {
            if (!world.isRemote) {
                Entity closest = findAbsorbTarget();
                setAbsorbTargetId(closest == null ? -1 : closest.getEntityId());
                resetAbsorbCooldown();
            }
        } else if (getAbsorbAmount() <= 0.0F) {
            setAbsorbAmount(1.0F);
            playSound(ACSoundRegistry.RAYCAT_ABSORB, 1.0F, getSoundPitch());
        } else {
            setAbsorbAmount(Math.max(0.0F, getAbsorbAmount() - 0.05F));
            if (world.isRemote) {
                spawnAbsorbParticles(absorbTarget);
            }
            if (getAbsorbAmount() <= 0.0F) {
                absorbRadiation(absorbTarget);
                setAbsorbTargetId(-1);
                if (!world.isRemote) {
                    resetAbsorbCooldown();
                }
            }
        }
    }

    @Nullable
    private Entity findAbsorbTarget() {
        EntityLivingBase owner = getOwner();
        EntityLivingBase closest = null;
        if (owner != null && getDistance(owner) < 20.0F && owner.isPotionActive(ACEffectRegistry.IRRADIATED)) {
            closest = owner;
        } else {
            for (EntityLivingBase living : world.getEntitiesWithinAABB(EntityLivingBase.class, getEntityBoundingBox().grow(9.0D))) {
                if (living != this && !(living instanceof RaycatEntity) && living.isPotionActive(ACEffectRegistry.IRRADIATED) && (isTamed() || !(living instanceof EntityPlayer)) && (closest == null || living.getDistanceSq(this) < closest.getDistanceSq(this))) {
                    closest = living;
                }
            }
        }
        return closest;
    }

    private void absorbRadiation(Entity absorbTarget) {
        int currentAmplifier = isPotionActive(ACEffectRegistry.IRRADIATED) ? getActivePotionEffect(ACEffectRegistry.IRRADIATED).getAmplifier() + 1 : 0;
        heal(10.0F);
        addPotionEffect(new PotionEffect(ACEffectRegistry.IRRADIATED, 200, currentAmplifier));
        if (absorbTarget instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) absorbTarget;
            PotionEffect effect = living.getActivePotionEffect(ACEffectRegistry.IRRADIATED);
            if (effect != null) {
                int duration = effect.getDuration();
                int amplifier = effect.getAmplifier();
                living.removePotionEffect(ACEffectRegistry.IRRADIATED);
                if (amplifier > 0) {
                    living.addPotionEffect(new PotionEffect(ACEffectRegistry.IRRADIATED, duration, amplifier - 1));
                }
            }
        }
    }

    private void spawnAbsorbParticles(Entity target) {
        for (int i = 0; i < 2; i++) {
            double x = posX + (target.posX - posX) * rand.nextDouble();
            double y = posY + height * 0.5D + (target.posY + target.height * 0.5D - (posY + height * 0.5D)) * rand.nextDouble();
            double z = posZ + (target.posZ - posZ) * rand.nextDouble();
            world.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0.2D, 1.0D, 0.0D);
        }
    }

    private void resetAbsorbCooldown() {
        absorbCooldown = 300 + rand.nextInt(300);
    }

    @Nullable
    public Entity getAbsorbTarget() {
        int id = dataManager.get(ABSORB_TARGET_ID);
        return id < 0 ? null : world.getEntityByID(id);
    }

    public void setAbsorbTargetId(int id) {
        dataManager.set(ABSORB_TARGET_ID, id);
    }

    public float getSitProgress(float partialTicks) {
        return (prevSitProgress + (sitProgress - prevSitProgress) * partialTicks) * 0.2F;
    }

    public float getLayProgress(float partialTicks) {
        return (prevLayProgress + (layProgress - prevLayProgress) * partialTicks) * 0.2F;
    }

    public float getAbsorbAmount(float partialTicks) {
        return prevAbsorbAmount + (getAbsorbAmount() - prevAbsorbAmount) * partialTicks;
    }

    public float getAbsorbAmount() {
        return dataManager.get(ABSORB_AMOUNT);
    }

    public void setAbsorbAmount(float absorbAmount) {
        dataManager.set(ABSORB_AMOUNT, absorbAmount);
    }

    public int getCommand() {
        return dataManager.get(COMMAND);
    }

    public void setCommand(int command) {
        dataManager.set(COMMAND, command);
    }

    public int getLayTime() {
        return dataManager.get(LAY_TIME);
    }

    public void setLayTime(int layTime) {
        dataManager.set(LAY_TIME, layTime);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("Command", getCommand());
        compound.setInteger("LayTime", getLayTime());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setCommand(compound.getInteger("Command"));
        setLayTime(compound.getInteger("LayTime"));
        aiSit.setSitting(isSitting());
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() == ACItemRegistry.RADGILL.item() && !isTamed()) {
            if (!world.isRemote) {
                if (!player.capabilities.isCreativeMode) {
                    stack.shrink(1);
                }
                playSound(ACSoundRegistry.RAYCAT_EAT, 1.0F, 1.0F);
                if (rand.nextInt(3) == 0) {
                    setTamedBy(player);
                    navigator.clearPath();
                    setAttackTarget(null);
                    setCommand(1);
                    aiSit.setSitting(true);
                    world.setEntityState(this, (byte) 7);
                } else {
                    world.setEntityState(this, (byte) 6);
                }
            }
            return true;
        }
        if (isTamed() && isOwner(player) && !isBreedingItem(stack) && !player.isSneaking()) {
            if (!world.isRemote) {
                setCommand((getCommand() + 1) % 3);
                boolean sit = getCommand() == 1;
                aiSit.setSitting(sit);
                setSitting(sit);
                player.sendStatusMessage(new net.minecraft.util.text.TextComponentTranslation("entity.alexscaves.all.command_" + getCommand(), getName()), true);
            }
            return true;
        }
        return super.processInteract(player, hand);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == ACItemRegistry.RADGILL.item();
    }

    @Override
    public boolean canMateWith(EntityAnimal otherAnimal) {
        return otherAnimal instanceof RaycatEntity && otherAnimal != this && isTamed() && ((RaycatEntity) otherAnimal).isTamed() && isInLove() && otherAnimal.isInLove();
    }

    @Nullable
    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        RaycatEntity raycat = new RaycatEntity(world);
        EntityLivingBase owner = getOwner();
        if (owner instanceof EntityPlayer) {
            raycat.setTamedBy((EntityPlayer) owner);
        }
        return raycat;
    }

    @Override
    public boolean canBeHitWithPotion() {
        return false;
    }

    @Override
    public boolean isPotionApplicable(PotionEffect effect) {
        return effect.getPotion() != ACEffectRegistry.IRRADIATED && super.isPotionApplicable(effect);
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }

    @Override
    protected boolean canDespawn() {
        return !isTamed() && super.canDespawn();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return isTamed() ? ACSoundRegistry.RAYCAT_TAME_IDLE : ACSoundRegistry.RAYCAT_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.RAYCAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.RAYCAT_HURT;
    }

    private static class RaycatFollowOwnerAI extends EntityAIFollowOwner {
        private final RaycatEntity raycat;

        private RaycatFollowOwnerAI(RaycatEntity raycat, double speed, float minDist, float maxDist) {
            super(raycat, speed, minDist, maxDist);
            this.raycat = raycat;
        }

        @Override
        public boolean shouldExecute() {
            return raycat.getCommand() == 2 && super.shouldExecute();
        }
    }
}
