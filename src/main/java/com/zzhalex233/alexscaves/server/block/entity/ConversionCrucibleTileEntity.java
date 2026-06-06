package com.zzhalex233.alexscaves.server.block.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.block.ConversionCrucibleBlock;
import com.zzhalex233.alexscaves.server.entity.util.MagnetUtil;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.item.BiomeTreatItem;
import com.zzhalex233.alexscaves.server.item.CaveInfoItem;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;

public class ConversionCrucibleTileEntity extends TileEntity implements ITickable {
    public static final int MAX_FILL_AMOUNT = 10;
    public static final int MAX_CONVERSION_TIME = 100;

    private final List<RecursiveBlockPlacement> recursiveBlockPlacements = new ArrayList<>();
    public int tickCount;
    private float prevConversionProgress;
    private float conversionProgress;
    private float prevSplashProgress;
    private float splashProgress;
    private float prevItemDisplayProgress;
    private float itemDisplayProgress;
    private int filledLevel;
    private int biomeColor = -1;
    private int splashTimer;
    private int conversionTime;
    private String convertingToBiome;
    private ItemStack displayStack = ItemStack.EMPTY;
    private ItemStack wantStack = ItemStack.EMPTY;
    private IBlockState topBlockForBiome = Blocks.GRASS.getDefaultState();
    private IBlockState middleBlockForBiome = Blocks.DIRT.getDefaultState();
    private IBlockState bottomBlockForBiome = Blocks.STONE.getDefaultState();

