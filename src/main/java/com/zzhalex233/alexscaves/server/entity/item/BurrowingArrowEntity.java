package com.zzhalex233.alexscaves.server.entity.item;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BurrowingArrowEntity extends EntityArrow {
    private static final DataParameter<Integer> DUG_BLOCK_COUNT = EntityDataManager.createKey(BurrowingArrowEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> DIGGING = EntityDataManager.createKey(BurrowingArrowEntity.class, DataSerializers.BOOLEAN);

    private float prevDiggingProgress;
    private float diggingProgress;
    private int miningTime;
    private int lastMineBlockBreakProgress = -1;
    private int soundTime;
    private BlockPos hitPos;

    public BurrowingArrowEntity(World worldIn) {
        super(worldIn);
        setDamage(3.5D);
    }

    public BurrowingArrowEntity(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
        setDamage(3.5D);
    }

    public BurrowingArrowEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        setDamage(3.5D);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(DUG_BLOCK_COUNT, 0);
        dataManager.register(DIGGING, false);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        prevDiggingProgress = diggingProgress;
        if (isDigging() && diggingProgress < 5.0F) {
            diggingProgress++;
        }
        if (!isDigging() && diggingProgress > 0.0F) {
            diggingProgress--;
        }
        if (inGround && hitPos != null && canMine(hitPos)) {
            setDigging(true);
            IBlockState state = world.getBlockState(hitPos);
            int hardness = (int) (Math.max(state.getBlockHardness(world, hitPos), 0.2F) * 15.0F);
            int progress = (int) ((float) miningTime / hardness * 10.0F);
            if (progress != lastMineBlockBreakProgress) {
                world.sendBlockBreakProgress(getEntityId(), hitPos, progress);
                lastMineBlockBreakProgress = progress;
            }
            if (miningTime % 8 == 0) {
                playSound(state.getBlock().getSoundType(state, world, hitPos, this).getHitSound(), 1.0F, 1.0F);
            }
            Vec3d centerOf = new Vec3d(hitPos.getX() + 0.5D, hitPos.getY() + 0.5D, hitPos.getZ() + 0.5D).subtract(getPositionVector());
            if (miningTime++ > hardness && !world.isRemote) {
                world.destroyBlock(hitPos, true);
                setDugBlockCount(getDugBlockCount() + 1);
                miningTime = 0;
                lastMineBlockBreakProgress = -1;
                Vec3d direction = centerOf.lengthSquared() > 1.0E-4D ? centerOf.normalize().scale(0.3D) : getLookVec().scale(0.3D);
                inGround = false;
                motionX = direction.x;
                motionY = direction.y;
                motionZ = direction.z;
                velocityChanged = true;
            } else {
                Vec3d direction = centerOf.scale(0.2D);
                motionX = direction.x;
                motionY = direction.y;
                motionZ = direction.z;
                velocityChanged = true;
            }
        } else {
            clearBlockBreakProgress();
            hitPos = null;
            setDigging(false);
        }
        if (isDigging() && soundTime-- <= 0) {
            soundTime = 38;
            playSound(ACSoundRegistry.CORRODENT_TEETH, 1.0F, 1.0F);
        }
    }

    @Override
    protected void onHit(RayTraceResult result) {
        super.onHit(result);
        if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            hitPos = result.getBlockPos();
        }
    }

    @Override
    public void setDead() {
        clearBlockBreakProgress();
        super.setDead();
    }

    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(ACItemRegistry.BURROWING_ARROW.item());
    }

    private boolean canMine(BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        float hardness = state.getBlockHardness(world, pos);
        return getDugBlockCount() < 5 && hardness >= 0.0F && hardness < 50.0F && state.getMaterial().isSolid() && state.getBlock().getExplosionResistance(world, pos, this, null) < 1200.0F;
    }

    private void clearBlockBreakProgress() {
        if (hitPos != null && lastMineBlockBreakProgress != -1) {
            world.sendBlockBreakProgress(getEntityId(), hitPos, -1);
            lastMineBlockBreakProgress = -1;
        }
    }

    private int getDugBlockCount() {
        return dataManager.get(DUG_BLOCK_COUNT);
    }

    private void setDugBlockCount(int count) {
        dataManager.set(DUG_BLOCK_COUNT, count);
    }

    private boolean isDigging() {
        return dataManager.get(DIGGING);
    }

    private void setDigging(boolean digging) {
        dataManager.set(DIGGING, digging);
    }

    public float getDiggingAmount(float partialTicks) {
        return (prevDiggingProgress + (diggingProgress - prevDiggingProgress) * partialTicks) * 0.2F;
    }
}
