package com.zzhalex233.alexscaves.server.block;

import com.zzhalex233.alexscaves.server.entity.living.BucketableWaterMob;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class MuckBlock extends BasicFallingBlock {
    private static final AxisAlignedBB COLLISION_BOX = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.8125D, 1.0D);

    public MuckBlock() {
        super(Material.CLAY, 0.5F, 0.5F, SoundType.SLIME);
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (isOceanEntity(entityIn)) {
            entityIn.motionY += 0.1D;
            entityIn.velocityChanged = true;
        } else {
            entityIn.motionX *= 0.85D;
            entityIn.motionZ *= 0.85D;
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return COLLISION_BOX;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    private boolean isOceanEntity(Entity entity) {
        if (entity instanceof BucketableWaterMob) {
            return true;
        }
        if (entity instanceof EntityLivingBase) {
            ItemStack boots = ((EntityLivingBase) entity).getItemStackFromSlot(EntityEquipmentSlot.FEET);
            return !boots.isEmpty() && boots.getItem() == ACItemRegistry.DIVING_BOOTS.item();
        }
        return false;
    }
}
