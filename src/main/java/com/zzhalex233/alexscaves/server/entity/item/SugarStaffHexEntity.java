package com.zzhalex233.alexscaves.server.entity.item;

import java.util.UUID;

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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class SugarStaffHexEntity extends Entity {
    private static final DataParameter<Integer> LIFESPAN = EntityDataManager.createKey(SugarStaffHexEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Float> HEX_SCALE = EntityDataManager.createKey(SugarStaffHexEntity.class, DataSerializers.FLOAT);
    private EntityLivingBase owner;
    private UUID ownerUUID;
    private int despawnsIn = -1;
    private int prevDespawnsIn;
    private final float yRenderOffset = rand.nextFloat() * 0.05F;

    public SugarStaffHexEntity(World worldIn) {
        super(worldIn);
        setSize(4.0F, 0.25F);
        noClip = true;
    }

    @Override
    protected void entityInit() {
        dataManager.register(LIFESPAN, 100);
        dataManager.register(HEX_SCALE, 1.0F);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (despawnsIn == -1) {
            despawnsIn = getLifespan();
        }
        prevDespawnsIn = despawnsIn;
        if (despawnsIn-- <= 0 && !world.isRemote) {
            setDead();
        }
        if (world.isRemote) {
            if (despawnsIn < 5) {
                for (int i = 0; i < 8 + rand.nextInt(8); i++) {
                    world.spawnParticle(EnumParticleTypes.SPELL_WITCH, posX + (rand.nextFloat() - 0.5F) * width, posY + rand.nextFloat() * 0.3F, posZ + (rand.nextFloat() - 0.5F) * width, 0.0D, 0.0D, 0.0D);
                }
            } else if (rand.nextFloat() < 0.6F) {
                world.spawnParticle(EnumParticleTypes.SPELL_WITCH, posX + (rand.nextFloat() * 4.0F - 2.0F) * getHexScale(), posY + 0.1D, posZ + (rand.nextFloat() * 4.0F - 2.0F) * getHexScale(), 0.0D, 0.0D, 0.0D);
            }
        }
        hurtEntities(despawnsIn < 5);
        noClip = true;
        move(MoverType.SELF, motionX, motionY, motionZ);
        motionX *= 0.7D;
        motionY *= 0.8D;
        motionZ *= 0.7D;
    }

    private void hurtEntities(boolean finalExplosion) {
        EntityLivingBase owner = getOwner();
        for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, getEntityBoundingBox().grow(getHexScale() * 2.0D, 0.5D, getHexScale() * 2.0D))) {
            if (owner != null && entity != owner && entity.getDistance(this) <= 4.0F * getHexScale() && !entity.isOnSameTeam(owner)) {
                entity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, owner), finalExplosion ? 6.0F : 1.0F);
                if (finalExplosion) {
                    entity.knockBack(this, 0.9F, posX - entity.posX, posZ - entity.posZ);
                }
            }
        }
    }

    public void setOwner(EntityLivingBase owner) {
        this.owner = owner;
        this.ownerUUID = owner == null ? null : owner.getUniqueID();
    }

    public EntityLivingBase getOwner() {
        if (owner == null && ownerUUID != null && !world.isRemote) {
            EntityPlayer player = world.getPlayerEntityByUUID(ownerUUID);
            if (player != null) {
                owner = player;
            }
        }
        return owner;
    }

    public float getDespawnTime(float partialTicks) {
        return prevDespawnsIn + (despawnsIn - prevDespawnsIn) * partialTicks;
    }

    public int getLifespan() {
        return dataManager.get(LIFESPAN);
    }

    public void setLifespan(int lifespan) {
        dataManager.set(LIFESPAN, lifespan);
    }

    public float getHexScale() {
        return dataManager.get(HEX_SCALE);
    }

    public void setHexScale(float scale) {
        dataManager.set(HEX_SCALE, scale);
        setSize(scale * 4.0F, 0.25F);
    }

    public float getYRenderOffset() {
        return yRenderOffset;
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (HEX_SCALE.equals(key)) {
            setSize(getHexScale() * 4.0F, 0.25F);
        }
        super.notifyDataManagerChange(key);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        despawnsIn = compound.hasKey("DespawnsIn") ? compound.getInteger("DespawnsIn") : -1;
        setLifespan(compound.getInteger("Lifespan"));
        setHexScale(compound.hasKey("HexScale") ? compound.getFloat("HexScale") : 1.0F);
        if (compound.hasUniqueId("Owner")) {
            ownerUUID = compound.getUniqueId("Owner");
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("DespawnsIn", despawnsIn);
        compound.setInteger("Lifespan", getLifespan());
        compound.setFloat("HexScale", getHexScale());
        if (ownerUUID != null) {
            compound.setUniqueId("Owner", ownerUUID);
        }
    }
}
