package com.zzhalex233.alexscaves.server.item;

import javax.annotation.Nullable;

import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.block.fluid.ACFluidRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;

public class PurpleSodaBucketItem extends Item {
    public PurpleSodaBucketItem() {
        setMaxStackSize(1);
        setContainerItem(Items.BUCKET);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new FluidHandlerItemStackSimple.SwapEmpty(stack, new ItemStack(Items.BUCKET), Fluid.BUCKET_VOLUME) {
            @Override
            public FluidStack getFluid() {
                return container.getItem() == PurpleSodaBucketItem.this ? new FluidStack(ACFluidRegistry.PURPLE_SODA, Fluid.BUCKET_VOLUME) : null;
            }

            @Override
            public boolean canFillFluidType(FluidStack fluid) {
                return false;
            }
        };
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        RayTraceResult hit = rayTrace(world, player, false);
        if (hit == null) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        if (hit.typeOfHit != RayTraceResult.Type.BLOCK) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }

        BlockPos pos = hit.getBlockPos().offset(hit.sideHit);
        if (!player.canPlayerEdit(pos, hit.sideHit, stack) || !tryPlaceContainedLiquid(player, world, pos)) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.capabilities.isCreativeMode ? stack : new ItemStack(Items.BUCKET));
    }

    public boolean tryPlaceContainedLiquid(EntityPlayer player, World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        boolean replaceable = state.getMaterial().isReplaceable();
        if (!world.isAirBlock(pos) && !replaceable) {
            return false;
        }
        if (!world.isRemote) {
            if (replaceable && !state.getMaterial().isLiquid()) {
                world.destroyBlock(pos, true);
            }
            world.playSound(null, pos, ACSoundRegistry.PURPLE_SODA_UNSUBMERGE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.setBlockState(pos, ACBlockRegistry.PURPLE_SODA.block().getDefaultState(), 11);
        }
        return block != ACBlockRegistry.PURPLE_SODA.block();
    }
}
