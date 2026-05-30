package com.zzhalex233.alexscaves.server.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zzhalex233.alexscaves.AlexsCaves;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class ACSoundRegistry {
    private static final Pattern SOUND_JSON_KEY = Pattern.compile("^\\s*\"([^\"]+)\"\\s*:\\s*\\{\\s*$");
    private static final Map<String, SoundEvent> MUTABLE_SOUNDS = new LinkedHashMap<>();

    public static final SoundEvent FUSION_MUSIC_DISC = sound("fusion_music_disc");
    public static final SoundEvent TASTY_MUSIC_DISC = sound("tasty_music_disc");
    public static final SoundEvent HAZMAT_BLOCK_STEP = sound("hazmat_block_step");
    public static final SoundEvent HAZMAT_BLOCK_PLACE = sound("hazmat_block_place");
    public static final SoundEvent HAZMAT_BLOCK_BREAK = sound("hazmat_block_break");
    public static final SoundEvent HAZMAT_BLOCK_BREAKING = sound("hazmat_block_breaking");
    public static final SoundEvent CINDER_BLOCK_STEP = sound("cinder_block_step");
    public static final SoundEvent CINDER_BLOCK_PLACE = sound("cinder_block_place");
    public static final SoundEvent CINDER_BLOCK_BREAK = sound("cinder_block_break");
    public static final SoundEvent CINDER_BLOCK_BREAKING = sound("cinder_block_breaking");
    public static final SoundEvent UNREFINED_WASTE_STEP = sound("unrefined_waste_step");
    public static final SoundEvent UNREFINED_WASTE_PLACE = sound("unrefined_waste_place");
    public static final SoundEvent UNREFINED_WASTE_BREAK = sound("unrefined_waste_break");
    public static final SoundEvent UNREFINED_WASTE_BREAKING = sound("unrefined_waste_breaking");
    public static final SoundEvent SOFT_CANDY_STEP = sound("soft_candy_step");
    public static final SoundEvent SOFT_CANDY_PLACE = sound("soft_candy_place");
    public static final SoundEvent SOFT_CANDY_BREAK = sound("soft_candy_break");
    public static final SoundEvent SOFT_CANDY_BREAKING = sound("soft_candy_breaking");
    public static final SoundEvent DENSE_CANDY_STEP = sound("dense_candy_step");
    public static final SoundEvent DENSE_CANDY_PLACE = sound("dense_candy_place");
    public static final SoundEvent DENSE_CANDY_BREAK = sound("dense_candy_break");
    public static final SoundEvent DENSE_CANDY_BREAKING = sound("dense_candy_breaking");
    public static final SoundEvent HARD_CANDY_STEP = sound("hard_candy_step");
    public static final SoundEvent HARD_CANDY_PLACE = sound("hard_candy_place");
    public static final SoundEvent HARD_CANDY_BREAK = sound("hard_candy_break");
    public static final SoundEvent HARD_CANDY_BREAKING = sound("hard_candy_breaking");
    public static final SoundEvent SQUISHY_CANDY_STEP = sound("squishy_candy_step");
    public static final SoundEvent SQUISHY_CANDY_PLACE = sound("squishy_candy_place");
    public static final SoundEvent SQUISHY_CANDY_BREAK = sound("squishy_candy_break");
    public static final SoundEvent SQUISHY_CANDY_BREAKING = sound("squishy_candy_breaking");
    public static final SoundEvent TRILOCARIS_HURT = sound("trilocaris_hurt");
    public static final SoundEvent TRILOCARIS_DEATH = sound("trilocaris_death");
    public static final SoundEvent TRILOCARIS_STEP = sound("trilocaris_step");
    public static final SoundEvent LANTERNFISH_HURT = sound("lanternfish_hurt");
    public static final SoundEvent LANTERNFISH_FLOP = sound("lanternfish_flop");
    public static final SoundEvent TRIPODFISH_HURT = sound("tripodfish_hurt");
    public static final SoundEvent TRIPODFISH_FLOP = sound("tripodfish_flop");
    public static final SoundEvent SEA_PIG_IDLE = sound("sea_pig_idle");
    public static final SoundEvent SEA_PIG_HURT = sound("sea_pig_hurt");
    public static final SoundEvent SEA_PIG_DEATH = sound("sea_pig_death");
    public static final SoundEvent SEA_PIG_EAT = sound("sea_pig_eat");
    public static final SoundEvent SWEETISH_FISH_HURT = sound("sweetish_fish_hurt");
    public static final SoundEvent SWEETISH_FISH_FLOP = sound("sweetish_fish_flop");
    public static final SoundEvent PURPLE_SODA_IDLE = sound("purple_soda_idle");
    public static final SoundEvent PURPLE_SODA_SWIM = sound("purple_soda_swim");
    public static final SoundEvent PURPLE_SODA_SUBMERGE = sound("purple_soda_submerge");
    public static final SoundEvent PURPLE_SODA_UNSUBMERGE = sound("purple_soda_unsubmerge");
    public static final SoundEvent RAINBOUNCE_BOOTS_BOUNCE = sound("rainbounce_boots_bounce");
    public static final SoundEvent FROSTMINT_SPEAR_THROW = sound("frostmint_spear_throw");
    public static final SoundEvent FROSTMINT_SPEAR_HIT = sound("frostmint_spear_hit");
    public static final SoundEvent CAVE_BOOK_OPEN = sound("cave_book_open");
    public static final SoundEvent CAVE_BOOK_CLOSE = sound("cave_book_close");
    public static final SoundEvent CAVE_BOOK_TURN = sound("cave_book_turn");
    public static final SoundEvent CANIAC_IDLE = sound("caniac_idle");
    public static final SoundEvent CANIAC_HURT = sound("caniac_hurt");
    public static final SoundEvent CANIAC_DEATH = sound("caniac_death");
    public static final SoundEvent CANIAC_ATTACK = sound("caniac_attack");
    public static final SoundEvent CANIAC_SWING = sound("caniac_swing");
    public static final SoundEvent PRIMITIVE_CLUB_HIT = sound("primitive_club_hit");
    public static final SoundEvent PRIMITIVE_CLUB_MISS = sound("primitive_club_miss");

    public static final List<SoundEvent> SOUNDS = Collections.unmodifiableList(allSounds());

    private static SoundEvent sound(String name) {
        SoundEvent existing = MUTABLE_SOUNDS.get(name);
        if (existing != null) {
            return existing;
        }
        ResourceLocation id = new ResourceLocation(AlexsCaves.MODID, name);
        SoundEvent sound = new SoundEvent(id).setRegistryName(id);
        MUTABLE_SOUNDS.put(name, sound);
        return sound;
    }

    private static List<SoundEvent> allSounds() {
        try (InputStream input = ACSoundRegistry.class.getClassLoader().getResourceAsStream("assets/alexscaves/sounds.json")) {
            if (input != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Matcher matcher = SOUND_JSON_KEY.matcher(line);
                        if (matcher.matches()) {
                            sound(matcher.group(1));
                        }
                    }
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read alexscaves sounds.json", exception);
        }
        return new ArrayList<>(MUTABLE_SOUNDS.values());
    }
}
