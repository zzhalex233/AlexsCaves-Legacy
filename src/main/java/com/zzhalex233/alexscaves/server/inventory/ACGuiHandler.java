package com.zzhalex233.alexscaves.server.inventory;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.block.entity.NuclearFurnaceTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ACGuiHandler implements IGuiHandler {
    public static final int SPELUNKERY_TABLE = 0;
    public static final int NUCLEAR_FURNACE = 1;

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == SPELUNKERY_TABLE) {
            return new SpelunkeryTableContainer(player.inventory, world, new BlockPos(x, y, z));
        }
        if (id == NUCLEAR_FURNACE) {
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
            return tile instanceof NuclearFurnaceTileEntity ? new NuclearFurnaceContainer(player.inventory, (NuclearFurnaceTileEntity) tile) : null;
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == SPELUNKERY_TABLE) {
            return AlexsCaves.PROXY.getSpelunkeryTableGui(player, world, new BlockPos(x, y, z));
        }
        if (id == NUCLEAR_FURNACE) {
            return AlexsCaves.PROXY.getNuclearFurnaceGui(player, world, new BlockPos(x, y, z));
        }
        return null;
    }
}
