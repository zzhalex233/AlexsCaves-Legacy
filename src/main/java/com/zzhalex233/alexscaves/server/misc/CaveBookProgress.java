package com.zzhalex233.alexscaves.server.misc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;

public class CaveBookProgress {
    public static final String PLAYER_CAVE_BOOK_PROGRESS_TAG = "AlexsCavesBookProgress";

    private final Map<String, Subcategory> unlockedPages = new HashMap<>();

    private CaveBookProgress(NBTTagCompound tag) {
        if (tag.hasKey("Pages", 9)) {
            NBTTagList pages = tag.getTagList("Pages", 10);
            for (int i = 0; i < pages.tagCount(); i++) {
                NBTTagCompound page = pages.getCompoundTagAt(i);
                unlockedPages.put(page.getString("Category"), Subcategory.getByOrdinal(page.getInteger("SubCategory")));
            }
        }
    }

    public static CaveBookProgress getCaveBookProgress(EntityPlayer player) {
        return new CaveBookProgress(player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag(PLAYER_CAVE_BOOK_PROGRESS_TAG));
    }

    public static void saveCaveBookProgress(CaveBookProgress progress, EntityPlayer player) {
        NBTTagCompound data = player.getEntityData();
        NBTTagCompound persisted = data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        persisted.setTag(PLAYER_CAVE_BOOK_PROGRESS_TAG, progress.save());
        data.setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
    }

    public boolean unlockNextFor(String biomeCategory, boolean onlyOneResearchNeeded) {
        int previous = unlockedPages.getOrDefault(biomeCategory, Subcategory.EMPTY).ordinal();
        if (previous >= Subcategory.values().length - 1 || !Subcategory.canUnlockNext(biomeCategory, previous)) {
            return false;
        }
        unlockedPages.put(biomeCategory, onlyOneResearchNeeded ? Subcategory.getLastUnlockableFor(biomeCategory) : Subcategory.getByOrdinal(previous + 1));
        return true;
    }

    public boolean isUnlockedFor(String biomeCategory, Subcategory subcategory) {
        return subcategory.ordinal() <= unlockedPages.getOrDefault(biomeCategory, Subcategory.EMPTY).ordinal();
    }

    public Subcategory getLastUnlockedCategory(String biomeCategory) {
        return unlockedPages.getOrDefault(biomeCategory, Subcategory.EMPTY);
    }

    public boolean isUnlockedFor(String key) {
        return isUnlockedFor(getBiomeFromPage(key), getSubcategoryFromPage(key));
    }

    public String getBiomeFromPage(String key) {
        int index = key.lastIndexOf('_');
        return index >= 0 && index + 1 < key.length() ? key.substring(0, index) : "";
    }

    public Subcategory getSubcategoryFromPage(String key) {
        int index = key.lastIndexOf('_');
        if (index >= 0 && index + 1 < key.length()) {
            return Subcategory.valueOf(key.substring(index + 1).toUpperCase(Locale.ROOT));
        }
        return Subcategory.EMPTY;
    }

    private NBTTagCompound save() {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList pages = new NBTTagList();
        for (Map.Entry<String, Subcategory> entry : unlockedPages.entrySet()) {
            NBTTagCompound page = new NBTTagCompound();
            page.setString("Category", entry.getKey());
            page.setInteger("SubCategory", entry.getValue().ordinal());
            pages.appendTag(page);
        }
        tag.setTag("Pages", pages);
        return tag;
    }

    public enum Subcategory {
        EMPTY,
        GENERAL,
        RESOURCES,
        MOBS,
        UTILITIES,
        SECRETS("alexscaves:primordial_caves", "alexscaves:toxic_caves");

        private final String[] limitedTo;

        Subcategory(String... limitedTo) {
            this.limitedTo = limitedTo;
        }

        public static Subcategory getByOrdinal(int ordinal) {
            return Subcategory.values()[MathHelper.clamp(ordinal, 0, Subcategory.values().length - 1)];
        }

        public static boolean canUnlockNext(String category, int currentLevel) {
            Subcategory next = getByOrdinal(currentLevel + 1);
            return next.limitedTo.length == 0 || Arrays.asList(next.limitedTo).contains(category);
        }

        public static Subcategory getLastUnlockableFor(String category) {
            return canUnlockNext(category, SECRETS.ordinal() - 1) ? SECRETS : UTILITIES;
        }
    }
}
