package com.zzhalex233.alexscaves.server.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.entity.living.GossamerWormEntity;
import com.zzhalex233.alexscaves.server.entity.living.LanternfishEntity;
import com.zzhalex233.alexscaves.server.entity.living.SeaPigEntity;
import com.zzhalex233.alexscaves.server.entity.living.TripodfishEntity;
import com.zzhalex233.alexscaves.server.entity.util.GummyColors;
import com.zzhalex233.alexscaves.server.entity.item.CinderBrickEntity;
import com.zzhalex233.alexscaves.server.entity.item.GuanoEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;
import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.SoundEvents;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.common.util.EnumHelper;

public class ACItemRegistry {
    public static final IRarity RARITY_NUCLEAR = rarity("alexscaves:nuclear", TextFormatting.GREEN);
    public static final IRarity RARITY_DEMONIC = rarity("alexscaves:demonic", TextFormatting.DARK_PURPLE);
    public static final IRarity RARITY_SWEET = rarity("alexscaves:sweet", TextFormatting.LIGHT_PURPLE);
    public static final IRarity RARITY_RAINBOW = rarity("alexscaves:rainbow", TextFormatting.AQUA);
    public static final ItemArmor.ArmorMaterial PRIMORDIAL_ARMOR_MATERIAL = EnumHelper.addArmorMaterial("ALEXSCAVES_PRIMORDIAL", AlexsCaves.MODID + ":primordial", 20, new int[] {3, 4, 3, 2}, 25, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F);
    public static final ItemArmor.ArmorMaterial HAZMAT_SUIT_ARMOR_MATERIAL = EnumHelper.addArmorMaterial("ALEXSCAVES_HAZMAT_SUIT", AlexsCaves.MODID + ":hazmat_suit", 20, new int[] {2, 4, 5, 2}, 25, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.5F);
    public static final ItemArmor.ArmorMaterial DIVING_SUIT_ARMOR_MATERIAL = EnumHelper.addArmorMaterial("ALEXSCAVES_DIVING_SUIT", AlexsCaves.MODID + ":diving_suit", 20, new int[] {2, 6, 5, 2}, 25, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F);
    public static final ItemArmor.ArmorMaterial DARKNESS_ARMOR_MATERIAL = EnumHelper.addArmorMaterial("ALEXSCAVES_DARKNESS", AlexsCaves.MODID + ":darkness", 15, new int[] {4, 5, 1, 1}, 40, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.5F);
    public static final ItemArmor.ArmorMaterial RAINBOUNCE_ARMOR_MATERIAL = EnumHelper.addArmorMaterial("ALEXSCAVES_RAINBOUNCE", AlexsCaves.MODID + ":rainbounce", 6, new int[] {2, 2, 1, 2}, 40, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0F);
    public static final ItemArmor.ArmorMaterial GINGERBREAD_ARMOR_MATERIAL = EnumHelper.addArmorMaterial("ALEXSCAVES_GINGERBREAD", AlexsCaves.MODID + ":gingerbread", 10, new int[] {2, 4, 5, 2}, 25, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F);

    private static final List<ItemEntry> MUTABLE_ITEMS = new ArrayList<>();

    public static final ItemEntry ADVANCEMENT_TAB_ICON = hiddenStackLimitedRarityItem("advancement_tab_icon", 1, net.minecraft.item.EnumRarity.UNCOMMON);
    public static final ItemEntry CAVE_TABLET = register("cave_tablet", CaveTab.MAGNETIC_CAVES, new CaveInfoItem(true), null);
    public static final ItemEntry CAVE_CODEX = register("cave_codex", CaveTab.MAGNETIC_CAVES, new CaveInfoItem(false), null);
    public static final ItemEntry CAVE_BOOK = register("cave_book", CaveTab.MAGNETIC_CAVES, new CaveBookItem(), null);

    public static final ItemEntry RAW_SCARLET_NEODYMIUM = item("raw_scarlet_neodymium", CaveTab.MAGNETIC_CAVES);
    public static final ItemEntry RAW_AZURE_NEODYMIUM = item("raw_azure_neodymium", CaveTab.MAGNETIC_CAVES);
    public static final ItemEntry SCARLET_NEODYMIUM_INGOT = item("scarlet_neodymium_ingot", CaveTab.MAGNETIC_CAVES);
    public static final ItemEntry AZURE_NEODYMIUM_INGOT = item("azure_neodymium_ingot", CaveTab.MAGNETIC_CAVES);
    public static final ItemEntry TELECORE = item("telecore", CaveTab.MAGNETIC_CAVES);
    public static final ItemEntry NOTOR_GIZMO = item("notor_gizmo", CaveTab.MAGNETIC_CAVES);
    public static final ItemEntry HEAVYWEIGHT = item("heavyweight", CaveTab.MAGNETIC_CAVES);
    public static final ItemEntry FERROUSLIME_BALL = item("ferrouslime_ball", CaveTab.MAGNETIC_CAVES);
    public static final ItemEntry DISC_FRAGMENT_FUSION = item("disc_fragment_fusion", CaveTab.TOXIC_CAVES);
    public static final ItemEntry MUSIC_DISC_FUSION = record("music_disc_fusion", CaveTab.TOXIC_CAVES, ACSoundRegistry.FUSION_MUSIC_DISC, RARITY_NUCLEAR);

