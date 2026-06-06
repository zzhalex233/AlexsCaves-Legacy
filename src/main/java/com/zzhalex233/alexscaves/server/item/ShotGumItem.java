package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.zzhalex233.alexscaves.server.entity.item.GumballEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ShotGumItem extends Item {
    public static final int MAX_AMMO = 4;

    public ShotGumItem() {
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (hasAmmo(stack)) {
            if (getShootTime(stack) == 0 && !isShooting(stack)) {
                setShooting(stack, true);
            }
            playerIn.addStat(StatList.getObjectUseStats(this));
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        ItemStack ammo = findAmmo(playerIn);
        boolean reload = playerIn.capabilities.isCreativeMode;
        if (!ammo.isEmpty()) {
            ammo.shrink(1);
            reload = true;
        }
        if (reload) {
            setGumballsLeft(stack, MAX_AMMO);
            worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, ACSoundRegistry.SHOTGUM_RELOAD, SoundCategory.PLAYERS, 1.0F, 1.0F);
        } else {
            worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, ACSoundRegistry.SHOTGUM_EMPTY, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
        boolean shooting = isShooting(stack);
        int shootTime = getShootTime(stack);
        NBTTagCompound tag = getOrCreateTag(stack);
        tag.setInteger("PrevShootTime", shootTime);
        tag.setFloat("PrevCrankAngle", getCrankAngle(stack));
        if (shootTime == 5 && shooting) {
            setShooting(stack, false);
            worldIn.playSound(null, entityIn.posX, entityIn.posY, entityIn.posZ, ACSoundRegistry.SHOTGUM_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);
            if (entityIn instanceof EntityLivingBase && !worldIn.isRemote) {
                shootGumballs(stack, worldIn, (EntityLivingBase) entityIn);
            }
            if (!(entityIn instanceof EntityPlayer && ((EntityPlayer) entityIn).capabilities.isCreativeMode)) {
                setGumballsLeft(stack, Math.max(getGumballsLeft(stack) - 1, 0));
            }
        }
        if (shooting && shootTime < 5) {
            setShootTime(stack, shootTime + 1);
        }
        if (!shooting && shootTime > 0) {
            setShootTime(stack, shootTime - 1);
        }
        if (shooting) {
            setCrankAngle(stack, getCrankAngle(stack) + 45.0F);
        }
    }

    private void shootGumballs(ItemStack stack, World world, EntityLivingBase living) {
        boolean explosive = EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.EXPLOSIVE_FLAVOR, stack) > 0;
        boolean leftHand = isLeftHandedShot(stack, living);
        int count = explosive ? 1 : 2;
        for (int i = 0; i < count; i++) {
            GumballEntity gumball = new GumballEntity(world, living);
            Vec3d offset = rotateOffset(new Vec3d((explosive ? 0.0D : i == 0 ? 0.15D : -0.15D) + (leftHand ? 0.35D : -0.35D), 0.0D, 0.75D), living.rotationPitch, living.rotationYawHead);
            gumball.setPosition(living.posX + offset.x, living.posY + living.height * 0.8D + offset.y, living.posZ + offset.z);
            gumball.setTargetsOnBounce(EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.TARGETED_RICOCHET, stack) > 0);
            gumball.setSplitsOnHit(EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.TRIPLE_SPLIT, stack) > 0);
            gumball.setExplosive(explosive);
            gumball.setMaximumBounces(4 + EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.BOUNCY_BALL, stack) * 2);
            gumball.setDamage(4.0F);
            gumball.shoot(living, living.rotationPitch, living.rotationYaw, 0.0F, explosive ? 0.5F : 1.5F, 5.0F);
            world.spawnEntity(gumball);
        }
    }

    private boolean isLeftHandedShot(ItemStack stack, EntityLivingBase living) {
        return living.getHeldItemMainhand() == stack && living.getPrimaryHand() == EnumHandSide.LEFT || living.getHeldItemOffhand() == stack && living.getPrimaryHand() == EnumHandSide.RIGHT;
    }

    private Vec3d rotateOffset(Vec3d vec, float pitch, float yaw) {
        return vec.rotatePitch(-pitch * ((float) Math.PI / 180.0F)).rotateYaw(-yaw * ((float) Math.PI / 180.0F));
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
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return 0xFF9FFF;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0D - getGumballsLeft(stack) / (double) MAX_AMMO;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getGumballsLeft(stack) != MAX_AMMO;
    }

    public static float getLerpedShootTime(ItemStack stack, float partialTicks) {
        NBTTagCompound tag = stack.getTagCompound();
        float prev = tag != null ? tag.getInteger("PrevShootTime") : 0.0F;
        float current = tag != null ? tag.getInteger("ShootTime") : 0.0F;
        return prev + partialTicks * (current - prev);
    }

    public static float getLerpedCrankAngle(ItemStack stack, float partialTicks) {
        NBTTagCompound tag = stack.getTagCompound();
        float prev = tag != null ? tag.getFloat("PrevCrankAngle") : 0.0F;
        float current = tag != null ? tag.getFloat("CrankAngle") : 0.0F;
        return prev + partialTicks * (current - prev);
    }

    public static boolean shouldBeHeldUpright(ItemStack stack) {
        return hasAmmo(stack);
    }

    private static boolean hasAmmo(ItemStack stack) {
        return getGumballsLeft(stack) > 0;
    }

    private static int getShootTime(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null ? 0 : tag.getInteger("ShootTime");
    }

    private static void setShootTime(ItemStack stack, int time) {
        getOrCreateTag(stack).setInteger("ShootTime", time);
    }

    public static float getCrankAngle(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null ? 0.0F : tag.getFloat("CrankAngle");
    }

    public static void setCrankAngle(ItemStack stack, float angle) {
        getOrCreateTag(stack).setFloat("CrankAngle", angle);
    }

    public static int getGumballsLeft(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey("Gumballs") ? tag.getInteger("Gumballs") : MAX_AMMO;
    }

    public static void setGumballsLeft(ItemStack stack, int count) {
        getOrCreateTag(stack).setInteger("Gumballs", count);
    }

    private static boolean isShooting(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.getBoolean("Shooting");
    }

    private static void setShooting(ItemStack stack, boolean shooting) {
        getOrCreateTag(stack).setBoolean("Shooting", shooting);
    }

    private static NBTTagCompound getOrCreateTag(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    private ItemStack findAmmo(EntityPlayer player) {
        if (player.capabilities.isCreativeMode) {
            return ItemStack.EMPTY;
        }
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack.getItem() == ACItemRegistry.GUMBALL_PILE.item()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}
