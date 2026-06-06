package com.zzhalex233.alexscaves.server.item;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.config.ACConfig;
import com.zzhalex233.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.zzhalex233.alexscaves.server.message.UpdateItemTagMessage;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class TotemOfPossessionItem extends Item implements UpdatesStackTags {
    public TotemOfPossessionItem() {
        setMaxStackSize(1);
        setMaxDamage(1000);
        addPropertyOverride(new ResourceLocation("totem"), (stack, world, entity) -> {
            if (!isBound(stack)) {
                return 0.0F;
            }
            return entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getActiveItemStack() == stack ? 1.0F : 0.5F;
        });
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
        if (!world.isRemote && world instanceof WorldServer) {
            updateEntityIdFromServer((WorldServer) world, player, stack);
        }
        Entity controlled = getControlledEntity(world, stack);
        if (isBound(stack) && (controlled == null || !controlled.isEntityAlive()) && !world.isRemote) {
            setPossessed(controlled, false);
            resetBound(stack);
        }
        if (isBound(stack) && controlled != null && (isEntityLookingAt(player, controlled, 5.0D) || EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.SIGHTLESS, stack) > 0)) {
            player.playSound(ACSoundRegistry.TOTEM_OF_POSSESSION_USE, 1.0F, 1.0F);
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase user, int timeLeft) {
        Entity controlled = getControlledEntity(world, stack);
        if (controlled != null) {
            controlled.setGlowing(false);
        }
        if (world.isRemote) {
            AlexsCaves.NETWORK_WRAPPER.sendToServer(new UpdateItemTagMessage(user.getEntityId(), stack));
        }
        if (stack.getItemDamage() >= stack.getMaxDamage()) {
            stack.shrink(1);
        }
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase user, int count) {
        World world = user.world;
        Entity controlled = getControlledEntity(world, stack);
        if (isBound(stack) && (controlled == null || !controlled.isEntityAlive()) || stack.getItemDamage() >= stack.getMaxDamage()) {
            if (controlled != null && EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.DETONATING_DEATH, stack) > 0 && !world.isRemote) {
                world.createExplosion(user, controlled.posX, controlled.posY, controlled.posZ, 2.0F + controlled.width, false);
            }
            setPossessed(controlled, false);
            resetBound(stack);
            user.stopActiveHand();
            if (world.isRemote) {
                AlexsCaves.NETWORK_WRAPPER.sendToServer(new UpdateItemTagMessage(user.getEntityId(), stack));
            }
            return;
        }
        if (!isBound(stack) || controlled == null || !isEntityLookingAt(user, controlled, 5.0D) && EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.SIGHTLESS, stack) == 0 || controlled instanceof EntityPlayer && !ACConfig.doesTotemOfPossessionWorkOnPlayers()) {
            user.stopActiveHand();
            if (world.isRemote) {
                AlexsCaves.NETWORK_WRAPPER.sendToServer(new UpdateItemTagMessage(user.getEntityId(), stack));
            }
            return;
        }
        if (!world.isRemote && count % 2 == 0 && !(user instanceof EntityPlayer && ((EntityPlayer) user).capabilities.isCreativeMode)) {
            stack.damageItem(1, user);
        }

        int usedTicks = getMaxItemUseDuration(stack) - count;
        int realStart = 15;
        float time = usedTicks < realStart ? usedTicks / (float) realStart : 1.0F;
        float maxDist = 32.0F * time;
        float speed = 1.25F + 0.35F * EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.RAPID_POSSESSION, stack);
        Vec3d targetVec = getControlTarget(world, user, controlled, maxDist);
        if (controlled instanceof EntityLiving) {
            EntityLiving mob = (EntityLiving) controlled;
            mob.getNavigator().tryMoveToXYZ(targetVec.x, targetVec.y, targetVec.z, time * speed);
            if (EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.SIGHTLESS, stack) > 0) {
                controlled.setGlowing(true);
            }
        } else {
            moveControlledEntity(world, controlled, targetVec, speed);
        }
        if (world.isRemote) {
            for (int i = 0; i < 1 + controlled.width * 2.0F; i++) {
                world.spawnParticle(EnumParticleTypes.REDSTONE, controlled.posX + (world.rand.nextFloat() - 0.5F) * controlled.width, controlled.posY + world.rand.nextFloat() * controlled.height, controlled.posZ + (world.rand.nextFloat() - 0.5F) * controlled.width, 1.0D, 0.0D, 0.0D);
            }
        } else {
            directControlledAttacks(stack, user, controlled, usedTicks);
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
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        Multimap<String, AttributeModifier> modifiers = HashMultimap.create(super.getItemAttributeModifiers(slot));
        if (slot == EntityEquipmentSlot.MAINHAND) {
            modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 2.0D, 0));
            modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -2.4D, 0));
        }
        return modifiers;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flagIn) {
        NBTTagCompound entityTag = stack.getSubCompound("BoundEntityTag");
        if (entityTag != null && entityTag.hasKey("id")) {
            tooltip.add(TextFormatting.GRAY + I18n.format(EntityList.getTranslationName(new ResourceLocation(entityTag.getString("id")))));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (isInvalidPossessionTarget(target)) {
            if (attacker instanceof EntityPlayer) {
                ((EntityPlayer) attacker).sendStatusMessage(new TextComponentTranslation("item.alexscaves.totem_of_possession.invalid"), true);
            }
            return true;
        }
        bindToEntity(stack, target);
        setPossessed(target, true);
        attacker.playSound(ACSoundRegistry.TOTEM_OF_POSSESSION_USE, 1.0F, 1.0F);
        stack.damageItem(1, attacker);
        return true;
    }

    @Override
    public void updateTagFromServer(Entity holder, ItemStack stack, NBTTagCompound tag) {
        stack.setTagCompound(tag == null ? null : tag.copy());
    }

    private static void resetBound(ItemStack stack) {
        NBTTagCompound tag = getOrCreateTag(stack);
        tag.removeTag("BoundEntityTag");
        tag.removeTag("BoundEntityUUIDMost");
        tag.removeTag("BoundEntityUUIDLeast");
        tag.removeTag("ControllingEntityID");
    }

    public static UUID getBoundEntityUUID(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasUniqueId("BoundEntityUUID") ? tag.getUniqueId("BoundEntityUUID") : null;
    }

    public static boolean isBound(ItemStack stack) {
        return getBoundEntityUUID(stack) != null;
    }

    private static void updateEntityIdFromServer(WorldServer world, EntityPlayer player, ItemStack stack) {
        UUID uuid = getBoundEntityUUID(stack);
        NBTTagCompound tag = getOrCreateTag(stack);
        int previous = tag.hasKey("ControllingEntityID") ? tag.getInteger("ControllingEntityID") : -1;
        int current = -1;
        if (uuid != null) {
            Entity entity = world.getEntityFromUuid(uuid);
            current = entity == null ? -1 : entity.getEntityId();
        }
        tag.setInteger("ControllingEntityID", current);
        if (previous != current) {
            AlexsCaves.NETWORK_WRAPPER.sendToAll(new UpdateItemTagMessage(player.getEntityId(), stack));
        }
    }

    @Nullable
    private static Entity getControlledEntity(World world, ItemStack stack) {
        if (world.isRemote) {
            NBTTagCompound tag = getOrCreateTag(stack);
            int id = tag.hasKey("ControllingEntityID") ? tag.getInteger("ControllingEntityID") : -1;
            return id == -1 ? null : world.getEntityByID(id);
        }
        if (world instanceof WorldServer) {
            UUID uuid = getBoundEntityUUID(stack);
            return uuid == null ? null : ((WorldServer) world).getEntityFromUuid(uuid);
        }
        return null;
    }

    private static boolean isEntityLookingAt(EntityLivingBase looker, Entity seen, double degree) {
        degree *= 1.0D + looker.getDistance(seen) * 0.1D;
        Vec3d look = looker.getLook(1.0F).normalize();
        Vec3d toSeen = new Vec3d(seen.posX - looker.posX, seen.getEntityBoundingBox().minY + seen.height * 0.5D - (looker.posY + looker.getEyeHeight()), seen.posZ - looker.posZ);
        double distance = toSeen.length();
        if (distance <= 0.0D) {
            return true;
        }
        double dot = look.dotProduct(toSeen.normalize());
        return dot > 1.0D - degree / distance && looker.canEntityBeSeen(seen);
    }

    private static Vec3d getControlTarget(World world, EntityLivingBase user, Entity controlled, float maxDist) {
        Vec3d start = user.getPositionEyes(1.0F);
        Vec3d end = start.add(user.getLookVec().scale(maxDist));
        RayTraceResult block = world.rayTraceBlocks(start, end, false, true, false);
        Vec3d target = block == null ? end : block.hitVec;
        double closest = block == null ? maxDist : start.distanceTo(block.hitVec);
        AxisAlignedBB search = user.getEntityBoundingBox().expand(user.getLookVec().x * maxDist, user.getLookVec().y * maxDist, user.getLookVec().z * maxDist).grow(1.0D);
        for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(user, search)) {
            if (entity == controlled || !entity.canBeCollidedWith()) {
                continue;
            }
            RayTraceResult hit = entity.getEntityBoundingBox().grow(entity.getCollisionBorderSize()).calculateIntercept(start, end);
            if (hit != null) {
                double dist = start.distanceTo(hit.hitVec);
                if (dist < closest) {
                    closest = dist;
                    target = hit.hitVec;
                }
            }
        }
        return target;
    }

    private static void moveControlledEntity(World world, Entity controlled, Vec3d target, float speed) {
        Vec3d delta = target.subtract(controlled.getPositionVector());
        boolean jump = !controlled.isInWater() && controlled.onGround && controlled.collidedHorizontally && delta.y > 0.0D;
        if (!controlled.isInWater() && delta.y > 0.0D) {
            delta = new Vec3d(delta.x, 0.0D, delta.z);
        }
        if (delta.length() > 1.0D) {
            Vec3d normalized = delta.normalize();
            float yaw = -((float) MathHelper.atan2(normalized.x, normalized.z)) * (180.0F / (float) Math.PI);
            if (!world.isRemote) {
                controlled.rotationYaw = yaw;
            }
            Vec3d add = normalized.scale(0.15D * speed);
            if (jump) {
                add = add.add(0.0D, 0.6D, 0.0D);
            }
            controlled.motionX = controlled.motionX * 0.8D + add.x;
            controlled.motionY = controlled.motionY * 0.8D + add.y;
            controlled.motionZ = controlled.motionZ * 0.8D + add.z;
            controlled.velocityChanged = true;
        }
    }

    private static void directControlledAttacks(ItemStack stack, EntityLivingBase user, Entity controlled, int usedTicks) {
        AxisAlignedBB hitBox = controlled.getEntityBoundingBox().grow(3.0D);
        if (!(controlled instanceof EntityLiving) && !(controlled instanceof EntityPlayer)) {
            return;
        }
        for (EntityLivingBase target : controlled.world.getEntitiesWithinAABB(EntityLivingBase.class, hitBox)) {
            if (target == controlled || target == user || target.isOnSameTeam(controlled) || isRidingTogether(target, controlled)) {
                continue;
            }
            if (controlled instanceof EntityLiving) {
                EntityLiving mob = (EntityLiving) controlled;
                mob.setAttackTarget(target);
                if (usedTicks % 4 == 0 && target.getHealth() > mob.getHealth() && !isInvalidPossessionTarget(target) && EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.ASTRAL_TRANSFERRING, stack) > 0) {
                    bindToEntity(stack, target);
                    setPossessed(target, true);
                    user.playSound(ACSoundRegistry.TOTEM_OF_POSSESSION_USE, 1.0F, 1.0F);
                    if (user.world instanceof WorldServer && user instanceof EntityPlayer) {
                        updateEntityIdFromServer((WorldServer) user.world, (EntityPlayer) user, stack);
                    }
                }
            } else {
                ((EntityPlayer) controlled).attackTargetEntityWithCurrentItem(target);
                ((EntityPlayer) controlled).resetCooldown();
            }
        }
    }

    private static void bindToEntity(ItemStack stack, Entity target) {
        NBTTagCompound tag = getOrCreateTag(stack);
        tag.setUniqueId("BoundEntityUUID", target.getUniqueID());
        NBTTagCompound entityTag = new NBTTagCompound();
        ResourceLocation id = EntityList.getKey(target);
        if (id != null) {
            entityTag.setString("id", id.toString());
        }
        if (target instanceof EntityPlayer) {
            entityTag.setUniqueId("UUID", target.getUniqueID());
        } else {
            target.writeToNBTOptional(entityTag);
        }
        tag.setTag("BoundEntityTag", entityTag);
    }

    private static boolean isInvalidPossessionTarget(EntityLivingBase target) {
        return target instanceof EntityArmorStand || target instanceof EntityDragon || target instanceof EntityWither || target instanceof EntityPlayer && !ACConfig.doesTotemOfPossessionWorkOnPlayers();
    }

    private static boolean isRidingTogether(Entity first, Entity second) {
        return first.getRidingEntity() != null && first.getRidingEntity() == second.getRidingEntity();
    }

    private static void setPossessed(@Nullable Entity entity, boolean possessed) {
        if (entity == null) {
            return;
        }
        if (possessed) {
            entity.getEntityData().setBoolean("TotemPossessed", true);
        } else {
            entity.getEntityData().removeTag("TotemPossessed");
        }
    }

    private static NBTTagCompound getOrCreateTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }
}