    public static final ItemEntry PINE_NUTS = food("pine_nuts", CaveTab.PRIMORDIAL_CAVES, Food.of(2, 0.175F));
    public static final ItemEntry PEWEN_SAP = item("pewen_sap", CaveTab.PRIMORDIAL_CAVES);
    public static final ItemEntry AMBER_CURIOSITY = item("amber_curiosity", CaveTab.PRIMORDIAL_CAVES);
    public static final ItemEntry DINOSAUR_NUGGET = food("dinosaur_nugget", CaveTab.PRIMORDIAL_CAVES, Food.of(3, 0.3F).withMeat().withFastEating());
    public static final ItemEntry TOUGH_HIDE = item("tough_hide", CaveTab.PRIMORDIAL_CAVES);
    public static final ItemEntry HEAVY_BONE = stackLimitedItem("heavy_bone", CaveTab.PRIMORDIAL_CAVES, 16);
    public static final ItemEntry PRIMITIVE_CLUB = register("primitive_club", CaveTab.PRIMORDIAL_CAVES, new PrimitiveClubItem(), null);
    public static final ItemEntry OMINOUS_CATALYST = rarityItem("ominous_catalyst", CaveTab.PRIMORDIAL_CAVES, net.minecraft.item.EnumRarity.UNCOMMON);
    public static final ItemEntry TECTONIC_SHARD = rarityItem("tectonic_shard", CaveTab.PRIMORDIAL_CAVES, RARITY_DEMONIC);
    public static final ItemEntry DINOSAUR_POTTERY_SHERD = item("dinosaur_pottery_sherd", CaveTab.PRIMORDIAL_CAVES);
    public static final ItemEntry FOOTPRINT_POTTERY_SHERD = item("footprint_pottery_sherd", CaveTab.PRIMORDIAL_CAVES);
    public static final ItemEntry TRILOCARIS_BUCKET = register("trilocaris_bucket", CaveTab.PRIMORDIAL_CAVES, new TrilocarisBucketItem(), null);
    public static final ItemEntry TRILOCARIS_TAIL = food("trilocaris_tail", CaveTab.PRIMORDIAL_CAVES, Food.of(2, 0.3F).withMeat());
    public static final ItemEntry COOKED_TRILOCARIS_TAIL = food("cooked_trilocaris_tail", CaveTab.PRIMORDIAL_CAVES, Food.of(5, 0.5F).withMeat());
    public static final ItemEntry SERENE_SALAD = food("serene_salad", CaveTab.PRIMORDIAL_CAVES, Food.of(5, 0.35F).withBowlRemainder());
    public static final ItemEntry SEETHING_STEW = food("seething_stew", CaveTab.PRIMORDIAL_CAVES, Food.of(6, 0.6F).effect(new PotionEffect(ACEffectRegistry.RAGE, 2200), 1.0F).withBowlRemainder());
    public static final ItemEntry PRIMORDIAL_SOUP = food("primordial_soup", CaveTab.PRIMORDIAL_CAVES, Food.of(6, 0.6F).effect(new PotionEffect(MobEffects.HASTE, 800), 1.0F).withBowlRemainder());
    public static final ItemEntry PRIMORDIAL_HELMET = armor("primordial_helmet", CaveTab.PRIMORDIAL_CAVES, new PrimordialArmorItem(PRIMORDIAL_ARMOR_MATERIAL, EntityEquipmentSlot.HEAD));
    public static final ItemEntry PRIMORDIAL_TUNIC = armor("primordial_tunic", CaveTab.PRIMORDIAL_CAVES, new PrimordialArmorItem(PRIMORDIAL_ARMOR_MATERIAL, EntityEquipmentSlot.CHEST));
    public static final ItemEntry PRIMORDIAL_PANTS = armor("primordial_pants", CaveTab.PRIMORDIAL_CAVES, new PrimordialArmorItem(PRIMORDIAL_ARMOR_MATERIAL, EntityEquipmentSlot.LEGS));
    public static final ItemEntry LIMESTONE_SPEAR = register("limestone_spear", CaveTab.PRIMORDIAL_CAVES, new LimestoneSpearItem(), null);
    public static final ItemEntry DINOSAUR_TRAIN = hiddenStackLimitedRarityItem("dinosaur_train", 1, net.minecraft.item.EnumRarity.UNCOMMON);

