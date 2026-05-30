package com.zzhalex233.alexscaves.server.item;

import java.util.function.Function;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;

public class ThrownProjectileItem extends Item {
    private final Function<EntityPlayer, EntityThrowable> projectileSupplier;
    private final float pitchOffset;
    private final float velocity;
    private final float inaccuracy;

    public ThrownProjectileItem(Function<EntityPlayer, EntityThrowable> projectileSupplier, float pitchOffset, float velocity, float inaccuracy) {
        this.projectileSupplier = projectileSupplier;
        this.pitchOffset = pitchOffset;
        this.velocity = velocity;
        this.inaccuracy = inaccuracy;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        if (!worldIn.isRemote) {
            EntityThrowable projectile = projectileSupplier.apply(playerIn);
            projectile.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, pitchOffset, velocity, inaccuracy);
            worldIn.spawnEntity(projectile);
        }
        if (!playerIn.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        playerIn.addStat(StatList.getObjectUseStats(this));
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
