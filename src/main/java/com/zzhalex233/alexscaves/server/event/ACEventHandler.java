package com.zzhalex233.alexscaves.server.event;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.block.FrostmintBlock;
import com.zzhalex233.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.zzhalex233.alexscaves.server.entity.item.SeekingArrowEntity;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.item.ExtinctionSpearItem;
import com.zzhalex233.alexscaves.server.item.RainbounceBootsItem;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
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
    public static void onLivingAttack(LivingAttackEvent event) {
        EntityLivingBase living = event.getEntityLiving();
        Entity direct = event.getSource().getImmediateSource();
        ItemStack shield = living.getActiveItemStack();
        if (direct instanceof EntityArrow && !(direct instanceof SeekingArrowEntity) && !living.world.isRemote && !shield.isEmpty() && shield.getItem() == ACItemRegistry.RESISTOR_SHIELD.item() && living.isActiveItemStackBlocking() && EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.ARROW_INDUCTING, shield) > 0) {
            EntityArrow arrow = (EntityArrow) direct;
            SeekingArrowEntity seekingArrow = new SeekingArrowEntity(living.world, living);
            seekingArrow.copyLocationAndAnglesFrom(arrow);
            seekingArrow.motionX = arrow.motionX * -0.4D;
            seekingArrow.motionY = arrow.motionY * -0.4D;
            seekingArrow.motionZ = arrow.motionZ * -0.4D;
            seekingArrow.rotationYaw = arrow.rotationYaw + 180.0F;
            living.world.spawnEntity(seekingArrow);
            arrow.setDead();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        EntityLivingBase living = event.getEntityLiving();
        ItemStack stack = living.getActiveItemStack();
        if (living instanceof EntityPlayer && !living.world.isRemote && !stack.isEmpty() && stack.getItem() == ACItemRegistry.EXTINCTION_SPEAR.item() && ExtinctionSpearItem.killGrottoGhostsFor((EntityPlayer) living, true)) {
            living.world.playSound(null, living.posX, living.posY, living.posZ, SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
            event.setCanceled(true);
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
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = event.getItemStack();
        if (stack.getItem() != ACItemRegistry.HOLOCODER.item() || !(event.getTarget() instanceof EntityLivingBase) || event.getTarget() instanceof EntityArmorStand || !event.getTarget().isEntityAlive()) {
            return;
        }
        if (event.getWorld().isRemote) {
            event.setCancellationResult(EnumActionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }
        NBTTagCompound tag = stack.getTagCompound() == null ? new NBTTagCompound() : stack.getTagCompound().copy();
        tag.setUniqueId("BoundEntityUUID", event.getTarget().getUniqueID());
        NBTTagCompound entityTag = new NBTTagCompound();
        ResourceLocation id = net.minecraft.entity.EntityList.getKey(event.getTarget());
        if (id != null) {
            entityTag.setString("id", id.toString());
        }
        if (event.getTarget() instanceof EntityPlayer) {
            entityTag.setUniqueId("UUID", event.getTarget().getUniqueID());
        } else {
            event.getTarget().writeToNBTOptional(entityTag);
        }
        tag.setTag("BoundEntityTag", entityTag);
        ItemStack bound = new ItemStack(ACItemRegistry.HOLOCODER.item());
        bound.setTagCompound(tag);
        stack.shrink(1);
        player.swingArm(event.getHand());
        if (!player.inventory.addItemStackToInventory(bound)) {
            EntityItem item = player.dropItem(bound, false);
            if (item != null) {
                item.setNoPickupDelay();
                item.setThrower(player.getName());
            }
        }
        event.setCancellationResult(EnumActionResult.SUCCESS);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = event.getItemStack();
        BlockPos pos = event.getPos();
        IBlockState pottedPlant = pottedPlantStateFor(stack);
        if (pottedPlant == null || event.getWorld().getBlockState(pos).getBlock() != Blocks.FLOWER_POT) {
            return;
        }
        TileEntity tileEntity = event.getWorld().getTileEntity(pos);
        if (!(tileEntity instanceof TileEntityFlowerPot) || !((TileEntityFlowerPot) tileEntity).getFlowerItemStack().isEmpty()) {
            return;
        }
        if (!event.getWorld().isBlockModifiable(player, pos) || !player.canPlayerEdit(pos, event.getFace(), stack)) {
            return;
        }
        if (!event.getWorld().isRemote) {
            event.getWorld().setBlockState(pos, pottedPlant, 3);
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }
        player.swingArm(event.getHand());
        event.setCancellationResult(EnumActionResult.SUCCESS);
        event.setCanceled(true);
    }

    private static IBlockState pottedPlantStateFor(ItemStack stack) {
        if (stack.getItem() == ACBlockRegistry.PEWEN_PINES.item()) {
            return ACBlockRegistry.POTTED_PEWEN_PINES.block().getDefaultState();
        }
        if (stack.getItem() == ACBlockRegistry.CYCAD.item()) {
            return ACBlockRegistry.POTTED_CYCAD.block().getDefaultState();
        }
        if (stack.getItem() == ACBlockRegistry.FIDDLEHEAD.item()) {
            return ACBlockRegistry.POTTED_FIDDLEHEAD.block().getDefaultState();
        }
        if (stack.getItem() == ACBlockRegistry.CURLY_FERN.item()) {
            return ACBlockRegistry.POTTED_CURLY_FERN.block().getDefaultState();
        }
        if (stack.getItem() == ACBlockRegistry.FLYTRAP.item()) {
            return ACBlockRegistry.POTTED_FLYTRAP.block().getDefaultState();
        }
        if (stack.getItem() == ACBlockRegistry.UNDERWEED.item()) {
            return ACBlockRegistry.POTTED_UNDERWEED.block().getDefaultState();
        }
        if (stack.getItem() == ACBlockRegistry.THORNWOOD_BRANCH.item()) {
            return ACBlockRegistry.POTTED_THORNWOOD_BRANCH.block().getDefaultState();
        }
        return null;
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
