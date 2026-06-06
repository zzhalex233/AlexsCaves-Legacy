package com.zzhalex233.alexscaves.server.block.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.config.ACConfig;
import com.zzhalex233.alexscaves.server.entity.living.GrottoceratopsEntity;
import com.zzhalex233.alexscaves.server.entity.living.RelicheirusEntity;
import com.zzhalex233.alexscaves.server.entity.living.SubterranodonEntity;
import com.zzhalex233.alexscaves.server.entity.living.VallumraptorEntity;
import com.zzhalex233.alexscaves.server.message.AmberMonolithMessage;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class AmberMonolithTileEntity extends TileEntity implements ITickable {
    private static final List<SpawnSelection> PRIMORDIAL_CAVE_SPAWNS = Arrays.asList(
            new SpawnSelection(SubterranodonEntity.class, 6, 3, 5),
            new SpawnSelection(VallumraptorEntity.class, 6, 6, 7),
            new SpawnSelection(GrottoceratopsEntity.class, 27, 2, 4),
            new SpawnSelection(RelicheirusEntity.class, 13, 1, 1)
    );

    public int tickCount;
    private int spawnsMobIn;
    private int spawnCount = 1;
    private Class<? extends EntityLiving> spawnClass;
    private EntityLiving displayEntity;
    private float previousRotation;
    private float rotation = (float) (Math.random() * 360.0F);

    @Override
    public void update() {
        tickCount++;
        previousRotation = rotation;
        rotation += spawnsMobIn <= 1000 ? 1.0F + (1000.0F - spawnsMobIn) / 50.0F : 1.0F;
        if (world == null || world.isRemote) {
            return;
        }
        if (spawnClass == null) {
            generateSpawnData();
        }
        if (spawnsMobIn > 0) {
            spawnsMobIn--;
        } else if (spawnMobs()) {
            world.playSound(null, pos, ACSoundRegistry.AMBER_MONOLITH_SUMMON, SoundCategory.BLOCKS, 1.0F, 1.0F);
            generateSpawnData();
        }
    }

    private void generateSpawnData() {
        SpawnSelection selection = chooseSpawnSelection();
        spawnClass = selection.entityClass;
        spawnCount = selection.nextCount(world.rand);
        int meanTime = Math.max(1000, ACConfig.getAmberMonolithMeanTime());
        spawnsMobIn = meanTime / 2 + world.rand.nextInt(meanTime);
        markDirty();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
    }

    private boolean spawnMobs() {
        if (spawnClass == null || world.getClosestPlayer(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 28.0D, false) == null) {
            return false;
        }
        int spawned = 0;
        int count = Math.max(1, spawnCount);
        for (int i = 0; i < count; i++) {
            BlockPos spawnPos = getRandomSpawnPos();
            if (spawnPos == null || !world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(spawnPos).grow(1.0D)).isEmpty()) {
                continue;
            }
            try {
                EntityLiving entity = spawnClass.getConstructor(World.class).newInstance(world);
                entity.setLocationAndAngles(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, world.rand.nextFloat() * 360.0F, 0.0F);
                if (!entity.getCanSpawnHere() || ForgeEventFactory.canEntitySpawn(entity, world, (float) spawnPos.getX() + 0.5F, spawnPos.getY(), (float) spawnPos.getZ() + 0.5F) == Result.DENY) {
                    continue;
                }
                if (world.spawnEntity(entity)) {
                    spawned++;
                    spawnParticlesTo(entity);
                }
            } catch (ReflectiveOperationException exception) {
                AlexsCaves.LOGGER.warn("Failed to create amber monolith spawn", exception);
            }
        }
        return spawned > 0;
    }

    private BlockPos getRandomSpawnPos() {
        for (int i = 0; i < 20; i++) {
            BlockPos cursor = pos.add(world.rand.nextInt(21) - 10, 1, world.rand.nextInt(21) - 10);
            while (cursor.getY() > 1 && world.isAirBlock(cursor)) {
                cursor = cursor.down();
            }
            BlockPos spawnPos = cursor.up();
            if (Math.abs(spawnPos.getY() - pos.getY()) < 20 && world.isAirBlock(spawnPos) && world.isAirBlock(spawnPos.up()) && world.isAirBlock(spawnPos.up(2))) {
                return spawnPos;
            }
        }
        return null;
    }

    public EntityLiving getDisplayEntity(World world) {
        if (spawnClass == null || world == null) {
            return null;
        }
        if (displayEntity != null && displayEntity.getClass() == spawnClass) {
            return displayEntity;
        }
        try {
            displayEntity = spawnClass.getConstructor(World.class).newInstance(world);
            return displayEntity;
        } catch (ReflectiveOperationException exception) {
            return null;
        }
    }

    private SpawnSelection chooseSpawnSelection() {
        List<SpawnSelection> candidates = new ArrayList<>();
        if (world instanceof WorldServer) {
            Biome biome = world.getBiome(pos);
            collectCandidates(candidates, biome.getSpawnableList(EnumCreatureType.CREATURE));
        }
        return WeightedRandom.getRandomItem(world.rand, candidates.isEmpty() ? PRIMORDIAL_CAVE_SPAWNS : candidates);
    }

    private void collectCandidates(List<SpawnSelection> candidates, List<SpawnListEntry> entries) {
        for (SpawnListEntry entry : entries) {
            if (entry == null || entry.entityClass == null || !isPrimordialSpawnClass(entry.entityClass)) {
                continue;
            }
            candidates.add(new SpawnSelection(entry.entityClass, entry.itemWeight, entry.minGroupCount, entry.maxGroupCount));
        }
    }

    private static boolean isPrimordialSpawnClass(Class<? extends EntityLiving> entityClass) {
        for (SpawnSelection selection : PRIMORDIAL_CAVE_SPAWNS) {
            if (selection.entityClass == entityClass) {
                return true;
            }
        }
        return false;
    }

    public float getRotation(float partialTicks) {
        return previousRotation + (rotation - previousRotation) * partialTicks;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.add(-2, -2, -2), pos.add(3, 4, 3));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("SpawnMobsIn", spawnsMobIn);
        compound.setInteger("SpawnCount", spawnCount);
        if (spawnClass != null) {
            compound.setString("SpawnClass", spawnClass.getName());
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        spawnsMobIn = compound.getInteger("SpawnMobsIn");
        spawnCount = Math.max(1, compound.getInteger("SpawnCount"));
        if (compound.hasKey("SpawnClass")) {
            spawnClass = classForName(compound.getString("SpawnClass"));
        }
        displayEntity = null;
    }

    private Class<? extends EntityLiving> classForName(String name) {
        for (SpawnSelection selection : PRIMORDIAL_CAVE_SPAWNS) {
            if (selection.entityClass.getName().equals(name)) {
                return selection.entityClass;
            }
        }
        return null;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    private void spawnParticlesTo(EntityLiving entity) {
        Vec3d from = new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        Vec3d to = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        AlexsCaves.NETWORK_WRAPPER.sendToAllAround(new AmberMonolithMessage(from, to), new TargetPoint(world.provider.getDimension(), from.x, from.y, from.z, 64.0D));
    }

    private static class SpawnSelection extends WeightedRandom.Item {
        private final Class<? extends EntityLiving> entityClass;
        private final int minGroupCount;
        private final int maxGroupCount;

        private SpawnSelection(Class<? extends EntityLiving> entityClass, int weight, int minGroupCount, int maxGroupCount) {
            super(weight);
            this.entityClass = entityClass;
            this.minGroupCount = minGroupCount;
            this.maxGroupCount = maxGroupCount;
        }

        private int nextCount(java.util.Random random) {
            int spread = Math.max(0, maxGroupCount - minGroupCount);
            return spread == 0 ? minGroupCount : minGroupCount + random.nextInt(spread + 1);
        }
    }
}
