package com.zzhalex233.alexscaves.server.entity.living;

import java.util.List;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

public class SeaPigEntity extends BucketableWaterMob {
    private static final DataParameter<ItemStack> DIGESTING_ITEM = EntityDataManager.createKey(SeaPigEntity.class, DataSerializers.ITEM_STACK);
    private static final ResourceLocation DIGESTION_LOOT_TABLE = new ResourceLocation(AlexsCaves.MODID, "gameplay/sea_pig_digestion");

    private float digestProgress;
    private float prevDigestProgress;
    private float squishProgress;
    private float prevSquishProgress;
    private int swimChangeCooldown;
    private double swimVecX;
    private double swimVecY;
    private double swimVecZ;

    public SeaPigEntity(World world) {
        super(world);
        setSize(0.5F, 0.65F);
        experienceValue = 1;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(DIGESTING_ITEM, ItemStack.EMPTY);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(2, new EntityAILookIdle(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.05D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        prevDigestProgress = digestProgress;
        prevSquishProgress = squishProgress;
        updateDigestion();

        boolean grounded = onGround && !isInWater();
        if (grounded && squishProgress < 5.0F) {
            squishProgress++;
        }
        if (!grounded && squishProgress > 0.0F) {
            squishProgress--;
        }

        if (!world.isRemote) {
            if (isInWater()) {
                setAir(300);
                updateSwimmingVector();
            } else {
                handleDryingOut();
            }
        }
        if (isInWater()) {
            motionX += swimVecX * 0.015D;
            motionY += swimVecY * 0.012D - 0.015D;
            motionZ += swimVecZ * 0.015D;
            if (collidedHorizontally) {
                motionY += 0.03D;
            }
            motionX *= 0.85D;
            motionY *= 0.85D;
            motionZ *= 0.85D;
            if (motionX * motionX + motionZ * motionZ > 0.001D) {
                rotationYaw = (float) (-Math.atan2(motionX, motionZ) * (180D / Math.PI));
                renderYawOffset = rotationYaw;
            }
        }
    }

    private void updateSwimmingVector() {
        if (--swimChangeCooldown > 0) {
            return;
        }
        swimChangeCooldown = 30 + rand.nextInt(50);
        double angle = rand.nextDouble() * Math.PI * 2.0D;
        swimVecX = Math.cos(angle) * 0.5D;
        swimVecY = rand.nextDouble() * 0.4D - 0.1D;
        swimVecZ = Math.sin(angle) * 0.5D;
        BlockPos below = getPosition().down();
        while (below.getY() > 1 && world.getBlockState(below).getMaterial().isLiquid()) {
            below = below.down();
        }
        if (posY - below.getY() > 2.0D) {
            swimVecY -= 0.4D;
        }
    }

    private void handleDryingOut() {
        setAir(getAir() - 1);
        if (onGround) {
            motionX += (rand.nextFloat() * 2.0F - 1.0F) * 0.1F;
            motionY += 0.25D;
            motionZ += (rand.nextFloat() * 2.0F - 1.0F) * 0.1F;
        }
        if (getAir() <= -20) {
            setAir(0);
            attackEntityFrom(DamageSource.DROWN, 2.0F);
        }
    }

    private void updateDigestion() {
        if (!isDigesting()) {
            digestProgress = 0.0F;
            prevDigestProgress = 0.0F;
            return;
        }
        if (digestProgress == 0.0F) {
            playSound(ACSoundRegistry.SEA_PIG_EAT, 1.0F, getSoundPitch());
        }
        digestProgress += 0.05F;
        if (digestProgress >= 1.0F) {
            digestProgress = 0.0F;
            prevDigestProgress = 0.0F;
            digestItem();
        }
    }

    private void digestItem() {
        if (!world.isRemote && world instanceof WorldServer) {
            WorldServer serverWorld = (WorldServer) world;
            LootTable table = serverWorld.getLootTableManager().getLootTableFromLocation(DIGESTION_LOOT_TABLE);
            LootContext context = new LootContext(0.0F, serverWorld, serverWorld.getLootTableManager(), this, null, null);
            List<ItemStack> items = table.generateLootForPools(rand, context);
            for (ItemStack stack : items) {
                entityDropItem(stack, 0.0F);
            }
        }
        setDigestingItem(ItemStack.EMPTY);
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack held = player.getHeldItem(hand);
        if (!held.isEmpty() && canDigest(held) && !isDigesting()) {
            if (!world.isRemote) {
                ItemStack copy = held.copy();
                copy.setCount(1);
                setDigestingItem(copy);
                if (!player.capabilities.isCreativeMode) {
                    held.shrink(1);
                }
            }
            return true;
        }
        return super.processInteract(player, hand);
    }

    private boolean canDigest(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.CLAY_BALL || item == Item.getItemFromBlock(ACBlockRegistry.MUCK.block());
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (isDigesting()) {
            compound.setTag("DigestingItem", getDigestingItem().writeToNBT(new NBTTagCompound()));
            compound.setFloat("DigestProgress", digestProgress);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("DigestingItem")) {
            setDigestingItem(new ItemStack(compound.getCompoundTag("DigestingItem")));
            digestProgress = compound.getFloat("DigestProgress");
            prevDigestProgress = digestProgress;
        }
    }

    public boolean isDigesting() {
        return !getDigestingItem().isEmpty();
    }

    public ItemStack getDigestingItem() {
        return dataManager.get(DIGESTING_ITEM);
    }

    private void setDigestingItem(ItemStack stack) {
        dataManager.set(DIGESTING_ITEM, stack);
    }

    public float getDigestProgress(float partialTicks) {
        return Math.min(1.0F, prevDigestProgress + (digestProgress - prevDigestProgress) * partialTicks);
    }

    public float getSquishProgress(float partialTicks) {
        return (prevSquishProgress + (squishProgress - prevSquishProgress) * partialTicks) * 0.2F;
    }

    @Override
    protected Item getBucketItem() {
        return ACItemRegistry.SEA_PIG_BUCKET.item();
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    protected int decreaseAirSupply(int air) {
        return air;
    }

    @Override
    public boolean getCanSpawnHere() {
        return isInWater() && isNotColliding() && posY < world.getSeaLevel() - 25;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.SEA_PIG_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.SEA_PIG_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.SEA_PIG_DEATH;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        dropItem(ACItemRegistry.SEA_PIG.item(), 1);
    }
}
