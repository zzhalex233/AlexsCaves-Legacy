package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BasicSlabBlock extends BlockSlab {
    public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

    private BasicSlabBlock singleSlab;

    protected BasicSlabBlock(Material material, float hardness, float resistance, SoundType sound) {
        super(material);
        setHardness(hardness);
        setResistance(resistance);
        setSoundType(sound);
        useNeighborBrightness = !isDouble();
        IBlockState state = blockState.getBaseState().withProperty(VARIANT, Variant.DEFAULT);
        if (!isDouble()) {
            state = state.withProperty(HALF, EnumBlockHalf.BOTTOM);
        }
        setDefaultState(state);
    }

    public BasicSlabBlock singleSlab(BasicSlabBlock singleSlab) {
        this.singleSlab = singleSlab;
        return this;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(singleSlab == null ? this : singleSlab);
    }

    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        return new ItemStack(getItemDropped(state, RANDOM, 0));
    }

    @Override
    public String getTranslationKey(int meta) {
        return getTranslationKey();
    }

    @Override
    public IProperty<?> getVariantProperty() {
        return VARIANT;
    }

    @Override
    public Comparable<?> getTypeForItem(ItemStack stack) {
        return Variant.DEFAULT;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = getDefaultState().withProperty(VARIANT, Variant.DEFAULT);
        if (!isDouble()) {
            state = state.withProperty(HALF, (meta & 8) == 0 ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);
        }
        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return !isDouble() && state.getValue(HALF) == EnumBlockHalf.TOP ? 8 : 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return isDouble() ? new BlockStateContainer(this, VARIANT) : new BlockStateContainer(this, HALF, VARIANT);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }

    public enum Variant implements IStringSerializable {
        DEFAULT;

        @Override
        public String getName() {
            return "default";
        }
    }

    public static class Half extends BasicSlabBlock {
        public Half(Material material, float hardness, float resistance, SoundType sound) {
            super(material, hardness, resistance, sound);
        }

        @Override
        public boolean isDouble() {
            return false;
        }
    }

    public static class Double extends BasicSlabBlock {
        public Double(Material material, float hardness, float resistance, SoundType sound) {
            super(material, hardness, resistance, sound);
        }

        @Override
        public boolean isDouble() {
            return true;
        }
    }
}
