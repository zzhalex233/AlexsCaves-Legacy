package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.zzhalex233.alexscaves.server.entity.item.SpinningPeppermintEntity;
import com.zzhalex233.alexscaves.server.entity.item.SugarStaffHexEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;

public class SugarStaffItem extends Item {
    public SugarStaffItem() {
        setMaxStackSize(1);
        setMaxDamage(100);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        playerIn.swingArm(handIn);
        if (!worldIn.isRemote) {
            if (playerIn.isSneaking()) {
                spawnHex(worldIn, playerIn, stack);
                playerIn.getCooldownTracker().setCooldown(this, 100);
            } else {
                spawnPeppermints(worldIn, playerIn, stack);
            }
            if (!playerIn.capabilities.isCreativeMode) {
                stack.damageItem(1, playerIn);
            }
        }
        playerIn.addStat(StatList.getObjectUseStats(this));
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private void spawnHex(World world, EntityPlayer player, ItemStack stack) {
        BlockPos ground = findGroundBelow(world, player.getPositionEyes(1.0F));
        SugarStaffHexEntity hex = new SugarStaffHexEntity(world);
        hex.setOwner(player);
        hex.setPosition(ground.getX() + 0.5D, ground.getY() + 1.0D, ground.getZ() + 0.5D);
        hex.setHexScale(1.0F + 0.25F * EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.HUMUNGOUS_HEX, stack));
        hex.setLifespan(100 + 60 * EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.SPELL_LASTING, stack));
        world.spawnEntity(hex);
        world.playSound(null, player.posX, player.posY, player.posZ, ACSoundRegistry.SUGAR_STAFF_CAST_HEX, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    private void spawnPeppermints(World world, EntityPlayer player, ItemStack stack) {
        Entity lookingAt = SeaStaffItem.getClosestLookingAtEntityFor(world, player, 32.0D);
        int count = 3 + EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.MULTIPLE_MINT, stack);
        int cooldown = 80;
        for (int i = 0; i < count; i++) {
            SpinningPeppermintEntity peppermint = new SpinningPeppermintEntity(world);
            peppermint.setOwner(player);
            peppermint.setPosition(player.posX, player.posY + player.height * 0.45D, player.posZ);
            if (EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.PEPPERMINT_PUNTING, stack) > 0) {
                peppermint.setStraight(true);
                peppermint.rotationYaw = 180.0F + player.rotationYawHead + (i - 1) * 15.0F;
                peppermint.setSpinSpeed(8.0F);
                cooldown = 20;
            } else {
                peppermint.setStraight(false);
                peppermint.rotationYaw = 180.0F + (i - 1) * 30.0F;
                peppermint.setSpinSpeed(12.0F);
            }
            if (lookingAt != null && EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.SEEKCANDY, stack) > 0) {
                peppermint.setSeekingEntityId(lookingAt.getEntityId());
                peppermint.setSpinSpeed(50.0F);
                cooldown = 50;
            }
            peppermint.setSpinRadius(3.5F);
            peppermint.setStartAngle(i * 360.0F / count);
            peppermint.setLifespan(80);
            world.spawnEntity(peppermint);
        }
        world.playSound(null, player.posX, player.posY, player.posZ, ACSoundRegistry.SUGAR_STAFF_CAST_PEPPERMINT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        player.getCooldownTracker().setCooldown(this, cooldown);
    }

    private BlockPos findGroundBelow(World world, Vec3d eyes) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos((int) Math.floor(eyes.x), (int) Math.floor(eyes.y), (int) Math.floor(eyes.z));
        while (pos.getY() > 1 && world.isAirBlock(pos)) {
            pos.move(net.minecraft.util.EnumFacing.DOWN);
        }
        return pos.toImmutable();
    }

    @Override
    public int getItemEnchantability() {
        return 1;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack) {
        return net.minecraft.item.EnumRarity.UNCOMMON;
    }
}
