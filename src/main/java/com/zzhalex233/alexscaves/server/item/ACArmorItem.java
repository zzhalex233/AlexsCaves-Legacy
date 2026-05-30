package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.armor.ACArmorModel;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ACArmorItem extends ItemArmor {
    private final String textureName;
    private final IRarity rarity;

    public ACArmorItem(ArmorMaterial material, EntityEquipmentSlot slot, String textureName) {
        this(material, slot, textureName, null);
    }

    public ACArmorItem(ArmorMaterial material, EntityEquipmentSlot slot, String textureName, IRarity rarity) {
        super(material, 0, slot);
        this.textureName = textureName;
        this.rarity = rarity;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return AlexsCaves.MODID + ":textures/armor/" + textureName + "_" + (slot == EntityEquipmentSlot.LEGS ? 1 : 0) + ".png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        ModelBiped model = ACArmorModel.get(textureName);
        return model == null ? _default : model;
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack) {
        return rarity == null ? super.getForgeRarity(stack) : rarity;
    }
}