    public static final ItemEntry SULFUR_DUST = item("sulfur_dust", CaveTab.TOXIC_CAVES);
    public static final ItemEntry RADON_BOTTLE = stackLimitedItem("radon_bottle", CaveTab.TOXIC_CAVES, 16).withContainer(Items.GLASS_BOTTLE);
    public static final ItemEntry RADGILL = food("radgill", CaveTab.TOXIC_CAVES, Food.of(2, 0.2F).effect(new PotionEffect(ACEffectRegistry.IRRADIATED, 2000), 1.0F));
    public static final ItemEntry COOKED_RADGILL = food("cooked_radgill", CaveTab.TOXIC_CAVES, Food.of(5, 0.3F).effect(new PotionEffect(ACEffectRegistry.IRRADIATED, 1000), 0.1F));
    public static final ItemEntry ACID_BUCKET = register("acid_bucket", CaveTab.TOXIC_CAVES, new AcidBucketItem(), null);
    public static final ItemEntry RADGILL_BUCKET = register("radgill_bucket", CaveTab.TOXIC_CAVES, new RadgillBucketItem(), null);
    public static final ItemEntry URANIUM = radioactiveItem("uranium", CaveTab.TOXIC_CAVES, 0.001F, null);
    public static final ItemEntry URANIUM_SHARD = radioactiveItem("uranium_shard", CaveTab.TOXIC_CAVES, 0.001F, null);
    public static final ItemEntry SPELUNKIE = radiationRemovingFood("spelunkie", CaveTab.TOXIC_CAVES, Food.of(2, 0.1F).withFastEating());
    public static final ItemEntry SLAM = radiationRemovingFood("slam", CaveTab.TOXIC_CAVES, Food.of(4, 0.5F).withMeat().effect(new PotionEffect(MobEffects.STRENGTH, 400), 1.0F));
    public static final ItemEntry GREEN_SOYLENT = radiationRemovingFood("green_soylent", CaveTab.TOXIC_CAVES, Food.of(3, 0.35F).withAlwaysEdible().withMeat().withDrink());
    public static final ItemEntry CINDER_BRICK = register("cinder_brick", CaveTab.TOXIC_CAVES, new ThrownProjectileItem(player -> new CinderBrickEntity(player.world, player), -20.0F, 0.65F, 0.9F), null);
    public static final ItemEntry TOXIC_PASTE = item("toxic_paste", CaveTab.TOXIC_CAVES);
    public static final ItemEntry POLYMER_PLATE = item("polymer_plate", CaveTab.TOXIC_CAVES);
    public static final ItemEntry HAZMAT_MASK = armor("hazmat_mask", CaveTab.TOXIC_CAVES, new HazmatArmorItem(HAZMAT_SUIT_ARMOR_MATERIAL, EntityEquipmentSlot.HEAD));
    public static final ItemEntry HAZMAT_CHESTPLATE = armor("hazmat_chestplate", CaveTab.TOXIC_CAVES, new HazmatArmorItem(HAZMAT_SUIT_ARMOR_MATERIAL, EntityEquipmentSlot.CHEST));
    public static final ItemEntry HAZMAT_LEGGINGS = armor("hazmat_leggings", CaveTab.TOXIC_CAVES, new HazmatArmorItem(HAZMAT_SUIT_ARMOR_MATERIAL, EntityEquipmentSlot.LEGS));
    public static final ItemEntry HAZMAT_BOOTS = armor("hazmat_boots", CaveTab.TOXIC_CAVES, new HazmatArmorItem(HAZMAT_SUIT_ARMOR_MATERIAL, EntityEquipmentSlot.FEET));
    public static final ItemEntry FISSILE_CORE = radioactiveItem("fissile_core", CaveTab.TOXIC_CAVES, 0.001F, net.minecraft.item.EnumRarity.UNCOMMON);
    public static final ItemEntry CHARRED_REMNANT = radioactiveItem("charred_remnant", CaveTab.TOXIC_CAVES, 0.0005F, null);

