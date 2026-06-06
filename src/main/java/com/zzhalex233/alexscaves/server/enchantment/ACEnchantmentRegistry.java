package com.zzhalex233.alexscaves.server.enchantment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.google.common.base.Predicate;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;

public class ACEnchantmentRegistry {
    private static final List<Enchantment> MUTABLE_ENCHANTMENTS = new ArrayList<>();
    public static final EnumEnchantmentType SEA_STAFF_TYPE = EnumHelper.addEnchantmentType("ALEXSCAVES_SEA_STAFF", (Predicate<Item>) item -> item == ACItemRegistry.SEA_STAFF.item());
    public static final EnumEnchantmentType ORTHOLANCE_TYPE = EnumHelper.addEnchantmentType("ALEXSCAVES_ORTHOLANCE", (Predicate<Item>) item -> item == ACItemRegistry.ORTHOLANCE.item());
    public static final EnumEnchantmentType SHOT_GUM_TYPE = EnumHelper.addEnchantmentType("ALEXSCAVES_SHOT_GUM", (Predicate<Item>) item -> item == ACItemRegistry.SHOT_GUM.item());
    public static final EnumEnchantmentType SUGAR_STAFF_TYPE = EnumHelper.addEnchantmentType("ALEXSCAVES_SUGAR_STAFF", (Predicate<Item>) item -> item == ACItemRegistry.SUGAR_STAFF.item());
    public static final EnumEnchantmentType CANDY_CANE_HOOK_TYPE = EnumHelper.addEnchantmentType("ALEXSCAVES_CANDY_CANE_HOOK", (Predicate<Item>) item -> item == ACItemRegistry.CANDY_CANE_HOOK.item());
    public static final EnumEnchantmentType DREADBOW_TYPE = EnumHelper.addEnchantmentType("ALEXSCAVES_DREADBOW", (Predicate<Item>) item -> item == ACItemRegistry.DREADBOW.item());
    public static final EnumEnchantmentType GALENA_GAUNTLET_TYPE = EnumHelper.addEnchantmentType("ALEXSCAVES_GALENA_GAUNTLET", (Predicate<Item>) item -> item == ACItemRegistry.GALENA_GAUNTLET.item());
    public static final EnumEnchantmentType RESISTOR_SHIELD_TYPE = EnumHelper.addEnchantmentType("ALEXSCAVES_RESISTOR_SHIELD", (Predicate<Item>) item -> item == ACItemRegistry.RESISTOR_SHIELD.item());
    public static final EnumEnchantmentType RAYGUN_TYPE = EnumHelper.addEnchantmentType("ALEXSCAVES_RAYGUN", (Predicate<Item>) item -> item == ACItemRegistry.RAYGUN.item());
    public static final EnumEnchantmentType TOTEM_OF_POSSESSION_TYPE = EnumHelper.addEnchantmentType("ALEXSCAVES_TOTEM_OF_POSSESSION", (Predicate<Item>) item -> item == ACItemRegistry.TOTEM_OF_POSSESSION.item());
    public static final EnumEnchantmentType EXTINCTION_SPEAR_TYPE = EnumHelper.addEnchantmentType("ALEXSCAVES_EXTINCTION_SPEAR", (Predicate<Item>) item -> item == ACItemRegistry.EXTINCTION_SPEAR.item());

