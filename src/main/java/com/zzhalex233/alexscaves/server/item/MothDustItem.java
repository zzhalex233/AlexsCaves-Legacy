package com.zzhalex233.alexscaves.server.item;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MothDustItem extends Item {
    public MothDustItem() {
        setMaxStackSize(64);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase living, int timeLeft) {
        if (!(living instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) living;
        float strength = getPowerForTime(getMaxItemUseDuration(stack) - timeLeft);
        double distance = strength * 5.0D;
        Vec3d eye = player.getPositionEyes(1.0F);
        Vec3d look = player.getLook(1.0F);
        Vec3d end = eye.add(look.scale(distance));
        RayTraceResult hit = world.rayTraceBlocks(eye, end, false, true, false);
        Vec3d target = hit == null ? end : hit.hitVec;
        for (int i = 0; i < MathHelper.ceil(distance * 3.0D); i++) {
            Vec3d from = eye.add(randOffset(world), randOffset(world), randOffset(world));
            Vec3d motion = target.subtract(eye).normalize().scale(strength * 0.35D);
            world.spawnParticle(EnumParticleTypes.SPELL_MOB, from.x, from.y, from.z, motion.x, motion.y + 0.02D, motion.z);
        }
        if (!world.isRemote) {
            List<EntityMob> mobs = world.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB(target.x - 8.0D, target.y - 8.0D, target.z - 8.0D, target.x + 8.0D, target.y + 8.0D, target.z + 8.0D), mob -> mob.getAttackTarget() == null);
            for (EntityMob mob : mobs) {
                mob.setAttackTarget(player);
            }
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }
    }

    private double randOffset(World world) {
        return world.rand.nextDouble() - 0.5D;
    }

    public static float getPowerForTime(int time) {
        float f = (float) time / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        return Math.min(f, 1.0F);
    }
}