    public static final ItemEntry LANTERNFISH = food("lanternfish", CaveTab.ABYSSAL_CHASM, Food.of(1, 0.175F).withFastEating());
    public static final ItemEntry COOKED_LANTERNFISH = food("cooked_lanternfish", CaveTab.ABYSSAL_CHASM, Food.of(2, 0.3F).withFastEating());
    public static final ItemEntry LANTERNFISH_BUCKET = register("lanternfish_bucket", CaveTab.ABYSSAL_CHASM, new BucketableWaterMobItem(LanternfishEntity::new), null);
    public static final ItemEntry TRIPODFISH = food("tripodfish", CaveTab.ABYSSAL_CHASM, Food.of(2, 0.2F));
    public static final ItemEntry COOKED_TRIPODFISH = food("cooked_tripodfish", CaveTab.ABYSSAL_CHASM, Food.of(5, 0.34F));
    public static final ItemEntry TRIPODFISH_BUCKET = register("tripodfish_bucket", CaveTab.ABYSSAL_CHASM, new BucketableWaterMobItem(TripodfishEntity::new), null);
    public static final ItemEntry SEA_PIG_BUCKET = register("sea_pig_bucket", CaveTab.ABYSSAL_CHASM, new BucketableWaterMobItem(SeaPigEntity::new), null);
    public static final ItemEntry GOSSAMER_WORM_BUCKET = register("gossamer_worm_bucket", CaveTab.ABYSSAL_CHASM, new BucketableWaterMobItem(GossamerWormEntity::new), null);
    public static final ItemEntry SEA_PIG = food("sea_pig", CaveTab.ABYSSAL_CHASM, Food.of(1, 0.2F).effect(new PotionEffect(MobEffects.HUNGER, 1200), 0.7F));
    public static final ItemEntry MARINE_SNOW = register("marine_snow", CaveTab.ABYSSAL_CHASM, new MarineSnowItem(), null);
    public static final ItemEntry BIOLUMINESSCENCE = item("bioluminesscence", CaveTab.ABYSSAL_CHASM);
    public static final ItemEntry PEARL = item("pearl", CaveTab.ABYSSAL_CHASM);
    public static final ItemEntry COOKED_MUSSEL = food("cooked_mussel", CaveTab.ABYSSAL_CHASM, Food.of(4, 0.3F).withFastEating());
    public static final ItemEntry DEEP_SEA_SUSHI_ROLL = food("deep_sea_sushi_roll", CaveTab.ABYSSAL_CHASM, Food.of(7, 0.4F));
    public static final ItemEntry SEA_GLASS_SHARDS = item("sea_glass_shards", CaveTab.ABYSSAL_CHASM);
    public static final ItemEntry DIVING_HELMET = armor("diving_helmet", CaveTab.ABYSSAL_CHASM, new DivingArmorItem(DIVING_SUIT_ARMOR_MATERIAL, EntityEquipmentSlot.HEAD));
    public static final ItemEntry DIVING_CHESTPLATE = armor("diving_chestplate", CaveTab.ABYSSAL_CHASM, new DivingArmorItem(DIVING_SUIT_ARMOR_MATERIAL, EntityEquipmentSlot.CHEST));
    public static final ItemEntry DIVING_LEGGINGS = armor("diving_leggings", CaveTab.ABYSSAL_CHASM, new DivingArmorItem(DIVING_SUIT_ARMOR_MATERIAL, EntityEquipmentSlot.LEGS));
    public static final ItemEntry DIVING_BOOTS = armor("diving_boots", CaveTab.ABYSSAL_CHASM, new DivingArmorItem(DIVING_SUIT_ARMOR_MATERIAL, EntityEquipmentSlot.FEET));
    public static final ItemEntry FLOATER = register("floater", CaveTab.ABYSSAL_CHASM, new FloaterItem(), null);
    public static final ItemEntry GUARDIAN_POTTERY_SHERD = item("guardian_pottery_sherd", CaveTab.ABYSSAL_CHASM);
    public static final ItemEntry HERO_POTTERY_SHERD = item("hero_pottery_sherd", CaveTab.ABYSSAL_CHASM);
    public static final ItemEntry GAME_CONTROLLER = hiddenRarityItem("game_controller", net.minecraft.item.EnumRarity.RARE);
    public static final ItemEntry STINKY_FISH = hiddenFood("stinky_fish", Food.of(1, 0.1F).effect(new PotionEffect(ACEffectRegistry.STUNNED, 100), 1.0F), net.minecraft.item.EnumRarity.RARE);
    public static final ItemEntry IMMORTAL_EMBRYO = rarityItem("immortal_embryo", CaveTab.ABYSSAL_CHASM, net.minecraft.item.EnumRarity.EPIC);

