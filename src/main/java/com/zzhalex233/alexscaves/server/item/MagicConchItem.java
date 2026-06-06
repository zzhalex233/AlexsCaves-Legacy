package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.server.entity.living.DeepOneBaseEntity;
import com.zzhalex233.alexscaves.server.entity.living.DeepOneEntity;
import com.zzhalex233.alexscaves.server.entity.living.DeepOneKnightEntity;
import com.zzhalex233.alexscaves.server.entity.living.DeepOneMageEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;

import java.util.function.Function;

public class MagicConchItem extends Item {
    public MagicConchItem() {
        setMaxStackSize(1);
        setMaxDamage(5);
        addPropertyOverride(new ResourceLocation("tooting"), (stack, world, entity) -> entity != null && entity.getActiveItemStack() == stack ? 1.0F : 0.0F);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        playerIn.setActiveHand(handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase living, int timeLeft) {
        int usedTicks = getMaxItemUseDuration(stack) - timeLeft;
        if (usedTicks <= 25) {
            return;
        }
        world.playSound(null, living.posX, living.posY, living.posZ, ACSoundRegistry.MAGIC_CONCH_CAST, SoundCategory.RECORDS, 16.0F, 1.0F);
        int summonTime = 1200;
        if (!world.isRemote) {
            int summoned = 0;
            summoned += summonGroup(world, living, DeepOneEntity::new, 3 + world.rand.nextInt(2));
            summoned += summonGroup(world, living, DeepOneKnightEntity::new, 2 + world.rand.nextInt(2));
            summoned += summonGroup(world, living, DeepOneMageEntity::new, 1 + world.rand.nextInt(2));
            if (summoned > 0) {
                world.playSound(null, living.posX, living.posY, living.posZ, ACSoundRegistry.MAGIC_CONCH_SUMMON, SoundCategory.RECORDS, 12.0F, 1.0F);
            }
            if (living instanceof EntityPlayer) {
                ((EntityPlayer) living).getCooldownTracker().setCooldown(this, summonTime);
            }
            if (!(living instanceof EntityPlayer) || !((EntityPlayer) living).capabilities.isCreativeMode) {
                stack.damageItem(1, living);
            }
        }
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1200;
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

    private int summonGroup(World world, EntityLivingBase summoner, Function<World, DeepOneBaseEntity> factory, int count) {
        int summoned = 0;
        for (int i = 0; i < count; i++) {
            if (summonDeepOne(world, summoner, factory) != null) {
                summoned++;
            }
        }
        return summoned;
    }

    private DeepOneBaseEntity summonDeepOne(World world, EntityLivingBase summoner, Function<World, DeepOneBaseEntity> factory) {
        for (int tries = 0; tries < 99; tries++) {
            BlockPos pos = summoner.getPosition().add(world.rand.nextInt(21) - 10, 7, world.rand.nextInt(21) - 10);
            while (pos.getY() > 1 && (world.isAirBlock(pos) || world.getBlockState(pos).getMaterial() == Material.WATER)) {
                pos = pos.down();
            }
            BlockPos spawnPos = world.getBlockState(pos).getMaterial() == Material.WATER ? pos : pos.up();
            if (!world.isAirBlock(spawnPos) && world.getBlockState(spawnPos).getMaterial() != Material.WATER) {
                continue;
            }
            if (world.getBlockState(pos).getMaterial() != Material.WATER && !world.getBlockState(pos).isSideSolid(world, pos, EnumFacing.UP)) {
                continue;
            }
            DeepOneBaseEntity deepOne = factory.apply(world);
            deepOne.setLocationAndAngles(spawnPos.getX() + 0.5D, spawnPos.getY() + 0.1D, spawnPos.getZ() + 0.5D, world.rand.nextFloat() * 360.0F, 0.0F);
            if (summoner instanceof EntityLiving) {
                deepOne.setAttackTarget(((EntityLiving) summoner).getAttackTarget());
            }
            if (deepOne.getCanSpawnHere()) {
                world.spawnEntity(deepOne);
                return deepOne;
            }
        }
        return null;
    }
}
