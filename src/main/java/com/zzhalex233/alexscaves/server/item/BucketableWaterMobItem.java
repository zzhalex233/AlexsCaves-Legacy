package com.zzhalex233.alexscaves.server.item;

import java.util.function.Function;

import com.zzhalex233.alexscaves.server.entity.living.BucketableWaterMob;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class BucketableWaterMobItem extends Item {
    private final Function<World, ? extends BucketableWaterMob> entityFactory;

    public BucketableWaterMobItem(Function<World, ? extends BucketableWaterMob> entityFactory) {
        this.entityFactory = entityFactory;
        setMaxStackSize(1);
        setContainerItem(Items.BUCKET);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        RayTraceResult hit = rayTrace(world, player, true);
        if (hit == null || hit.typeOfHit != RayTraceResult.Type.BLOCK) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }

        BlockPos pos = hit.getBlockPos();
        if (world.getBlockState(pos).getMaterial() != Material.WATER) {
            pos = pos.offset(hit.sideHit);
        }
        if (!player.canPlayerEdit(pos, hit.sideHit, stack)) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        if (!canHoldBucketFluid(world, pos)) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        if (!world.isRemote) {
            if (!placeFluid(player, world, pos)) {
                return new ActionResult<>(EnumActionResult.FAIL, stack);
            }
            BucketableWaterMob entity = entityFactory.apply(world);
            entity.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 0.2D, pos.getZ() + 0.5D, player.rotationYaw, 0.0F);
            entity.setFromBucket(true);
            if (stack.hasDisplayName()) {
                entity.setCustomNameTag(stack.getDisplayName());
            }
            world.spawnEntity(entity);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.capabilities.isCreativeMode ? stack : new ItemStack(Items.BUCKET));
    }

    protected boolean placeFluid(EntityPlayer player, World world, BlockPos pos) {
        if (world.getBlockState(pos).getMaterial() == Material.WATER) {
            return true;
        }
        return ((ItemBucket) Items.WATER_BUCKET).tryPlaceContainedLiquid(player, world, pos);
    }

    protected Block getFluidBlock() {
        return Blocks.WATER;
    }

    protected net.minecraft.util.SoundEvent getEmptySound() {
        return SoundEvents.ITEM_BUCKET_EMPTY;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String key = getTranslationKey(stack);
        if (key.endsWith(".name")) {
            key = key.substring(0, key.length() - 5);
        }
        return I18n.translateToLocal(key + ".name").trim();
    }

    private boolean canHoldBucketFluid(World world, BlockPos pos) {
        Material material = world.getBlockState(pos).getMaterial();
        return world.getBlockState(pos).getBlock() == getFluidBlock() || material.isReplaceable() || world.isAirBlock(pos);
    }
}