    public static final ItemEntry DARK_TATTERS = item("dark_tatters", CaveTab.FORLORN_HOLLOWS);
    public static final ItemEntry CORRODENT_TEETH = item("corrodent_teeth", CaveTab.FORLORN_HOLLOWS);
    public static final ItemEntry VESPER_WING = food("vesper_wing", CaveTab.FORLORN_HOLLOWS, Food.of(3, 0.2F).effect(new PotionEffect(MobEffects.HUNGER, 1200), 1.0F));
    public static final ItemEntry VESPER_STEW = food("vesper_stew", CaveTab.FORLORN_HOLLOWS, Food.of(5, 0.3F).withAlwaysEdible().effect(new PotionEffect(MobEffects.NIGHT_VISION, 2400), 1.0F).withBowlRemainder());
    public static final ItemEntry MOTH_DUST = register("moth_dust", CaveTab.FORLORN_HOLLOWS, new MothDustItem(), null);
    public static final ItemEntry OCCULT_GEM = register("occult_gem", CaveTab.FORLORN_HOLLOWS, new OccultGemItem(), null);
    public static final ItemEntry DESOLATE_DAGGER = register("desolate_dagger", CaveTab.FORLORN_HOLLOWS, new DesolateDaggerItem(), null);
    public static final ItemEntry PURE_DARKNESS = rarityItem("pure_darkness", CaveTab.FORLORN_HOLLOWS, RARITY_DEMONIC);
    public static final ItemEntry SHADOW_SILK = item("shadow_silk", CaveTab.FORLORN_HOLLOWS);
    public static final ItemEntry HOOD_OF_DARKNESS = armor("hood_of_darkness", CaveTab.FORLORN_HOLLOWS, new DarknessArmorItem(DARKNESS_ARMOR_MATERIAL, EntityEquipmentSlot.HEAD));
    public static final ItemEntry CLOAK_OF_DARKNESS = armor("cloak_of_darkness", CaveTab.FORLORN_HOLLOWS, new DarknessArmorItem(DARKNESS_ARMOR_MATERIAL, EntityEquipmentSlot.CHEST));
    public static final ItemEntry GUANO = register("guano", CaveTab.FORLORN_HOLLOWS, new ThrownProjectileItem(player -> new GuanoEntity(player.world, player), 0.0F, 1.0F, 1.0F), null);
    public static final ItemEntry FERTILIZER = register("fertilizer", CaveTab.FORLORN_HOLLOWS, new FertilizerItem(), null);
    public static final ItemEntry DARKENED_APPLE = darkenedApple("darkened_apple", CaveTab.FORLORN_HOLLOWS, Food.of(4, 0.35F).withAlwaysEdible());

