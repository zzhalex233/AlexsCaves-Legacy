package com.zzhalex233.alexscaves.server.registry;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.block.fluid.ACFluidRegistry;
import com.zzhalex233.alexscaves.server.block.entity.AbyssalAltarTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.ACSignTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.AmberMonolithTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.AmbersolTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.ConversionCrucibleTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.HologramProjectorTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.MagnetTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.MetalBarrelTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.MusselTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.NuclearFurnaceTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.NuclearSirenTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.QuarryTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.SirenLightTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.TeslaBulbTileEntity;
import com.zzhalex233.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.zzhalex233.alexscaves.server.entity.item.CinderBrickEntity;
import com.zzhalex233.alexscaves.server.entity.item.GuanoEntity;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;
import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.IProjectile;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fluids.DispenseFluidContainer;
import net.minecraftforge.oredict.OreDictionary;

@Mod.EventBusSubscriber(modid = AlexsCaves.MODID)
public class ACRegistryHandler {
    private static final int BREWING_RECIPE_COUNT = 11;
    private static boolean brewingRecipesRegistered;
    private static boolean smeltingRecipesRegistered;

    public static final List<WoodLogOreDictionaryEntry> WOOD_LOG_ORE_DICTIONARY_ENTRIES = Collections.unmodifiableList(Arrays.asList(
            woodLogOre("pewenLog", ACBlockRegistry.PEWEN_LOG),
            woodLogOre("pewenLog", ACBlockRegistry.PEWEN_WOOD),
            woodLogOre("pewenLog", ACBlockRegistry.STRIPPED_PEWEN_LOG),
            woodLogOre("pewenLog", ACBlockRegistry.STRIPPED_PEWEN_WOOD),
            woodLogOre("thornwoodLog", ACBlockRegistry.THORNWOOD_LOG),
            woodLogOre("thornwoodLog", ACBlockRegistry.THORNWOOD_WOOD),
            woodLogOre("thornwoodLog", ACBlockRegistry.STRIPPED_THORNWOOD_LOG),
            woodLogOre("thornwoodLog", ACBlockRegistry.STRIPPED_THORNWOOD_WOOD)
    ));

