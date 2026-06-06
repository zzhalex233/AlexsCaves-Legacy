package com.zzhalex233.alexscaves.server.entity.item;

import com.zzhalex233.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ExtinctionSpearEntity extends EntityArrow {
    private static final DataParameter<Boolean> WIGGLING = EntityDataManager.createKey(ExtinctionSpearEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<ItemStack> SPEAR_ITEM = EntityDataManager.createKey(ExtinctionSpearEntity.class, DataSerializers.ITEM_STACK);
    private boolean dealtDamage;
    private int ticksWiggling;

    public ExtinctionSpearEntity(World worldIn) {
        super(worldIn);
        setDamage(10.0D);
    }

    public ExtinctionSpearEntity(World worldIn, EntityLivingBase shooter, ItemStack itemStack) {
        super(worldIn, shooter);
        setDamage(10.0D);
        setSpearStack(itemStack);
    }

    public ExtinctionSpearEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        setDamage(10.0D);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(WIGGLING, false);
        dataManager.register(SPEAR_ITEM, new ItemStack(ACItemRegistry.EXTINCTION_SPEAR.item()));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        Entity owner = shootingEntity;
        if ((inGround || noClip) && owner != null) {
            if (!isAcceptableReturnOwner(owner)) {
                if (!world.isRemote && pickupStatus == PickupStatus.ALLOWED) {
                    entityDropItem(getArrowStack(), 0.1F);
                }
                setDead();
            } else if (isWiggling()) {
                if (ticksWiggling++ > 20) {
                    setWiggling(false);
                    explode();
                }
            } else {
                Vec3d toOwner = new Vec3d(owner.posX, owner.posY + owner.getEyeHeight() * 0.5D, owner.posZ).subtract(getPositionVector());
                inGround = false;
                noClip = true;
                motionX = motionX * 0.95D + toOwner.normalize().x * 0.3D;
                motionY = motionY * 0.95D + toOwner.normalize().y * 0.3D;
                motionZ = motionZ * 0.95D + toOwner.normalize().z * 0.3D;
                if (!world.isRemote && owner instanceof EntityPlayer && getDistanceSq(owner) < 1.5D) {
                    EntityPlayer player = (EntityPlayer) owner;
                    ItemStack stack = getArrowStack();
                    if (player.capabilities.isCreativeMode || player.inventory.addItemStackToInventory(stack)) {
                        world.playSound(null, posX, posY, posZ, net.minecraft.init.SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, 1.0F);
                        setDead();
                    }
                }
            }
        }
        if (world.isRemote && !inGround) {
            world.spawnParticle(EnumParticleTypes.FLAME, posX, posY + 0.25D, posZ, -motionX * 0.2D, -motionY * 0.2D, -motionZ * 0.2D);
        }
    }

    @Override
    protected void onHit(RayTraceResult raytraceResultIn) {
        if (raytraceResultIn.typeOfHit == RayTraceResult.Type.BLOCK && ticksExisted > 1) {
            setWiggling(true);
        }
        super.onHit(raytraceResultIn);
    }

    @Override
    protected void arrowHit(EntityLivingBase living) {
        if (!dealtDamage) {
            dealtDamage = true;
            living.setFire(5);
            Entity owner = shootingEntity;
            if (!world.isRemote && owner != null) {
                DinosaurSpiritEntity spirit = new DinosaurSpiritEntity(world);
                spirit.setPosition(living.posX, living.posY + living.height, living.posZ);
                spirit.setDinosaurType(DinosaurSpiritEntity.DinosaurType.SUBTERRANODON);
                if (owner instanceof EntityPlayer) {
                    spirit.setPlayer((EntityPlayer) owner);
                } else {
                    spirit.setPlayerUUID(owner.getUniqueID());
                }
                spirit.setAttackingEntityId(living.getEntityId());
                spirit.faceTarget(owner);
                spirit.setEnchantmentLevel(EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.PLUMMETING_FLIGHT, getSpearStack()));
                world.spawnEntity(spirit);
                world.playSound(null, posX, posY, posZ, ACSoundRegistry.EXTINCTION_SPEAR_SUMMON, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
            world.playSound(null, posX, posY, posZ, ACSoundRegistry.EXTINCTION_SPEAR_HIT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
        super.arrowHit(living);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer entityIn) {
        if (shootingEntity == null || shootingEntity == entityIn) {
            super.onCollideWithPlayer(entityIn);
        }
    }

    private boolean isAcceptableReturnOwner(Entity owner) {
        return owner.isEntityAlive() && (!(owner instanceof EntityPlayer) || !((EntityPlayer) owner).isSpectator());
    }

    private void explode() {
        if (!world.isRemote) {
            world.createExplosion(shootingEntity == null ? this : shootingEntity, posX, posY + height * 0.5D, posZ, 1.5F, false);
            world.playSound(null, posX, posY, posZ, ACSoundRegistry.TEPHRA_HIT, SoundCategory.BLOCKS, 4.0F, 0.7F + rand.nextFloat() * 0.2F);
        }
        setDead();
    }

    public boolean isWiggling() {
        return dataManager.get(WIGGLING);
    }

    public void setWiggling(boolean wiggling) {
        dataManager.set(WIGGLING, wiggling);
    }

    private ItemStack getSpearStack() {
        ItemStack stack = dataManager.get(SPEAR_ITEM);
        return stack.isEmpty() ? new ItemStack(ACItemRegistry.EXTINCTION_SPEAR.item()) : stack;
    }

    private void setSpearStack(ItemStack stack) {
        dataManager.set(SPEAR_ITEM, stack.isEmpty() ? new ItemStack(ACItemRegistry.EXTINCTION_SPEAR.item()) : stack.copy());
    }

    @Override
    protected ItemStack getArrowStack() {
        return getSpearStack().copy();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("DealtDamage", dealtDamage);
        compound.setBoolean("Wiggling", isWiggling());
        compound.setInteger("TicksWiggling", ticksWiggling);
        compound.setTag("SpearItem", getSpearStack().writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        dealtDamage = compound.getBoolean("DealtDamage");
        setWiggling(compound.getBoolean("Wiggling"));
        ticksWiggling = compound.getInteger("TicksWiggling");
        if (compound.hasKey("SpearItem", 10)) {
            setSpearStack(new ItemStack(compound.getCompoundTag("SpearItem")));
        }
    }
}