    public static final ItemEntry PEPPERMINT_POWDER = food("peppermint_powder", CaveTab.CANDY_CAVITY, Food.of(1, 0.1F).withFastEating().effect(new PotionEffect(ACEffectRegistry.SUGAR_RUSH, 200), 0.01F));
    public static final ItemEntry CARAMEL = food("caramel", CaveTab.CANDY_CAVITY, Food.of(2, 0.3F).effect(new PotionEffect(ACEffectRegistry.SUGAR_RUSH, 200), 0.04F));
    public static final ItemEntry CARAMEL_APPLE = food("caramel_apple", CaveTab.CANDY_CAVITY, Food.of(6, 0.2F).effect(new PotionEffect(ACEffectRegistry.SUGAR_RUSH, 200), 0.02F));
    public static final ItemEntry HOT_CHOCOLATE_BOTTLE = bottledDrink("hot_chocolate_bottle", CaveTab.CANDY_CAVITY, Food.of(4, 0.25F).effect(new PotionEffect(ACEffectRegistry.SUGAR_RUSH, 200), 0.02F).withDrink());
    public static final ItemEntry SUNDAE = food("sundae", CaveTab.CANDY_CAVITY, Food.of(12, 0.35F).effect(new PotionEffect(ACEffectRegistry.SUGAR_RUSH, 400), 0.2F).withBowlRemainder(), RARITY_SWEET);
    public static final ItemEntry GUMBALL_PILE = food("gumball_pile", CaveTab.CANDY_CAVITY, Food.of(3, 0.2F).effect(new PotionEffect(ACEffectRegistry.SUGAR_RUSH, 200), 0.01F));
    public static final ItemEntry PURPLE_SODA_BUCKET = register("purple_soda_bucket", CaveTab.CANDY_CAVITY, new PurpleSodaBucketItem(), null);
    public static final ItemEntry PURPLE_SODA_BOTTLE = bottledDrink("purple_soda_bottle", CaveTab.CANDY_CAVITY, Food.of(2, 0.1F).effect(new PotionEffect(ACEffectRegistry.SUGAR_RUSH, 200), 0.01F).withDrink());
    public static final ItemEntry SWEETISH_FISH_RED_BUCKET = register("sweetish_fish_red_bucket", CaveTab.CANDY_CAVITY, new SweetishFishBucketItem(GummyColors.RED), null);
    public static final ItemEntry SWEETISH_FISH_GREEN_BUCKET = register("sweetish_fish_green_bucket", CaveTab.CANDY_CAVITY, new SweetishFishBucketItem(GummyColors.GREEN), null);
    public static final ItemEntry SWEETISH_FISH_BLUE_BUCKET = register("sweetish_fish_blue_bucket", CaveTab.CANDY_CAVITY, new SweetishFishBucketItem(GummyColors.BLUE), null);
    public static final ItemEntry SWEETISH_FISH_YELLOW_BUCKET = register("sweetish_fish_yellow_bucket", CaveTab.CANDY_CAVITY, new SweetishFishBucketItem(GummyColors.YELLOW), null);
    public static final ItemEntry SWEETISH_FISH_PINK_BUCKET = register("sweetish_fish_pink_bucket", CaveTab.CANDY_CAVITY, new SweetishFishBucketItem(GummyColors.PINK), null);
    public static final ItemEntry SWEETISH_FISH_RED = food("sweetish_fish_red", CaveTab.CANDY_CAVITY, Food.of(2, 0.2F).effect(new PotionEffect(ACEffectRegistry.SUGAR_RUSH, 200), 0.01F));
    public static final ItemEntry SWEETISH_FISH_GREEN = food("sweetish_fish_green", CaveTab.CANDY_CAVITY, Food.of(2, 0.2F).effect(new PotionEffect(ACEffectRegistry.SUGAR_RUSH, 200), 0.01F));
    public static final ItemEntry SWEETISH_FISH_BLUE = food("sweetish_fish_blue", CaveTab.CANDY_CAVITY, Food.of(2, 0.2F).effect(new PotionEffect(ACEffectRegistry.SUGAR_RUSH, 200), 0.01F));
    public static final ItemEntry SWEETISH_FISH_YELLOW = food("sweetish_fish_yellow", CaveTab.CANDY_CAVITY, Food.of(2, 0.2F).effect(new PotionEffect(ACEffectRegistry.SUGAR_RUSH, 200), 0.01F));
    public static final ItemEntry SWEETISH_FISH_PINK = food("sweetish_fish_pink", CaveTab.CANDY_CAVITY, Food.of(2, 0.2F).effect(new PotionEffect(ACEffectRegistry.SUGAR_RUSH, 200), 0.01F));
    public static final ItemEntry GELATIN_RED = food("gelatin_red", CaveTab.CANDY_CAVITY, Food.of(3, 0.25F).effect(new PotionEffect(ACEffectRegistry.SUGAR_RUSH, 200), 0.01F));
    public static final ItemEntry GELATIN_GREEN = food("gelatin_green", CaveTab.CANDY_CAVITY, Food.of(3, 0.25F).effect(new PotionEffect(ACEffectRegistry.SUGAR_RUSH, 200), 0.01F));
    public static final ItemEntry GELATIN_BLUE = food("gelatin_blue", CaveTab.CANDY_CAVITY, Food.of(3, 0.25F).effect(new PotionEffect(ACEffectRegistry.SUGAR_RUSH, 200), 0.01F));
    public static final ItemEntry GELATIN_YELLOW = food("gelatin_yellow", CaveTab.CANDY_CAVITY, Food.of(3, 0.25F).effect(new PotionEffect(ACEffectRegistry.SUGAR_RUSH, 200), 0.01F));
    public static final ItemEntry GELATIN_PINK = food("gelatin_pink", CaveTab.CANDY_CAVITY, Food.of(3, 0.25F).effect(new PotionEffect(ACEffectRegistry.SUGAR_RUSH, 200), 0.01F));
    public static final ItemEntry GINGERBREAD_CRUMBS = food("gingerbread_crumbs", CaveTab.CANDY_CAVITY, Food.of(1, 0.1F).effect(new PotionEffect(ACEffectRegistry.SUGAR_RUSH, 200), 0.01F));
    public static final ItemEntry RAINBOUNCE_BOOTS = armor("rainbounce_boots", CaveTab.CANDY_CAVITY, new RainbounceBootsItem(RAINBOUNCE_ARMOR_MATERIAL));
    public static final ItemEntry GINGERBREAD_HELMET = armor("gingerbread_helmet", CaveTab.CANDY_CAVITY, new GingerbreadArmorItem(GINGERBREAD_ARMOR_MATERIAL, EntityEquipmentSlot.HEAD));
    public static final ItemEntry GINGERBREAD_CHESTPLATE = armor("gingerbread_chestplate", CaveTab.CANDY_CAVITY, new GingerbreadArmorItem(GINGERBREAD_ARMOR_MATERIAL, EntityEquipmentSlot.CHEST));
    public static final ItemEntry GINGERBREAD_LEGGINGS = armor("gingerbread_leggings", CaveTab.CANDY_CAVITY, new GingerbreadArmorItem(GINGERBREAD_ARMOR_MATERIAL, EntityEquipmentSlot.LEGS));
    public static final ItemEntry GINGERBREAD_BOOTS = armor("gingerbread_boots", CaveTab.CANDY_CAVITY, new GingerbreadArmorItem(GINGERBREAD_ARMOR_MATERIAL, EntityEquipmentSlot.FEET));
    public static final ItemEntry SHARPENED_CANDY_CANE = register("sharpened_candy_cane", CaveTab.CANDY_CAVITY, new SharpenedCandyCaneItem(), null);
    public static final ItemEntry PURPLE_SODA_BOTTLE_ROCKET = register("purple_soda_bottle_rocket", CaveTab.CANDY_CAVITY, new SodaBottleRocketItem(), null);
    public static final ItemEntry FROSTMINT_SPEAR = register("frostmint_spear", CaveTab.CANDY_CAVITY, new FrostmintSpearItem(), null);
    public static final ItemEntry SWEET_TOOTH = rarityItem("sweet_tooth", CaveTab.CANDY_CAVITY, RARITY_SWEET);
    public static final ItemEntry GUM_WORM_TOOTH = item("gum_worm_tooth", CaveTab.CANDY_CAVITY);
    public static final ItemEntry RADIANT_ESSENCE = rarityItem("radiant_essence", CaveTab.CANDY_CAVITY, RARITY_RAINBOW);
    public static final ItemEntry LICOWITCH_RADIANT_ESSENCE = hiddenRarityItem("licowitch_radiant_essence", RARITY_RAINBOW);
    public static final ItemEntry DISC_FRAGMENT_TASTY = item("disc_fragment_tasty", CaveTab.CANDY_CAVITY);
    public static final ItemEntry MUSIC_DISC_TASTY = record("music_disc_tasty", CaveTab.CANDY_CAVITY, ACSoundRegistry.TASTY_MUSIC_DISC, RARITY_SWEET);

