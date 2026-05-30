package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class UnrefinedWasteBlock extends BasicFallingBlock {
    private static final AxisAlignedBB COLLISION_BOX = new AxisAlignedBB(0.00625D, 0.00625D, 0.00625D, 0.99375D, 0.875D, 0.99375D);

    public UnrefinedWasteBlock() {
        super(Material.SAND, 0.5F, 0.5F, ACSoundTypes.UNREFINED_WASTE);
        setLightLevel(0.1875F);
        setLightOpacity(0);
        setTickRandomly(true);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return COLLISION_BOX;
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (entityIn instanceof EntityLivingBase) {
            entityIn.motionX *= 0.9D;
            entityIn.motionZ *= 0.9D;
            ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(ACEffectRegistry.IRRADIATED, 4000));
        }
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }
}
