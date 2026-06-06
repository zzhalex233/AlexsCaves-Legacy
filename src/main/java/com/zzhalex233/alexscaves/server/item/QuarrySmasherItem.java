package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.server.entity.item.QuarrySmasherEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class QuarrySmasherItem extends Item {
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        RayTraceResult hit = rayTrace(world, player, true);
        if (hit == null || hit.typeOfHit != RayTraceResult.Type.BLOCK) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        Vec3d look = player.getLook(1.0F);
        for (Entity entity : world.getEntitiesInAABBexcluding(player, player.getEntityBoundingBox().expand(look.x * 5.0D, look.y * 5.0D, look.z * 5.0D).grow(1.0D), entity -> !(entity instanceof EntityPlayer) || !((EntityPlayer) entity).isSpectator())) {
            AxisAlignedBB box = entity.getEntityBoundingBox().grow(entity.getCollisionBorderSize());
            if (box.contains(player.getPositionEyes(1.0F))) {
                return new ActionResult<>(EnumActionResult.PASS, stack);
            }
        }
        QuarrySmasherEntity smasher = new QuarrySmasherEntity(world);
        smasher.setPosition(hit.hitVec.x, hit.hitVec.y, hit.hitVec.z);
        smasher.rotationYaw = player.rotationYaw;
        smasher.setInactive(true);
        if (!world.getCollisionBoxes(smasher, smasher.getEntityBoundingBox()).isEmpty()) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        if (!world.isRemote) {
            world.spawnEntity(smasher);
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }
        player.swingArm(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
