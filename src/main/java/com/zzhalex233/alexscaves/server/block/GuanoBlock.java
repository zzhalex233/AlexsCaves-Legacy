package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuanoBlock extends BasicFallingBlock {
    private static final AxisAlignedBB COLLISION_BOX = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D);

    public GuanoBlock() {
        super(Material.CLAY, 0.3F, 0.3F, SoundType.SNOW);
        setTickRandomly(true);
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (isForlornEntity(entityIn)) {
            entityIn.motionX *= 0.9D;
            entityIn.motionZ *= 0.9D;
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

    @Override
    public int quantityDropped(Random random) {
        return 4;
    }

    @Override
    public net.minecraft.item.Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return com.zzhalex233.alexscaves.server.item.ACItemRegistry.GUANO.item();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (rand.nextInt(20) == 0) {
            com.zzhalex233.alexscaves.client.particle.FlyParticle.spawn(worldIn, pos, rand);
        }
    }

    public static boolean isForlornEntity(Entity entity) {
        return entity instanceof EntityBat;
    }
}
