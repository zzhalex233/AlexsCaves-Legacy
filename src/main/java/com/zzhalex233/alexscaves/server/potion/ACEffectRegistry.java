package com.zzhalex233.alexscaves.server.potion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.zzhalex233.alexscaves.AlexsCaves;

import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;

public class ACEffectRegistry {
    private static final List<Potion> MUTABLE_EFFECTS = new ArrayList<>();
    private static final List<PotionTypeEntry> MUTABLE_POTION_TYPES = new ArrayList<>();

    public static final Potion MAGNETIZING = register(new TickingPotion("magnetizing", false, 0X53556C));
    public static final Potion STUNNED = register(new StunnedEffect());
    public static final Potion RAGE = register(new RageEffect());
    public static final Potion IRRADIATED = register(new IrradiatedEffect());
    public static final Potion BUBBLED = register(new BubbledEffect());
    public static final Potion DEEPSIGHT = register(new TrackedDurationEffect("deepsight", false, 0X002972));
    public static final Potion DARKNESS_INCARNATE = register(new DarknessIncarnateEffect());
    public static final Potion SUGAR_RUSH = register(new SugarRushEffect());

    public static final PotionTypeEntry MAGNETIZING_POTION = potionType("magnetizing", "magnetizing", new PotionEffect(MAGNETIZING, 3600));
    public static final PotionTypeEntry LONG_MAGNETIZING_POTION = potionType("long_magnetizing", "magnetizing", new PotionEffect(MAGNETIZING, 9600));
    public static final PotionTypeEntry DEEPSIGHT_POTION = potionType("deepsight", "deepsight", new PotionEffect(DEEPSIGHT, 3600));
    public static final PotionTypeEntry LONG_DEEPSIGHT_POTION = potionType("long_deepsight", "deepsight", new PotionEffect(DEEPSIGHT, 9600));
    public static final PotionTypeEntry GLOWING_POTION = potionType("glowing", "glowing", new PotionEffect(MobEffects.GLOWING, 3600));
    public static final PotionTypeEntry LONG_GLOWING_POTION = potionType("long_glowing", "glowing", new PotionEffect(MobEffects.GLOWING, 9600));
    public static final PotionTypeEntry HASTE_POTION = potionType("haste", "haste", new PotionEffect(MobEffects.HASTE, 3600));
    public static final PotionTypeEntry LONG_HASTE_POTION = potionType("long_haste", "haste", new PotionEffect(MobEffects.HASTE, 9600));
    public static final PotionTypeEntry STRONG_HASTE_POTION = potionType("strong_haste", "haste", new PotionEffect(MobEffects.HASTE, 1800, 1));
    public static final PotionTypeEntry STRONG_HUNGER_POTION = potionType("strong_hunger", "hunger", new PotionEffect(MobEffects.HUNGER, 1800, 4));
    public static final PotionTypeEntry SUGAR_RUSH_POTION = potionType("sugar_rush", "sugar_rush", new PotionEffect(SUGAR_RUSH, 1800));
    public static final PotionTypeEntry LONG_SUGAR_RUSH_POTION = potionType("long_sugar_rush", "sugar_rush", new PotionEffect(SUGAR_RUSH, 3600));

    public static final List<Potion> EFFECTS = Collections.unmodifiableList(MUTABLE_EFFECTS);
    public static final List<PotionTypeEntry> POTION_TYPES = Collections.unmodifiableList(MUTABLE_POTION_TYPES);

    public static Potion byName(String name) {
        for (Potion effect : EFFECTS) {
            if (effect.getRegistryName() != null && effect.getRegistryName().getPath().equals(name)) {
                return effect;
            }
        }
        throw new IllegalArgumentException("Unknown Alex's Caves effect: " + name);
    }

    public static PotionType potionTypeByName(String name) {
        for (PotionTypeEntry entry : POTION_TYPES) {
            if (entry.name().equals(name)) {
                return entry.type();
            }
        }
        throw new IllegalArgumentException("Unknown Alex's Caves potion type: " + name);
    }

    private static Potion register(Potion potion) {
        MUTABLE_EFFECTS.add(potion);
        return potion;
    }

    private static PotionTypeEntry potionType(String name, String translationName, PotionEffect effect) {
        PotionType potionType = new PotionType(translationName, effect);
        potionType.setRegistryName(new ResourceLocation(AlexsCaves.MODID, name));
        PotionTypeEntry entry = new PotionTypeEntry(name, translationName, potionType);
        MUTABLE_POTION_TYPES.add(entry);
        return entry;
    }

    public static class PotionTypeEntry {
        private final String name;
        private final String translationName;
        private final PotionType type;

        private PotionTypeEntry(String name, String translationName, PotionType type) {
            this.name = name;
            this.translationName = translationName;
            this.type = type;
        }

        public String name() {
            return name;
        }

        public String translationName() {
            return translationName;
        }

        public PotionType type() {
            return type;
        }
    }
}
