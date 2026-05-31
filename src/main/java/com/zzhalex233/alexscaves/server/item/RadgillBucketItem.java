package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.entity.living.RadgillEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RadgillBucketItem extends BucketableWaterMobItem {
    public RadgillBucketItem() {
        super(RadgillEntity::new);
    }

    @Override
    protected Block getFluidBlock() {
        return ACBlockRegistry.ACID.block();
    }

    @Override
    protected boolean placeFluid(EntityPlayer player, World world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() == getFluidBlock()) {
            return true;
        }
        return PurpleSodaBucketItem.tryPlaceContainedLiquid(player, world, pos, getFluidBlock(), getEmptySound());
    }

    @Override
    protected net.minecraft.util.SoundEvent getEmptySound() {
        return ACSoundRegistry.ACID_UNSUBMERGE;
    }
}
