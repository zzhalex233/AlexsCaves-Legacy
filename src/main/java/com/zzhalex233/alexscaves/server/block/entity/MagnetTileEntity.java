package com.zzhalex233.alexscaves.server.block.entity;

import java.util.ArrayList;
import java.util.List;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.block.MagnetBlock;
import com.zzhalex233.alexscaves.server.entity.item.MovingMetalBlockEntity;
import com.zzhalex233.alexscaves.server.entity.util.MagnetUtil;
import com.zzhalex233.alexscaves.server.entity.util.MovingBlockData;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockGlazedTerracotta;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MagnetTileEntity extends TileEntity implements ITickable {
    private static final int MAXIMUM_BLOCKS_PUSHED = 27;
    public int age;
    private float rangeVisuality;
    private float prevRangeVisuality;
    private int extenderIngots;
    private int retracterIngots;
    private boolean locallyActive;

    @Override
    public void update() {
        prevRangeVisuality = rangeVisuality;
        age++;
        if (world.isRemote) {
            if (showRangeBox(net.minecraft.client.Minecraft.getMinecraft().player)) {
                rangeVisuality = Math.min(1.0F, rangeVisuality + 0.2F);
            } else {
                rangeVisuality = Math.max(0.0F, rangeVisuality - 0.2F);
            }
            IBlockState state = world.getBlockState(pos);
            locallyActive = state.getBlock() instanceof MagnetBlock && state.getValue(MagnetBlock.POWERED);
            if (locallyActive) {
                AlexsCaves.PROXY.playMagnetSound(this);
                if (world.rand.nextFloat() < 0.1F) {
                    spawnMagneticFlow(state);
                }
            }
            return;
        }
        IBlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof MagnetBlock) || !state.getValue(MagnetBlock.POWERED)) {
            return;
        }
        pullBlocks(state.getValue(MagnetBlock.FACING));
        for (Entity entity : world.getEntitiesWithinAABB(Entity.class, getRangeBB(getEffectiveRange(), false), candidate -> !(candidate instanceof EntityPlayer && ((EntityPlayer) candidate).isSpectator()))) {
            if (MagnetUtil.isPulledByMagnets(entity)) {
                pushEntity(entity);
            }
        }
    }

    private void spawnMagneticFlow(IBlockState state) {
        EnumFacing direction = state.getValue(MagnetBlock.FACING);
        Vec3d normal = new Vec3d(direction.getDirectionVec());
        Vec3d blockVec = new Vec3d(pos).add(0.5D, 0.5D, 0.5D);
        Vec3d edgeVec = blockVec.add(normal.scale(getEffectiveRange() - 1.5D));
        if (isAzure()) {
            Vec3d target = edgeVec.add(world.rand.nextFloat() - 0.5F, world.rand.nextFloat() - 0.5F, world.rand.nextFloat() - 0.5F);
            AlexsCaves.PROXY.spawnMagneticFlow(world, blockVec, target, true);
        } else {
            blockVec = blockVec.add(normal.scale(2.0D));
            edgeVec = edgeVec.add(normal.scale(2.0D));
            Vec3d origin = edgeVec.add(world.rand.nextFloat() - 0.5F, world.rand.nextFloat() - 0.5F, world.rand.nextFloat() - 0.5F);
            AlexsCaves.PROXY.spawnMagneticFlow(world, origin, blockVec, false);
        }
    }

    private void pullBlocks(EnumFacing direction) {
        for (int i = 1; i <= getEffectiveRange(); i++) {
            BlockPos checkPos = pos.offset(direction, i);
            IBlockState state = world.getBlockState(checkPos);
            if (!MagnetUtil.isMagneticBlock(state) || MagnetUtil.isUnmoveable(state)) {
                continue;
            }
            List<BlockPos> gathered = new ArrayList<>();
            gatherAttachedBlocks(checkPos, gathered);
            if (!gathered.isEmpty()) {
                spawnMovingBlock(checkPos, gathered);
            }
        }
    }

    private void spawnMovingBlock(BlockPos origin, List<BlockPos> gathered) {
        List<MovingBlockData> allData = new ArrayList<>();
        for (BlockPos blockPos : gathered) {
            IBlockState moveState = world.getBlockState(blockPos);
            TileEntity tile = world.getTileEntity(blockPos);
            NBTTagCompound tileData = tile == null ? null : tile.writeToNBT(new NBTTagCompound());
            if (tile != null) {
                world.removeTileEntity(blockPos);
            }
            allData.add(new MovingBlockData(moveState, blockPos.subtract(origin), tileData));
        }
        gathered.sort(this::sortGatheredBlocks);
        for (BlockPos blockPos : gathered) {
            world.setBlockToAir(blockPos);
        }
        MovingMetalBlockEntity entity = new MovingMetalBlockEntity(world);
        entity.setPosition(origin.getX() + 0.5D, origin.getY() + 0.5D, origin.getZ() + 0.5D);
        entity.setBlockData(allData);
        entity.setPlacementCooldown(1);
        world.spawnEntity(entity);
    }

    private void gatherAttachedBlocks(BlockPos blockPos, List<BlockPos> gathered) {
        if (gathered.size() >= MAXIMUM_BLOCKS_PUSHED || gathered.contains(blockPos)) {
            return;
        }
        IBlockState state = world.getBlockState(blockPos);
        if (state.getMaterial() == Material.AIR || MagnetUtil.isUnmoveable(state)) {
            return;
        }
        gathered.add(blockPos);
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos offset = blockPos.offset(facing);
            if (!pos.equals(offset) && canMove(blockPos, offset)) {
                gatherAttachedBlocks(offset, gathered);
            }
        }
    }

    private boolean canMove(BlockPos from, BlockPos to) {
        IBlockState state = world.getBlockState(to);
        IBlockState other = world.getBlockState(from);
        if (state.getMaterial() == Material.AIR || MagnetUtil.isUnmoveable(state)) {
            return false;
        }
        if (MagnetUtil.isMagneticBlock(state)) {
            return true;
        }
        return sticksTo(state, other) || sticksTo(other, state);
    }

    private boolean sticksTo(IBlockState stickyState, IBlockState otherState) {
        return stickyState.getBlock() == Blocks.SLIME_BLOCK && !(otherState.getBlock() instanceof BlockGlazedTerracotta);
    }

    private int sortGatheredBlocks(BlockPos first, BlockPos second) {
        IBlockState firstState = world.getBlockState(first);
        IBlockState secondState = world.getBlockState(second);
        int order = firstState.getBlock() instanceof BlockDoor || secondState.getBlock() instanceof BlockDoor ? 1 : -1;
        return order * Integer.compare(first.getY(), second.getY());
    }

    private void pushEntity(Entity entity) {
        double strength = entity instanceof EntityLivingBase ? 0.2D : 0.04D;
        Vec3d center = new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        Vec3d pull = isAzure() ? entity.getPositionVector().subtract(center) : center.subtract(entity.getPositionVector());
        if (pull.length() > 1.0D) {
            pull = pull.normalize();
        }
        if (entity instanceof MovingMetalBlockEntity) {
            double distance = entity.getDistance(center.x, center.y, center.z);
            if (isAzure()) {
                double f = Math.max(getEffectiveRange() - 1.0D, 1.0D);
                strength *= Math.max((f - (distance + 1.0D)) / f, 0.0D);
            } else if (distance <= 1.0D) {
                strength = 0.0D;
            }
            ((MovingMetalBlockEntity) entity).setPlacementCooldown(2);
        } else if (entity instanceof EntityFallingBlock) {
            strength = 0.04D;
            entity.motionX *= 0.5D;
            entity.motionY *= 0.5D;
            entity.motionZ *= 0.5D;
        } else if (entity instanceof EntityItem) {
            strength = 0.08D;
        }
        if (entity instanceof EntityLivingBase) {
            if (Math.abs(pull.x) > Math.abs(pull.y) && Math.abs(pull.x) > Math.abs(pull.z)) {
                pull = new Vec3d(pull.x, 0.0D, 0.0D);
            } else if (Math.abs(pull.y) > Math.abs(pull.x) && Math.abs(pull.y) > Math.abs(pull.z)) {
                pull = new Vec3d(0.0D, pull.y, 0.0D);
            } else if (Math.abs(pull.z) > Math.abs(pull.x) && Math.abs(pull.z) > Math.abs(pull.y)) {
                pull = new Vec3d(0.0D, 0.0D, pull.z);
            }
            entity.fallDistance = 0.0F;
        }
        if (!MagnetUtil.isEntityOnMovingMetal(entity)) {
            entity.motionX += strength * pull.x;
            entity.motionY += strength * pull.y;
            entity.motionZ += strength * pull.z;
            entity.velocityChanged = true;
        }
    }

    public AxisAlignedBB getRangeBB(double effectiveRange, boolean includeMagnet) {
        AxisAlignedBB box = new AxisAlignedBB(includeMagnet ? pos : pos.offset(getDirection()));
        double i = effectiveRange - 1.0D;
        switch (getDirection()) {
            case UP:
                return new AxisAlignedBB(box.minX, box.minY, box.minZ, box.maxX, box.maxY + i, box.maxZ);
            case DOWN:
                return new AxisAlignedBB(box.minX, box.minY - i, box.minZ, box.maxX, box.maxY, box.maxZ);
            case EAST:
                return new AxisAlignedBB(box.minX, box.minY, box.minZ, box.maxX + i, box.maxY, box.maxZ);
            case WEST:
                return new AxisAlignedBB(box.minX - i, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
            case SOUTH:
                return new AxisAlignedBB(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ + i);
            case NORTH:
            default:
                return new AxisAlignedBB(box.minX, box.minY, box.minZ - i, box.maxX, box.maxY, box.maxZ);
        }
    }

    public EnumFacing getDirection() {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof MagnetBlock ? state.getValue(MagnetBlock.FACING) : EnumFacing.UP;
    }

    public boolean isAzure() {
        return world.getBlockState(pos).getBlock() == ACBlockRegistry.AZURE_MAGNET.block();
    }

    public boolean isLocallyActive() {
        return locallyActive;
    }

    public int getEffectiveRange() {
        int total = MathHelper.clamp(5 + extenderIngots - retracterIngots, 1, 64);
        return isAzure() ? total : total + 1;
    }

    public boolean canAddRange() {
        int modifier = extenderIngots - retracterIngots;
        return 5 + modifier < 64 && extenderIngots + retracterIngots < 64;
    }

    public boolean canRemoveRange() {
        int modifier = extenderIngots - retracterIngots;
        return 5 + modifier > 1 && extenderIngots + retracterIngots < 64;
    }

    public void increaseRange(int by) {
        if (by > 0) {
            extenderIngots += by;
        } else {
            retracterIngots -= by;
        }
        markDirty();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
    }

    public boolean showRangeBox(EntityPlayer player) {
        return player != null && (isExtenderItem(player.getHeldItemMainhand()) || isExtenderItem(player.getHeldItemOffhand()) || isRetracterItem(player.getHeldItemMainhand()) || isRetracterItem(player.getHeldItemOffhand()));
    }

    public boolean isExtenderItem(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == (isAzure() ? ACItemRegistry.AZURE_NEODYMIUM_INGOT.item() : ACItemRegistry.SCARLET_NEODYMIUM_INGOT.item());
    }

    public boolean isRetracterItem(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == (isAzure() ? ACItemRegistry.SCARLET_NEODYMIUM_INGOT.item() : ACItemRegistry.AZURE_NEODYMIUM_INGOT.item());
    }

    public void dropIngots(boolean azure) {
        dropStack(azure ? ACItemRegistry.AZURE_NEODYMIUM_INGOT.item() : ACItemRegistry.SCARLET_NEODYMIUM_INGOT.item(), extenderIngots);
        dropStack(azure ? ACItemRegistry.SCARLET_NEODYMIUM_INGOT.item() : ACItemRegistry.AZURE_NEODYMIUM_INGOT.item(), retracterIngots);
    }

    private void dropStack(net.minecraft.item.Item item, int count) {
        if (count > 0) {
            Block.spawnAsEntity(world, pos, new ItemStack(item, count));
        }
    }

    public float getRangeVisuality(float partialTicks) {
        return prevRangeVisuality + (rangeVisuality - prevRangeVisuality) * partialTicks;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return getRangeBB(getEffectiveRange() + 2, true);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("ExtenderIngots", extenderIngots);
        compound.setInteger("RetractorIngots", retracterIngots);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        extenderIngots = compound.getInteger("ExtenderIngots");
        retracterIngots = compound.getInteger("RetractorIngots");
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
}
