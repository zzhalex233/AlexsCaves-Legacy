package com.zzhalex233.alexscaves.server.block;

import com.zzhalex233.alexscaves.server.block.entity.HologramProjectorTileEntity;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class HologramProjectorBlock extends BlockContainer {
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);

    public HologramProjectorBlock() {
        super(Material.IRON);
        setHardness(1.0F);
        setResistance(5.0F);
        setSoundType(SoundType.METAL);
        setLightLevel(10.0F / 15.0F);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new HologramProjectorTileEntity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack held = player.getHeldItem(hand);
        TileEntity tile = world.getTileEntity(pos);
        if (player.isSneaking() || held.getItem() != ACItemRegistry.HOLOCODER.item() || !(tile instanceof HologramProjectorTileEntity)) {
            return false;
        }
        if (!world.isRemote) {
            NBTTagCompound entityTag = getEntityTag(held, player);
            ((HologramProjectorTileEntity) tile).setEntity(entityTag, player.rotationYawHead);
            world.playSound(null, pos, ACSoundRegistry.HOLOGRAM_STOP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!player.capabilities.isCreativeMode) {
                held.shrink(1);
            }
        }
        player.swingArm(hand);
        return true;
    }

    private NBTTagCompound getEntityTag(ItemStack held, EntityPlayer player) {
        NBTTagCompound tag = held.getTagCompound();
        if (tag != null && tag.hasKey("BoundEntityTag", 10)) {
            NBTTagCompound bound = tag.getCompoundTag("BoundEntityTag").copy();
            if (bound.hasKey("id") && EntityList.isRegistered(new net.minecraft.util.ResourceLocation(bound.getString("id")))) {
                return bound;
            }
        }
        NBTTagCompound playerTag = new NBTTagCompound();
        playerTag.setString("id", "minecraft:player");
        playerTag.setUniqueId("UUID", player.getUniqueID());
        return playerTag;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SHAPE;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
}
