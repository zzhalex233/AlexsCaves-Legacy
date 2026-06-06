package com.zzhalex233.alexscaves.server.item;

import java.util.List;

import javax.annotation.Nullable;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ResistorShieldItem extends ItemShield {
    public ResistorShieldItem() {
        setMaxStackSize(1);
        setMaxDamage(1000);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        player.setActiveHand(hand);
        if (player.isSneaking()) {
            setPolarity(stack, !isScarlet(stack));
        }
        world.playSound(null, player.posX, player.posY, player.posZ, ACSoundRegistry.RESITOR_SHIELD_SPIN, SoundCategory.PLAYERS, 1.0F, 1.0F);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.GRAY + I18n.format("item.alexscaves.resistor_shield.desc"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase living, int count) {
        World world = living.world;
        int useTicks = getMaxItemUseDuration(stack) - count;
        boolean scarlet = isScarlet(stack);
        boolean firstHit = useTicks >= 10 && useTicks <= 12;
        int slam = EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.HEAVY_SLAM, stack);
        float range = 5.0F;
        if (world.isRemote) {
            setUseTime(stack, useTicks);
            if (useTicks == 10) {
                living.playSound(ACSoundRegistry.RESITOR_SHIELD_SLAM, 1.0F, 1.0F);
            }
            if (useTicks >= 10 && useTicks % 5 == 0) {
                AlexsCaves.PROXY.playResistorShieldSound(living, scarlet);
                Vec3d from = living.getPositionVector().add(0.0D, 0.2D, 0.0D);
                int particles = 5 + living.getRNG().nextInt(5);
                for (int i = 0; i < particles; i++) {
                    Vec3d offset = new Vec3d((living.getRNG().nextFloat() - 0.5F) * 0.3F, (living.getRNG().nextFloat() - 0.5F) * 0.3F, range * (0.5F + 0.5F * living.getRNG().nextFloat())).rotateYaw((float) (i / (double) particles * Math.PI * 2.0D));
                    Vec3d to = from.add(offset);
                    AlexsCaves.PROXY.spawnMagneticFlow(world, scarlet ? to : from, scarlet ? from : to, !scarlet);
                }
            }
        }
        if (useTicks >= 10 && useTicks % 5 == 0) {
            AxisAlignedBB bashBox = living.getEntityBoundingBox().grow(range, 1.0D, range);
            for (EntityLivingBase target : world.getEntitiesWithinAABB(EntityLivingBase.class, bashBox)) {
                if (target != living && !living.isOnSameTeam(target) && target.getDistance(living) <= range) {
                    target.attackEntityFrom(DamageSource.causeMobDamage(living), firstHit ? 6.0F + slam * 3.0F : 2.0F);
                    target.knockBack(living, firstHit ? 0.5F : 0.2F, scarlet ? living.posX - target.posX : target.posX - living.posX, scarlet ? living.posZ - target.posZ : target.posZ - living.posZ);
                }
            }
        }
        if (useTicks == 10 && !world.isRemote) {
            stack.damageItem(1, living);
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase living, int timeLeft) {
        super.onPlayerStoppedUsing(stack, world, living, timeLeft);
        AlexsCaves.PROXY.clearSoundCacheFor(living);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.onUpdate(stack, world, entity, slot, selected);
        if (getUseTime(stack) != 0 && entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getActiveItemStack() != stack) {
            setUseTime(stack, 0);
            getOrCreateTag(stack).setInteger("PrevUseTime", 0);
        }
        if (world.isRemote) {
            int switchTime = getSwitchTime(stack);
            NBTTagCompound tag = getOrCreateTag(stack);
            if (tag.getInteger("PrevSwitchTime") != tag.getInteger("SwitchTime")) {
                tag.setInteger("PrevSwitchTime", switchTime);
            }
            if (isScarlet(stack) && switchTime < 5) {
                setSwitchTime(stack, switchTime + 1);
            } else if (!isScarlet(stack) && switchTime > 0) {
                setSwitchTime(stack, switchTime - 1);
            }
        }
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BLOCK;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
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
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        Item item = repair.getItem();
        return item == ACItemRegistry.SCARLET_NEODYMIUM_INGOT.item() || item == ACItemRegistry.AZURE_NEODYMIUM_INGOT.item() || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem() || slotChanged;
    }

    public static void setUseTime(ItemStack stack, int useTime) {
        NBTTagCompound tag = getOrCreateTag(stack);
        tag.setInteger("PrevUseTime", getUseTime(stack));
        tag.setInteger("UseTime", useTime);
    }

    public static void setSwitchTime(ItemStack stack, int switchTime) {
        NBTTagCompound tag = getOrCreateTag(stack);
        tag.setInteger("PrevSwitchTime", getSwitchTime(stack));
        tag.setInteger("SwitchTime", switchTime);
    }

    public static void setPolarity(ItemStack stack, boolean scarlet) {
        getOrCreateTag(stack).setBoolean("Polarity", scarlet);
    }

    public static int getUseTime(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null ? 0 : tag.getInteger("UseTime");
    }

    public static float getLerpedUseTime(ItemStack stack, float partialTicks) {
        NBTTagCompound tag = stack.getTagCompound();
        float prev = tag == null ? 0.0F : tag.getInteger("PrevUseTime");
        float current = tag == null ? 0.0F : tag.getInteger("UseTime");
        return prev + partialTicks * (current - prev);
    }

    public static int getSwitchTime(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null ? 0 : tag.getInteger("SwitchTime");
    }

    public static float getLerpedSwitchTime(ItemStack stack, float partialTicks) {
        NBTTagCompound tag = stack.getTagCompound();
        float prev = tag == null ? 0.0F : tag.getInteger("PrevSwitchTime");
        float current = tag == null ? 0.0F : tag.getInteger("SwitchTime");
        return prev + partialTicks * (current - prev);
    }

    public static boolean isScarlet(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.getBoolean("Polarity");
    }

    private static NBTTagCompound getOrCreateTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }
}
