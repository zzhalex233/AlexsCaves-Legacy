package com.zzhalex233.alexscaves.server.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.zzhalex233.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.zzhalex233.alexscaves.server.entity.item.WaveEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;

public class OrtholanceItem extends Item {
    public OrtholanceItem() {
        setMaxStackSize(1);
        setMaxDamage(340);
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
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (stack.getItemDamage() >= stack.getMaxDamage() - 1) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        playerIn.setActiveHand(handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase living, int timeLeft) {
        int charge = MathHelper.clamp(getMaxItemUseDuration(stack) - timeLeft, 0, 60);
        int flinging = EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.FLINGING, stack);
        if (charge <= 0) {
            return;
        }
        float power = 0.1F * charge + flinging * 0.1F;
        Vec3d vec = living.getLookVec().normalize();
        Vec3d launch = new Vec3d(living.motionX, living.motionY, living.motionZ).add(vec.x * power, vec.y * power * 0.15F, vec.z * power);
        if (charge >= 10 && !world.isRemote) {
            world.playSound(null, living.posX, living.posY, living.posZ, ACSoundRegistry.ORTHOLANCE_WAVE, SoundCategory.NEUTRAL, 4.0F, 1.0F);
            stack.damageItem(1, living);
            int maxWaves = spawnWaves(stack, world, living, launch, charge);
            damageChargePath(stack, world, living, launch, maxWaves);
        }
        living.addVelocity(launch.x, launch.y + (living.onGround ? 0.2D : 0.0D) + flinging * 0.1D, launch.z);
        living.velocityChanged = true;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        stack.damageItem(1, attacker);
        if (!attacker.world.isRemote && EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.SEA_SWING, stack) > 0) {
            Vec3d look = attacker.getLookVec();
            WaveEntity wave = new WaveEntity(attacker.world, attacker);
            wave.setPosition(attacker.posX, target.posY, attacker.posZ);
            wave.setLifespan(5);
            wave.setWaveYaw(yawFrom(look));
            attacker.world.spawnEntity(wave);
        }
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        if (state.getBlockHardness(worldIn, pos) != 0.0D) {
            stack.damageItem(2, entityLiving);
        }
        return true;
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        Multimap<String, AttributeModifier> modifiers = HashMultimap.create();
        if (slot == EntityEquipmentSlot.MAINHAND) {
            modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 5.0D, 0));
            modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -2.4D, 0));
        }
        return modifiers;
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

    private int spawnWaves(ItemStack stack, World world, EntityLivingBase living, Vec3d launch, int charge) {
        boolean tsunami = EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.TSUNAMI, stack) > 0;
        int maxWaves = charge / 5;
        if (tsunami) {
            maxWaves = 5;
            Vec3d pos = living.getPositionVector().add(launch);
            WaveEntity wave = new WaveEntity(world, living);
            wave.setPosition(pos.x, living.posY, pos.z);
            wave.setLifespan(20);
            wave.setWaveScale(5.0F);
            wave.setWaitingTicks(2);
            wave.setWaveYaw(yawFrom(launch));
            world.spawnEntity(wave);
            return maxWaves;
        }
        spawnWaveFan(world, living, launch, maxWaves, 0);
        if (EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.SECOND_WAVE, stack) > 0) {
            spawnWaveFan(world, living, launch, Math.max(1, maxWaves - 1), 8);
        }
        return maxWaves;
    }

    private void spawnWaveFan(World world, EntityLivingBase living, Vec3d launch, int maxWaves, int wait) {
        for (int waveIndex = 0; waveIndex < maxWaves; waveIndex++) {
            float fraction = waveIndex / (float) maxWaves;
            int lifespan = 3 + (int) ((1.0F - fraction) * 3.0F);
            Vec3d pos = living.getPositionVector().add(launch.scale(fraction * 2.0D));
            spawnWave(world, living, pos, lifespan, yawFrom(launch) + 60.0F - 15.0F * waveIndex, wait);
            spawnWave(world, living, pos, lifespan, yawFrom(launch) - 60.0F + 15.0F * waveIndex, wait);
        }
    }

    private void spawnWave(World world, EntityLivingBase living, Vec3d pos, int lifespan, float yaw, int wait) {
        WaveEntity wave = new WaveEntity(world, living);
        wave.setPosition(pos.x, living.posY, pos.z);
        wave.setLifespan(lifespan);
        wave.setWaitingTicks(wait);
        wave.setWaveYaw(yaw);
        world.spawnEntity(wave);
    }

    private void damageChargePath(ItemStack stack, World world, EntityLivingBase living, Vec3d launch, int maxWaves) {
        AxisAlignedBB box = new AxisAlignedBB(living.getPositionVector(), living.getPositionVector().add(launch.scale(maxWaves))).grow(1.0D);
        for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, box)) {
            if (entity != living && !entity.isOnSameTeam(living) && living.canEntityBeSeen(entity)) {
                entity.attackEntityFrom(DamageSource.causeMobDamage(living), 5.0F);
                entity.dismountRidingEntity();
            }
        }
    }

    private float yawFrom(Vec3d vec) {
        return -(float) (MathHelper.atan2(vec.x, vec.z) * (180.0D / Math.PI));
    }
}
