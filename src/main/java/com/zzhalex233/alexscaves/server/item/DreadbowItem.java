package com.zzhalex233.alexscaves.server.item;

import java.util.Random;

import com.zzhalex233.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.zzhalex233.alexscaves.server.entity.item.DarkArrowEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;
import com.zzhalex233.alexscaves.server.potion.DarknessIncarnateEffect;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DreadbowItem extends ItemBow {
    public DreadbowItem() {
        setMaxStackSize(1);
        setMaxDamage(500);
        addPropertyOverride(new ResourceLocation("pull"), (stack, world, entity) -> entity == null || entity.getActiveItemStack() != stack ? 0.0F : getPullingAmount(stack, 1.0F));
        addPropertyOverride(new ResourceLocation("pulling"), (stack, world, entity) -> entity != null && entity.getActiveItemStack() == stack ? 1.0F : 0.0F);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack bow = player.getHeldItem(hand);
        ItemStack ammo = findAmmo(player);
        if (player.capabilities.isCreativeMode || !ammo.isEmpty() || canUseRespite(bow, player)) {
            setUseTime(bow, 0);
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, bow);
        }
        return new ActionResult<>(EnumActionResult.FAIL, bow);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, world, entity, itemSlot, isSelected);
        boolean using = entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getActiveItemStack() == stack;
        int useTime = getUseTime(stack);
        NBTTagCompound tag = getOrCreateTag(stack);
        tag.setInteger("PrevUseTime", useTime);
        if (world.isRemote && using) {
            if (useTime < getMaxLoadTime(stack)) {
                int speed = EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.RELENTLESS_DARKNESS, stack) > 0 ? 3 : 1;
                setUseTime(stack, Math.min(getMaxLoadTime(stack), useTime + speed));
            }
            int perfection = EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.TWILIGHT_PERFECTION, stack);
            if (perfection > 0 && getUseTime(stack) >= getMaxLoadTime(stack) && getPerfectShotTicks(stack) <= 0) {
                setPerfectShotTicks(stack, 4 + (perfection - 1) * 3);
            }
            if (getPerfectShotTicks(stack) > 0) {
                setPerfectShotTicks(stack, getPerfectShotTicks(stack) - 1);
            }
        }
        if (!using && useTime > 0) {
            setUseTime(stack, Math.max(0, useTime - 5));
            setPerfectShotTicks(stack, 0);
        }
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        if (!player.world.isRemote && player instanceof EntityPlayer && EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.RELENTLESS_DARKNESS, stack) > 0 && count % 3 == 0) {
            fireRelentless(stack, player.world, (EntityPlayer) player);
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
        if (!(entity instanceof EntityPlayer) || EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.RELENTLESS_DARKNESS, stack) > 0) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        float power = getPowerForTime(getMaxItemUseDuration(stack) - timeLeft, stack);
        if (power <= 0.1F) {
            return;
        }
        ItemStack ammo = findAmmo(player);
        boolean respite = canUseRespite(stack, player);
        if (ammo.isEmpty() && respite) {
            ammo = new ItemStack(Items.ARROW);
        }
        if (ammo.isEmpty() && !player.capabilities.isCreativeMode) {
            return;
        }
        if (!world.isRemote) {
            spawnArrowRain(world, player, stack, ammo, power);
        }
        world.playSound(null, player.posX, player.posY, player.posZ, ACSoundRegistry.DREADBOW_RELEASE, SoundCategory.PLAYERS, 1.0F, 1.0F);
        consumeAmmoAndDamage(stack, player, ammo, respite);
        player.addStat(StatList.getObjectUseStats(this));
        setUseTime(stack, 0);
        setPerfectShotTicks(stack, 0);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public int getItemEnchantability() {
        return 1;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    public static int getUseTime(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null ? 0 : tag.getInteger("UseTime");
    }

    public static void setUseTime(ItemStack stack, int useTime) {
        getOrCreateTag(stack).setInteger("UseTime", useTime);
    }

    public static int getPerfectShotTicks(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null ? 0 : tag.getInteger("PerfectShotTicks");
    }

    public static void setPerfectShotTicks(ItemStack stack, int ticks) {
        getOrCreateTag(stack).setInteger("PerfectShotTicks", ticks);
    }

    public static float getLerpedUseTime(ItemStack stack, float partialTicks) {
        NBTTagCompound tag = stack.getTagCompound();
        float prev = tag == null ? 0.0F : tag.getInteger("PrevUseTime");
        float current = tag == null ? 0.0F : tag.getInteger("UseTime");
        return prev + partialTicks * (current - prev);
    }

    public static float getPullingAmount(ItemStack stack, float partialTicks) {
        return Math.min(getLerpedUseTime(stack, partialTicks) / (float) getMaxLoadTime(stack), 1.0F);
    }

    public static float getPowerForTime(int use, ItemStack stack) {
        float f = use / (float) getMaxLoadTime(stack);
        f = (f * f + f * 2.0F) / 3.0F;
        return Math.min(f, 1.0F);
    }

    private static int getMaxLoadTime(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.RELENTLESS_DARKNESS, stack) > 0 ? 5 : 40 - 8 * EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.DARK_NOCK, stack);
    }

    private void spawnArrowRain(World world, EntityPlayer player, ItemStack bow, ItemStack ammo, float power) {
        Vec3d target = getLookTarget(world, player, 42.0D + 86.0D * power);
        BlockPos sky = findArrowRainStart(world, target);
        boolean precise = EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.PRECISE_VOLLEY, bow) > 0;
        boolean perfect = EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.TWILIGHT_PERFECTION, bow) > 0 && getPerfectShotTicks(bow) > 0;
        boolean dark = isConvertibleArrow(ammo);
        int maxArrows = dark ? 30 : 8;
        Random random = world.rand;
        for (int i = 0; i < Math.ceil(maxArrows * power); i++) {
            EntityArrow arrow = dark ? newDarkArrow(world, player, precise, perfect) : createVanillaArrow(world, player, ammo);
            Vec3d start = new Vec3d(sky.getX() + 0.5D + random.nextFloat() * 16.0F - 8.0F, sky.getY() + random.nextFloat() * 4.0F - 2.0F, sky.getZ() + 0.5D + random.nextFloat() * 16.0F - 8.0F);
            arrow.setPosition(start.x, start.y, start.z);
            Vec3d direction = target.subtract(start);
            float randomness = precise ? 0.0F : (dark ? 20.0F : 5.0F) + random.nextFloat() * 10.0F;
            arrow.shoot(direction.x, direction.y, direction.z, 0.5F + 1.5F * random.nextFloat(), randomness);
            arrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
            world.spawnEntity(arrow);
        }
        if (dark) {
            world.playSound(null, target.x, target.y, target.z, ACSoundRegistry.DREADBOW_RAIN, SoundCategory.PLAYERS, 12.0F, 1.0F);
        }
    }

    private BlockPos findArrowRainStart(World world, Vec3d target) {
        BlockPos sky = new BlockPos(target.x, target.y + 1.5D, target.z);
        int tries = 0;
        while (sky.getY() < world.getHeight() && world.isAirBlock(sky) && tries < 15) {
            sky = sky.up();
            tries++;
        }
        return sky;
    }

    private void fireRelentless(ItemStack bow, World world, EntityPlayer player) {
        ItemStack ammo = findAmmo(player);
        boolean respite = canUseRespite(bow, player);
        if (ammo.isEmpty() && respite) {
            ammo = new ItemStack(Items.ARROW);
        }
        if (ammo.isEmpty() && !player.capabilities.isCreativeMode) {
            return;
        }
        boolean dark = isConvertibleArrow(ammo);
        int count = dark ? 1 + world.rand.nextInt(2) : 1;
        float randomness = 0.5F;
        for (int i = 0; i < count; i++) {
            EntityArrow arrow = dark ? newDarkArrow(world, player, false, false) : createVanillaArrow(world, player, ammo);
            arrow.setPosition(player.posX + world.rand.nextFloat() - 0.5F, player.posY + player.getEyeHeight() + world.rand.nextFloat() - 0.5F, player.posZ + world.rand.nextFloat() - 0.5F);
            Vec3d look = player.getLookVec();
            arrow.shoot(look.x, look.y, look.z, 4.0F + 3.0F * world.rand.nextFloat(), randomness);
            arrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
            world.spawnEntity(arrow);
            randomness += 2.0F;
        }
        world.playSound(null, player.posX, player.posY, player.posZ, ACSoundRegistry.DREADBOW_RELEASE, SoundCategory.PLAYERS, 1.0F, 1.0F);
        consumeAmmoAndDamage(bow, player, ammo, respite);
    }

    private EntityArrow createVanillaArrow(World world, EntityPlayer player, ItemStack ammo) {
        ItemArrow arrowItem = ammo.getItem() instanceof ItemArrow ? (ItemArrow) ammo.getItem() : (ItemArrow) Items.ARROW;
        return arrowItem.createArrow(world, ammo, player);
    }

    private DarkArrowEntity newDarkArrow(World world, EntityPlayer player, boolean precise, boolean perfect) {
        DarkArrowEntity arrow = new DarkArrowEntity(world, player);
        arrow.setShadowArrowDamage(precise ? 2.0F : 3.0F);
        arrow.setPerfectShot(perfect);
        return arrow;
    }

    private boolean isConvertibleArrow(ItemStack ammo) {
        return ammo.isEmpty() || ammo.getItem() == Items.ARROW;
    }

    private boolean canUseRespite(ItemStack bow, EntityPlayer player) {
        return EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.SHADED_RESPITE, bow) > 0 && !DarknessIncarnateEffect.isInLight(player, 11);
    }

    private void consumeAmmoAndDamage(ItemStack bow, EntityPlayer player, ItemStack ammo, boolean respite) {
        if (player.capabilities.isCreativeMode) {
            return;
        }
        if (!respite) {
            bow.damageItem(1, player);
        }
        if (!respite || ammo.getItem() != Items.ARROW) {
            ammo.shrink(1);
        }
    }

    private Vec3d getLookTarget(World world, EntityPlayer player, double range) {
        Vec3d eyes = player.getPositionEyes(1.0F);
        Vec3d look = player.getLookVec();
        Vec3d end = eyes.add(look.scale(range));
        RayTraceResult result = world.rayTraceBlocks(eyes, end, false, true, false);
        return result == null || result.hitVec == null ? end : result.hitVec;
    }

    private static NBTTagCompound getOrCreateTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }
}
