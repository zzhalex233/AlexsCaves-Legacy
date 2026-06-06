package com.zzhalex233.alexscaves.server.entity.util;

import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.entity.item.MovingMetalBlockEntity;
import com.zzhalex233.alexscaves.server.entity.living.NucleeperEntity;
import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class MagnetUtil {
    private MagnetUtil() {
    }

    public static boolean isPulledByMagnets(Entity entity) {
        if (entity instanceof MovingMetalBlockEntity) {
            return true;
        }
        if (entity instanceof EntityItem) {
            return isMagneticItem(((EntityItem) entity).getItem());
        }
        if (entity instanceof EntityFallingBlock) {
            IBlockState state = ((EntityFallingBlock) entity).getBlock();
            return state != null && isMagneticBlock(state);
        }
        if (entity instanceof EntityMinecart || entity instanceof EntityIronGolem || entity instanceof NucleeperEntity) {
            return true;
        }
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isSpectator()) {
            return false;
        }
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (isMagneticItem(entity instanceof net.minecraft.entity.EntityLivingBase ? ((net.minecraft.entity.EntityLivingBase) entity).getItemStackFromSlot(slot) : ItemStack.EMPTY)) {
                return true;
            }
        }
        return entity instanceof net.minecraft.entity.EntityLivingBase && ((net.minecraft.entity.EntityLivingBase) entity).isPotionActive(ACEffectRegistry.MAGNETIZING);
    }

    public static boolean isMagneticBlock(IBlockState state) {
        Block block = state.getBlock();
        return block == ACBlockRegistry.SCRAP_METAL.block()
                || block == ACBlockRegistry.SCRAP_METAL_PLATE.block()
                || block == ACBlockRegistry.METAL_SWARF.block()
                || block == ACBlockRegistry.METAL_SCAFFOLDING.block()
                || block == ACBlockRegistry.METAL_REBAR.block()
                || block == ACBlockRegistry.METAL_BARREL.block()
                || block == ACBlockRegistry.RUSTY_SCRAP_METAL.block()
                || block == ACBlockRegistry.RUSTY_SCRAP_METAL_PLATE.block()
                || block == ACBlockRegistry.RUSTY_SCAFFOLDING.block()
                || block == ACBlockRegistry.RUSTY_REBAR.block()
                || block == ACBlockRegistry.WASTE_DRUM.block()
                || block == ACBlockRegistry.MAGNETIC_ACTIVATOR.block()
                || block == ACBlockRegistry.MAGNETIC_LIGHT.block()
                || block == ACBlockRegistry.HEART_OF_IRON.block()
                || block == ACBlockRegistry.QUARRY.block()
                || block == ACBlockRegistry.NUCLEAR_SIREN.block()
                || block == ACBlockRegistry.FLOOD_BASALT.block()
                || block == Blocks.IRON_BLOCK
                || block == Blocks.IRON_ORE
                || block == Blocks.ANVIL
                || block == Blocks.IRON_BARS
                || block == Blocks.IRON_DOOR
                || block == Blocks.IRON_TRAPDOOR
                || block == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE
                || block == Blocks.HOPPER
                || block == Blocks.CAULDRON
                || block == Blocks.MOB_SPAWNER
                || block instanceof BlockRailBase;
    }

    public static boolean isUnmoveable(IBlockState state) {
        return state.getMaterial() == Material.AIR || state.getBlock() == Blocks.BEDROCK || state.getBlock() == Blocks.BARRIER || state.getBlock() instanceof com.zzhalex233.alexscaves.server.block.MagnetBlock;
    }

    public static boolean isMagneticItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        if (item instanceof ItemArmor && ((ItemArmor) item).getArmorMaterial() == ItemArmor.ArmorMaterial.IRON) {
            return true;
        }
        if (item == Items.IRON_INGOT || item == Items.IRON_NUGGET || item == Items.IRON_SWORD || item == Items.IRON_PICKAXE || item == Items.IRON_AXE || item == Items.IRON_SHOVEL || item == Items.IRON_HOE
                || item == Items.SHEARS || item == Items.FLINT_AND_STEEL || item == Items.BUCKET || item == Items.COMPASS || item == Items.MINECART || item == Items.HOPPER_MINECART
                || item == Items.CHEST_MINECART || item == Items.FURNACE_MINECART || item == Items.TNT_MINECART || item == Items.COMMAND_BLOCK_MINECART) {
            return true;
        }
        for (int id : OreDictionary.getOreIDs(stack)) {
            String name = OreDictionary.getOreName(id);
            if (name.contains("Iron") || name.equals("ingotIron") || name.equals("nuggetIron") || name.equals("blockIron") || name.equals("oreIron")) {
                return true;
            }
        }
        if (item instanceof net.minecraft.item.ItemBlock) {
            return isMagneticBlock(((net.minecraft.item.ItemBlock) item).getBlock().getStateFromMeta(stack.getMetadata()));
        }
        return item == ACBlockRegistry.METAL_SWARF.item()
                || item == ACBlockRegistry.SCRAP_METAL.item()
                || item == ACBlockRegistry.METAL_SCAFFOLDING.item()
                || item == ACBlockRegistry.METAL_BARREL.item()
                || item == ACBlockRegistry.WASTE_DRUM.item()
                || item == ACBlockRegistry.FLOOD_BASALT.item();
    }

    public static boolean isEntityOnMovingMetal(Entity entity) {
        return !(entity instanceof MovingMetalBlockEntity) && !entity.world.getEntitiesWithinAABB(MovingMetalBlockEntity.class, entity.getEntityBoundingBox().grow(0.4D)).isEmpty();
    }

    public static BlockPos centerPos(Entity entity) {
        return new BlockPos(entity.posX, entity.posY + entity.height * 0.5D, entity.posZ);
    }
}
