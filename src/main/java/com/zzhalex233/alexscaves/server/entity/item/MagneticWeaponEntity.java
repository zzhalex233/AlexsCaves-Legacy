package com.zzhalex233.alexscaves.server.entity.item;

import java.util.UUID;

import com.google.common.collect.Multimap;
import com.zzhalex233.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.zzhalex233.alexscaves.server.entity.living.TeletorEntity;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MagneticWeaponEntity extends Entity {
    private static final DataParameter<ItemStack> ITEMSTACK = EntityDataManager.createKey(MagneticWeaponEntity.class, DataSerializers.ITEM_STACK);
    private static final DataParameter<Integer> CONTROLLER_ID = EntityDataManager.createKey(MagneticWeaponEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TARGET_ID = EntityDataManager.createKey(MagneticWeaponEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> IDLING = EntityDataManager.createKey(MagneticWeaponEntity.class, DataSerializers.BOOLEAN);
    private float prevStrikeProgress;
    private float strikeProgress;
    private float prevReturnProgress;
    private float returnProgress;
    private UUID controllerUUID;
    private boolean comingBack;
    private int playerUseCooldown;
    private float destroyBlockProgress;
    private BlockPos lastSelectedBlock;
    private int totalMiningTime;
    private boolean hadPlayerController;
    private boolean spawnedItem;

    public MagneticWeaponEntity(World world) {
        super(world);
        setSize(0.5F, 0.5F);
        noClip = true;
    }

    @Override
    protected void entityInit() {
        dataManager.register(ITEMSTACK, new ItemStack(Items.IRON_SWORD));
        dataManager.register(CONTROLLER_ID, -1);
        dataManager.register(TARGET_ID, -1);
        dataManager.register(IDLING, true);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        prevStrikeProgress = strikeProgress;
        prevReturnProgress = returnProgress;
        Entity controller = getController();
        Entity target = getTarget();
        if (!world.isRemote) {
            if (controller == null && ticksExisted > 20 || getItemStack().isEmpty()) {
                setDead();
                return;
            }
            if (controller instanceof TeletorEntity) {
                TeletorEntity teletor = (TeletorEntity) controller;
                dataManager.set(CONTROLLER_ID, teletor.getEntityId());
                teletor.setWeapon(this);
                EntityLivingBase attackTarget = teletor.getAttackTarget();
                dataManager.set(TARGET_ID, attackTarget != null && attackTarget.isEntityAlive() ? attackTarget.getEntityId() : -1);
                target = getTarget();
            }
        }
        if (controller instanceof TeletorEntity) {
            tickTeletorWeapon((TeletorEntity) controller, target);
        } else if (controller instanceof EntityPlayer) {
            tickPlayerWeapon((EntityPlayer) controller);
        } else if (!world.isRemote && ticksExisted > 20) {
            if (hadPlayerController) {
                plopItem();
                return;
            }
            setDead();
        }
        if (playerUseCooldown > 0) {
            playerUseCooldown--;
        }
        move(MoverType.SELF, motionX, motionY, motionZ);
        motionX *= 0.9D;
        motionY *= 0.9D;
        motionZ *= 0.9D;
    }

    private void tickPlayerWeapon(EntityPlayer player) {
        dataManager.set(CONTROLLER_ID, player.getEntityId());
        dataManager.set(IDLING, false);
        hadPlayerController = true;
        boolean wearingGauntlet = player.getActiveItemStack().getItem() == ACItemRegistry.GALENA_GAUNTLET.item() && player.isEntityAlive();
        comingBack = !wearingGauntlet;
        if (comingBack) {
            if (returnProgress < 1.0F) {
                returnProgress = Math.min(1.0F, returnProgress + 0.2F);
            }
            directMovementTowards(player.getPositionVector().add(0.0D, 1.0D, 0.0D), 0.14F);
            if (!world.isRemote && getDistance(player) < 1.4F) {
                ItemStack stack = getItemStack();
                if (!stack.isEmpty() && !spawnedItem && player.inventory.addItemStackToInventory(stack.copy())) {
                    spawnedItem = true;
                    setDead();
                } else {
                    plopItem();
                }
            }
            return;
        }
        ItemStack gauntlet = player.getActiveItemStack();
        int fieldExtension = EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.FIELD_EXTENSION, gauntlet);
        boolean haste = EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.FERROUS_HASTE, gauntlet) > 0;
        double maxDist = 30.0D + fieldExtension * 5.0D;
        Vec3d eyes = player.getPositionEyes(1.0F);
        Vec3d look = player.getLookVec();
        Vec3d rayEnd = eyes.add(look.scale(maxDist));
        RayTraceResult hit = world.rayTraceBlocks(eyes, rayEnd, false, true, false);
        double blockHitDist = hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK ? eyes.squareDistanceTo(hit.hitVec) : maxDist * maxDist;
        EntityLivingBase target = findLookTarget(player, maxDist, blockHitDist);
        BlockPos miningBlock = null;
        if (target != null && playerUseCooldown <= 0) {
            Vec3d moveTo = target.getPositionVector().add(0.0D, target.height * 0.55D, 0.0D);
            directMovementTowards(moveTo, 0.2F);
            if (getDistance(target) < target.width + 1.5F) {
                if (strikeProgress < 1.0F) {
                    strikeProgress = Math.min(1.0F, strikeProgress + 0.35F);
                } else if (!world.isRemote) {
                    hurtEntity(player, target);
                    playerUseCooldown = haste ? 3 : 5 + rand.nextInt(5);
                    strikeProgress = 0.0F;
                }
            }
        } else {
            if (strikeProgress > 0.0F) {
                strikeProgress = Math.max(0.0F, strikeProgress - 0.1F);
            }
            Vec3d moveTo = rayEnd;
            if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
                moveTo = hit.hitVec;
                if (getDistanceSqToCenter(hit.getBlockPos()) < 2.25D && !world.getBlockState(hit.getBlockPos()).getBlock().isAir(world.getBlockState(hit.getBlockPos()), world, hit.getBlockPos())) {
                    miningBlock = hit.getBlockPos();
                }
            }
            directMovementTowards(moveTo, 0.1F);
        }
        tickPlayerMining(player, miningBlock, haste);
    }

    private void tickTeletorWeapon(TeletorEntity teletor, Entity target) {
        boolean attacking = !comingBack && target instanceof EntityLivingBase && target.isEntityAlive();
        Vec3d moveTo = attacking ? target.getPositionVector().add(0.0D, target.height * 0.55D, 0.0D) : teletor.getWeaponPosition();
        Vec3d want = moveTo.subtract(getPositionVector());
        dataManager.set(IDLING, !attacking);
        if (attacking) {
            if (want.length() < target.width + 1.0F) {
                if (strikeProgress < 1.0F) {
                    strikeProgress = Math.min(1.0F, strikeProgress + 0.35F);
                } else if (!world.isRemote) {
                    hurtEntity(teletor, target);
                    comingBack = true;
                    strikeProgress = 0.0F;
                }
            } else if (want.length() > 32.0D) {
                comingBack = true;
            }
        } else if (strikeProgress > 0.0F) {
            strikeProgress = Math.max(0.0F, strikeProgress - 0.1F);
        }
        directMovementTowards(moveTo, 0.1F);
        if (getDistance(teletor) < 2.5F && posY > teletor.posY) {
            comingBack = false;
        }
    }

    private void directMovementTowards(Vec3d moveTo, float speed) {
        Vec3d want = moveTo.subtract(getPositionVector());
        if (want.length() > 1.0D) {
            want = want.normalize();
        }
        float targetPitch = (float) (-(MathHelper.atan2(want.y, Math.sqrt(want.x * want.x + want.z * want.z)) * 57.2957763671875D));
        float targetYaw = (float) (-MathHelper.atan2(want.x, want.z) * 57.2957763671875D);
        if (isIdling()) {
            targetPitch = rotationPitch;
            targetYaw = rotationYaw + 5.0F;
        }
        rotationPitch = approachDegrees(rotationPitch, targetPitch, 5.0F);
        rotationYaw = approachDegrees(rotationYaw, targetYaw, 5.0F);
        motionX += want.x * speed;
        motionY += want.y * speed;
        motionZ += want.z * speed;
    }

    private float approachDegrees(float current, float target, float max) {
        float delta = MathHelper.wrapDegrees(target - current);
        return current + MathHelper.clamp(delta, -max, max);
    }

    private void hurtEntity(EntityLivingBase holder, Entity target) {
        ItemStack stack = getItemStack();
        float damage = (float) holder.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() + (float) getDamageForItem(stack);
        if (target.attackEntityFrom(DamageSource.causeMobDamage(holder), Math.max(2.0F, damage))) {
            damageItem(1, holder);
        }
    }

    private void tickPlayerMining(EntityPlayer player, BlockPos miningBlock, boolean haste) {
        if (miningBlock == null) {
            clearSelectedBlock(player);
            return;
        }
        if (lastSelectedBlock == null || !lastSelectedBlock.equals(miningBlock)) {
            clearSelectedBlock(player);
            lastSelectedBlock = miningBlock;
            destroyBlockProgress = 0.0F;
            totalMiningTime = 0;
        }
        IBlockState state = world.getBlockState(miningBlock);
        float hardness = state.getBlockHardness(world, miningBlock);
        float digSpeed = getDigSpeed(player, state);
        if (hardness < 0.0F || digSpeed <= 1.0F || state.getMaterial().isLiquid()) {
            return;
        }
        if (totalMiningTime++ % 4 == 0) {
            playSound(state.getBlock().getSoundType(state, world, miningBlock, this).getHitSound(), 0.4F, 0.7F);
        }
        strikeProgress = Math.abs(MathHelper.sin(ticksExisted * 0.6F) * 1.2F - 0.2F);
        destroyBlockProgress += digSpeed / hardness / (haste ? 8.0F : 10.0F);
        world.sendBlockBreakProgress(player.getEntityId(), miningBlock, (int) (destroyBlockProgress * 10.0F));
        if (destroyBlockProgress >= 1.0F && !world.isRemote) {
            ItemStack stack = getItemStack();
            TileEntity tile = world.getTileEntity(miningBlock);
            boolean harvested = state.getBlock().removedByPlayer(state, world, miningBlock, player, true);
            if (harvested) {
                state.getBlock().onPlayerDestroy(world, miningBlock, state);
                state.getBlock().harvestBlock(world, player, miningBlock, state, tile, stack);
                damageItem(1, player);
            }
            destroyBlockProgress = 0.0F;
            totalMiningTime = 0;
        }
    }

    private float getDigSpeed(EntityPlayer player, IBlockState state) {
        ItemStack stack = getItemStack();
        float speed = stack.getDestroySpeed(state);
        if (speed > 1.0F) {
            int efficiency = EnchantmentHelper.getEfficiencyModifier(player);
            if (efficiency > 0) {
                speed += efficiency * efficiency + 1;
            }
        }
        if (player.isPotionActive(MobEffects.HASTE)) {
            speed *= 1.0F + (player.getActivePotionEffect(MobEffects.HASTE).getAmplifier() + 1) * 0.2F;
        }
        if (player.isPotionActive(MobEffects.MINING_FATIGUE)) {
            switch (player.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
                case 0:
                    speed *= 0.3F;
                    break;
                case 1:
                    speed *= 0.09F;
                    break;
                case 2:
                    speed *= 0.0027F;
                    break;
                default:
                    speed *= 8.1E-4F;
                    break;
            }
        }
        if (player.isInsideOfMaterial(net.minecraft.block.material.Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier(player)) {
            speed /= 5.0F;
        }
        if (!player.onGround) {
            speed /= 5.0F;
        }
        return speed;
    }

    private void clearSelectedBlock(EntityPlayer player) {
        if (lastSelectedBlock != null) {
            world.sendBlockBreakProgress(player.getEntityId(), lastSelectedBlock, -1);
            lastSelectedBlock = null;
        }
        destroyBlockProgress = 0.0F;
        totalMiningTime = 0;
    }

    private void damageItem(int amount, EntityLivingBase holder) {
        ItemStack stack = getItemStack();
        if (stack.isItemStackDamageable() && !(holder instanceof EntityPlayer && ((EntityPlayer) holder).capabilities.isCreativeMode)) {
            stack.damageItem(amount, holder);
            setItemStack(stack);
            if (stack.getItemDamage() >= stack.getMaxDamage()) {
                setDead();
            }
        }
    }

    private EntityLivingBase findLookTarget(EntityPlayer player, double maxDist, double maxDistSq) {
        Vec3d eyes = player.getPositionEyes(1.0F);
        Vec3d end = eyes.add(player.getLookVec().scale(maxDist));
        EntityLivingBase best = null;
        double bestDist = maxDistSq;
        for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, player.getEntityBoundingBox().grow(maxDist))) {
            if (entity == player || !entity.isEntityAlive() || player.isOnSameTeam(entity)) {
                continue;
            }
            RayTraceResult intercept = entity.getEntityBoundingBox().grow(0.4D).calculateIntercept(eyes, end);
            if (intercept != null) {
                double dist = eyes.squareDistanceTo(intercept.hitVec);
                if (dist < bestDist) {
                    bestDist = dist;
                    best = entity;
                }
            }
        }
        return best;
    }

    private void plopItem() {
        if (getController() instanceof EntityPlayer) {
            clearSelectedBlock((EntityPlayer) getController());
        }
        if (!spawnedItem && !world.isRemote) {
            spawnedItem = true;
            EntityItem item = entityDropItem(getItemStack(), 0.0F);
            if (item != null) {
                item.setNoPickupDelay();
            }
        }
        setDead();
    }

    private double getDamageForItem(ItemStack stack) {
        Multimap<String, AttributeModifier> modifiers = stack.getAttributeModifiers(net.minecraft.inventory.EntityEquipmentSlot.MAINHAND);
        double damage = 0.0D;
        for (AttributeModifier modifier : modifiers.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) {
            damage += modifier.getAmount();
        }
        return damage;
    }

    public void setItemStack(ItemStack stack) {
        dataManager.set(ITEMSTACK, stack.copy());
    }

    public ItemStack getItemStack() {
        return dataManager.get(ITEMSTACK);
    }

    public void setController(Entity entity) {
        controllerUUID = entity == null ? null : entity.getUniqueID();
        dataManager.set(CONTROLLER_ID, entity == null ? -1 : entity.getEntityId());
    }

    public Entity getController() {
        if (world.isRemote) {
            int id = dataManager.get(CONTROLLER_ID);
            return id == -1 ? null : world.getEntityByID(id);
        }
        if (controllerUUID == null) {
            return null;
        }
        for (Entity entity : world.loadedEntityList) {
            if (controllerUUID.equals(entity.getUniqueID())) {
                return entity;
            }
        }
        return null;
    }

    public Entity getTarget() {
        int id = dataManager.get(TARGET_ID);
        return id == -1 ? null : world.getEntityByID(id);
    }

    public boolean isIdling() {
        return dataManager.get(IDLING);
    }

    public float getStrikeProgress(float partialTick) {
        return prevStrikeProgress + (strikeProgress - prevStrikeProgress) * partialTick;
    }

    public float getReturnProgress(float partialTick) {
        return prevReturnProgress + (returnProgress - prevReturnProgress) * partialTick;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("WeaponStack")) {
            setItemStack(new ItemStack(compound.getCompoundTag("WeaponStack")));
        }
        if (compound.hasUniqueId("ControllerUUID")) {
            controllerUUID = compound.getUniqueId("ControllerUUID");
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        if (!getItemStack().isEmpty()) {
            NBTTagCompound stackTag = new NBTTagCompound();
            getItemStack().writeToNBT(stackTag);
            compound.setTag("WeaponStack", stackTag);
        }
        if (controllerUUID != null) {
            compound.setUniqueId("ControllerUUID", controllerUUID);
        }
    }
}
