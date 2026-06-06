package com.zzhalex233.alexscaves.server.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class CaveMapItem extends Item {
    private static final int SEARCH_RADIUS = 2048;
    private static final int SEARCH_STEP = 64;

    public CaveMapItem() {
        setMaxStackSize(1);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!isFilled(stack)) {
            if (!worldIn.isRemote) {
                String target = getBiomeTarget(stack);
                if (target == null || target.isEmpty()) {
                    target = CaveInfoItem.caveBiomeAt(worldIn, playerIn.getPosition());
                    stack.setTagCompound(tagWithTarget(target));
                }
                BlockPos found = findNearestCaveBiome(worldIn, playerIn.getPosition(), target);
                if (found == null) {
                    playerIn.sendStatusMessage(new TextComponentTranslation("item.alexscaves.cave_map.error", SEARCH_RADIUS).setStyle(new net.minecraft.util.text.Style().setColor(TextFormatting.RED)), true);
                } else {
                    NBTTagCompound tag = stack.getTagCompound();
                    tag.setBoolean("Filled", true);
                    tag.setInteger("BiomeX", found.getX());
                    tag.setInteger("BiomeY", found.getY());
                    tag.setInteger("BiomeZ", found.getZ());
                    stack.setTagCompound(tag);
                    playerIn.sendStatusMessage(new TextComponentTranslation("item.alexscaves.cave_map.found", new TextComponentTranslation(biomeTranslationKey(target))), true);
                }
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isRemote && isSelected && isFilled(stack) && entityIn.ticksExisted % 200 == 0) {
            BlockPos pos = getBiomeBlockPos(stack);
            String target = getBiomeTarget(stack);
            if (target != null && !target.equals(CaveInfoItem.caveBiomeAt(worldIn, pos))) {
                stack.getTagCompound().setBoolean("Filled", false);
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String target = getBiomeTarget(stack);
        if (target != null && !target.isEmpty()) {
            tooltip.add(TextFormatting.GRAY + I18n.translateToLocal(biomeTranslationKey(target)));
        }
        if (isFilled(stack)) {
            BlockPos pos = getBiomeBlockPos(stack);
            tooltip.add(TextFormatting.DARK_GRAY + I18n.translateToLocalFormatted("item.alexscaves.cave_map.position", pos.getX(), pos.getY(), pos.getZ()));
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        int tabIndex = ACTabbedItem.ALL_CAVE_TABS.indexOf(tab);
        if (tabIndex >= 0) {
            items.add(createMap(CaveInfoItem.CAVE_BIOMES[tabIndex]));
        }
    }

    public static ItemStack createMap(String caveBiome) {
        ItemStack map = new ItemStack(ACItemRegistry.CAVE_MAP.item());
        map.setTagCompound(tagWithTarget(caveBiome));
        return map;
    }

    public static boolean isFilled(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().getBoolean("Filled");
    }

    public static boolean isLoading(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().getBoolean("Loading");
    }

    public static String getBiomeTarget(ItemStack stack) {
        return stack.hasTagCompound() ? stack.getTagCompound().getString("BiomeTargetResourceKey") : null;
    }

    public static BlockPos getBiomeBlockPos(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null ? BlockPos.ORIGIN : new BlockPos(tag.getInteger("BiomeX"), tag.getInteger("BiomeY"), tag.getInteger("BiomeZ"));
    }

    private static NBTTagCompound tagWithTarget(String caveBiome) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("BiomeTargetResourceKey", caveBiome);
        return tag;
    }

    @Nullable
    private static BlockPos findNearestCaveBiome(World world, BlockPos center, String target) {
        BlockPos best = null;
        int bestDist = Integer.MAX_VALUE;
        for (int radius = 0; radius <= SEARCH_RADIUS; radius += SEARCH_STEP) {
            for (int x = -radius; x <= radius; x += SEARCH_STEP) {
                for (int z = -radius; z <= radius; z += SEARCH_STEP) {
                    if (Math.abs(x) != radius && Math.abs(z) != radius) {
                        continue;
                    }
                    BlockPos pos = new BlockPos(center.getX() + x, Math.max(8, center.getY()), center.getZ() + z);
                    if (target.equals(CaveInfoItem.caveBiomeAt(world, pos))) {
                        int dist = x * x + z * z;
                        if (dist < bestDist) {
                            bestDist = dist;
                            best = pos;
                        }
                    }
                }
            }
            if (best != null) {
                return best;
            }
        }
        return null;
    }

    private static String biomeTranslationKey(String caveBiome) {
        return "biome." + caveBiome.replace(':', '.');
    }
}
