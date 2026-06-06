package com.zzhalex233.alexscaves.server.entity.item;

import java.util.UUID;

import javax.annotation.Nullable;

import com.zzhalex233.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.zzhalex233.alexscaves.server.entity.living.GumWormEntity;
import com.zzhalex233.alexscaves.server.entity.living.GumWormSegmentEntity;
import com.zzhalex233.alexscaves.server.item.CandyCaneHookItem;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CandyCaneHookEntity extends EntityThrowable {
    private static final DataParameter<Integer> OWNER_ID = EntityDataManager.createKey(CandyCaneHookEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> REELING = EntityDataManager.createKey(CandyCaneHookEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> OFFHAND = EntityDataManager.createKey(CandyCaneHookEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> HOOKED_ENTITY_ID = EntityDataManager.createKey(CandyCaneHookEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Float> DAMAGE = EntityDataManager.createKey(CandyCaneHookEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> RESISTS_GRAVITY = EntityDataManager.createKey(CandyCaneHookEntity.class, DataSerializers.BOOLEAN);

    private UUID hookedEntityUUID;
    private Vec3d hookedPosition;
    private boolean wasReeling;

    public CandyCaneHookEntity(World world) {
        super(world);
        setSize(0.6F, 0.6F);
    }

    public CandyCaneHookEntity(World world, EntityPlayer player, ItemStack stack, boolean offhand) {
        super(world, player);
        setSize(0.6F, 0.6F);
        setOwner(player);
        setOffhand(offhand);
        setDamage(EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.SHARP_CANE, stack) * 3.0F);
        dataManager.set(RESISTS_GRAVITY, EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.STRAIGHT_HOOK, stack) > 0);
        float yaw = player.rotationYaw;
        float pitch = player.rotationPitch;
        int side = offhand ? player.getPrimaryHand() == EnumHandSide.RIGHT ? -1 : 1 : player.getPrimaryHand() == EnumHandSide.LEFT ? -1 : 1;
        Vec3d offset = new Vec3d(side * -0.45D, 0.0D, 0.25D).rotateYaw(-yaw * ((float) Math.PI / 180.0F));
        setPosition(player.posX + offset.x, player.posY + player.getEyeHeight() - 0.1D, player.posZ + offset.z);
        float speed = 1.0F + EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.FAR_FLUNG, stack) * 0.2F;
        shoot(player, pitch, yaw, 0.0F, speed, 0.0F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(OWNER_ID, -1);
        dataManager.register(REELING, false);
        dataManager.register(OFFHAND, false);
        dataManager.register(HOOKED_ENTITY_ID, -1);
        dataManager.register(DAMAGE, 0.0F);
        dataManager.register(RESISTS_GRAVITY, false);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        Entity owner = getOwner();
        Entity hooked = getHookedEntity();
        EntityPlayer playerOwner = getPlayerOwner();
        if (!world.isRemote) {
            dataManager.set(OWNER_ID, owner == null ? -1 : owner.getEntityId());
            dataManager.set(HOOKED_ENTITY_ID, hooked == null ? -1 : hooked.getEntityId());
            boolean reelingFromHook = isOwnerHoldingHook(false);
            if (!isReeling() && reelingFromHook || playerOwner != null && playerOwner.isSneaking()) {
                setReeling(true);
            }
            if (isReeling() && !reelingFromHook) {
                setReeling(false);
            }
            if (owner == null || !owner.isEntityAlive()) {
                setDead();
                return;
            }
            if (isReeling() && ticksExisted > 5) {
                noClip = true;
                Vec3d toOwner = owner.getPositionEyes(1.0F).subtract(getPositionVector());
                if (toOwner.length() < 0.85D) {
                    setDead();
                    return;
                }
                Vec3d pull = toOwner.length() > 1.0D ? toOwner.normalize() : toOwner;
                motionX = pull.x;
                motionY = pull.y;
                motionZ = pull.z;
                isAirBorne = true;
                velocityChanged = true;
            } else if (hooked != null && hooked.isEntityAlive()) {
                noClip = false;
                if (hooked instanceof GumWormEntity) {
                    hookedPosition = ((GumWormEntity) hooked).getHookPosition(getHookSide());
                } else {
                    hookedPosition = hooked.getPositionVector().add(0.0D, hooked.height * 0.5D, 0.0D);
                }
            } else {
                noClip = false;
            }
            if (hookedPosition == null) {
                hookNearbyEntity();
            } else if (!isReeling()) {
                setPosition(hookedPosition.x, hookedPosition.y, hookedPosition.z);
            }
            if (hookedPosition == null && onGround) {
                hookedPosition = getPositionVector();
            }
        }
        if (hooked instanceof GumWormEntity && playerOwner != null && !isReeling() && !playerOwner.isSneaking()) {
            rideGumWorm((GumWormEntity) hooked, playerOwner);
            faceTowards(hooked.getPositionVector().add(0.0D, 0.5D * hooked.width, 0.0D).subtract(getPositionVector()));
        } else if (hookedPosition == null || isReeling()) {
            updateHookRotation();
        } else {
            motionX = motionY = motionZ = 0.0D;
            updateHookRotation();
        }
        if (!wasReeling && isReeling()) {
            wasReeling = true;
            if (isOwnerHoldingHook(true)) {
                fling();
            }
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (world.isRemote || isReeling()) {
            return;
        }
        motionX = motionY = motionZ = 0.0D;
        if (result.entityHit != null && result.entityHit != getOwner()) {
            setHookedEntity(result.entityHit, result.hitVec);
            if (getDamage() > 0.0F) {
                result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getOwner()), getDamage());
            }
        } else if (result.typeOfHit == RayTraceResult.Type.BLOCK && result.hitVec != null) {
            hookedPosition = result.hitVec;
        }
    }

    @Override
    public void setDead() {
        postReel();
        super.setDead();
    }

    @Override
    protected float getGravityVelocity() {
        return isReeling() || dataManager.get(RESISTS_GRAVITY) && new Vec3d(motionX, 0.0D, motionZ).length() > 0.05D ? 0.0F : 0.08F;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Reeling", isReeling());
        compound.setBoolean("Offhand", isOffhand());
        if (hookedEntityUUID != null) {
            compound.setLong("HookedEntityUUIDMost", hookedEntityUUID.getMostSignificantBits());
            compound.setLong("HookedEntityUUIDLeast", hookedEntityUUID.getLeastSignificantBits());
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setReeling(compound.getBoolean("Reeling"));
        setOffhand(compound.getBoolean("Offhand"));
        if (compound.hasKey("HookedEntityUUIDMost") && compound.hasKey("HookedEntityUUIDLeast")) {
            hookedEntityUUID = new UUID(compound.getLong("HookedEntityUUIDMost"), compound.getLong("HookedEntityUUIDLeast"));
        }
    }

    public void fling() {
        Entity owner = getOwner();
        Entity hooked = getHookedEntity();
        if (owner != null && hookedPosition != null) {
            if (hooked == null) {
                Vec3d delta = hookedPosition.subtract(owner.getPositionVector()).scale(0.2D);
                if (delta.length() > 1.0D) {
                    delta = delta.normalize();
                }
                owner.motionX += delta.x;
                owner.motionY += delta.y + 0.2D;
                owner.motionZ += delta.z;
                owner.velocityChanged = true;
            } else if (!(hooked instanceof GumWormEntity)) {
                Vec3d delta = owner.getPositionVector().subtract(hooked.getPositionVector()).scale(0.2D);
                if (delta.length() > 1.0D) {
                    delta = delta.normalize();
                }
                hooked.motionX += delta.x;
                hooked.motionY += delta.y + 0.2D;
                hooked.motionZ += delta.z;
                hooked.velocityChanged = true;
            }
        }
    }

    public Entity getOwner() {
        Entity thrower = getThrower();
        int ownerId = dataManager.get(OWNER_ID);
        return ownerId == -1 ? thrower : world.getEntityByID(ownerId);
    }

    public void setOwner(Entity owner) {
        dataManager.set(OWNER_ID, owner == null ? -1 : owner.getEntityId());
    }

    @Nullable
    public EntityPlayer getPlayerOwner() {
        Entity owner = getOwner();
        return owner instanceof EntityPlayer ? (EntityPlayer) owner : null;
    }

    public EnumHand getHandLaunchedFrom() {
        return isOffhand() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    }

    public boolean isReeling() {
        return dataManager.get(REELING);
    }

    public void setReeling(boolean reeling) {
        dataManager.set(REELING, reeling);
    }

    public boolean isOffhand() {
        return dataManager.get(OFFHAND);
    }

    public void setOffhand(boolean offhand) {
        dataManager.set(OFFHAND, offhand);
    }

    public float getDamage() {
        return dataManager.get(DAMAGE);
    }

    public void setDamage(float damage) {
        dataManager.set(DAMAGE, damage);
    }

    @Nullable
    public Vec3d getHookedPosition() {
        return hookedPosition;
    }

    public boolean isOwnerHoldingHook(boolean ignoreReeling) {
        EntityPlayer player = getPlayerOwner();
        if (player == null) {
            return true;
        }
        ItemStack stack = player.getHeldItem(getHandLaunchedFrom());
        UUID hookUUID = CandyCaneHookItem.getLaunchedHookUUID(stack);
        if (stack.getItem() instanceof CandyCaneHookItem) {
            return hookUUID != null && hookUUID.equals(getUniqueID()) && (ignoreReeling || CandyCaneHookItem.isReelingIn(stack));
        }
        return true;
    }

    public void postReel() {
        EntityPlayer player = getPlayerOwner();
        if (player != null) {
            ItemStack stack = player.getHeldItem(getHandLaunchedFrom());
            if (stack.getItem() instanceof CandyCaneHookItem) {
                CandyCaneHookItem.setLastLaunchedHookUUID(stack, null);
            }
        }
    }

    @Nullable
    public Entity getHookedEntity() {
        if (hookedEntityUUID == null) {
            int hookedId = dataManager.get(HOOKED_ENTITY_ID);
            return hookedId == -1 ? null : world.getEntityByID(hookedId);
        }
        for (Entity entity : world.loadedEntityList) {
            if (hookedEntityUUID.equals(entity.getUniqueID())) {
                return entity;
            }
        }
        return null;
    }

    private void setHookedEntity(Entity entity) {
        setHookedEntity(entity, null);
    }

    private void setHookedEntity(Entity entity, @Nullable Vec3d hitPosition) {
        if (entity instanceof GumWormSegmentEntity) {
            Entity head = ((GumWormSegmentEntity) entity).getHeadEntity();
            if (head != null) {
                entity = head;
            }
        }
        hookedEntityUUID = entity.getUniqueID();
        dataManager.set(HOOKED_ENTITY_ID, entity.getEntityId());
        hookedPosition = hitPosition == null ? entity.getPositionVector().add(0.0D, entity.height * 0.5D, 0.0D) : hitPosition;
    }

    private void hookNearbyEntity() {
        AxisAlignedBB box = getEntityBoundingBox().grow(1.0D);
        Entity owner = getOwner();
        Entity closest = null;
        for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(this, box)) {
            Entity hookTarget = entity instanceof GumWormSegmentEntity ? ((GumWormSegmentEntity) entity).getHeadEntity() : entity;
            if (hookTarget != null && hookTarget.isEntityAlive() && hookTarget != owner && !(hookTarget instanceof IProjectile) && (closest == null || hookTarget.getDistanceSq(this) < closest.getDistanceSq(this) || hookTarget instanceof GumWormEntity && !(closest instanceof GumWormEntity))) {
                closest = hookTarget;
            }
        }
        if (closest != null) {
            setHookedEntity(closest);
            if (getDamage() > 0.0F) {
                closest.attackEntityFrom(DamageSource.causeThrownDamage(this, owner), getDamage());
            }
        }
    }

    private void updateHookRotation() {
        Vec3d vec = new Vec3d(motionX, motionY, motionZ);
        if (vec.length() > 0.1D) {
            faceTowards(vec);
        }
    }

    private int getHookSide() {
        EntityPlayer player = getPlayerOwner();
        if (player == null) {
            return 0;
        }
        if (getHandLaunchedFrom() == EnumHand.MAIN_HAND) {
            return player.getPrimaryHand() == EnumHandSide.RIGHT ? 1 : -1;
        }
        return player.getPrimaryHand() == EnumHandSide.LEFT ? 1 : -1;
    }

    private void rideGumWorm(GumWormEntity gumWorm, EntityPlayer player) {
        Entity ridingSegment = gumWorm.getRidingSegment();
        if (ridingSegment instanceof GumWormSegmentEntity && player.getRidingEntity() != ridingSegment) {
            Vec3d ridePosition = ((GumWormSegmentEntity) ridingSegment).getRiderPosition(player);
            Vec3d moveVec = ridePosition.subtract(player.getPositionVector());
            if (!player.isRiding()) {
                gumWorm.onMounted();
            }
            if (!world.isRemote && moveVec.length() < 4.0D) {
                player.startRiding(ridingSegment);
            } else {
                if (moveVec.length() >= 1.0D) {
                    moveVec = moveVec.normalize();
                }
                player.motionX = player.motionX * 0.8D + moveVec.x;
                player.motionY = player.motionY * 0.8D + moveVec.y;
                player.motionZ = player.motionZ * 0.8D + moveVec.z;
                player.velocityChanged = true;
            }
        }
        gumWorm.setHookId(isOffhand(), getEntityId());
    }

    private void faceTowards(Vec3d vec) {
        double horizontal = MathHelper.sqrt(vec.x * vec.x + vec.z * vec.z);
        float yawAdd = isReeling() ? 180.0F : 0.0F;
        float pitchMul = isReeling() ? -1.0F : 1.0F;
        rotationYaw = (float) (Math.atan2(vec.x, vec.z) * (180F / Math.PI)) + yawAdd;
        rotationPitch = pitchMul * (float) (Math.atan2(vec.y, horizontal) * (180F / Math.PI));
    }
}
