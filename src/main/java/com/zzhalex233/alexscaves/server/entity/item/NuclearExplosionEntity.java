package com.zzhalex233.alexscaves.server.entity.item;

import java.util.List;

import com.zzhalex233.alexscaves.server.config.ACConfig;
import com.zzhalex233.alexscaves.server.entity.living.RaycatEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;
import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class NuclearExplosionEntity extends Entity {
    private static final DataParameter<Float> SIZE = EntityDataManager.createKey(NuclearExplosionEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> NO_GRIEFING = EntityDataManager.createKey(NuclearExplosionEntity.class, DataSerializers.BOOLEAN);
    private boolean detonated;

    public NuclearExplosionEntity(World world) {
        super(world);
        setSize(2.0F, 2.0F);
        noClip = true;
        ignoreFrustumCheck = true;
    }

    @Override
    protected void entityInit() {
        dataManager.register(SIZE, 1.0F);
        dataManager.register(NO_GRIEFING, false);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (ticksExisted == 1) {
            playExplosionEffects();
            if (!world.isRemote && !detonated) {
                detonate();
                detonated = true;
            }
        }
        if (world.isRemote) {
            spawnClientCloud();
        }
        if (ticksExisted > 80) {
            setDead();
        }
    }

    private void detonate() {
        float radius = (float) (16.0D * Math.max(0.1D, ACConfig.getNukeExplosionSizeModifier()) * getNukeSize());
        boolean doGrief = !isNoGriefing() && world.getGameRules().getBoolean("mobGriefing") && ACConfig.getNukeMaxBlockExplosionResistance() > 0;
        damageEntities(radius);
        if (doGrief) {
            carveBlocks(radius);
        }
    }

    private void damageEntities(float radius) {
        AxisAlignedBB bounds = getEntityBoundingBox().grow(radius * 1.5D, radius * 0.8D, radius * 1.5D);
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, bounds);
        for (EntityLivingBase living : entities) {
            if (living instanceof RaycatEntity) {
                continue;
            }
            double distance = living.getDistance(this);
            float strength = MathHelper.clamp((float) ((radius * 1.5D - distance) / (radius * 1.5D)), 0.0F, 1.0F);
            if (strength <= 0.0F) {
                continue;
            }
            living.attackEntityFrom(DamageSource.causeExplosionDamage(new Explosion(world, this, posX, posY, posZ, radius, false, false)), strength * (getNukeSize() <= 1.5F ? 100.0F : 250.0F));
            living.addPotionEffect(new PotionEffect(ACEffectRegistry.IRRADIATED, 48000, getNukeSize() <= 1.5F ? 1 : 2, false, false));
            Vec3d push = new Vec3d(living.posX - posX, living.posY + living.height * 0.5D - posY, living.posZ - posZ).normalize();
            double fling = strength * getNukeSize() * (living instanceof EntityPlayer ? 0.2D : 0.45D);
            living.motionX += push.x * fling;
            living.motionY += Math.max(0.05D, push.y * fling);
            living.motionZ += push.z * fling;
            living.velocityChanged = true;
        }
    }

    private void carveBlocks(float radius) {
        int maxResistance = ACConfig.getNukeMaxBlockExplosionResistance();
        int horizontal = MathHelper.ceil(radius);
        int vertical = MathHelper.ceil(radius * 0.45F);
        BlockPos center = getPosition();
        int attempts = Math.min(12000, horizontal * horizontal * Math.max(1, vertical) / 2);
        for (int i = 0; i < attempts; i++) {
            int x = center.getX() + rand.nextInt(horizontal * 2 + 1) - horizontal;
            int y = center.getY() + rand.nextInt(vertical * 2 + 1) - vertical;
            int z = center.getZ() + rand.nextInt(horizontal * 2 + 1) - horizontal;
            BlockPos pos = new BlockPos(x, y, z);
            double flat = pos.distanceSq(center.getX(), y, center.getZ());
            double yFactor = 1.0D - Math.abs(y - center.getY()) / (double) Math.max(1, vertical);
            if (flat > radius * radius * Math.max(0.1D, yFactor)) {
                continue;
            }
            net.minecraft.block.state.IBlockState state = world.getBlockState(pos);
            if (state.getBlock().isAir(state, world, pos) || state.getBlock() == Blocks.BEDROCK || state.getBlock().getExplosionResistance(world, pos, this, null) >= maxResistance) {
                continue;
            }
            if (ACConfig.doNukesSpawnItemDrops() && rand.nextFloat() < 0.025F / Math.max(1.0F, getNukeSize())) {
                state.getBlock().dropBlockAsItemWithChance(world, pos, state, 0.1F, 0);
            }
            world.setBlockToAir(pos);
            if (rand.nextFloat() < 0.03F && world.isAirBlock(pos.up()) && world.getBlockState(pos.down()).isSideSolid(world, pos.down(), net.minecraft.util.EnumFacing.UP)) {
                world.setBlockState(pos, Blocks.FIRE.getDefaultState());
            }
        }
    }

    private void playExplosionEffects() {
        float size = getNukeSize();
        world.playSound(posX, posY, posZ, size > 1.5F ? ACSoundRegistry.LARGE_NUCLEAR_EXPLOSION : ACSoundRegistry.NUCLEAR_EXPLOSION, SoundCategory.HOSTILE, 8.0F, 1.0F, false);
        world.playSound(posX, posY, posZ, ACSoundRegistry.NUCLEAR_EXPLOSION_RUMBLE, SoundCategory.HOSTILE, 8.0F, 1.0F, false);
        world.playSound(posX, posY, posZ, ACSoundRegistry.NUCLEAR_EXPLOSION_RINGING, SoundCategory.HOSTILE, 4.0F, 1.0F, false);
    }

    private void spawnClientCloud() {
        float size = getNukeSize();
        if (ticksExisted == 1) {
            for (int i = 0; i < 24; i++) {
                world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, posX + rand.nextGaussian() * size, posY + rand.nextDouble() * 4.0D, posZ + rand.nextGaussian() * size, 0.0D, 0.0D, 0.0D);
            }
        }
        for (int i = 0; i < 8; i++) {
            double spread = size * (1.0D + ticksExisted * 0.18D);
            double y = posY + Math.min(45.0D * size, ticksExisted * 0.8D) + rand.nextGaussian() * 2.0D;
            world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, posX + rand.nextGaussian() * spread, y, posZ + rand.nextGaussian() * spread, 0.0D, 0.04D, 0.0D);
            world.spawnParticle(EnumParticleTypes.FLAME, posX + rand.nextGaussian() * spread * 0.3D, posY + rand.nextDouble() * 6.0D, posZ + rand.nextGaussian() * spread * 0.3D, 0.0D, 0.02D, 0.0D);
        }
    }

    public float getNukeSize() {
        return dataManager.get(SIZE);
    }

    public void setNukeSize(float size) {
        dataManager.set(SIZE, size);
    }

    public boolean isNoGriefing() {
        return dataManager.get(NO_GRIEFING);
    }

    public void setNoGriefing(boolean noGriefing) {
        dataManager.set(NO_GRIEFING, noGriefing);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        setNukeSize(compound.getFloat("Size"));
        setNoGriefing(compound.getBoolean("NoGriefing"));
        detonated = compound.getBoolean("Detonated");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setFloat("Size", getNukeSize());
        compound.setBoolean("NoGriefing", isNoGriefing());
        compound.setBoolean("Detonated", detonated);
    }
}
