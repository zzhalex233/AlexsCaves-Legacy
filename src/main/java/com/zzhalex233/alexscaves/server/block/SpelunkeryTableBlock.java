package com.zzhalex233.alexscaves.server.block;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.inventory.ACGuiHandler;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;

public class SpelunkeryTableBlock extends BasicCaveBlock {
    public SpelunkeryTableBlock() {
        super(Material.WOOD, 2.5F, 2.5F, SoundType.WOOD);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(AlexsCaves.INSTANCE, ACGuiHandler.SPELUNKERY_TABLE, world, pos.getX(), pos.getY(), pos.getZ());
            player.addStat(StatList.getObjectUseStats(ACItemRegistry.CAVE_TABLET.item()));
        }
        player.swingArm(hand);
        return true;
    }
}
