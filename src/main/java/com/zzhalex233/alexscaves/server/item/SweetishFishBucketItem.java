package com.zzhalex233.alexscaves.server.item;

import java.util.List;

import com.zzhalex233.alexscaves.server.entity.living.SweetishFishEntity;
import com.zzhalex233.alexscaves.server.entity.util.GummyColors;
import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SweetishFishBucketItem extends BucketableWaterMobItem {
    private final GummyColors color;

    public SweetishFishBucketItem(GummyColors color) {
        super(world -> new SweetishFishEntity(world, color));
        this.color = color;
    }

    @Override
    protected Block getFluidBlock() {
        return ACBlockRegistry.PURPLE_SODA.block();
    }

    @Override
    protected SoundEvent getEmptySound() {
        return ACSoundRegistry.PURPLE_SODA_UNSUBMERGE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        tooltip.add(TextFormatting.GRAY + new TextComponentTranslation("item.alexscaves.sweetish_fish_bucket.desc_" + color.name().toLowerCase()).getFormattedText());
    }
}
