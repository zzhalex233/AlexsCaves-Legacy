package com.zzhalex233.alexscaves.server.entity.item;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.entity.util.KeybindUsingMount;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.message.MountedEntityKeyMessage;
import com.zzhalex233.alexscaves.server.misc.ACMath;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class SubmarineEntity extends Entity implements KeybindUsingMount {
    private static final DataParameter<Float> RIGHT_PROPELLER_ROT = EntityDataManager.createKey(SubmarineEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> LEFT_PROPELLER_ROT = EntityDataManager.createKey(SubmarineEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> BACK_PROPELLER_ROT = EntityDataManager.createKey(SubmarineEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> ACCELERATION = EntityDataManager.createKey(SubmarineEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> LIGHTS = EntityDataManager.createKey(SubmarineEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> WAXED = EntityDataManager.createKey(SubmarineEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> OXIDIZATION_LEVEL = EntityDataManager.createKey(SubmarineEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> DAMAGE_LEVEL = EntityDataManager.createKey(SubmarineEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> DANGER_ALERT_TICKS = EntityDataManager.createKey(SubmarineEntity.class, DataSerializers.VARINT);
    private static final float TOP_SPEED = 0.65F;

    private float prevLeftPropellerRot;
    private float prevRightPropellerRot;
    private float prevBackPropellerRot;
    private int controlUpTicks;
    private int controlDownTicks;
    private int turnRightTicks;
    private int turnLeftTicks;
    private int floodlightToggleCooldown;
    private double damageSustained;
    private int oxidizeTime = 24000 * (2 + rand.nextInt(2));
    public int submergedTicks;
    public int shakeTime;
    private float prevSonarFlashAmount;
    private float sonarFlashAmount;
    private int creakTime;
    private boolean wereLightsOn;

    public SubmarineEntity(World world) {
        super(world);
        setSize(2.8F, 1.8F);
    }

    @Override
    protected void entityInit() {
        dataManager.register(RIGHT_PROPELLER_ROT, 0.0F);
        dataManager.register(LEFT_PROPELLER_ROT, 0.0F);
        dataManager.register(BACK_PROPELLER_ROT, 0.0F);
        dataManager.register(ACCELERATION, 0.0F);
        dataManager.register(LIGHTS, false);
        dataManager.register(WAXED, false);
        dataManager.register(OXIDIZATION_LEVEL, 0);
        dataManager.register(DAMAGE_LEVEL, 0);
        dataManager.register(DANGER_ALERT_TICKS, 0);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        float leftPropellerRot = getLeftPropellerRot();
        float rightPropellerRot = getRightPropellerRot();
        float backPropellerRot = getBackPropellerRot();
        if (controlDownTicks > 0 || getDamageLevel() >= 4 && !onGround) {
            motionY -= 0.08D;
            controlDownTicks--;
        } else if (controlUpTicks > 0 && getWaterHeight() > 1.5F) {
            motionY += 0.08D;
            controlUpTicks--;
        }
        if (ticksExisted % 200 == 0 && damageSustained > 0.0D) {
            damageSustained--;
        }
        prevSonarFlashAmount = sonarFlashAmount;
        if (getDangerAlertTicks() > 0 && sonarFlashAmount < 1.0F) {
            sonarFlashAmount += 0.25F;
        }
        if (getDangerAlertTicks() <= 0 && sonarFlashAmount > 0.0F) {
            sonarFlashAmount -= 0.25F;
        }
        if (getDangerAlertTicks() > 0 && getDamageLevel() <= 3 && isBeingRidden() && ticksExisted % 20 == 0) {
            playSound(ACSoundRegistry.SUBMARINE_SONAR, 1.0F, 1.0F);
        }
        if (getDamageLevel() > 0 && isBeingRidden() && creakTime-- <= 0) {
            creakTime = 500 - getDamageLevel() * 120 + rand.nextInt(60);
            playSound(ACSoundRegistry.SUBMARINE_CREAK, 1.0F, 1.0F);
        }
        float acceleration = getAcceleration();
        if (world.isRemote) {
            EntityPlayer player = AlexsCaves.PROXY.getClientSidePlayer();
            if (player != null && player.getRidingEntity() == this) {
                if (AlexsCaves.PROXY.isKeyDown(0) && controlUpTicks < 2) {
                    AlexsCaves.NETWORK_WRAPPER.sendToServer(new MountedEntityKeyMessage(getEntityId(), player.getEntityId(), 0));
                    controlUpTicks = 10;
                }
                if (AlexsCaves.PROXY.isKeyDown(1) && controlDownTicks < 2) {
                    AlexsCaves.NETWORK_WRAPPER.sendToServer(new MountedEntityKeyMessage(getEntityId(), player.getEntityId(), 1));
                    controlDownTicks = 10;
                }
                if (AlexsCaves.PROXY.isKeyDown(2) && floodlightToggleCooldown <= 0) {
                    AlexsCaves.NETWORK_WRAPPER.sendToServer(new MountedEntityKeyMessage(getEntityId(), player.getEntityId(), 2));
                    floodlightToggleCooldown = 5;
                }
            }
            if (isBeingRidden() && isInWater() && !isDead) {
                AlexsCaves.PROXY.playSubmarineSound(this);
            }
        } else {
            if (acceleration < 0.0F) {
                setAcceleration(Math.min(0.0F, acceleration + 0.01F));
            } else if (acceleration > 0.0F) {
                setAcceleration(Math.max(0.0F, acceleration - 0.01F));
            }
            if (Math.abs(acceleration) > 0.0F) {
                Vec3d thrust = new Vec3d(0.0D, 0.0D, MathHelper.clamp(acceleration, -0.25F, TOP_SPEED) * 0.2F).rotatePitch(-rotationPitch * ((float) Math.PI / 180.0F)).rotateYaw(-rotationYaw * ((float) Math.PI / 180.0F));
                motionX += thrust.x;
                motionY += thrust.y;
                motionZ += thrust.z;
            }
            if (isInWater()) {
                move(MoverType.SELF, motionX, motionY, motionZ);
                motionX *= 0.8D;
                motionY *= 0.8D;
                motionZ *= 0.8D;
            } else {
                motionY -= 0.5D;
                move(MoverType.SELF, motionX * 0.9D, motionY * 0.9D, motionZ * 0.9D);
                motionX *= 0.1D;
                motionY *= 0.3D;
                motionZ *= 0.1D;
            }
            if (!isWaxed() && getOxidizationLevel() < 3) {
                if (oxidizeTime-- <= 0) {
                    resetOxidizeTime();
                    setOxidizationLevel(getOxidizationLevel() + 1);
                }
            }
            if (getDangerAlertTicks() > 0) {
                setDangerAlertTicks(getDangerAlertTicks() - 1);
            }
        }

        float xRotSet = MathHelper.clamp(-(float) motionY * 2.0F, -1.0F, 1.0F) * -(180.0F / (float) Math.PI) * Math.signum(getAcceleration() + 0.01F);
        float rot = acceleration * 30.0F + Math.signum(acceleration) * 15.0F;
        setBackPropellerRot(backPropellerRot + rot);
        setLeftPropellerRot(leftPropellerRot + rot + (turnLeftTicks > 0 ? 5.0F * turnLeftTicks : 0.0F));
        setRightPropellerRot(rightPropellerRot + rot + (turnRightTicks > 0 ? 5.0F * turnRightTicks : 0.0F));

        if (getWaterHeight() >= 1.5F) {
            if (Math.abs(getAcceleration()) > 0.05F && world.isRemote) {
                Vec3d bubblesAt = new Vec3d(0.0D, 0.3D, -2.0D).rotatePitch(rotationPitch * ((float) Math.PI / 180.0F)).rotateYaw(-rotationYaw * ((float) Math.PI / 180.0F));
                for (int i = 0; i < 1 + rand.nextInt(4); i++) {
                    world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX + bubblesAt.x + rand.nextFloat() - 0.5F, posY + height * 0.5F + bubblesAt.y + rand.nextFloat() - 0.5F, posZ + bubblesAt.z + rand.nextFloat() - 0.5F, 0.0D, 0.0D, 0.0D);
                }
            }
            if (submergedTicks < 10) {
                submergedTicks++;
            }
        } else if (submergedTicks > 0) {
            submergedTicks = 0;
        }
        if (floodlightToggleCooldown > 0) {
            floodlightToggleCooldown--;
        }
        if (turnLeftTicks > 0) {
            turnLeftTicks--;
        }
        if (turnRightTicks > 0) {
            turnRightTicks--;
        }
        if (shakeTime > 0) {
            shakeTime--;
        }
        if (wereLightsOn != areLightsOn()) {
            playSound(wereLightsOn ? ACSoundRegistry.SUBMARINE_LIGHT_OFF : ACSoundRegistry.SUBMARINE_LIGHT_ON, 1.0F, 1.0F);
            wereLightsOn = areLightsOn();
        }
        rotationPitch = ACMath.approachRotation(rotationPitch, MathHelper.clamp(getDamageLevel() >= 4 ? 0.0F : xRotSet, -50.0F, 50.0F), 2.0F);
        prevLeftPropellerRot = leftPropellerRot;
        prevRightPropellerRot = rightPropellerRot;
        prevBackPropellerRot = backPropellerRot;
    }

    @Override
    public void setDead() {
        AlexsCaves.PROXY.clearSoundCacheFor(this);
        super.setDead();
    }

    @Override
    public void updatePassenger(Entity passenger) {
        if (isPassenger(passenger)) {
            clampRotation(passenger);
            if (passenger instanceof EntityPlayer) {
                tickController((EntityPlayer) passenger);
            }
            float pitchOffset = -(rotationPitch / 40.0F);
            Vec3d seatOffset = new Vec3d(0.0D, -0.2D, 0.8D + pitchOffset).rotatePitch(rotationPitch * ((float) Math.PI / 180.0F)).rotateYaw(-rotationYaw * ((float) Math.PI / 180.0F));
            passenger.setPosition(posX + seatOffset.x, posY + height * 0.5F + seatOffset.y + passenger.getYOffset(), posZ + seatOffset.z);
            passenger.fallDistance = 0.0F;
            passenger.setAir(Math.min(passenger.getAir() + 2, 300));
            if (getDamageLevel() >= 4) {
                passenger.dismountRidingEntity();
            }
        } else {
            super.updatePassenger(passenger);
        }
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (player.isSneaking()) {
            return false;
        }
        ItemStack itemStack = player.getHeldItem(hand);
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemAxe && (getOxidizationLevel() > 0 || isWaxed())) {
            player.swingArm(hand);
            if (!player.capabilities.isCreativeMode) {
                itemStack.damageItem(1, player);
            }
            if (isWaxed()) {
                playSound(SoundEvents.BLOCK_METAL_STEP, 1.0F, 1.0F);
                setWaxed(false);
            } else {
                setOxidizationLevel(getOxidizationLevel() - 1);
                playSound(SoundEvents.BLOCK_METAL_BREAK, 1.0F, 1.0F);
            }
            world.setEntityState(this, (byte) 46);
            resetOxidizeTime();
            return true;
        }
        if (isWaxItem(itemStack) && !isWaxed()) {
            player.swingArm(hand);
            if (!player.capabilities.isCreativeMode) {
                itemStack.shrink(1);
            }
            playSound(SoundEvents.BLOCK_SLIME_PLACE, 1.0F, 1.0F);
            setWaxed(true);
            world.setEntityState(this, (byte) 45);
            return true;
        }
        if (isCopperIngot(itemStack) && getDamageLevel() > 0) {
            player.swingArm(hand);
            if (!player.capabilities.isCreativeMode) {
                itemStack.shrink(1);
            }
            playSound(ACSoundRegistry.SUBMARINE_REPAIR, 1.0F, 1.0F);
            setDamageLevel(Math.max(getDamageLevel() - 1, 0));
            damageSustained = 0.0D;
            return true;
        }
        return !world.isRemote && getDamageLevel() < 4 && player.startRiding(this) || world.isRemote;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (isEntityInvulnerable(source) || source == DamageSource.DROWN || source == DamageSource.FALL || source == DamageSource.IN_FIRE || source == DamageSource.ON_FIRE || source == DamageSource.CACTUS) {
            return false;
        }
        damageSustained += amount;
        boolean destroyed = false;
        world.setEntityState(this, (byte) 48);
        if (damageSustained >= 10.0D) {
            damageSustained = 0.0D;
            world.setEntityState(this, (byte) 47);
            if (getDamageLevel() >= 4) {
                if (!isDead) {
                    for (int i = 0; i < 2 + rand.nextInt(3); i++) {
                        entityDropItem(new ItemStack(Items.IRON_INGOT), 0.0F);
                    }
                }
                setDead();
                destroyed = true;
                playSound(ACSoundRegistry.SUBMARINE_DESTROY, 1.0F, 1.0F);
            } else {
                setDamageLevel(getDamageLevel() + 1);
            }
        }
        if (!destroyed) {
            playSound(ACSoundRegistry.SUBMARINE_HIT, 1.0F, 1.0F);
        }
        return true;
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 45 || id == 46) {
            for (int i = 0; i < 5; i++) {
                world.spawnParticle(id == 45 ? EnumParticleTypes.VILLAGER_HAPPY : EnumParticleTypes.SMOKE_NORMAL, posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5D) * width, (rand.nextFloat() - 0.5F) * 0.1F, rand.nextFloat() * 0.15F, (rand.nextFloat() - 0.5F) * 0.1F);
            }
        } else if (id == 47) {
            ItemStack stack = new ItemStack(ACItemRegistry.SUBMARINE.item());
            for (int i = 0; i < 10; i++) {
                world.spawnParticle(EnumParticleTypes.ITEM_CRACK, posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5D) * width, (rand.nextDouble() - 0.5D) * 0.1D, rand.nextDouble() * 0.15D, (rand.nextDouble() - 0.5D) * 0.1D, Item.getIdFromItem(stack.getItem()), stack.getMetadata());
            }
            shakeTime = 20;
        } else if (id == 48) {
            shakeTime = 10;
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        setOxidizationLevel(tag.getInteger("Oxidization"));
        setDamageLevel(tag.getInteger("DamageLevel"));
        setWaxed(tag.getBoolean("Waxed"));
        setLightsOn(tag.getBoolean("LightsOn"));
        if (tag.hasKey("OxidizeTime")) {
            oxidizeTime = tag.getInteger("OxidizeTime");
        }
        if (tag.hasKey("DamageSustained")) {
            damageSustained = tag.getDouble("DamageSustained");
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        tag.setInteger("Oxidization", getOxidizationLevel());
        tag.setInteger("DamageLevel", getDamageLevel());
        tag.setBoolean("Waxed", isWaxed());
        tag.setBoolean("LightsOn", areLightsOn());
        tag.setInteger("OxidizeTime", oxidizeTime);
        tag.setDouble("DamageSustained", damageSustained);
    }

    @Override
    public boolean canBeCollidedWith() {
        return !isDead;
    }

    @Override
    public boolean canBePushed() {
        return !isDead;
    }

    @Override
    public boolean isPushedByWater() {
        return false;
    }

    @Override
    public Entity getControllingPassenger() {
        return getPassengers().isEmpty() ? null : getPassengers().get(0);
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return new ItemStack(ACItemRegistry.SUBMARINE.item());
    }

    @Override
    public void onKeyPacket(Entity keyPresser, int type) {
        if (keyPresser.getRidingEntity() == this) {
            if (type == 0) {
                controlUpTicks = 10;
            } else if (type == 1) {
                controlDownTicks = 10;
            } else if (type == 2) {
                setLightsOn(!areLightsOn());
                floodlightToggleCooldown = 5;
            }
        }
    }

    public boolean areLightsOn() {
        return dataManager.get(LIGHTS);
    }

    public void setLightsOn(boolean lights) {
        dataManager.set(LIGHTS, lights);
    }

    public boolean isWaxed() {
        return dataManager.get(WAXED);
    }

    public void setWaxed(boolean waxed) {
        dataManager.set(WAXED, waxed);
    }

    public int getOxidizationLevel() {
        return dataManager.get(OXIDIZATION_LEVEL);
    }

    public void setOxidizationLevel(int level) {
        dataManager.set(OXIDIZATION_LEVEL, MathHelper.clamp(level, 0, 3));
    }

    public int getDamageLevel() {
        return dataManager.get(DAMAGE_LEVEL);
    }

    public void setDamageLevel(int level) {
        dataManager.set(DAMAGE_LEVEL, MathHelper.clamp(level, 0, 4));
    }

    public int getDangerAlertTicks() {
        return dataManager.get(DANGER_ALERT_TICKS);
    }

    public void setDangerAlertTicks(int ticks) {
        dataManager.set(DANGER_ALERT_TICKS, ticks);
    }

    public float getLeftPropellerRot() {
        return dataManager.get(LEFT_PROPELLER_ROT);
    }

    public void setLeftPropellerRot(float rotation) {
        dataManager.set(LEFT_PROPELLER_ROT, rotation);
    }

    public float getLeftPropellerRot(float partialTicks) {
        return prevLeftPropellerRot + (getLeftPropellerRot() - prevLeftPropellerRot) * partialTicks;
    }

    public float getRightPropellerRot() {
        return dataManager.get(RIGHT_PROPELLER_ROT);
    }

    public void setRightPropellerRot(float rotation) {
        dataManager.set(RIGHT_PROPELLER_ROT, rotation);
    }

    public float getRightPropellerRot(float partialTicks) {
        return prevRightPropellerRot + (getRightPropellerRot() - prevRightPropellerRot) * partialTicks;
    }

    public float getBackPropellerRot() {
        return dataManager.get(BACK_PROPELLER_ROT);
    }

    public void setBackPropellerRot(float rotation) {
        dataManager.set(BACK_PROPELLER_ROT, rotation);
    }

    public float getBackPropellerRot(float partialTicks) {
        return prevBackPropellerRot + (getBackPropellerRot() - prevBackPropellerRot) * partialTicks;
    }

    public float getAcceleration() {
        return dataManager.get(ACCELERATION);
    }

    public void setAcceleration(float acceleration) {
        dataManager.set(ACCELERATION, acceleration);
    }

    public float getWaterHeight() {
        return isInWater() ? 2.0F : 0.0F;
    }

    public float getSonarFlashAmount(float partialTicks) {
        float flash = prevSonarFlashAmount + (sonarFlashAmount - prevSonarFlashAmount) * partialTicks;
        float pulse = (float) (flash * (Math.cos((ticksExisted + partialTicks) * 0.4F) + 1.0F) * 0.5F);
        return 1.0F - flash + pulse;
    }

    public static void alertSubmarineMountOf(EntityLivingBase living) {
        if (living.isEntityAlive() && living.getRidingEntity() instanceof SubmarineEntity) {
            SubmarineEntity submarine = (SubmarineEntity) living.getRidingEntity();
            if (submarine.getDamageLevel() <= 3) {
                submarine.setDangerAlertTicks(100);
            }
        }
    }

    private void tickController(EntityPlayer passenger) {
        if (passenger.moveStrafing != 0.0F) {
            float turn = -Math.signum(passenger.moveStrafing);
            if (turn > 0.0F) {
                turnLeftTicks = 5;
            } else {
                turnRightTicks = 5;
            }
            rotationYaw += turn * 2.5F;
        }
        if (passenger.moveForward != 0.0F) {
            float back = -Math.signum(passenger.moveForward);
            setAcceleration(ACMath.approach(getAcceleration(), back < 0.0F ? 1.0F : -0.5F, 0.02F));
        }
    }

    private void clampRotation(Entity passenger) {
        passenger.rotationYaw = rotationYaw;
        passenger.prevRotationYaw = rotationYaw;
        if (passenger instanceof EntityLivingBase) {
            ((EntityLivingBase) passenger).renderYawOffset = rotationYaw;
            ((EntityLivingBase) passenger).rotationYawHead = rotationYaw;
        }
    }

    private void resetOxidizeTime() {
        oxidizeTime = 24000 * (2 + rand.nextInt(2));
    }

    private static boolean isWaxItem(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem().getRegistryName() != null && stack.getItem().getRegistryName().toString().equals("minecraft:honeycomb");
    }

    private static boolean isCopperIngot(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        for (int id : OreDictionary.getOreIDs(stack)) {
            String name = OreDictionary.getOreName(id);
            if ("ingotCopper".equals(name)) {
                return true;
            }
        }
        return stack.getItem() == Items.IRON_INGOT;
    }
}