    public static final ACWeaponEnchantment FIELD_EXTENSION = register("field_extension", new ACWeaponEnchantment("field_extension", Enchantment.Rarity.COMMON, GALENA_GAUNTLET_TYPE, 4, 6, EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND));
    public static final ACWeaponEnchantment CRYSTALLIZATION = register("crystallization", new ACWeaponEnchantment("crystallization", Enchantment.Rarity.RARE, GALENA_GAUNTLET_TYPE, 1, 15, EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND));
    public static final ACWeaponEnchantment FERROUS_HASTE = register("ferrous_haste", new ACWeaponEnchantment("ferrous_haste", Enchantment.Rarity.RARE, GALENA_GAUNTLET_TYPE, 1, 15, EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND));
    public static final ACWeaponEnchantment HEAVY_SLAM = register("heavy_slam", new ACWeaponEnchantment("heavy_slam", Enchantment.Rarity.COMMON, RESISTOR_SHIELD_TYPE, 3, 6, EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND));
    public static final ACWeaponEnchantment ARROW_INDUCTING = register("arrow_inducting", new ACWeaponEnchantment("arrow_inducting", Enchantment.Rarity.RARE, RESISTOR_SHIELD_TYPE, 1, 18, EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND));
    public static final ACWeaponEnchantment ENERGY_EFFICIENCY = register("energy_efficiency", new ACWeaponEnchantment("energy_efficiency", Enchantment.Rarity.COMMON, RAYGUN_TYPE, 3, 5, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment SOLAR = register("solar", new ACWeaponEnchantment("solar", Enchantment.Rarity.COMMON, RAYGUN_TYPE, 1, 14, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment X_RAY = register("x_ray", new ACWeaponEnchantment("x_ray", Enchantment.Rarity.COMMON, RAYGUN_TYPE, 1, 12, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment GAMMA_RAY = register("gamma_ray", new ACWeaponEnchantment("gamma_ray", Enchantment.Rarity.RARE, RAYGUN_TYPE, 1, 18, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment SECOND_WAVE = register("second_wave", new ACWeaponEnchantment("second_wave", Enchantment.Rarity.RARE, ORTHOLANCE_TYPE, 1, 12, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment FLINGING = register("flinging", new ACWeaponEnchantment("flinging", Enchantment.Rarity.COMMON, ORTHOLANCE_TYPE, 3, 8, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment SEA_SWING = register("sea_swing", new ACWeaponEnchantment("sea_swing", Enchantment.Rarity.RARE, ORTHOLANCE_TYPE, 1, 10, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment TSUNAMI = register("tsunami", new ACWeaponEnchantment("tsunami", Enchantment.Rarity.VERY_RARE, ORTHOLANCE_TYPE, 1, 20, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment TARGETED_RICOCHET = register("targeted_ricochet", new ACWeaponEnchantment("targeted_ricochet", Enchantment.Rarity.RARE, SHOT_GUM_TYPE, 1, 16, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment TRIPLE_SPLIT = register("triple_split", new ACWeaponEnchantment("triple_split", Enchantment.Rarity.RARE, SHOT_GUM_TYPE, 1, 12, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment BOUNCY_BALL = register("bouncy_ball", new ACWeaponEnchantment("bouncy_ball", Enchantment.Rarity.COMMON, SHOT_GUM_TYPE, 3, 7, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment EXPLOSIVE_FLAVOR = register("explosive_flavor", new ACWeaponEnchantment("explosive_flavor", Enchantment.Rarity.VERY_RARE, SHOT_GUM_TYPE, 1, 16, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment SPELL_LASTING = register("spell_lasting", new ACWeaponEnchantment("spell_lasting", Enchantment.Rarity.COMMON, SUGAR_STAFF_TYPE, 3, 8, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment PEPPERMINT_PUNTING = register("peppermint_punting", new ACWeaponEnchantment("peppermint_punting", Enchantment.Rarity.RARE, SUGAR_STAFF_TYPE, 1, 12, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment HUMUNGOUS_HEX = register("humungous_hex", new ACWeaponEnchantment("humungous_hex", Enchantment.Rarity.UNCOMMON, SUGAR_STAFF_TYPE, 2, 9, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment MULTIPLE_MINT = register("multiple_mint", new ACWeaponEnchantment("multiple_mint", Enchantment.Rarity.UNCOMMON, SUGAR_STAFF_TYPE, 2, 9, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment SEEKCANDY = register("seekcandy", new ACWeaponEnchantment("seekcandy", Enchantment.Rarity.RARE, SUGAR_STAFF_TYPE, 1, 16, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment FAR_FLUNG = register("far_flung", new ACWeaponEnchantment("far_flung", Enchantment.Rarity.COMMON, CANDY_CANE_HOOK_TYPE, 3, 6, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment SHARP_CANE = register("sharp_cane", new ACWeaponEnchantment("sharp_cane", Enchantment.Rarity.COMMON, CANDY_CANE_HOOK_TYPE, 2, 8, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment STRAIGHT_HOOK = register("straight_hook", new ACWeaponEnchantment("straight_hook", Enchantment.Rarity.RARE, CANDY_CANE_HOOK_TYPE, 1, 12, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment PRECISE_VOLLEY = register("precise_volley", new ACWeaponEnchantment("precise_volley", Enchantment.Rarity.RARE, DREADBOW_TYPE, 1, 18, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment DARK_NOCK = register("dark_nock", new ACWeaponEnchantment("dark_nock", Enchantment.Rarity.RARE, DREADBOW_TYPE, 3, 10, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment RELENTLESS_DARKNESS = register("relentless_darkness", new ACWeaponEnchantment("relentless_darkness", Enchantment.Rarity.VERY_RARE, DREADBOW_TYPE, 1, 20, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment TWILIGHT_PERFECTION = register("twilight_perfection", new ACWeaponEnchantment("twilight_perfection", Enchantment.Rarity.RARE, DREADBOW_TYPE, 3, 7, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment SHADED_RESPITE = register("shaded_respite", new ACWeaponEnchantment("shaded_respite", Enchantment.Rarity.VERY_RARE, DREADBOW_TYPE, 1, 9, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment ENVELOPING_BUBBLE = register("enveloping_bubble", new ACWeaponEnchantment("enveloping_bubble", Enchantment.Rarity.RARE, SEA_STAFF_TYPE, 1, 13, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment BOUNCING_BOLT = register("bouncing_bolt", new ACWeaponEnchantment("bouncing_bolt", Enchantment.Rarity.RARE, SEA_STAFF_TYPE, 1, 13, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment SEAPAIRING = register("seapairing", new ACWeaponEnchantment("seapairing", Enchantment.Rarity.VERY_RARE, SEA_STAFF_TYPE, 1, 10, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment TRIPLE_SPLASH = register("triple_splash", new ACWeaponEnchantment("triple_splash", Enchantment.Rarity.RARE, SEA_STAFF_TYPE, 1, 15, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment SOAK_SEEKING = register("soak_seeking", new ACWeaponEnchantment("soak_seeking", Enchantment.Rarity.COMMON, SEA_STAFF_TYPE, 3, 5, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment DETONATING_DEATH = register("detonating_death", new ACWeaponEnchantment("detonating_death", Enchantment.Rarity.RARE, TOTEM_OF_POSSESSION_TYPE, 1, 11, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment RAPID_POSSESSION = register("rapid_possession", new ACWeaponEnchantment("rapid_possession", Enchantment.Rarity.COMMON, TOTEM_OF_POSSESSION_TYPE, 3, 5, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment SIGHTLESS = register("sightless", new ACWeaponEnchantment("sightless", Enchantment.Rarity.RARE, TOTEM_OF_POSSESSION_TYPE, 1, 13, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment ASTRAL_TRANSFERRING = register("astral_transferring", new ACWeaponEnchantment("astral_transferring", Enchantment.Rarity.RARE, TOTEM_OF_POSSESSION_TYPE, 1, 12, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment PLUMMETING_FLIGHT = register("plummeting_flight", new ACWeaponEnchantment("plummeting_flight", Enchantment.Rarity.RARE, EXTINCTION_SPEAR_TYPE, 3, 13, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment HERD_PHALANX = register("herd_phalanx", new ACWeaponEnchantment("herd_phalanx", Enchantment.Rarity.RARE, EXTINCTION_SPEAR_TYPE, 3, 13, EntityEquipmentSlot.MAINHAND));
    public static final ACWeaponEnchantment CHOMPING_SPIRIT = register("chomping_spirit", new ACWeaponEnchantment("chomping_spirit", Enchantment.Rarity.RARE, EXTINCTION_SPEAR_TYPE, 2, 10, EntityEquipmentSlot.MAINHAND));

    public static final List<Enchantment> ENCHANTMENTS = Collections.unmodifiableList(MUTABLE_ENCHANTMENTS);

    public static boolean areCompatible(ACWeaponEnchantment enchantment1, Enchantment enchantment2) {
        if (enchantment1 == SECOND_WAVE && enchantment2 == TSUNAMI || enchantment1 == TSUNAMI && enchantment2 == SECOND_WAVE) {
            return false;
        }
        if (enchantment1 == X_RAY && enchantment2 == GAMMA_RAY || enchantment1 == GAMMA_RAY && enchantment2 == X_RAY) {
            return false;
        }
        if (enchantment1 == DETONATING_DEATH && enchantment2 == ASTRAL_TRANSFERRING || enchantment1 == ASTRAL_TRANSFERRING && enchantment2 == DETONATING_DEATH) {
            return false;
        }
        if (enchantment1 == TARGETED_RICOCHET && enchantment2 == TRIPLE_SPLIT || enchantment1 == TRIPLE_SPLIT && enchantment2 == TARGETED_RICOCHET) {
            return false;
        }
        return enchantment1 != BOUNCING_BOLT || enchantment2 != TRIPLE_SPLASH;
    }

    private static ACWeaponEnchantment register(String name, ACWeaponEnchantment enchantment) {
        ResourceLocation id = new ResourceLocation(AlexsCaves.MODID, name);
        enchantment.setRegistryName(id);
        MUTABLE_ENCHANTMENTS.add(enchantment);
        return enchantment;
    }
}
