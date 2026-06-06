package com.zzhalex233.alexscaves.server.item;

import java.util.List;

import javax.annotation.Nullable;

import com.zzhalex233.alexscaves.server.config.ACConfig;
import com.zzhalex233.alexscaves.server.misc.CaveBookProgress;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class CaveInfoItem extends Item {
    public static final String[] CAVE_BIOMES = {
            "alexscaves:magnetic_caves",
            "alexscaves:primordial_caves",
            "alexscaves:toxic_caves",
            "alexscaves:abyssal_chasm",
            "alexscaves:forlorn_hollows",
            "alexscaves:candy_cavity"
    };

    private final boolean hideCaveId;

    public CaveInfoItem(boolean hideCaveId) {
        this.hideCaveId = hideCaveId;
        setMaxStackSize(1);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        String caveBiome = getCaveBiome(stack);
        if (this == ACItemRegistry.CAVE_CODEX.item() && caveBiome != null) {
            CaveBookProgress progress = CaveBookProgress.getCaveBookProgress(playerIn);
            if (progress.unlockNextFor(caveBiome, ACConfig.isOnlyOneResearchNeeded())) {
                playerIn.swingArm(handIn);
                if (!worldIn.isRemote) {
                    CaveBookProgress.saveCaveBookProgress(progress, playerIn);
                    CaveBookProgress.Subcategory subcategory = progress.getLastUnlockedCategory(caveBiome);
                    TextComponentTranslation biomeTitle = new TextComponentTranslation(biomeTranslationKey(caveBiome));
                    if (ACConfig.isOnlyOneResearchNeeded()) {
                        playerIn.sendStatusMessage(new TextComponentTranslation("item.alexscaves.cave_codex.add_all", biomeTitle), true);
                    } else {
                        playerIn.sendStatusMessage(new TextComponentTranslation("item.alexscaves.cave_codex.add", biomeTitle, new TextComponentTranslation("item.alexscaves.cave_book." + subcategory.name().toLowerCase(java.util.Locale.ROOT))), true);
                    }
                }
                if (!playerIn.capabilities.isCreativeMode) {
                    stack.shrink(1);
                }
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
            if (!worldIn.isRemote) {
                playerIn.sendStatusMessage(new TextComponentTranslation("item.alexscaves.cave_codex.end").setStyle(new net.minecraft.util.text.Style().setColor(TextFormatting.RED)), true);
            }
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String caveBiome = getCaveBiome(stack);
        if (caveBiome != null && !hideCaveId) {
            tooltip.add(TextFormatting.GRAY + I18n.translateToLocal(biomeTranslationKey(caveBiome)));
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        int tabIndex = ACTabbedItem.ALL_CAVE_TABS.indexOf(tab);
        if (tabIndex >= 0) {
            items.add(create(this, CAVE_BIOMES[tabIndex]));
        }
    }

    public static ItemStack create(Item item, String caveBiome) {
        ItemStack stack = new ItemStack(item);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("CaveBiome", caveBiome);
        stack.setTagCompound(tag);
        return stack;
    }

    public static String getCaveBiome(ItemStack stack) {
        return stack.hasTagCompound() ? stack.getTagCompound().getString("CaveBiome") : null;
    }

    public static String caveBiomeAt(World world, net.minecraft.util.math.BlockPos pos) {
        String biomeName = world.getBiome(pos).getRegistryName() == null ? "" : world.getBiome(pos).getRegistryName().toString();
        for (String caveBiome : CAVE_BIOMES) {
            if (biomeName.equals(caveBiome) || biomeName.endsWith(caveBiome.substring(caveBiome.indexOf(':') + 1))) {
                return caveBiome;
            }
        }
        return CAVE_BIOMES[Math.abs((pos.getX() >> 6) + (pos.getZ() >> 6)) % CAVE_BIOMES.length];
    }

    private static String biomeTranslationKey(String caveBiome) {
        return "biome." + caveBiome.replace(':', '.');
    }
}