    public static final List<ItemEntry> ITEMS = Collections.unmodifiableList(MUTABLE_ITEMS);

    public static ItemEntry byName(String name) {
        for (ItemEntry entry : ITEMS) {
            if (entry.name().equals(name)) {
                return entry;
            }
        }
        throw new IllegalArgumentException("Unknown Alex's Caves item: " + name);
    }

    public static List<ItemEntry> itemsFor(CaveTab tab) {
        List<ItemEntry> matches = new ArrayList<>();
        for (ItemEntry entry : ITEMS) {
            if (entry.tab() == tab) {
                matches.add(entry);
            }
        }
        return Collections.unmodifiableList(matches);
    }

    private static ItemEntry item(String name, CaveTab tab) {
        return register(name, tab, new Item(), null);
    }

    private static ItemEntry stackLimitedItem(String name, CaveTab tab, int stackSize) {
        return register(name, tab, new Item().setMaxStackSize(stackSize), null);
    }

    private static ItemEntry rarityItem(String name, CaveTab tab, IRarity rarity) {
        return register(name, tab, new RarityItem(rarity), null);
    }

    private static ItemEntry hiddenRarityItem(String name, IRarity rarity) {
        return rarityItem(name, null, rarity);
    }

    private static ItemEntry stackLimitedRarityItem(String name, CaveTab tab, int stackSize, IRarity rarity) {
        return register(name, tab, new RarityItem(rarity).setMaxStackSize(stackSize), null);
    }

    private static ItemEntry hiddenStackLimitedRarityItem(String name, int stackSize, IRarity rarity) {
        return register(name, null, new RarityItem(rarity).setMaxStackSize(stackSize), null);
    }

    private static ItemEntry armor(String name, CaveTab tab, ItemArmor item) {
        return register(name, tab, item, null);
    }

    private static ItemEntry food(String name, CaveTab tab, Food food) {
        return register(name, tab, new ACFoodItem(food), food);
    }

    private static ItemEntry food(String name, CaveTab tab, Food food, IRarity rarity) {
        return register(name, tab, new ACFoodItem(food) {
            @Override
            public IRarity getForgeRarity(net.minecraft.item.ItemStack stack) {
                return rarity;
            }
        }, food);
    }

    private static ItemEntry hiddenFood(String name, Food food, IRarity rarity) {
        return food(name, null, food, rarity);
    }

    private static ItemEntry bottledDrink(String name, CaveTab tab, Food food) {
        return register(name, tab, new DrinkableBottledItem(food), food);
    }

    private static ItemEntry radiationRemovingFood(String name, CaveTab tab, Food food) {
        return register(name, tab, new RadiationRemovingFoodItem(food), food);
    }

    private static ItemEntry darkenedApple(String name, CaveTab tab, Food food) {
        return register(name, tab, new DarkenedAppleItem(food) {
            @Override
            public IRarity getForgeRarity(net.minecraft.item.ItemStack stack) {
                return RARITY_DEMONIC;
            }
        }, food);
    }