    @Override
    public void update() {
        prevConversionProgress = conversionProgress;
        prevSplashProgress = splashProgress;
        prevItemDisplayProgress = itemDisplayProgress;
        if (biomeColor == -1 && convertingToBiome != null) {
            biomeColor = colorForBiome(convertingToBiome);
        }
        if (!wantStack.isEmpty() && matchesWantedItem(displayStack)) {
            itemDisplayProgress = Math.min(5.0F, itemDisplayProgress + 1.0F);
        } else {
            itemDisplayProgress = Math.max(0.0F, itemDisplayProgress - 1.0F);
        }
        if (splashTimer > 0) {
            splashTimer--;
            splashProgress = Math.min(5.0F, splashProgress + 1.0F);
        } else {
            splashProgress = Math.max(0.0F, splashProgress - 1.0F);
        }
        if (filledLevel >= MAX_FILL_AMOUNT && convertingToBiome != null) {
            displayStack = ItemStack.EMPTY;
            wantStack = ItemStack.EMPTY;
            if (conversionTime < MAX_CONVERSION_TIME) {
                if (!world.isRemote && conversionTime % 10 == 0 && conversionTime >= 20) {
                    updateTopAndBottomBlocks();
                    recursivelySpreadBiomeBlocks(new ArrayList<BlockPos>(), pos.down(), 10, 10);
                }
                if (!world.isRemote && conversionTime == 0) {
                    world.playSound(null, pos, ACSoundRegistry.CONVERSION_CRUCIBLE_CONVERT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                conversionTime++;
            } else {
                if (!world.isRemote) {
                    convertBiome();
                    conversionTime = 0;
                    setFilledLevel(0);
                    markUpdated();
                }
            }
            conversionProgress = conversionTime / (float) MAX_CONVERSION_TIME * 20.0F;
        } else {
            conversionProgress = Math.max(0.0F, conversionProgress - 1.0F);
        }
        if (itemDisplayProgress == 0.0F) {
            displayStack = wantStack;
        }
        tickCount++;
        if (world.isRemote) {
            spawnConversionParticles(conversionTime > MAX_CONVERSION_TIME - 2 || filledLevel == 0);
        } else {
            if (tickCount % 5 == 0 && absorbItemsAtAndAbove()) {
                markUpdated();
            }
            processRecursivePlacements();
        }
    }

    private boolean absorbItemsAtAndAbove() {
        boolean changed = false;
        AxisAlignedBB aabb = ConversionCrucibleBlock.SUCK_AABB.offset(pos);
        for (EntityItem entityItem : world.getEntitiesWithinAABB(EntityItem.class, aabb)) {
            ItemStack stack = entityItem.getItem();
            if (stack.isEmpty()) {
                continue;
            }
            if (convertingToBiome == null && stack.getItem() == ACItemRegistry.BIOME_TREAT.item()) {
                String caveBiome = CaveInfoItem.getCaveBiome(stack);
                if (caveBiome != null && !caveBiome.isEmpty()) {
                    setConvertingToBiome(caveBiome);
                    setFilledLevel(1);
                    rerollWantedItem();
                    stack.shrink(1);
                    changed = true;
                }
            } else if (!wantStack.isEmpty() && matchesWantedItem(stack)) {
                consumeItem(stack);
                if (stack.isEmpty()) {
                    entityItem.setDead();
                }
                changed = true;
                break;
            }
            if (stack.isEmpty()) {
                entityItem.setDead();
            }
        }
        return changed;
    }

    private void processRecursivePlacements() {
        Iterator<RecursiveBlockPlacement> iterator = recursiveBlockPlacements.iterator();
        while (iterator.hasNext()) {
            RecursiveBlockPlacement placement = iterator.next();
            placement.placeIn--;
            if (placement.placeIn <= 0) {
                IBlockState state = world.getBlockState(placement.pos);
                if (canReplaceForConversion(placement.pos, state)) {
                    world.setBlockState(placement.pos, placement.toPlace, 3);
                }
                iterator.remove();
            }
        }
    }

    public void consumeItem(ItemStack stack) {
        if (!stack.isEmpty()) {
            stack.shrink(1);
        }
        setFilledLevel(Math.min(filledLevel + 1, MAX_FILL_AMOUNT));
        rerollWantedItem();
        splashTimer = 10;
    }

    public void rerollWantedItem() {
        if (convertingToBiome == null) {
            wantStack = ItemStack.EMPTY;
            displayStack = ItemStack.EMPTY;
            return;
        }
        updateTopAndBottomBlocks();
        world.playSound(null, pos, filledLevel >= MAX_FILL_AMOUNT ? ACSoundRegistry.CONVERSION_CRUCIBLE_ACTIVATE : ACSoundRegistry.CONVERSION_CRUCIBLE_ADD, SoundCategory.BLOCKS, 1.0F, 1.0F);
        if (filledLevel > MAX_FILL_AMOUNT - 2) {
            wantStack = finalSacrificeForBiome(convertingToBiome);
        } else {
            switch (world.rand.nextInt(3)) {
                case 0:
                    wantStack = blockStack(topBlockForBiome.getBlock());
                    break;
                case 1:
                    wantStack = blockStack(middleBlockForBiome.getBlock());
                    break;
                default:
                    wantStack = blockStack(bottomBlockForBiome.getBlock());
                    break;
            }
        }
    }

    public boolean matchesWantedItem(ItemStack stack) {
        if (stack.isEmpty() || wantStack.isEmpty() || stack.getItem() != wantStack.getItem()) {
            return false;
        }
        int wantedMeta = wantStack.getMetadata();
        return wantedMeta == OreDictionary.WILDCARD_VALUE || wantedMeta == stack.getMetadata();
    }

    private void updateTopAndBottomBlocks() {
        if (CaveInfoItem.CAVE_BIOMES[0].equals(convertingToBiome)) {
            topBlockForBiome = ACBlockRegistry.GALENA.block().getDefaultState();
            middleBlockForBiome = ACBlockRegistry.GALENA_BRICKS.block().getDefaultState();
            bottomBlockForBiome = ACBlockRegistry.SCRAP_METAL.block().getDefaultState();
        } else if (CaveInfoItem.CAVE_BIOMES[1].equals(convertingToBiome)) {
            topBlockForBiome = ACBlockRegistry.LIMESTONE.block().getDefaultState();
            middleBlockForBiome = ACBlockRegistry.SMOOTH_LIMESTONE.block().getDefaultState();
            bottomBlockForBiome = ACBlockRegistry.FLOOD_BASALT.block().getDefaultState();
        } else if (CaveInfoItem.CAVE_BIOMES[2].equals(convertingToBiome)) {
            topBlockForBiome = ACBlockRegistry.RADROCK.block().getDefaultState();
            middleBlockForBiome = ACBlockRegistry.RADROCK_BRICKS.block().getDefaultState();
            bottomBlockForBiome = ACBlockRegistry.CINDER_BLOCK.block().getDefaultState();
        } else if (CaveInfoItem.CAVE_BIOMES[3].equals(convertingToBiome)) {
            topBlockForBiome = ACBlockRegistry.ABYSSMARINE.block().getDefaultState();
            middleBlockForBiome = ACBlockRegistry.GUANOSTONE.block().getDefaultState();
            bottomBlockForBiome = ACBlockRegistry.ABYSSMARINE_TILES.block().getDefaultState();
        } else if (CaveInfoItem.CAVE_BIOMES[4].equals(convertingToBiome)) {
            topBlockForBiome = ACBlockRegistry.COPROLITH.block().getDefaultState();
            middleBlockForBiome = ACBlockRegistry.SMOOTH_COPROLITH.block().getDefaultState();
            bottomBlockForBiome = ACBlockRegistry.GUANO_BLOCK.block().getDefaultState();
        } else if (CaveInfoItem.CAVE_BIOMES[5].equals(convertingToBiome)) {
            topBlockForBiome = ACBlockRegistry.BLOCK_OF_CHOCOLATE.block().getDefaultState();
            middleBlockForBiome = ACBlockRegistry.GINGERBREAD_BLOCK.block().getDefaultState();
            bottomBlockForBiome = ACBlockRegistry.CANDY_CANE_BLOCK.block().getDefaultState();
        } else {
            topBlockForBiome = Blocks.GRASS.getDefaultState();
            middleBlockForBiome = Blocks.DIRT.getDefaultState();
            bottomBlockForBiome = Blocks.STONE.getDefaultState();
        }
    }

    private boolean isBiomeBlock(IBlockState state) {
        Block block = state.getBlock();
        return block == topBlockForBiome.getBlock() || block == middleBlockForBiome.getBlock() || block == bottomBlockForBiome.getBlock();
    }

    private void recursivelySpreadBiomeBlocks(List<BlockPos> crossed, BlockPos to, int maxDistance, int distanceIn) {
        if (distanceIn <= 0 || !world.isBlockLoaded(to)) {
            return;
        }
        IBlockState state = world.getBlockState(to);
        if (!isBiomeBlock(state) && canReplaceForConversion(to, state)) {
            IBlockState above = world.getBlockState(to.up());
            IBlockState above2 = world.getBlockState(to.up(2));
            IBlockState toPlace = above.getMaterial().isReplaceable() ? topBlockForBiome : above2.getMaterial().isReplaceable() ? middleBlockForBiome : bottomBlockForBiome;
            recursiveBlockPlacements.add(new RecursiveBlockPlacement(2 * (maxDistance - distanceIn), to, toPlace));
        }
        crossed.add(to);
        distanceIn--;
        List<BlockPos> possibles = new ArrayList<>();
        for (EnumFacing direction : EnumFacing.VALUES) {
            BlockPos offset = to.offset(direction);
            if (!crossed.contains(offset) && world.isBlockLoaded(offset)) {
                IBlockState offsetState = world.getBlockState(offset);
                if (canReplaceForConversion(offset, offsetState) && (direction != EnumFacing.DOWN || world.rand.nextInt(3) == 0)) {
                    possibles.add(offset);
                }
            }
        }
        if (!possibles.isEmpty()) {
            recursivelySpreadBiomeBlocks(crossed, possibles.get(world.rand.nextInt(possibles.size())), maxDistance, distanceIn);
        }
    }

    private boolean canReplaceForConversion(BlockPos blockPos, IBlockState state) {
        return !(state.getBlock() instanceof BlockAir)
                && !state.getMaterial().isLiquid()
                && !state.getMaterial().isReplaceable()
                && world.getTileEntity(blockPos) == null
                && state.getBlockHardness(world, blockPos) >= 0.0F
                && !MagnetUtil.isUnmoveable(state);
    }

    public void convertBiome() {
        if (convertingToBiome == null) {
            return;
        }
        Biome biome = Biome.REGISTRY.getObject(new ResourceLocation(convertingToBiome));
        if (biome == null) {
            return;
        }
        int biomeId = Biome.getIdForBiome(biome);
        int radius = (int) Math.ceil(getConversionAreaWidth() * 0.5F) + 1;
        for (int x = pos.getX() - radius; x <= pos.getX() + radius; x++) {
            for (int z = pos.getZ() - radius; z <= pos.getZ() + radius; z++) {
                double dx = x - pos.getX();
                double dz = z - pos.getZ();
                if (dx * dx + dz * dz <= radius * radius) {
                    Chunk chunk = world.getChunk(new BlockPos(x, 0, z));
                    byte[] biomes = chunk.getBiomeArray();
                    biomes[(z & 15) << 4 | (x & 15)] = (byte) biomeId;
                    chunk.markDirty();
                }
            }
        }
        world.markBlockRangeForRenderUpdate(pos.add(-radius, -1, -radius), pos.add(radius, 1, radius));
    }

    private void spawnConversionParticles(boolean explosion) {
        float progress = getConversionProgress(1.0F);
        if (progress > 0.0F) {
            float diameter = getConversionAreaWidth() * progress;
            float r = (float) (getConvertingToColor() >> 16 & 255) / 255.0F;
            float g = (float) (getConvertingToColor() >> 8 & 255) / 255.0F;
            float b = (float) (getConvertingToColor() & 255) / 255.0F;
            int count = explosion ? 35 : 3;
            for (int i = 0; i < count; i++) {
                float x = (world.rand.nextFloat() - 0.5F) * diameter;
                float z = (world.rand.nextFloat() - 0.5F) * diameter;
                if (x * x + z * z < diameter * diameter * 0.25F) {
                    world.spawnParticle(explosion ? EnumParticleTypes.SMOKE_LARGE : EnumParticleTypes.REDSTONE, pos.getX() + 0.5F + x, pos.getY() + world.rand.nextFloat() * 0.5F, pos.getZ() + 0.5F + z, r, g, b);
                }
            }
        }
        if (filledLevel > 0 && world.rand.nextFloat() < 0.33F) {
            float r = (float) (getConvertingToColor() >> 16 & 255) / 255.0F;
            float g = (float) (getConvertingToColor() >> 8 & 255) / 255.0F;
            float b = (float) (getConvertingToColor() & 255) / 255.0F;
            world.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX() + 0.25F + world.rand.nextFloat() * 0.5F, pos.getY() + 0.2F + filledLevel * 0.1F, pos.getZ() + 0.25F + world.rand.nextFloat() * 0.5F, r, g, b);
        }
    }

    private int colorForBiome(String caveBiome) {
        return BiomeTreatItem.getBiomeTreatColor(CaveInfoItem.create(ACItemRegistry.BIOME_TREAT.item(), caveBiome));
    }

    private ItemStack finalSacrificeForBiome(String caveBiome) {
        if (CaveInfoItem.CAVE_BIOMES[0].equals(caveBiome)) {
            return new ItemStack(ACBlockRegistry.HEART_OF_IRON.block());
        } else if (CaveInfoItem.CAVE_BIOMES[1].equals(caveBiome)) {
            return new ItemStack(ACItemRegistry.TECTONIC_SHARD.item());
        } else if (CaveInfoItem.CAVE_BIOMES[2].equals(caveBiome)) {
            return new ItemStack(ACItemRegistry.FISSILE_CORE.item());
        } else if (CaveInfoItem.CAVE_BIOMES[3].equals(caveBiome)) {
            return new ItemStack(ACItemRegistry.GAZING_PEARL.item());
        } else if (CaveInfoItem.CAVE_BIOMES[4].equals(caveBiome)) {
            return new ItemStack(ACItemRegistry.PURE_DARKNESS.item());
        } else if (CaveInfoItem.CAVE_BIOMES[5].equals(caveBiome)) {
            return new ItemStack(ACItemRegistry.SWEET_TOOTH.item());
        }
        return new ItemStack(Items.DIAMOND);
    }

    private ItemStack blockStack(Block block) {
        Item item = Item.getItemFromBlock(block);
        return item == Items.AIR ? new ItemStack(Blocks.STONE) : new ItemStack(item);
    }

    public String getConvertingToBiome() {
        return convertingToBiome;
    }

    public void setConvertingToBiome(String caveBiome) {
        convertingToBiome = caveBiome;
        biomeColor = -1;
    }

    public int getFilledLevel() {
        return filledLevel;
    }

    public void setFilledLevel(int filledLevel) {
        this.filledLevel = filledLevel;
    }

    public ItemStack getDisplayItem() {
        return displayStack;
    }

    public ItemStack getWantItem() {
        return wantStack;
    }

    public String getDisplayText() {
        return displayStack.isEmpty() ? "" : displayStack.getDisplayName();
    }

    public float getConversionProgress(float partialTicks) {
        return (prevConversionProgress + (conversionProgress - prevConversionProgress) * partialTicks) * 0.05F;
    }

    public float getSplashProgress(float partialTicks) {
        return (prevSplashProgress + (splashProgress - prevSplashProgress) * partialTicks) * 0.2F;
    }

    public float getItemDisplayProgress(float partialTicks) {
        return (prevItemDisplayProgress + (itemDisplayProgress - prevItemDisplayProgress) * partialTicks) * 0.2F;
    }

    public int getConvertingToColor() {
        return biomeColor == -1 ? 0xFFFFFF : biomeColor;
    }

    public float getConversionAreaWidth() {
        return 10.0F;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos).grow(16.0D, 3.0D, 16.0D);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (!wantStack.isEmpty()) {
            compound.setTag("WantStack", wantStack.writeToNBT(new NBTTagCompound()));
        }
        if (!displayStack.isEmpty()) {
            compound.setTag("DisplayStack", displayStack.writeToNBT(new NBTTagCompound()));
        }
        if (convertingToBiome != null) {
            compound.setString("ConvertingToBiome", convertingToBiome);
        }
        compound.setInteger("FilledLevel", filledLevel);
        compound.setInteger("BiomeColor", biomeColor);
        compound.setInteger("SplashTimer", splashTimer);
        compound.setInteger("ConversionTime", conversionTime);
        compound.setFloat("ConversionProgress", conversionProgress);
        compound.setFloat("SplashProgress", splashProgress);
        compound.setFloat("ItemDisplayProgress", itemDisplayProgress);
        compound.setInteger("TickCount", tickCount);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        wantStack = compound.hasKey("WantStack", 10) ? new ItemStack(compound.getCompoundTag("WantStack")) : ItemStack.EMPTY;
        displayStack = compound.hasKey("DisplayStack", 10) ? new ItemStack(compound.getCompoundTag("DisplayStack")) : ItemStack.EMPTY;
        convertingToBiome = compound.hasKey("ConvertingToBiome", 8) ? compound.getString("ConvertingToBiome") : null;
        filledLevel = compound.getInteger("FilledLevel");
        biomeColor = compound.getInteger("BiomeColor");
        splashTimer = compound.getInteger("SplashTimer");
        conversionTime = compound.getInteger("ConversionTime");
        conversionProgress = compound.getFloat("ConversionProgress");
        splashProgress = compound.getFloat("SplashProgress");
        itemDisplayProgress = compound.getFloat("ItemDisplayProgress");
        tickCount = compound.getInteger("TickCount");
        prevConversionProgress = conversionProgress;
        prevSplashProgress = splashProgress;
        prevItemDisplayProgress = itemDisplayProgress;
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

    public void markUpdated() {
        markDirty();
        if (world != null) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }

    private static class RecursiveBlockPlacement {
        private int placeIn;
        private final BlockPos pos;
        private final IBlockState toPlace;

        private RecursiveBlockPlacement(int placeIn, BlockPos pos, IBlockState toPlace) {
            this.placeIn = placeIn;
            this.pos = pos;
            this.toPlace = toPlace;
        }
    }
}
