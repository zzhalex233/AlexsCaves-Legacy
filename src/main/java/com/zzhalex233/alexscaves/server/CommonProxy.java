package com.zzhalex233.alexscaves.server;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.zzhalex233.alexscaves.server.block.entity.HologramProjectorTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.MagnetTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.NuclearFurnaceTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.NuclearSirenTileEntity;
import com.zzhalex233.alexscaves.server.entity.item.QuarrySmasherEntity;
import com.zzhalex233.alexscaves.server.entity.item.SubmarineEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
    }

    public void openBookGUI(ItemStack stack) {
    }

    public Object getSpelunkeryTableGui(EntityPlayer player, World world, BlockPos pos) {
        return null;
    }

    public Object getNuclearFurnaceGui(EntityPlayer player, World world, BlockPos pos) {
        return null;
    }

    public void spawnTeslaBulbLightning(World world, Vec3d from, Vec3d to) {
    }

    public void spawnQuarryBorderLightning(World world, Vec3d from, Vec3d to) {
    }

    public void spawnMagneticFlow(World world, Vec3d from, Vec3d to, boolean azure) {
    }

    public void spawnMagneticOrbit(World world, Vec3d center, boolean azure) {
    }

    public void spawnNuclearSirenSonar(World world, Vec3d pos, Vec3d direction) {
    }

    public void spawnAmberMonolithParticles(World world, Vec3d from, Vec3d to) {
    }

    public void playHologramProjectorSound(HologramProjectorTileEntity hologramProjector) {
    }

    public void playMagnetSound(MagnetTileEntity magnet) {
    }

    public void playNuclearFurnaceSound(NuclearFurnaceTileEntity furnace) {
    }

    public void playNuclearSirenSound(NuclearSirenTileEntity siren) {
    }

    public void playQuarrySmasherSound(QuarrySmasherEntity quarrySmasher) {
    }

    public void playGalenaGauntletSound(EntityLivingBase user) {
    }

    public void playResistorShieldSound(EntityLivingBase user, boolean scarlet) {
    }

    public void playRaygunSound(EntityLivingBase user) {
    }

    public void playSubmarineSound(SubmarineEntity submarine) {
    }

    public void clearSoundCacheFor(Entity entity) {
    }

    public EntityPlayer getClientSidePlayer() {
        return null;
    }

    public boolean isKeyDown(int keyType) {
        return false;
    }

    public float getPartialTicks() {
        return 0.0F;
    }

    public int getPlayerTime() {
        return 0;
    }

    public void runOnClientThread(Runnable runnable) {
    }
}
