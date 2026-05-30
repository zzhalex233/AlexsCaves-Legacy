package com.zzhalex233.alexscaves.server.event;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.block.FrostmintBlock;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.item.RainbounceBootsItem;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = AlexsCaves.MODID)
public class ACEventHandler {
    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == ACItemRegistry.RAINBOUNCE_BOOTS.item()) {
            RainbounceBootsItem.bounce(event.getEntityLiving(), event.getDistance());
            event.setDistance(0.0F);
            event.setDamageMultiplier(0.0F);
        }
    }

    @SubscribeEvent
    public static void onFillBucket(FillBucketEvent event) {
        if (event.getResult() != Event.Result.DEFAULT || event.getEmptyBucket().getItem() != Items.BUCKET) {
            return;
        }
        RayTraceResult target = event.getTarget();
        if (target == null || target.typeOfHit != RayTraceResult.Type.BLOCK) {
            return;
        }
        BlockPos pos = target.getBlockPos();
        IBlockState state = event.getWorld().getBlockState(pos);
        if (state.getBlock() != ACBlockRegistry.PURPLE_SODA.block() || state.getValue(BlockFluidBase.LEVEL) != 0) {
            return;
        }
        event.setFilledBucket(new ItemStack(ACItemRegistry.PURPLE_SODA_BUCKET.item()));
        event.setResult(Event.Result.ALLOW);
        if (!event.getWorld().isRemote) {
            event.getWorld().setBlockToAir(pos);
            event.getWorld().playSound(null, pos, ACSoundRegistry.PURPLE_SODA_SUBMERGE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        RayTraceResult target = event.getRayTraceResult();
        if (target != null && target.typeOfHit == RayTraceResult.Type.BLOCK) {
            FrostmintBlock.scheduleColumnFall(event.getEntity().world, target.getBlockPos());
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = event.getItemStack();
        if (stack.getItem() != Items.GLASS_BOTTLE) {
            return;
        }
        RayTraceResult target = rayTraceFluid(event.getWorld(), player);
        if (target == null || target.typeOfHit != RayTraceResult.Type.BLOCK) {
            return;
        }
        BlockPos pos = target.getBlockPos();
        if (!event.getWorld().isBlockModifiable(player, pos) || !player.canPlayerEdit(pos, target.sideHit, stack)) {
            return;
        }
        IBlockState state = event.getWorld().getBlockState(pos);
        if (state.getBlock() != ACBlockRegistry.PURPLE_SODA.block() || state.getValue(BlockFluidBase.LEVEL) != 0) {
            return;
        }
        if (event.getWorld().isRemote) {
            return;
        }
        event.getWorld().playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        player.addStat(StatList.getObjectUseStats(Items.GLASS_BOTTLE));
        if (!player.inventory.addItemStackToInventory(new ItemStack(ACItemRegistry.PURPLE_SODA_BOTTLE.item()))) {
            player.dropItem(new ItemStack(ACItemRegistry.PURPLE_SODA_BOTTLE.item()), false);
        }
        player.swingArm(event.getHand());
        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        event.setCancellationResult(EnumActionResult.SUCCESS);
        event.setCanceled(true);
    }

    private static RayTraceResult rayTraceFluid(net.minecraft.world.World world, EntityPlayer player) {
        Vec3d start = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3d look = player.getLook(1.0F);
        double reach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        Vec3d end = start.add(look.x * reach, look.y * reach, look.z * reach);
        return world.rayTraceBlocks(start, end, true, false, false);
    }
}