    public static final List<SmeltingRecipeEntry> SMELTING_RECIPES = Collections.unmodifiableList(Arrays.asList(
            smelting("galena_iron_ore", "minecraft:iron_ingot", () -> new ItemStack(ACBlockRegistry.GALENA_IRON_ORE.block()), () -> new ItemStack(Items.IRON_INGOT), 0.7F),
            smelting("packed_galena", "minecraft:iron_nugget", () -> new ItemStack(ACBlockRegistry.PACKED_GALENA.block()), () -> new ItemStack(Items.IRON_NUGGET), 0.05F),
            smelting("metal_swarf", "alexscaves:scrap_metal", () -> new ItemStack(ACBlockRegistry.METAL_SWARF.block()), () -> new ItemStack(ACBlockRegistry.SCRAP_METAL.block()), 0.1F),
            smelting("limestone", "alexscaves:smooth_limestone", () -> new ItemStack(ACBlockRegistry.LIMESTONE.block()), () -> new ItemStack(ACBlockRegistry.SMOOTH_LIMESTONE.block()), 0.1F),
            smelting("minecraft:bone_block", "alexscaves:smooth_bone", () -> new ItemStack(Blocks.BONE_BLOCK), () -> new ItemStack(ACBlockRegistry.SMOOTH_BONE.block()), 0.1F),
            smelting("coprolith", "alexscaves:smooth_coprolith", () -> new ItemStack(ACBlockRegistry.COPROLITH.block()), () -> new ItemStack(ACBlockRegistry.SMOOTH_COPROLITH.block()), 0.1F),
            smelting("radrock_uranium_ore", "alexscaves:uranium", () -> new ItemStack(ACBlockRegistry.RADROCK_URANIUM_ORE.block()), () -> new ItemStack(ACItemRegistry.URANIUM.item()), 1.0F),
            smelting("unrefined_waste", "alexscaves:uranium_shard", () -> new ItemStack(ACBlockRegistry.UNREFINED_WASTE.block()), () -> new ItemStack(ACItemRegistry.URANIUM_SHARD.item()), 1.0F),
            smelting("guanostone_redstone_ore", "minecraft:redstone", () -> new ItemStack(ACBlockRegistry.GUANOSTONE_REDSTONE_ORE.block()), () -> new ItemStack(Items.REDSTONE), 0.7F),
            smelting("guano_block", "alexscaves:guanostone", () -> new ItemStack(ACBlockRegistry.GUANO_BLOCK.block()), () -> new ItemStack(ACBlockRegistry.GUANOSTONE.block()), 0.1F),
            smelting("coprolith_coal_ore", "minecraft:coal", () -> new ItemStack(ACBlockRegistry.COPROLITH_COAL_ORE.block()), () -> new ItemStack(Items.COAL), 0.7F),
            smelting("radgill", "alexscaves:cooked_radgill", () -> new ItemStack(ACItemRegistry.RADGILL.item()), () -> new ItemStack(ACItemRegistry.COOKED_RADGILL.item()), 0.15F),
            smelting("trilocaris_tail", "alexscaves:cooked_trilocaris_tail", () -> new ItemStack(ACItemRegistry.TRILOCARIS_TAIL.item()), () -> new ItemStack(ACItemRegistry.COOKED_TRILOCARIS_TAIL.item()), 0.35F),
            smelting("dinosaur_chop", "alexscaves:cooked_dinosaur_chop", () -> new ItemStack(ACBlockRegistry.DINOSAUR_CHOP.block()), () -> new ItemStack(ACBlockRegistry.COOKED_DINOSAUR_CHOP.block()), 0.35F),
            smelting("lanternfish", "alexscaves:cooked_lanternfish", () -> new ItemStack(ACItemRegistry.LANTERNFISH.item()), () -> new ItemStack(ACItemRegistry.COOKED_LANTERNFISH.item()), 0.15F),
            smelting("tripodfish", "alexscaves:cooked_tripodfish", () -> new ItemStack(ACItemRegistry.TRIPODFISH.item()), () -> new ItemStack(ACItemRegistry.COOKED_TRIPODFISH.item()), 0.15F),
            smelting("mussel", "alexscaves:cooked_mussel", () -> new ItemStack(ACBlockRegistry.MUSSEL.block()), () -> new ItemStack(ACItemRegistry.COOKED_MUSSEL.item()), 0.15F),
            smelting("sweetish_fish_red", "alexscaves:gelatin_red", () -> new ItemStack(ACItemRegistry.SWEETISH_FISH_RED.item()), () -> new ItemStack(ACItemRegistry.GELATIN_RED.item()), 0.15F),
            smelting("sweetish_fish_green", "alexscaves:gelatin_green", () -> new ItemStack(ACItemRegistry.SWEETISH_FISH_GREEN.item()), () -> new ItemStack(ACItemRegistry.GELATIN_GREEN.item()), 0.15F),
            smelting("sweetish_fish_blue", "alexscaves:gelatin_blue", () -> new ItemStack(ACItemRegistry.SWEETISH_FISH_BLUE.item()), () -> new ItemStack(ACItemRegistry.GELATIN_BLUE.item()), 0.15F),
            smelting("sweetish_fish_yellow", "alexscaves:gelatin_yellow", () -> new ItemStack(ACItemRegistry.SWEETISH_FISH_YELLOW.item()), () -> new ItemStack(ACItemRegistry.GELATIN_YELLOW.item()), 0.15F),
            smelting("sweetish_fish_pink", "alexscaves:gelatin_pink", () -> new ItemStack(ACItemRegistry.SWEETISH_FISH_PINK.item()), () -> new ItemStack(ACItemRegistry.GELATIN_PINK.item()), 0.15F)
    ));

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        ACFluidRegistry.registerFluids();
        for (ACBlockRegistry.BlockEntry entry : ACBlockRegistry.BLOCKS) {
            event.getRegistry().register(entry.block());
        }
        for (Block block : ACBlockRegistry.AUXILIARY_BLOCKS) {
            event.getRegistry().register(block);
        }
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for (ACBlockRegistry.BlockEntry entry : ACBlockRegistry.BLOCKS) {
            if (entry.hasItem()) {
                event.getRegistry().register(entry.item());
            }
        }
        for (ACItemRegistry.ItemEntry entry : ACItemRegistry.ITEMS) {
            event.getRegistry().register(entry.item());
        }
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        for (SoundEvent sound : ACSoundRegistry.SOUNDS) {
            event.getRegistry().register(sound);
        }
    }

    @SubscribeEvent
    public static void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        for (Enchantment enchantment : ACEnchantmentRegistry.ENCHANTMENTS) {
            event.getRegistry().register(enchantment);
        }
    }

    @SubscribeEvent
    public static void registerEffects(RegistryEvent.Register<Potion> event) {
        for (Potion effect : ACEffectRegistry.EFFECTS) {
            event.getRegistry().register(effect);
        }
    }

    @SubscribeEvent
    public static void registerPotionTypes(RegistryEvent.Register<PotionType> event) {
        for (ACEffectRegistry.PotionTypeEntry entry : ACEffectRegistry.POTION_TYPES) {
            event.getRegistry().register(entry.type());
        }
    }

    public static int registerOreDictionaryEntries() {
        int count = 0;
        OreDictionary.registerOre("dustSulfur", ACItemRegistry.SULFUR_DUST.item());
        count++;
        OreDictionary.registerOre("concrete", new ItemStack(Blocks.CONCRETE, 1, OreDictionary.WILDCARD_VALUE));
        count++;
        OreDictionary.registerOre("alexscavesFern", new ItemStack(Blocks.TALLGRASS, 1, 2));
        count++;
        OreDictionary.registerOre("alexscavesFern", new ItemStack(Blocks.DOUBLE_PLANT, 1, 3));
        count++;
        for (ACBlockRegistry.BlockEntry entry : ACBlockRegistry.blocksFor(ACBlockRegistry.CaveBlockTab.CANDY_CAVITY)) {
            if (entry.name().startsWith("rock_candy_")) {
                OreDictionary.registerOre("rockCandy", entry.item());
                count++;
            }
        }
        for (ACItemRegistry.ItemEntry entry : ACItemRegistry.itemsFor(ACItemRegistry.CaveTab.CANDY_CAVITY)) {
            if (entry.name().startsWith("gelatin_")) {
                OreDictionary.registerOre("gelatin", entry.item());
                count++;
            }
        }
        for (ACBlockRegistry.BlockEntry entry : ACBlockRegistry.blocksFor(ACBlockRegistry.CaveBlockTab.TOXIC_CAVES)) {
            if (entry.name().startsWith("radon_lamp_")) {
                OreDictionary.registerOre("radonLamp", entry.item());
                count++;
            }
        }
        for (WoodLogOreDictionaryEntry entry : WOOD_LOG_ORE_DICTIONARY_ENTRIES) {
            OreDictionary.registerOre(entry.oreName(), entry.block().item());
            count++;
        }
        return count;
    }

    public static void registerDispenserBehaviors() {
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ACItemRegistry.ACID_BUCKET.item(), DispenseFluidContainer.getInstance());
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ACItemRegistry.RADGILL_BUCKET.item(), DispenseFluidContainer.getInstance());
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ACItemRegistry.PURPLE_SODA_BUCKET.item(), DispenseFluidContainer.getInstance());
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ACItemRegistry.TRILOCARIS_BUCKET.item(), DispenseFluidContainer.getInstance());
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ACItemRegistry.LANTERNFISH_BUCKET.item(), DispenseFluidContainer.getInstance());
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ACItemRegistry.TRIPODFISH_BUCKET.item(), DispenseFluidContainer.getInstance());
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ACItemRegistry.SEA_PIG_BUCKET.item(), DispenseFluidContainer.getInstance());
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ACItemRegistry.GOSSAMER_WORM_BUCKET.item(), DispenseFluidContainer.getInstance());
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ACItemRegistry.CINDER_BRICK.item(), new BehaviorProjectileDispense() {
            @Override
            protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                return new CinderBrickEntity(worldIn, position.getX(), position.getY(), position.getZ());
            }

            @Override
            protected float getProjectileInaccuracy() {
                return 0.9F;
            }

            @Override
            protected float getProjectileVelocity() {
                return 0.65F;
            }
        });
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ACItemRegistry.GUANO.item(), new BehaviorProjectileDispense() {
            @Override
            protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                return new GuanoEntity(worldIn, position.getX(), position.getY(), position.getZ());
            }
        });
    }

    public static void registerTileEntities() {
        TileEntity.register(AlexsCaves.MODID + ":ambersol", AmbersolTileEntity.class);
        TileEntity.register(AlexsCaves.MODID + ":amber_monolith", AmberMonolithTileEntity.class);
        TileEntity.register(AlexsCaves.MODID + ":mussel", MusselTileEntity.class);
        TileEntity.register(AlexsCaves.MODID + ":metal_barrel", MetalBarrelTileEntity.class);
        TileEntity.register(AlexsCaves.MODID + ":magnet", MagnetTileEntity.class);
        TileEntity.register(AlexsCaves.MODID + ":hologram_projector", HologramProjectorTileEntity.class);
        TileEntity.register(AlexsCaves.MODID + ":quarry", QuarryTileEntity.class);
        TileEntity.register(AlexsCaves.MODID + ":nuclear_furnace", NuclearFurnaceTileEntity.class);
        TileEntity.register(AlexsCaves.MODID + ":nuclear_siren", NuclearSirenTileEntity.class);
        TileEntity.register(AlexsCaves.MODID + ":siren_light", SirenLightTileEntity.class);
        TileEntity.register(AlexsCaves.MODID + ":tesla_bulb", TeslaBulbTileEntity.class);
        TileEntity.register(AlexsCaves.MODID + ":abyssal_altar", AbyssalAltarTileEntity.class);
        TileEntity.register(AlexsCaves.MODID + ":conversion_crucible", ConversionCrucibleTileEntity.class);
        TileEntity.register(AlexsCaves.MODID + ":sign", ACSignTileEntity.class);
    }

    private static WoodLogOreDictionaryEntry woodLogOre(String oreName, ACBlockRegistry.BlockEntry block) {
        return new WoodLogOreDictionaryEntry(oreName, block);
    }

    public static int registerBrewingRecipes() {
        if (brewingRecipesRegistered) {
            return BREWING_RECIPE_COUNT;
        }
        int count = 0;
        count += addBrewingRecipe(PotionTypes.AWKWARD, ACItemRegistry.FERROUSLIME_BALL.item(), ACEffectRegistry.MAGNETIZING_POTION.type());
        count += addBrewingRecipe(ACEffectRegistry.MAGNETIZING_POTION.type(), Items.REDSTONE, ACEffectRegistry.LONG_MAGNETIZING_POTION.type());
        count += addBrewingRecipe(PotionTypes.AWKWARD, ACItemRegistry.LANTERNFISH.item(), ACEffectRegistry.DEEPSIGHT_POTION.type());
        count += addBrewingRecipe(ACEffectRegistry.DEEPSIGHT_POTION.type(), Items.REDSTONE, ACEffectRegistry.LONG_DEEPSIGHT_POTION.type());
        count += addBrewingRecipe(PotionTypes.AWKWARD, ACItemRegistry.BIOLUMINESSCENCE.item(), ACEffectRegistry.GLOWING_POTION.type());
        count += addBrewingRecipe(ACEffectRegistry.GLOWING_POTION.type(), Items.REDSTONE, ACEffectRegistry.LONG_GLOWING_POTION.type());
        count += addBrewingRecipe(PotionTypes.AWKWARD, ACItemRegistry.CORRODENT_TEETH.item(), ACEffectRegistry.HASTE_POTION.type());
        count += addBrewingRecipe(ACEffectRegistry.HASTE_POTION.type(), Items.REDSTONE, ACEffectRegistry.LONG_HASTE_POTION.type());
        count += addBrewingRecipe(ACEffectRegistry.HASTE_POTION.type(), Items.GLOWSTONE_DUST, ACEffectRegistry.STRONG_HASTE_POTION.type());
        count += addBrewingRecipe(PotionTypes.STRONG_SWIFTNESS, ACItemRegistry.SWEET_TOOTH.item(), ACEffectRegistry.SUGAR_RUSH_POTION.type());
        count += addBrewingRecipe(ACEffectRegistry.SUGAR_RUSH_POTION.type(), Items.REDSTONE, ACEffectRegistry.LONG_SUGAR_RUSH_POTION.type());
        brewingRecipesRegistered = true;
        return count;
    }

    public static int registerSmeltingRecipes() {
        if (smeltingRecipesRegistered) {
            return SMELTING_RECIPES.size();
        }
        for (SmeltingRecipeEntry entry : SMELTING_RECIPES) {
            FurnaceRecipes.instance().addSmeltingRecipe(entry.input(), entry.output(), entry.experience());
        }
        smeltingRecipesRegistered = true;
        return SMELTING_RECIPES.size();
    }

    private static int addBrewingRecipe(PotionType input, Item ingredient, PotionType output) {
        BrewingRecipeRegistry.addRecipe(potion(input), new ItemStack(ingredient), potion(output));
        return 1;
    }

    private static ItemStack potion(PotionType type) {
        return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), type);
    }

    private static SmeltingRecipeEntry smelting(String inputName, String outputName, Supplier<ItemStack> input, Supplier<ItemStack> output, float experience) {
        return new SmeltingRecipeEntry(inputName, outputName, input, output, experience);
    }

    public static class SmeltingRecipeEntry {
        private final String inputName;
        private final String outputName;
        private final Supplier<ItemStack> input;
        private final Supplier<ItemStack> output;
        private final float experience;

        private SmeltingRecipeEntry(String inputName, String outputName, Supplier<ItemStack> input, Supplier<ItemStack> output, float experience) {
            this.inputName = inputName;
            this.outputName = outputName;
            this.input = input;
            this.output = output;
            this.experience = experience;
        }

        public String inputName() {
            return inputName;
        }

        public String outputName() {
            return outputName;
        }

        public ItemStack input() {
            return input.get();
        }

        public ItemStack output() {
            return output.get();
        }

        public float experience() {
            return experience;
        }
    }

    public static class WoodLogOreDictionaryEntry {
        private final String oreName;
        private final ACBlockRegistry.BlockEntry block;

        private WoodLogOreDictionaryEntry(String oreName, ACBlockRegistry.BlockEntry block) {
            this.oreName = oreName;
            this.block = block;
        }

        public String oreName() {
            return oreName;
        }

        public String blockName() {
            return block.name();
        }

        public ACBlockRegistry.BlockEntry block() {
            return block;
        }
    }
}
