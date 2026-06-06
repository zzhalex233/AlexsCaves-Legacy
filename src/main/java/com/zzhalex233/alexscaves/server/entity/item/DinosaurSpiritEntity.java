package com.zzhalex233.alexscaves.server.entity.item;

import java.util.UUID;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DinosaurSpiritEntity extends Entity {
    private static final DataParameter<Integer> OWNER_ID = EntityDataManager.createKey(DinosaurSpiritEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> DINOSAUR_TYPE = EntityDataManager.createKey(DinosaurSpiritEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> ATTACKING_ENTITY_ID = EntityDataManager.createKey(DinosaurSpiritEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> DELAY_SPAWN = EntityDataManager.createKey(DinosaurSpiritEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> FADING = EntityDataManager.createKey(DinosaurSpiritEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> USING_ABILITY = EntityDataManager.createKey(DinosaurSpiritEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> ROTATE_OFFSET = EntityDataManager.createKey(DinosaurSpiritEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> ENCHANTMENT_LEVEL = EntityDataManager.createKey(DinosaurSpiritEntity.class, DataSerializers.VARINT);
    private UUID playerUUID;
    private float fadeIn;
    private float prevFadeIn;
    private float abilityProgress;
    private float prevAbilityProgress;
    private int duration;
    private boolean dealtDamage;

    public DinosaurSpiritEntity(World worldIn) {
        super(worldIn);
        setSize(1.0F, 1.0F);
        noClip = true;
    }

    @Override
    protected void entityInit() {
        dataManager.register(OWNER_ID, -1);
        dataManager.register(DINOSAUR_TYPE, 0);
        dataManager.register(ATTACKING_ENTITY_ID, -1);
        dataManager.register(DELAY_SPAWN, 0);
        dataManager.register(FADING, false);
        dataManager.register(USING_ABILITY, false);
        dataManager.register(ROTATE_OFFSET, 0.0F);
        dataManager.register(ENCHANTMENT_LEVEL, 0);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        EntityPlayer player = getUsingPlayer();
        prevFadeIn = fadeIn;
        prevAbilityProgress = abilityProgress;
        if (getDelaySpawn() > 0) {
            setDelaySpawn(getDelaySpawn() - 1);
            fadeIn = 0.0F;
            if (getDelaySpawn() == 0) {
                playSound(ACSoundRegistry.EXTINCTION_SPEAR_SUMMON, 1.0F, 1.0F);
            }
            return;
        }
        if (isFading() && fadeIn > 0.0F) {
            fadeIn--;
        }
        if (!isFading() && fadeIn < 10.0F) {
            fadeIn++;
        }
        if (isUsingAbility() && abilityProgress < 5.0F) {
            abilityProgress++;
        }
        if (!isUsingAbility() && abilityProgress > 0.0F) {
            abilityProgress--;
        }
        if (isFading() && fadeIn <= 0.0F) {
            setDead();
        }
        if (world.isRemote) {
            world.spawnParticle(EnumParticleTypes.FLAME, posX + (rand.nextDouble() - 0.5D), posY + rand.nextDouble(), posZ + (rand.nextDouble() - 0.5D), 0.0D, 0.0D, 0.0D);
        }
        move(MoverType.SELF, motionX, motionY, motionZ);
        motionX *= 0.9D;
        motionY *= 0.9D;
        motionZ *= 0.9D;
        if (player == null) {
            setFading(true);
            return;
        }
        switch (getDinosaurType()) {
            case SUBTERRANODON:
                tickSubterranodon();
                break;
            case GROTTOCERATOPS:
                tickGrottoceratops(player);
                break;
            case TREMORSAURUS:
                tickTremorsaurus(player);
                break;
            default:
                break;
        }
    }

    private void tickSubterranodon() {
        Entity target = getAttackingEntity();
        motionY += 0.03D + 0.005D * getEnchantmentLevel();
        if (target != null && duration < 40 + getEnchantmentLevel() * 5) {
            target.motionX = 0.0D;
            target.motionY = 0.0D;
            target.motionZ = 0.0D;
            target.setPosition(posX, posY - target.height, posZ);
            duration++;
        } else {
            setFading(true);
        }
    }

    private void tickGrottoceratops(EntityPlayer player) {
        float rot = getRotateOffset() + player.ticksExisted * 5.0F;
        Vec3d orbit = new Vec3d(0.0D, 1.0D, 2.0D).rotateYaw((float) -Math.toRadians(rot));
        Vec3d target = player.getPositionVector().add(orbit).subtract(getPositionVector());
        rotationPitch = 10.0F;
        motionX = target.x * 0.25D;
        motionY = target.y * 0.25D;
        motionZ = target.z * 0.25D;
        noClip = true;
        if (!world.isRemote && player.getActiveItemStack().getItem() != ACItemRegistry.EXTINCTION_SPEAR.item()) {
            setFading(true);
        }
    }

    private void tickTremorsaurus(EntityPlayer player) {
        Entity target = getAttackingEntity();
        if (target != null) {
            noClip = true;
            faceTarget(target);
            boolean inRange = getDistance(target) < target.width + 3.5D;
            if (!inRange) {
                Vec3d motion = target.getPositionVector().subtract(getPositionVector());
                if (motion.length() > 1.0D) {
                    motion = motion.normalize();
                }
                motionX = motion.x * 0.15D;
                motionY = motion.y * 0.15D;
                motionZ = motion.z * 0.15D;
            }
            setUsingAbility(true);
            if (inRange && abilityProgress >= 5.0F) {
                if (!dealtDamage && target.attackEntityFrom(DamageSource.causePlayerDamage(player).setMagicDamage(), 3.0F + 2.0F * getEnchantmentLevel())) {
                    dealtDamage = true;
                }
                setFading(true);
            }
        }
        if (duration++ > 20) {
            setFading(true);
        }
    }

    public void faceTarget(Entity target) {
        double dx = target.posX - posX;
        double dy = target.getEntityBoundingBox().minY + target.height * 0.5D - posY;
        double dz = target.posZ - posZ;
        double horizontal = MathHelper.sqrt(dx * dx + dz * dz);
        rotationYaw = (float) (MathHelper.atan2(dx, dz) * -57.2957763671875D);
        rotationPitch = (float) (MathHelper.atan2(dy, horizontal) * -57.2957763671875D);
    }

    public void setPlayer(EntityPlayer player) {
        setPlayerUUID(player.getUniqueID());
        dataManager.set(OWNER_ID, player.getEntityId());
    }

    public void setPlayerUUID(UUID uuid) {
        playerUUID = uuid;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public EntityPlayer getUsingPlayer() {
        Entity owner = dataManager.get(OWNER_ID) >= 0 ? world.getEntityByID(dataManager.get(OWNER_ID)) : null;
        if (owner instanceof EntityPlayer) {
            return (EntityPlayer) owner;
        }
        if (playerUUID == null) {
            return null;
        }
        for (EntityPlayer player : world.playerEntities) {
            if (playerUUID.equals(player.getUniqueID())) {
                dataManager.set(OWNER_ID, player.getEntityId());
                return player;
            }
        }
        return null;
    }

    public void setAttackingEntityId(int id) {
        dataManager.set(ATTACKING_ENTITY_ID, id);
    }

    public Entity getAttackingEntity() {
        int id = dataManager.get(ATTACKING_ENTITY_ID);
        return id < 0 ? null : world.getEntityByID(id);
    }

    public int getDelaySpawn() {
        return dataManager.get(DELAY_SPAWN);
    }

    public void setDelaySpawn(int delay) {
        dataManager.set(DELAY_SPAWN, delay);
    }

    public int getEnchantmentLevel() {
        return dataManager.get(ENCHANTMENT_LEVEL);
    }

    public void setEnchantmentLevel(int level) {
        dataManager.set(ENCHANTMENT_LEVEL, level);
    }

    public float getRotateOffset() {
        return dataManager.get(ROTATE_OFFSET);
    }

    public void setRotateOffset(float rotateOffset) {
        dataManager.set(ROTATE_OFFSET, rotateOffset);
    }

    public DinosaurType getDinosaurType() {
        int index = MathHelper.clamp(dataManager.get(DINOSAUR_TYPE), 0, DinosaurType.values().length - 1);
        return DinosaurType.values()[index];
    }

    public void setDinosaurType(DinosaurType type) {
        dataManager.set(DINOSAUR_TYPE, type.ordinal());
    }

    public boolean isFading() {
        return dataManager.get(FADING);
    }

    public void setFading(boolean fading) {
        dataManager.set(FADING, fading);
    }

    public boolean isUsingAbility() {
        return dataManager.get(USING_ABILITY);
    }

    public void setUsingAbility(boolean usingAbility) {
        dataManager.set(USING_ABILITY, usingAbility);
    }

    public float getFadeIn(float partialTicks) {
        return (prevFadeIn + (fadeIn - prevFadeIn) * partialTicks) * 0.1F;
    }

    public float getAbilityProgress(float partialTicks) {
        return (prevAbilityProgress + (abilityProgress - prevAbilityProgress) * partialTicks) * 0.2F;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        if (compound.hasUniqueId("UsingPlayerUUID")) {
            playerUUID = compound.getUniqueId("UsingPlayerUUID");
        }
        setDinosaurType(DinosaurType.values()[MathHelper.clamp(compound.getInteger("DinosaurType"), 0, DinosaurType.values().length - 1)]);
        setAttackingEntityId(compound.getInteger("AttackingEntityId"));
        setDelaySpawn(compound.getInteger("DelaySpawn"));
        setEnchantmentLevel(compound.getInteger("EnchantmentLevel"));
        setFading(compound.getBoolean("Fading"));
        duration = compound.getInteger("Duration");
        dealtDamage = compound.getBoolean("DealtDamage");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        if (playerUUID != null) {
            compound.setUniqueId("UsingPlayerUUID", playerUUID);
        }
        compound.setInteger("DinosaurType", getDinosaurType().ordinal());
        compound.setInteger("AttackingEntityId", dataManager.get(ATTACKING_ENTITY_ID));
        compound.setInteger("DelaySpawn", getDelaySpawn());
        compound.setInteger("EnchantmentLevel", getEnchantmentLevel());
        compound.setBoolean("Fading", isFading());
        compound.setInteger("Duration", duration);
        compound.setBoolean("DealtDamage", dealtDamage);
    }

    public enum DinosaurType {
        SUBTERRANODON,
        GROTTOCERATOPS,
        TREMORSAURUS
    }
}
