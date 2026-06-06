package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.block.AbyssalAltarBlock;
import com.zzhalex233.alexscaves.server.block.entity.AbyssalAltarTileEntity;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.level.storage.ACWorldData;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

import java.util.List;
import java.util.UUID;

public abstract class DeepOneBaseEntity extends EntityMob {
    private static final DataParameter<Boolean> SWIMMING = EntityDataManager.createKey(DeepOneBaseEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ANGRY = EntityDataManager.createKey(DeepOneBaseEntity.class, DataSerializers.BOOLEAN);
    protected int attackCooldown;
    protected float prevFishPitch;
    protected float fishPitch;
    private BlockPos lastAltarPos;
    private ItemStack tradedStack = ItemStack.EMPTY;
    private int tradingTime;

    protected DeepOneBaseEntity(World world) {
        super(world);
        setSize(0.9F, 1.95F);
        experienceValue = 8;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SWIMMING, false);
        dataManager.register(ANGRY, false);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new DeepOneAttackAI());
        tasks.addTask(1, new DeepOneBarterAI());
        tasks.addTask(2, new DeepOneWanderAI());
        tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0F));
        tasks.addTask(4, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(getBaseHealth());
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(getBaseDamage());
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
    }

    protected abstract double getBaseHealth();

    protected abstract double getBaseDamage();

    protected abstract SoundEvent getAttackSound();

    protected int getAttackInterval() {
        return 28;
    }

    @Override
    public void onLivingUpdate() {
        prevFishPitch = fishPitch;
        super.onLivingUpdate();
        boolean swimming = isInWater();
        setDeepOneSwimming(swimming);
        if (swimming) {
            motionY += 0.005D;
            fishPitch = approach(fishPitch, (float) MathHelper.clamp(motionY * -80.0D, -70.0D, 70.0D), 5.0F);
        } else {
            fishPitch = approach(fishPitch, 0.0F, 8.0F);
        }
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        if (!world.isRemote && tradingTime > 0) {
            updateTrading();
        }
        setSoundsAngry(getAttackTarget() != null);
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (isInWater()) {
            moveRelative(strafe, vertical, forward, 0.06F);
            move(MoverType.SELF, motionX, motionY, motionZ);
            motionX *= 0.8D;
            motionY *= 0.8D;
            motionZ *= 0.8D;
        } else {
            super.travel(strafe, vertical, forward);
        }
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    protected void startAttackBehavior(EntityLivingBase target) {
        double distance = getDistance(target);
        float reach = width + target.width + 0.7F;
        if (distance <= reach && attackCooldown <= 0) {
            attackCooldown = getAttackInterval();
            playSound(getAttackSound(), 1.0F, getSoundPitch());
            attackEntityAsMob(target);
        } else if (distance > reach) {
            getNavigator().tryMoveToEntityLiving(target, isInWater() ? 1.25D : 1.0D);
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        boolean attacked = entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
        if (attacked && entity instanceof EntityLivingBase) {
            float yaw = rotationYaw * 0.017453292F;
            entity.addVelocity(-MathHelper.sin(yaw) * 0.35D, 0.1D, MathHelper.cos(yaw) * 0.35D);
        }
        return attacked;
    }

    public boolean isDeepOneSwimming() {
        return dataManager.get(SWIMMING);
    }

    public void setDeepOneSwimming(boolean swimming) {
        dataManager.set(SWIMMING, swimming);
    }

    public boolean soundsAngry() {
        return dataManager.get(ANGRY);
    }

    public void setSoundsAngry(boolean angry) {
        dataManager.set(ANGRY, angry);
    }

    public float getFishPitch(float partialTicks) {
        return prevFishPitch + (fishPitch - prevFishPitch) * partialTicks;
    }

    protected float approach(float value, float target, float step) {
        if (value < target) {
            return Math.min(target, value + step);
        }
        return value > target ? Math.max(target, value - step) : value;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return soundsAngry() ? getHostileSound() : getIdleSound();
    }

    protected abstract SoundEvent getIdleSound();

    protected abstract SoundEvent getHostileSound();

    private void updateTrading() {
        tradingTime--;
        if (lastAltarPos == null) {
            tradedStack = ItemStack.EMPTY;
            return;
        }
        getLookHelper().setLookPosition(lastAltarPos.getX() + 0.5D, lastAltarPos.getY() + 1.0D, lastAltarPos.getZ() + 0.5D, 10.0F, getVerticalFaceSpeed());
        if (tradingTime == 14) {
            TileEntityLookup lookup = altarAt(lastAltarPos);
            if (lookup != null && lookup.altar.getStackInSlot(0).isEmpty()) {
                ItemStack loot = generateBarterLoot();
                lookup.altar.setInventorySlotContents(0, loot);
                lookup.altar.onEntityInteract(this, false);
            } else if (!tradedStack.isEmpty()) {
                entityDropItem(tradedStack.copy(), 0.0F);
            }
            tradedStack = ItemStack.EMPTY;
        }
    }

    private ItemStack generateBarterLoot() {
        if (world instanceof WorldServer) {
            LootTable table = ((WorldServer) world).getLootTableManager().getLootTableFromLocation(getBarterLootTable());
            LootContext context = new LootContext(0.0F, (WorldServer) world, ((WorldServer) world).getLootTableManager(), this, null, null);
            List<ItemStack> loot = table.generateLootForPools(rand, context);
            if (!loot.isEmpty()) {
                return loot.get(0);
            }
        }
        return new ItemStack(Items.PRISMARINE_SHARD, 4 + rand.nextInt(8));
    }

    protected ResourceLocation getBarterLootTable() {
        if (this instanceof DeepOneMageEntity) {
            return new ResourceLocation(AlexsCaves.MODID, "gameplay/deep_one_mage_barter");
        }
        if (this instanceof DeepOneKnightEntity) {
            return new ResourceLocation(AlexsCaves.MODID, "gameplay/deep_one_knight_barter");
        }
        return new ResourceLocation(AlexsCaves.MODID, "gameplay/deep_one_barter");
    }

    private static boolean isDeepOneBarter(ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() == ACItemRegistry.PEARL.item() || stack.getItem() == Items.PRISMARINE_CRYSTALS);
    }

    private TileEntityLookup altarAt(BlockPos pos) {
        if (pos == null || !(world.getBlockState(pos).getBlock() instanceof AbyssalAltarBlock) || !(world.getTileEntity(pos) instanceof AbyssalAltarTileEntity)) {
            return null;
        }
        return new TileEntityLookup((AbyssalAltarTileEntity) world.getTileEntity(pos));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (lastAltarPos != null) {
            compound.setInteger("AltarX", lastAltarPos.getX());
            compound.setInteger("AltarY", lastAltarPos.getY());
            compound.setInteger("AltarZ", lastAltarPos.getZ());
        }
        if (!tradedStack.isEmpty()) {
            compound.setTag("TradedStack", tradedStack.writeToNBT(new NBTTagCompound()));
        }
        compound.setInteger("TradingTime", tradingTime);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("AltarX") && compound.hasKey("AltarY") && compound.hasKey("AltarZ")) {
            lastAltarPos = new BlockPos(compound.getInteger("AltarX"), compound.getInteger("AltarY"), compound.getInteger("AltarZ"));
        }
        tradedStack = compound.hasKey("TradedStack", 10) ? new ItemStack(compound.getCompoundTag("TradedStack")) : ItemStack.EMPTY;
        tradingTime = compound.getInteger("TradingTime");
    }

    private class DeepOneAttackAI extends EntityAIBase {
        private DeepOneAttackAI() {
            setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            EntityLivingBase target = getAttackTarget();
            return target != null && target.isEntityAlive();
        }

        @Override
        public void updateTask() {
            EntityLivingBase target = getAttackTarget();
            if (target != null) {
                faceEntity(target, 30.0F, 30.0F);
                startAttackBehavior(target);
            }
        }
    }

    private class DeepOneWanderAI extends EntityAIBase {
        private int cooldown;

        private DeepOneWanderAI() {
            setMutexBits(1);
        }

        @Override
        public boolean shouldExecute() {
            return getAttackTarget() == null && --cooldown <= 0;
        }

        @Override
        public void updateTask() {
            cooldown = 60 + rand.nextInt(60);
            if (isInWater()) {
                Vec3d target = getPositionVector().add(rand.nextDouble() * 12.0D - 6.0D, rand.nextDouble() * 4.0D - 2.0D, rand.nextDouble() * 12.0D - 6.0D);
                getMoveHelper().setMoveTo(target.x, target.y, target.z, 1.0D);
            } else {
                getNavigator().tryMoveToXYZ(posX + rand.nextDouble() * 12.0D - 6.0D, posY, posZ + rand.nextDouble() * 12.0D - 6.0D, 1.0D);
            }
        }
    }

    private class DeepOneBarterAI extends EntityAIBase {
        private BlockPos altarPos;
        private int cooldown = 20;

        private DeepOneBarterAI() {
            setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            if (getAttackTarget() != null || tradingTime > 0 || --cooldown > 0) {
                return false;
            }
            cooldown = 100 + rand.nextInt(80);
            altarPos = findNearbyAltar();
            return altarPos != null;
        }

        @Override
        public boolean shouldContinueExecuting() {
            TileEntityLookup lookup = altarAt(altarPos);
            return lookup != null && isDeepOneBarter(lookup.altar.getStackInSlot(0)) && getAttackTarget() == null && tradingTime <= 0 && getDistanceSqToCenter(altarPos) < 80.0D;
        }

        @Override
        public void resetTask() {
            altarPos = null;
        }

        @Override
        public void updateTask() {
            getLookHelper().setLookPosition(altarPos.getX() + 0.5D, altarPos.getY() + 1.0D, altarPos.getZ() + 0.5D, 10.0F, getVerticalFaceSpeed());
            if (getDistanceSqToCenter(altarPos) > 9.0D) {
                getNavigator().tryMoveToXYZ(altarPos.getX() + 0.5D, altarPos.getY(), altarPos.getZ() + 0.5D, 1.0D);
                return;
            }
            TileEntityLookup lookup = altarAt(altarPos);
            if (lookup != null && lookup.altar.queueItemDrop(lookup.altar.getStackInSlot(0).copy())) {
                tradedStack = lookup.altar.getStackInSlot(0).copy();
                lookup.altar.onEntityInteract(DeepOneBaseEntity.this, true);
                lookup.altar.setInventorySlotContents(0, ItemStack.EMPTY);
                lastAltarPos = altarPos;
                tradingTime = 40;
                addReputation(lookup.altar, 5);
                getNavigator().clearPath();
            }
        }

        private BlockPos findNearbyAltar() {
            if (lastAltarPos != null && isValidAltar(lastAltarPos)) {
                return lastAltarPos;
            }
            BlockPos origin = getPosition();
            BlockPos best = null;
            double bestDistance = Double.MAX_VALUE;
            for (BlockPos pos : BlockPos.getAllInBoxMutable(origin.add(-16, -8, -16), origin.add(16, 8, 16))) {
                if (isValidAltar(pos)) {
                    double distance = getDistanceSqToCenter(pos);
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        best = pos.toImmutable();
                    }
                }
            }
            return best;
        }

        private boolean isValidAltar(BlockPos pos) {
            TileEntityLookup lookup = altarAt(pos);
            return lookup != null && isDeepOneBarter(lookup.altar.getStackInSlot(0)) && !world.getBlockState(pos).getValue(AbyssalAltarBlock.ACTIVE) && world.getTotalWorldTime() - lookup.altar.getLastInteractionTime() >= 40L;
        }

        private void addReputation(AbyssalAltarTileEntity altar, int amount) {
            ACWorldData data = ACWorldData.get(world);
            UUID uuid = altar.getPlacingPlayer();
            if (data != null && uuid != null) {
                data.setDeepOneReputation(uuid, data.getDeepOneReputation(uuid) + amount);
            }
        }
    }

    private static class TileEntityLookup {
        private final AbyssalAltarTileEntity altar;

        private TileEntityLookup(AbyssalAltarTileEntity altar) {
            this.altar = altar;
        }
    }
}