    private static ItemEntry radioactiveItem(String name, CaveTab tab, float chance, IRarity rarity) {
        Item item = rarity == null ? new RadioactiveItem(chance) : new RadioactiveItem(chance) {
            @Override
            public IRarity getForgeRarity(net.minecraft.item.ItemStack stack) {
                return rarity;
            }
        };
        return register(name, tab, item, null);
    }

    private static ItemEntry record(String name, CaveTab tab, net.minecraft.util.SoundEvent sound, IRarity rarity) {
        return register(name, tab, new ACRecordItem(AlexsCaves.MODID + "." + name, sound, rarity), null);
    }

    private static ItemEntry register(String name, CaveTab tab, Item item, Food food) {
        ResourceLocation id = new ResourceLocation(AlexsCaves.MODID, name);
        item.setRegistryName(id);
        item.setTranslationKey(AlexsCaves.MODID + "." + name);
        item.setCreativeTab(tab == null ? null : tab.creativeTab());
        ItemEntry entry = new ItemEntry(name, tab, item, food);
        MUTABLE_ITEMS.add(entry);
        return entry;
    }

    private static IRarity rarity(String name, TextFormatting color) {
        return new IRarity() {
            @Override
            public TextFormatting getColor() {
                return color;
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }

    public enum CaveTab {
        MAGNETIC_CAVES,
        PRIMORDIAL_CAVES,
        TOXIC_CAVES,
        ABYSSAL_CHASM,
        FORLORN_HOLLOWS,
        CANDY_CAVITY;

        public CreativeTabs creativeTab() {
            switch (this) {
                case MAGNETIC_CAVES:
                    return ACCreativeTabs.MAGNETIC_CAVES;
                case PRIMORDIAL_CAVES:
                    return ACCreativeTabs.PRIMORDIAL_CAVES;
                case TOXIC_CAVES:
                    return ACCreativeTabs.TOXIC_CAVES;
                case ABYSSAL_CHASM:
                    return ACCreativeTabs.ABYSSAL_CHASM;
                case FORLORN_HOLLOWS:
                    return ACCreativeTabs.FORLORN_HOLLOWS;
                case CANDY_CAVITY:
                    return ACCreativeTabs.CANDY_CAVITY;
                default:
                    throw new IllegalStateException("Unhandled cave tab: " + this);
            }
        }
    }

    public static class ItemEntry {
        private final String name;
        private final CaveTab tab;
        private final Item item;
        private final Food food;

        private ItemEntry(String name, CaveTab tab, Item item, Food food) {
            this.name = name;
            this.tab = tab;
            this.item = item;
            this.food = food;
        }

        public String name() {
            return name;
        }

        public CaveTab tab() {
            return tab;
        }

        public boolean hasCreativeTab() {
            return tab != null;
        }

        public Item item() {
            return item;
        }

        public boolean isFood() {
            return food != null;
        }

        public Food food() {
            if (food == null) {
                throw new IllegalStateException(name + " is not a food item");
            }
            return food;
        }

        private ItemEntry withContainer(Item containerItem) {
            item.setContainerItem(containerItem);
            return this;
        }
    }

    public static class Food {
        private final int healAmount;
        private final float saturation;
        private boolean meat;
        private boolean fast;
        private boolean alwaysEdible;
        private boolean bowlRemainder;
        private boolean drink;
        private PotionEffect effect;
        private float effectChance;

        private Food(int healAmount, float saturation) {
            this.healAmount = healAmount;
            this.saturation = saturation;
        }

        public static Food of(int healAmount, float saturation) {
            return new Food(healAmount, saturation);
        }

        public int healAmount() {
            return healAmount;
        }

        public float saturation() {
            return saturation;
        }

        public boolean meat() {
            return meat;
        }

        public boolean fast() {
            return fast;
        }

        public boolean alwaysEdible() {
            return alwaysEdible;
        }

        public boolean bowlRemainder() {
            return bowlRemainder;
        }

        public boolean drink() {
            return drink;
        }

        public PotionEffect effect() {
            return effect;
        }

        public float effectChance() {
            return effectChance;
        }

        public Food withMeat() {
            meat = true;
            return this;
        }

        public Food withFastEating() {
            fast = true;
            return this;
        }

        public Food withAlwaysEdible() {
            alwaysEdible = true;
            return this;
        }

        public Food withBowlRemainder() {
            bowlRemainder = true;
            return this;
        }

        public Food withDrink() {
            drink = true;
            return this;
        }

        public Food effect(PotionEffect effect, float chance) {
            this.effect = effect;
            this.effectChance = chance;
            return this;
        }
    }
}
