package com.zzhalex233.alexscaves.server.item;

import java.util.List;

import javax.annotation.Nullable;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.zzhalex233.alexscaves.server.message.UpdateEffectVisualityEntityMessage;
import com.zzhalex233.alexscaves.server.message.UpdateItemTagMessage;
import com.zzhalex233.alexscaves.server.misc.ACDamageSources;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;
import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;
import com.zzhalex233.alexscaves.server.potion.IrradiatedEffect;

import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class RaygunItem extends Item implements UpdatesStackTags {
    public static final int MAX_CHARGE = 1000;

    public RaygunItem() {
        setMaxStackSize(1);
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
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (hasCharge(stack)) {
            player.setActiveHand(hand);
            player.playSound(ACSoundRegistry.RAYGUN_START, 1.0F, 1.0F);
            player.addStat(StatList.getObjectUseStats(this));
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        ItemStack ammo = findAmmo(player);
        boolean reload = player.capabilities.isCreativeMode;
        if (!ammo.isEmpty()) {
            ammo.shrink(1);
            reload = true;
        }
        if (reload) {
            setCharge(stack, 0);
            world.playSound(null, player.posX, player.posY, player.posZ, ACSoundRegistry.RAYGUN_RELOAD, SoundCategory.PLAYERS, 1.0F, 1.0F);
        } else {
            world.playSound(null, player.posX, player.posY, player.posZ, ACSoundRegistry.RAYGUN_EMPTY, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, world, entity, itemSlot, isSelected);
        boolean using = entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getActiveItemStack() == stack;
        int useTime = getUseTime(stack);
        if (!world.isRemote) {
            if (EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.SOLAR, stack) > 0 && !using) {
                int charge = getCharge(stack);
                if (charge > 0 && world.rand.nextFloat() < 0.02F && world.canBlockSeeSky(new BlockPos(entity).up()) && world.isDaytime() && world.provider.hasSkyLight()) {
                    setCharge(stack, charge - 1);
                    setUseTime(stack, 0);
                }
            }
        } else {
            NBTTagCompound tag = getOrCreateTag(stack);
            if (tag.getInteger("PrevUseTime") != tag.getInteger("UseTime")) {
                tag.setInteger("PrevUseTime", useTime);
            }
            if (using && useTime < 5) {
                setUseTime(stack, useTime + 1);
            } else if (!using && useTime > 0) {
                setUseTime(stack, useTime - 1);
            }
        }
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase living, int count) {
        World world = living.world;
        int usedTicks = getMaxItemUseDuration(stack) - count;
        int realStart = 15;
        float time = usedTicks < realStart ? usedTicks / (float) realStart : 1.0F;
        float maxDist = 25.0F * time;
        boolean xRay = EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.X_RAY, stack) > 0;
        boolean gamma = EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.GAMMA_RAY, stack) > 0;
        if (!hasCharge(stack)) {
            if (world.isRemote) {
                AlexsCaves.NETWORK_WRAPPER.sendToServer(new UpdateItemTagMessage(living.getEntityId(), stack));
            }
            living.stopActiveHand();
            world.playSound(null, living.posX, living.posY, living.posZ, ACSoundRegistry.RAYGUN_EMPTY, SoundCategory.PLAYERS, 1.0F, 1.0F);
            return;
        }

        Vec3d start = living.getPositionEyes(1.0F);
        Vec3d end = start.add(living.getLookVec().scale(maxDist));
        RayTraceResult blockHit = xRay ? null : world.rayTraceBlocks(start, end, false, true, false);
        Vec3d rayEnd = blockHit == null ? end : blockHit.hitVec;
        EntityHit entityHit = findEntityOnPath(world, living, start, end, blockHit == null ? maxDist : start.distanceTo(blockHit.hitVec), xRay);
        Vec3d effectPos = entityHit == null ? rayEnd : entityHit.hitVec;

        if (world.isRemote) {
            setRayPosition(stack, effectPos.x, effectPos.y, effectPos.z);
            AlexsCaves.PROXY.playRaygunSound(living);
            int efficiency = EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.ENERGY_EFFICIENCY, stack);
            int divisor = 2 + (int) Math.floor(efficiency * 1.5F);
            if (time >= 1.0F && usedTicks % divisor == 0 && !(living instanceof EntityPlayer && ((EntityPlayer) living).capabilities.isCreativeMode)) {
                setCharge(stack, Math.min(getCharge(stack) + 1, MAX_CHARGE));
            }
            spawnRaygunParticles(world, effectPos, gamma, time);
        }
        if (!world.isRemote && usedTicks >= realStart && (usedTicks - realStart) % 3 == 0) {
            AxisAlignedBB hitBox = new AxisAlignedBB(effectPos.add(-1.0D, -1.0D, -1.0D), effectPos.add(1.0D, 1.0D, 1.0D));
            int radiationLevel = gamma ? IrradiatedEffect.BLUE_LEVEL : 0;
            for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(living, hitBox)) {
                if (entity.canBeCollidedWith() && entity != living && !entity.isOnSameTeam(living) && !isRidingTogether(living, entity) && entity.attackEntityFrom(ACDamageSources.raygun(living, gamma), gamma ? 2.0F : 1.5F) && entity instanceof EntityLivingBase) {
                    EntityLivingBase target = (EntityLivingBase) entity;
                    target.addPotionEffect(new PotionEffect(ACEffectRegistry.IRRADIATED, 800, radiationLevel));
                    AlexsCaves.NETWORK_WRAPPER.sendToAllAround(new UpdateEffectVisualityEntityMessage(target.getEntityId(), living.getEntityId(), gamma ? 4 : 0, 800), new NetworkRegistry.TargetPoint(world.provider.getDimension(), target.posX, target.posY, target.posZ, 64.0D));
                }
            }
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase living, int timeLeft) {
        super.onPlayerStoppedUsing(stack, world, living, timeLeft);
        if (world.isRemote) {
            AlexsCaves.NETWORK_WRAPPER.sendToServer(new UpdateItemTagMessage(living.getEntityId(), stack));
        }
        AlexsCaves.PROXY.clearSoundCacheFor(living);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.NONE;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getCharge(stack) != 0;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return getCharge(stack) / (double) MAX_CHARGE;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return 0x66FF44;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flagIn) {
        if (getCharge(stack) != 0) {
            tooltip.add(TextFormatting.GREEN + I18n.format("item.alexscaves.raygun.charge", MAX_CHARGE - getCharge(stack), MAX_CHARGE));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem() || slotChanged;
    }

    @Override
    public void updateTagFromServer(Entity holder, ItemStack stack, NBTTagCompound tag) {
        stack.setTagCompound(tag == null ? null : tag.copy());
    }

    public static boolean hasCharge(ItemStack stack) {
        return getCharge(stack) < MAX_CHARGE;
    }

    public static void setUseTime(ItemStack stack, int useTime) {
        NBTTagCompound tag = getOrCreateTag(stack);
        tag.setInteger("PrevUseTime", getUseTime(stack));
        tag.setInteger("UseTime", useTime);
    }

    public static void setRayPosition(ItemStack stack, double x, double y, double z) {
        NBTTagCompound tag = getOrCreateTag(stack);
        Vec3d prev = getRayPosition(stack);
        tag.setDouble("PrevRayX", prev.x);
        tag.setDouble("PrevRayY", prev.y);
        tag.setDouble("PrevRayZ", prev.z);
        tag.setDouble("RayX", x);
        tag.setDouble("RayY", y);
        tag.setDouble("RayZ", z);
    }

    public static int getUseTime(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null ? 0 : tag.getInteger("UseTime");
    }

    public static int getCharge(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null ? 0 : tag.getInteger("ChargeUsed");
    }

    public static void setCharge(ItemStack stack, int charge) {
        getOrCreateTag(stack).setInteger("ChargeUsed", charge);
    }

    public static Vec3d getRayPosition(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey("RayX") ? new Vec3d(tag.getDouble("RayX"), tag.getDouble("RayY"), tag.getDouble("RayZ")) : Vec3d.ZERO;
    }

    public static float getLerpedUseTime(ItemStack stack, float partialTicks) {
        NBTTagCompound tag = stack.getTagCompound();
        float prev = tag == null ? 0.0F : tag.getInteger("PrevUseTime");
        float current = tag == null ? 0.0F : tag.getInteger("UseTime");
        return prev + partialTicks * (current - prev);
    }

    @Nullable
    public static Vec3d getLerpedRayPosition(ItemStack stack, float partialTicks) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            return null;
        }
        double prevX = tag.getDouble("PrevRayX");
        double prevY = tag.getDouble("PrevRayY");
        double prevZ = tag.getDouble("PrevRayZ");
        double x = tag.getDouble("RayX");
        double y = tag.getDouble("RayY");
        double z = tag.getDouble("RayZ");
        return new Vec3d(prevX + partialTicks * (x - prevX), prevY + partialTicks * (y - prevY), prevZ + partialTicks * (z - prevZ));
    }

    private static ItemStack findAmmo(EntityPlayer player) {
        if (player.capabilities.isCreativeMode) {
            return ItemStack.EMPTY;
        }
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack.getItem() == ACBlockRegistry.URANIUM_ROD.item()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static EntityHit findEntityOnPath(World world, EntityLivingBase living, Vec3d start, Vec3d end, double blockDistance, boolean xRay) {
        Vec3d look = end.subtract(start);
        AxisAlignedBB search = living.getEntityBoundingBox().expand(look.x, look.y, look.z).grow(1.0D);
        Entity closest = null;
        Vec3d closestHit = null;
        double closestDistance = xRay ? Double.MAX_VALUE : blockDistance;
        for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(living, search)) {
            if (!entity.canBeCollidedWith() || entity.isOnSameTeam(living) || isRidingTogether(living, entity)) {
                continue;
            }
            AxisAlignedBB box = entity.getEntityBoundingBox().grow(entity.getCollisionBorderSize());
            RayTraceResult hit = box.calculateIntercept(start, end);
            Vec3d hitVec = box.contains(start) ? start : hit == null ? null : hit.hitVec;
            if (hitVec != null) {
                double distance = start.distanceTo(hitVec);
                if (distance < closestDistance) {
                    closest = entity;
                    closestHit = hitVec;
                    closestDistance = distance;
                }
            }
        }
        return closest == null ? null : new EntityHit(closest, closestHit);
    }

    private static void spawnRaygunParticles(World world, Vec3d pos, boolean gamma, float time) {
        EnumParticleTypes main = gamma ? EnumParticleTypes.CRIT_MAGIC : EnumParticleTypes.SPELL_MOB;
        EnumParticleTypes burst = gamma ? EnumParticleTypes.SPELL_INSTANT : EnumParticleTypes.REDSTONE;
        if (time >= 1.0F && world.rand.nextBoolean()) {
            world.spawnParticle(burst, pos.x + (world.rand.nextFloat() - 0.5F) * 0.45F, pos.y + 0.2D, pos.z + (world.rand.nextFloat() - 0.5F) * 0.45F, 0.0D, gamma ? 0.3D : 1.0D, gamma ? 1.0D : 0.0D);
        } else {
            world.spawnParticle(main, pos.x + (world.rand.nextFloat() - 0.5F) * 0.45F, pos.y + 0.2D, pos.z + (world.rand.nextFloat() - 0.5F) * 0.45F, (world.rand.nextFloat() - 0.5F) * 0.2F, (world.rand.nextFloat() - 0.5F) * 0.2F, (world.rand.nextFloat() - 0.5F) * 0.2F);
        }
    }

    private static boolean isRidingTogether(Entity first, Entity second) {
        return first.getRidingEntity() != null && first.getRidingEntity() == second.getRidingEntity();
    }

    private static NBTTagCompound getOrCreateTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    private static class EntityHit {
        private final Entity entity;
        private final Vec3d hitVec;

        private EntityHit(Entity entity, Vec3d hitVec) {
            this.entity = entity;
            this.hitVec = hitVec;
        }
    }
}
