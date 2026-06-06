package com.zzhalex233.alexscaves.server.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.item.ACCreativeTabs;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.item.EdibleBlockItem;
import com.zzhalex233.alexscaves.server.item.EdibleSlabItem;
import com.zzhalex233.alexscaves.server.item.GuanoLayerItem;
import com.zzhalex233.alexscaves.server.item.CaveSignItem;
import com.zzhalex233.alexscaves.server.item.MetalScaffoldingItem;
import com.zzhalex233.alexscaves.server.item.SprinklesItem;
import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;
import com.zzhalex233.alexscaves.server.entity.living.GrottoceratopsEntity;
import com.zzhalex233.alexscaves.server.entity.living.RelicheirusEntity;
import com.zzhalex233.alexscaves.server.entity.living.SubterranodonEntity;
import com.zzhalex233.alexscaves.server.entity.living.VallumraptorEntity;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemSlab;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class ACBlockRegistry {
    private static final List<BlockEntry> MUTABLE_BLOCKS = new ArrayList<>();
    private static final List<Block> MUTABLE_AUXILIARY_BLOCKS = new ArrayList<>();
    public static FrostmintBlock.Double FROSTMINT_DOUBLE;

    public static final BlockEntry GALENA = rock("galena", CaveBlockTab.MAGNETIC_CAVES, 3.5F, 10.0F, SoundType.STONE);
    public static final BlockEntry GALENA_STAIRS = stairs("galena_stairs", CaveBlockTab.MAGNETIC_CAVES, GALENA);
    public static final BlockEntry GALENA_SLAB = slab("galena_slab", CaveBlockTab.MAGNETIC_CAVES, Material.ROCK, 3.5F, 10.0F, SoundType.STONE);
    public static final BlockEntry GALENA_WALL = wall("galena_wall", CaveBlockTab.MAGNETIC_CAVES, 3.5F, 10.0F, SoundType.STONE);
    public static final BlockEntry PACKED_GALENA = rock("packed_galena", CaveBlockTab.MAGNETIC_CAVES, 3.5F, 10.0F, SoundType.STONE);
    public static final BlockEntry GALENA_BRICKS = rock("galena_bricks", CaveBlockTab.MAGNETIC_CAVES, 3.5F, 10.0F, SoundType.STONE);
    public static final BlockEntry GALENA_BRICK_STAIRS = stairs("galena_brick_stairs", CaveBlockTab.MAGNETIC_CAVES, GALENA_BRICKS);
    public static final BlockEntry GALENA_BRICK_SLAB = slab("galena_brick_slab", CaveBlockTab.MAGNETIC_CAVES, Material.ROCK, 3.5F, 10.0F, SoundType.STONE);
    public static final BlockEntry GALENA_BRICK_WALL = wall("galena_brick_wall", CaveBlockTab.MAGNETIC_CAVES, 3.5F, 10.0F, SoundType.STONE);
    public static final BlockEntry SPELUNKERY_TABLE = register("spelunkery_table", CaveBlockTab.MAGNETIC_CAVES, new SpelunkeryTableBlock());
    public static final BlockEntry GALENA_PILLAR = register("galena_pillar", CaveBlockTab.MAGNETIC_CAVES, new GalenaPillarBlock());
    public static final BlockEntry GALENA_SPIRE = register("galena_spire", CaveBlockTab.MAGNETIC_CAVES, new GalenaSpireBlock());
    public static final BlockEntry AMBER = register("amber", CaveBlockTab.PRIMORDIAL_CAVES, new AmberBlock());
    public static final BlockEntry AMBERSOL = register("ambersol", CaveBlockTab.PRIMORDIAL_CAVES, new AmbersolBlock());
    public static final BlockEntry AMBERSOL_LIGHT = blockOnly("ambersol_light", CaveBlockTab.PRIMORDIAL_CAVES, new AmbersolLightBlock());
    public static final BlockEntry AMBER_MONOLITH = register("amber_monolith", CaveBlockTab.PRIMORDIAL_CAVES, new AmberMonolithBlock());
    public static final BlockEntry ENERGIZED_GALENA_NEUTRAL = energizedGalena("energized_galena_neutral");
    public static final BlockEntry ENERGIZED_GALENA_SCARLET = energizedGalena("energized_galena_scarlet");
    public static final BlockEntry ENERGIZED_GALENA_AZURE = energizedGalena("energized_galena_azure");
    public static final BlockEntry GALENA_IRON_ORE = rock("galena_iron_ore", CaveBlockTab.MAGNETIC_CAVES, 3.0F, 5.0F, SoundType.STONE);
    public static final BlockEntry METAL_SWARF = falling("metal_swarf", CaveBlockTab.MAGNETIC_CAVES, 0.6F, 0.6F, SoundType.METAL);
    public static final BlockEntry METAL_REBAR = register("metal_rebar", CaveBlockTab.MAGNETIC_CAVES, new RebarBlock());
    public static final BlockEntry METAL_SCAFFOLDING = scaffolding("metal_scaffolding", CaveBlockTab.MAGNETIC_CAVES);
    public static final BlockEntry MAGNETIC_ACTIVATOR = register("magnetic_activator", CaveBlockTab.MAGNETIC_CAVES, new MagneticActivatorBlock());
    public static final BlockEntry MAGNETIC_LIGHT = register("magnetic_light", CaveBlockTab.MAGNETIC_CAVES, new MagneticLightBlock());
    public static final BlockEntry MAGNETIC_LEVITATION_RAIL = register("magnetic_levitation_rail", CaveBlockTab.MAGNETIC_CAVES, new MagneticLevitationRailBlock());
    public static final BlockEntry SCARLET_MAGNET = register("scarlet_magnet", CaveBlockTab.MAGNETIC_CAVES, new MagnetBlock(false));
    public static final BlockEntry AZURE_MAGNET = register("azure_magnet", CaveBlockTab.MAGNETIC_CAVES, new MagnetBlock(true));
    public static final BlockEntry HOLOGRAM_PROJECTOR = register("hologram_projector", CaveBlockTab.MAGNETIC_CAVES, new HologramProjectorBlock());
    public static final BlockEntry TESLA_BULB = register("tesla_bulb", CaveBlockTab.MAGNETIC_CAVES, new TeslaBulbBlock());
    public static final BlockEntry SCARLET_NEODYMIUM_NODE = register("scarlet_neodymium_node", CaveBlockTab.MAGNETIC_CAVES, new NeodymiumNodeBlock(false));
    public static final BlockEntry AZURE_NEODYMIUM_NODE = register("azure_neodymium_node", CaveBlockTab.MAGNETIC_CAVES, new NeodymiumNodeBlock(true));
    public static final BlockEntry SCARLET_NEODYMIUM_PILLAR = register("scarlet_neodymium_pillar", CaveBlockTab.MAGNETIC_CAVES, new NeodymiumPillarBlock(false));
    public static final BlockEntry AZURE_NEODYMIUM_PILLAR = register("azure_neodymium_pillar", CaveBlockTab.MAGNETIC_CAVES, new NeodymiumPillarBlock(true));
    public static final BlockEntry HEART_OF_IRON = register("heart_of_iron", CaveBlockTab.MAGNETIC_CAVES, new HeartOfIronBlock());
    public static final BlockEntry QUARRY = register("quarry", CaveBlockTab.MAGNETIC_CAVES, new QuarryBlock());
    public static final BlockEntry SCRAP_METAL = metal("scrap_metal", CaveBlockTab.MAGNETIC_CAVES, 5.0F, 15.0F);
    public static final BlockEntry SCRAP_METAL_PLATE = metal("scrap_metal_plate", CaveBlockTab.MAGNETIC_CAVES, 5.0F, 15.0F);
    public static final BlockEntry BLOCK_OF_SCARLET_NEODYMIUM = metal("block_of_scarlet_neodymium", CaveBlockTab.MAGNETIC_CAVES, 5.0F, 6.0F);
    public static final BlockEntry BLOCK_OF_AZURE_NEODYMIUM = metal("block_of_azure_neodymium", CaveBlockTab.MAGNETIC_CAVES, 5.0F, 6.0F);
    public static final BlockEntry LIMESTONE = rock("limestone", CaveBlockTab.PRIMORDIAL_CAVES, 1.2F, 4.5F, SoundType.STONE);
    public static final BlockEntry LIMESTONE_STAIRS = stairs("limestone_stairs", CaveBlockTab.PRIMORDIAL_CAVES, LIMESTONE);
    public static final BlockEntry LIMESTONE_SLAB = slab("limestone_slab", CaveBlockTab.PRIMORDIAL_CAVES, Material.ROCK, 1.2F, 4.5F, SoundType.STONE);
    public static final BlockEntry LIMESTONE_WALL = wall("limestone_wall", CaveBlockTab.PRIMORDIAL_CAVES, 1.2F, 4.5F, SoundType.STONE);
    public static final BlockEntry LIMESTONE_PILLAR = pillar("limestone_pillar", CaveBlockTab.PRIMORDIAL_CAVES, 1.2F, 4.5F, SoundType.STONE);
    public static final BlockEntry LIMESTONE_CHISELED = directional("limestone_chiseled", CaveBlockTab.PRIMORDIAL_CAVES, Material.ROCK, 1.2F, 4.5F, SoundType.STONE);
    public static final BlockEntry SMOOTH_LIMESTONE = register("smooth_limestone", CaveBlockTab.PRIMORDIAL_CAVES, new SmoothLimestoneBlock());
    public static final BlockEntry SMOOTH_LIMESTONE_STAIRS = stairs("smooth_limestone_stairs", CaveBlockTab.PRIMORDIAL_CAVES, SMOOTH_LIMESTONE);
    public static final BlockEntry SMOOTH_LIMESTONE_SLAB = slab("smooth_limestone_slab", CaveBlockTab.PRIMORDIAL_CAVES, Material.ROCK, 1.2F, 4.5F, SoundType.STONE);
    public static final BlockEntry SMOOTH_LIMESTONE_WALL = wall("smooth_limestone_wall", CaveBlockTab.PRIMORDIAL_CAVES, 1.2F, 4.5F, SoundType.STONE);
    public static final BlockEntry CAVE_PAINTING_AMBERSOL = cavePainting("cave_painting_ambersol");
    public static final BlockEntry CAVE_PAINTING_DARK = cavePainting("cave_painting_dark");
    public static final BlockEntry CAVE_PAINTING_FOOTPRINT = cavePainting("cave_painting_footprint");
    public static final BlockEntry CAVE_PAINTING_FOOTPRINTS = cavePainting("cave_painting_footprints");
    public static final BlockEntry CAVE_PAINTING_TREE_STARS = cavePainting("cave_painting_tree_stars");
    public static final BlockEntry CAVE_PAINTING_PEWEN = cavePainting("cave_painting_pewen");
    public static final BlockEntry CAVE_PAINTING_TRILOCARIS = cavePainting("cave_painting_trilocaris");
    public static final BlockEntry CAVE_PAINTING_GROTTOCERATOPS = cavePainting("cave_painting_grottoceratops");
    public static final BlockEntry CAVE_PAINTING_GROTTOCERATOPS_FRIEND = cavePainting("cave_painting_grottoceratops_friend");
    public static final BlockEntry CAVE_PAINTING_DINO_NUGGETS = cavePainting("cave_painting_dino_nuggets");
    public static final BlockEntry CAVE_PAINTING_VALLUMRAPTOR_CHEST = cavePainting("cave_painting_vallumraptor_chest");
    public static final BlockEntry CAVE_PAINTING_VALLUMRAPTOR_FRIEND = cavePainting("cave_painting_vallumraptor_friend");
    public static final BlockEntry CAVE_PAINTING_RELICHEIRUS = cavePainting("cave_painting_relicheirus");
    public static final BlockEntry CAVE_PAINTING_RELICHEIRUS_SLASH = cavePainting("cave_painting_relicheirus_slash");
    public static final BlockEntry CAVE_PAINTING_ENDERMAN = cavePainting("cave_painting_enderman");
    public static final BlockEntry CAVE_PAINTING_PORTAL = cavePainting("cave_painting_portal");
    public static final BlockEntry CAVE_PAINTING_SUBTERRANODON = cavePainting("cave_painting_subterranodon");
    public static final BlockEntry CAVE_PAINTING_SUBTERRANODON_RIDE = cavePainting("cave_painting_subterranodon_ride");
    public static final BlockEntry CAVE_PAINTING_TREMORSAURUS = cavePainting("cave_painting_tremorsaurus");
    public static final BlockEntry CAVE_PAINTING_TREMORSAURUS_FRIEND = cavePainting("cave_painting_tremorsaurus_friend");
    public static final BlockEntry CAVE_PAINTING_MYSTERY_1 = cavePainting("cave_painting_mystery_1");
    public static final BlockEntry CAVE_PAINTING_MYSTERY_2 = cavePainting("cave_painting_mystery_2");
    public static final BlockEntry CAVE_PAINTING_MYSTERY_3 = cavePainting("cave_painting_mystery_3");
    public static final BlockEntry CAVE_PAINTING_MYSTERY_4 = cavePainting("cave_painting_mystery_4");
    public static final BlockEntry CAVE_PAINTING_MYSTERY_5 = cavePainting("cave_painting_mystery_5");
    public static final BlockEntry CAVE_PAINTING_MYSTERY_6 = cavePainting("cave_painting_mystery_6");
    public static final BlockEntry CAVE_PAINTING_MYSTERY_7 = cavePainting("cave_painting_mystery_7");
    public static final BlockEntry CAVE_PAINTING_MYSTERY_8 = cavePainting("cave_painting_mystery_8");
    public static final BlockEntry CAVE_PAINTING_MYSTERY_9 = cavePainting("cave_painting_mystery_9");
    public static final BlockEntry CARMINE_FROGLIGHT = glowingPillar("carmine_froglight", CaveBlockTab.PRIMORDIAL_CAVES, 0.3F, 0.3F, SoundType.GLASS, 1.0F);
    public static final BlockEntry PEWEN_LOG = woodPillar("pewen_log", CaveBlockTab.PRIMORDIAL_CAVES, 2.0F, 2.0F);
    public static final BlockEntry PEWEN_WOOD = woodPillar("pewen_wood", CaveBlockTab.PRIMORDIAL_CAVES, 2.0F, 2.0F);
    public static final BlockEntry STRIPPED_PEWEN_LOG = woodPillar("stripped_pewen_log", CaveBlockTab.PRIMORDIAL_CAVES, 2.0F, 2.0F);
    public static final BlockEntry STRIPPED_PEWEN_WOOD = woodPillar("stripped_pewen_wood", CaveBlockTab.PRIMORDIAL_CAVES, 2.0F, 2.0F);
    public static final BlockEntry PEWEN_PLANKS = wood("pewen_planks", CaveBlockTab.PRIMORDIAL_CAVES, 2.0F, 3.0F);
    public static final BlockEntry PEWEN_BRANCH = register("pewen_branch", CaveBlockTab.PRIMORDIAL_CAVES, new PewenBranchBlock());
    public static final BlockEntry PEWEN_PINES = register("pewen_pines", CaveBlockTab.PRIMORDIAL_CAVES, new PewenPinesBlock());
    public static final BlockEntry POTTED_PEWEN_PINES = blockOnly("potted_pewen_pines", CaveBlockTab.PRIMORDIAL_CAVES, new PottedCavePlantBlock(PEWEN_PINES.block()));
    public static final BlockEntry PEWEN_STAIRS = stairs("pewen_stairs", CaveBlockTab.PRIMORDIAL_CAVES, PEWEN_PLANKS);
    public static final BlockEntry PEWEN_SLAB = slab("pewen_slab", CaveBlockTab.PRIMORDIAL_CAVES, Material.WOOD, 2.0F, 3.0F, SoundType.WOOD);
    public static final BlockEntry PEWEN_FENCE = fence("pewen_fence", CaveBlockTab.PRIMORDIAL_CAVES, 2.0F, 3.0F);
    public static final BlockEntry PEWEN_PRESSURE_PLATE = pressurePlate("pewen_pressure_plate", CaveBlockTab.PRIMORDIAL_CAVES);
    public static final BlockEntry PEWEN_TRAPDOOR = trapdoor("pewen_trapdoor", CaveBlockTab.PRIMORDIAL_CAVES);
    public static final BlockEntry PEWEN_BUTTON = button("pewen_button", CaveBlockTab.PRIMORDIAL_CAVES);
    public static final BlockEntry PEWEN_FENCE_GATE = fenceGate("pewen_fence_gate", CaveBlockTab.PRIMORDIAL_CAVES);
    public static final BlockEntry PEWEN_DOOR = door("pewen_door", CaveBlockTab.PRIMORDIAL_CAVES);
    public static final BlockEntry PEWEN_SIGN = sign("pewen_sign", "pewen", CaveBlockTab.PRIMORDIAL_CAVES);
    public static final BlockEntry FERN_THATCH = plantLike("fern_thatch", CaveBlockTab.PRIMORDIAL_CAVES, 0.5F, 0.5F);
    public static final BlockEntry CYCAD = register("cycad", CaveBlockTab.PRIMORDIAL_CAVES, new CycadBlock());
    public static final BlockEntry POTTED_CYCAD = blockOnly("potted_cycad", CaveBlockTab.PRIMORDIAL_CAVES, new PottedCavePlantBlock(CYCAD.block()));
    public static final BlockEntry FIDDLEHEAD = register("fiddlehead", CaveBlockTab.PRIMORDIAL_CAVES, new FiddleheadBlock());
    public static final BlockEntry POTTED_FIDDLEHEAD = blockOnly("potted_fiddlehead", CaveBlockTab.PRIMORDIAL_CAVES, new PottedCavePlantBlock(FIDDLEHEAD.block()));
    public static final BlockEntry CURLY_FERN = register("curly_fern", CaveBlockTab.PRIMORDIAL_CAVES, new CurlyFernBlock());
    public static final BlockEntry POTTED_CURLY_FERN = blockOnly("potted_curly_fern", CaveBlockTab.PRIMORDIAL_CAVES, new PottedCavePlantBlock(CURLY_FERN.block()));
    public static final BlockEntry FLYTRAP = register("flytrap", CaveBlockTab.PRIMORDIAL_CAVES, new FlytrapBlock());
    public static final BlockEntry POTTED_FLYTRAP = blockOnly("potted_flytrap", CaveBlockTab.PRIMORDIAL_CAVES, new PottedFlytrapBlock(FLYTRAP.block()));
    public static final BlockEntry TREE_STAR = register("tree_star", CaveBlockTab.PRIMORDIAL_CAVES, new TreeStarBlock());
    public static final BlockEntry THIN_BONE = register("thin_bone", CaveBlockTab.PRIMORDIAL_CAVES, new ThinBoneBlock());
    public static final BlockEntry BONE_RIBS = register("bone_ribs", CaveBlockTab.PRIMORDIAL_CAVES, new BoneRibsBlock());
    public static final BlockEntry DINOSAUR_CHOP = edibleRegister("dinosaur_chop", CaveBlockTab.PRIMORDIAL_CAVES, new DinosaurChopBlock(3, 0.2F), ACItemRegistry.Food.of(3, 0.2F).withMeat());
    public static final BlockEntry COOKED_DINOSAUR_CHOP = edibleRegister("cooked_dinosaur_chop", CaveBlockTab.PRIMORDIAL_CAVES, new DinosaurChopBlock(7, 0.35F), ACItemRegistry.Food.of(7, 0.35F).withMeat());
    public static final BlockEntry SUBTERRANODON_EGG = register("subterranodon_egg", CaveBlockTab.PRIMORDIAL_CAVES, new MultipleDinosaurEggsBlock(SubterranodonEntity.class, 4));
    public static final BlockEntry VALLUMRAPTOR_EGG = register("vallumraptor_egg", CaveBlockTab.PRIMORDIAL_CAVES, new MultipleDinosaurEggsBlock(VallumraptorEntity.class, 4));
    public static final BlockEntry GROTTOCERATOPS_EGG = register("grottoceratops_egg", CaveBlockTab.PRIMORDIAL_CAVES, new DinosaurEggBlock(GrottoceratopsEntity.class, 10, 12));
    public static final BlockEntry RELICHEIRUS_EGG = register("relicheirus_egg", CaveBlockTab.PRIMORDIAL_CAVES, new DinosaurEggBlock(RelicheirusEntity.class, 14, 16));
    public static final BlockEntry FLOOD_BASALT = pillar("flood_basalt", CaveBlockTab.PRIMORDIAL_CAVES, 3.0F, 100.0F, SoundType.STONE);
    public static final BlockEntry RADROCK = rock("radrock", CaveBlockTab.TOXIC_CAVES, 4.0F, 11.0F, SoundType.STONE);
    public static final BlockEntry RADROCK_STAIRS = stairs("radrock_stairs", CaveBlockTab.TOXIC_CAVES, RADROCK);
    public static final BlockEntry RADROCK_SLAB = slab("radrock_slab", CaveBlockTab.TOXIC_CAVES, Material.ROCK, 4.0F, 11.0F, SoundType.STONE);
    public static final BlockEntry RADROCK_WALL = wall("radrock_wall", CaveBlockTab.TOXIC_CAVES, 4.0F, 11.0F, SoundType.STONE);
    public static final BlockEntry RADROCK_BRICKS = rock("radrock_bricks", CaveBlockTab.TOXIC_CAVES, 4.0F, 11.0F, SoundType.STONE);
    public static final BlockEntry RADROCK_BRICK_STAIRS = stairs("radrock_brick_stairs", CaveBlockTab.TOXIC_CAVES, RADROCK_BRICKS);
    public static final BlockEntry RADROCK_BRICK_SLAB = slab("radrock_brick_slab", CaveBlockTab.TOXIC_CAVES, Material.ROCK, 4.0F, 11.0F, SoundType.STONE);
    public static final BlockEntry RADROCK_BRICK_WALL = wall("radrock_brick_wall", CaveBlockTab.TOXIC_CAVES, 4.0F, 11.0F, SoundType.STONE);
    public static final BlockEntry RADROCK_CHISELED = rock("radrock_chiseled", CaveBlockTab.TOXIC_CAVES, 4.0F, 11.0F, SoundType.STONE);
    public static final BlockEntry ACIDIC_RADROCK = register("acidic_radrock", CaveBlockTab.TOXIC_CAVES, new AcidicRadrockBlock());
    public static final BlockEntry RADROCK_URANIUM_ORE = ore("radrock_uranium_ore", CaveBlockTab.TOXIC_CAVES, 5.0F, 11.0F, SoundType.STONE, ACItemRegistry.URANIUM.item(), 1, 1, 0, 1);
    public static final BlockEntry CINDER_BLOCK = rock("cinder_block", CaveBlockTab.TOXIC_CAVES, 5.0F, 20.0F, ACSoundTypes.CINDER_BLOCK);
    public static final BlockEntry CINDER_BLOCK_STAIRS = stairs("cinder_block_stairs", CaveBlockTab.TOXIC_CAVES, CINDER_BLOCK);
    public static final BlockEntry CINDER_BLOCK_SLAB = slab("cinder_block_slab", CaveBlockTab.TOXIC_CAVES, Material.ROCK, 5.0F, 20.0F, ACSoundTypes.CINDER_BLOCK);
    public static final BlockEntry CINDER_BLOCK_WALL = wall("cinder_block_wall", CaveBlockTab.TOXIC_CAVES, 5.0F, 20.0F, ACSoundTypes.CINDER_BLOCK);
    public static final BlockEntry UNREFINED_WASTE = register("unrefined_waste", CaveBlockTab.TOXIC_CAVES, new UnrefinedWasteBlock());
    public static final BlockEntry SULFUR = register("sulfur", CaveBlockTab.TOXIC_CAVES, new SulfurBlock().sound(ACSoundTypes.SULFUR));
    public static final BlockEntry SULFUR_BUD_SMALL = register("sulfur_bud_small", CaveBlockTab.TOXIC_CAVES, new SulfurBudBlock(6, 4, 1, 1).sound(ACSoundTypes.SULFUR));
    public static final BlockEntry SULFUR_BUD_MEDIUM = register("sulfur_bud_medium", CaveBlockTab.TOXIC_CAVES, new SulfurBudBlock(6, 8, 1, 1).sound(ACSoundTypes.SULFUR));
    public static final BlockEntry SULFUR_BUD_LARGE = register("sulfur_bud_large", CaveBlockTab.TOXIC_CAVES, new SulfurBudBlock(6, 12, 1, 1).sound(ACSoundTypes.SULFUR));
    public static final BlockEntry SULFUR_CLUSTER = register("sulfur_cluster", CaveBlockTab.TOXIC_CAVES, new SulfurBudBlock(6, 14, 2, 5).sound(ACSoundTypes.SULFUR));
    public static final BlockEntry RUSTY_SCRAP_METAL = metal("rusty_scrap_metal", CaveBlockTab.TOXIC_CAVES, 5.0F, 15.0F);
    public static final BlockEntry RUSTY_SCRAP_METAL_PLATE = metal("rusty_scrap_metal_plate", CaveBlockTab.TOXIC_CAVES, 5.0F, 15.0F);
    public static final BlockEntry RUSTY_REBAR = register("rusty_rebar", CaveBlockTab.TOXIC_CAVES, new RebarBlock());
    public static final BlockEntry RUSTY_SCAFFOLDING = scaffolding("rusty_scaffolding", CaveBlockTab.TOXIC_CAVES);
    public static final BlockEntry METAL_BARREL = register("metal_barrel", CaveBlockTab.TOXIC_CAVES, new MetalBarrelBlock());
    public static final BlockEntry WASTE_DRUM = register("waste_drum", CaveBlockTab.TOXIC_CAVES, new WasteDrumBlock());
    public static final BlockEntry URANIUM_ROD = register("uranium_rod", CaveBlockTab.TOXIC_CAVES, new UraniumRodBlock());
    public static final BlockEntry NUCLEAR_FURNACE_COMPONENT = register("nuclear_furnace_component", CaveBlockTab.TOXIC_CAVES, new NuclearFurnaceComponentBlock());
    public static final BlockEntry NUCLEAR_FURNACE = blockOnly("nuclear_furnace", CaveBlockTab.TOXIC_CAVES, new NuclearFurnaceBlock());
    public static final BlockEntry NUCLEAR_SIREN = register("nuclear_siren", CaveBlockTab.TOXIC_CAVES, new NuclearSirenBlock());
    public static final BlockEntry SIREN_LIGHT = register("siren_light", CaveBlockTab.TOXIC_CAVES, new SirenLightBlock());
    public static final BlockEntry BLOCK_OF_URANIUM = register("block_of_uranium", CaveBlockTab.TOXIC_CAVES, new BasicCaveBlock(Material.IRON, 3.5F, 3.5F, SoundType.METAL).light(0.25F));
    public static final BlockEntry HAZMAT_BLOCK = hazmat("hazmat_block");
    public static final BlockEntry HAZMAT_WARNING_BLOCK = hazmat("hazmat_warning_block");
    public static final BlockEntry HAZMAT_SKULL_BLOCK = hazmat("hazmat_skull_block");
    public static final BlockEntry ACID = fluid("acid", CaveBlockTab.TOXIC_CAVES, new AcidBlock());
    public static final BlockEntry UNDERWEED = register("underweed", CaveBlockTab.TOXIC_CAVES, new CavePlantBlock(false));
    public static final BlockEntry POTTED_UNDERWEED = blockOnly("potted_underweed", CaveBlockTab.TOXIC_CAVES, new PottedCavePlantBlock(UNDERWEED.block()));
    public static final BlockEntry RADON_LAMP_WHITE = lamp("radon_lamp_white", CaveBlockTab.TOXIC_CAVES);
    public static final BlockEntry RADON_LAMP_ORANGE = lamp("radon_lamp_orange", CaveBlockTab.TOXIC_CAVES);
    public static final BlockEntry RADON_LAMP_MAGENTA = lamp("radon_lamp_magenta", CaveBlockTab.TOXIC_CAVES);
    public static final BlockEntry RADON_LAMP_LIGHT_BLUE = lamp("radon_lamp_light_blue", CaveBlockTab.TOXIC_CAVES);
    public static final BlockEntry RADON_LAMP_YELLOW = lamp("radon_lamp_yellow", CaveBlockTab.TOXIC_CAVES);
    public static final BlockEntry RADON_LAMP_LIME = lamp("radon_lamp_lime", CaveBlockTab.TOXIC_CAVES);
    public static final BlockEntry RADON_LAMP_PINK = lamp("radon_lamp_pink", CaveBlockTab.TOXIC_CAVES);
    public static final BlockEntry RADON_LAMP_GRAY = lamp("radon_lamp_gray", CaveBlockTab.TOXIC_CAVES);
    public static final BlockEntry RADON_LAMP_LIGHT_GRAY = lamp("radon_lamp_light_gray", CaveBlockTab.TOXIC_CAVES);
    public static final BlockEntry RADON_LAMP_CYAN = lamp("radon_lamp_cyan", CaveBlockTab.TOXIC_CAVES);
    public static final BlockEntry RADON_LAMP_PURPLE = lamp("radon_lamp_purple", CaveBlockTab.TOXIC_CAVES);
    public static final BlockEntry RADON_LAMP_BLUE = lamp("radon_lamp_blue", CaveBlockTab.TOXIC_CAVES);
    public static final BlockEntry RADON_LAMP_BROWN = lamp("radon_lamp_brown", CaveBlockTab.TOXIC_CAVES);
    public static final BlockEntry RADON_LAMP_GREEN = lamp("radon_lamp_green", CaveBlockTab.TOXIC_CAVES);
    public static final BlockEntry RADON_LAMP_RED = lamp("radon_lamp_red", CaveBlockTab.TOXIC_CAVES);
    public static final BlockEntry RADON_LAMP_BLACK = lamp("radon_lamp_black", CaveBlockTab.TOXIC_CAVES);
    public static final BlockEntry ABYSSMARINE = rock("abyssmarine", CaveBlockTab.ABYSSAL_CHASM, 2.5F, 50.0F, SoundType.STONE);
    public static final BlockEntry ABYSSMARINE_STAIRS = stairs("abyssmarine_stairs", CaveBlockTab.ABYSSAL_CHASM, ABYSSMARINE);
    public static final BlockEntry ABYSSMARINE_SLAB = slab("abyssmarine_slab", CaveBlockTab.ABYSSAL_CHASM, Material.ROCK, 2.5F, 50.0F, SoundType.STONE);
    public static final BlockEntry ABYSSMARINE_WALL = wall("abyssmarine_wall", CaveBlockTab.ABYSSAL_CHASM, 2.5F, 50.0F, SoundType.STONE);
    public static final BlockEntry ABYSSMARINE_BRICKS = rock("abyssmarine_bricks", CaveBlockTab.ABYSSAL_CHASM, 2.5F, 50.0F, SoundType.STONE);
    public static final BlockEntry ABYSSMARINE_BRICK_STAIRS = stairs("abyssmarine_brick_stairs", CaveBlockTab.ABYSSAL_CHASM, ABYSSMARINE_BRICKS);
    public static final BlockEntry ABYSSMARINE_BRICK_SLAB = slab("abyssmarine_brick_slab", CaveBlockTab.ABYSSAL_CHASM, Material.ROCK, 2.5F, 50.0F, SoundType.STONE);
    public static final BlockEntry ABYSSMARINE_BRICK_WALL = wall("abyssmarine_brick_wall", CaveBlockTab.ABYSSAL_CHASM, 2.5F, 50.0F, SoundType.STONE);
    public static final BlockEntry ABYSSMARINE_PILLAR = pillar("abyssmarine_pillar", CaveBlockTab.ABYSSAL_CHASM, 2.5F, 50.0F, SoundType.STONE);
    public static final BlockEntry ABYSSMARINE_TILES = rock("abyssmarine_tiles", CaveBlockTab.ABYSSAL_CHASM, 2.5F, 50.0F, SoundType.STONE);
    public static final BlockEntry GUANOSTONE = rock("guanostone", CaveBlockTab.ABYSSAL_CHASM, 1.3F, 2.0F, SoundType.STONE);
    public static final BlockEntry MUCK = register("muck", CaveBlockTab.ABYSSAL_CHASM, new MuckBlock());
    public static final BlockEntry GUANOSTONE_STAIRS = stairs("guanostone_stairs", CaveBlockTab.ABYSSAL_CHASM, GUANOSTONE);
    public static final BlockEntry GUANOSTONE_SLAB = slab("guanostone_slab", CaveBlockTab.ABYSSAL_CHASM, Material.ROCK, 1.3F, 2.0F, SoundType.STONE);
    public static final BlockEntry GUANOSTONE_WALL = wall("guanostone_wall", CaveBlockTab.ABYSSAL_CHASM, 1.3F, 2.0F, SoundType.STONE);
    public static final BlockEntry BLOCK_OF_PEARL = rock("block_of_pearl", CaveBlockTab.ABYSSAL_CHASM, 3.5F, 3.5F, SoundType.GLASS);
    public static final BlockEntry SMOOTH_BONE = rock("smooth_bone", CaveBlockTab.ABYSSAL_CHASM, 2.0F, 2.0F, SoundType.STONE);
    public static final BlockEntry SMOOTH_BONE_STAIRS = stairs("smooth_bone_stairs", CaveBlockTab.ABYSSAL_CHASM, SMOOTH_BONE);
    public static final BlockEntry SMOOTH_BONE_SLAB = slab("smooth_bone_slab", CaveBlockTab.ABYSSAL_CHASM, Material.ROCK, 2.0F, 2.0F, SoundType.STONE);
    public static final BlockEntry SMOOTH_BONE_WALL = wall("smooth_bone_wall", CaveBlockTab.ABYSSAL_CHASM, 2.0F, 2.0F, SoundType.STONE);
    public static final BlockEntry MUSSEL = register("mussel", CaveBlockTab.ABYSSAL_CHASM, new MusselBlock());
    public static final BlockEntry BIOLUMINESCENT_TORCH = register("bioluminescent_torch", CaveBlockTab.ABYSSAL_CHASM, new BioluminescentTorchBlock());
    public static final BlockEntry GUANOSTONE_BRICKS = rock("guanostone_bricks", CaveBlockTab.ABYSSAL_CHASM, 1.3F, 2.0F, SoundType.STONE);
    public static final BlockEntry GUANOSTONE_BRICK_STAIRS = stairs("guanostone_brick_stairs", CaveBlockTab.ABYSSAL_CHASM, GUANOSTONE_BRICKS);
    public static final BlockEntry GUANOSTONE_BRICK_SLAB = slab("guanostone_brick_slab", CaveBlockTab.ABYSSAL_CHASM, Material.ROCK, 1.3F, 2.0F, SoundType.STONE);
    public static final BlockEntry GUANOSTONE_BRICK_WALL = wall("guanostone_brick_wall", CaveBlockTab.ABYSSAL_CHASM, 1.3F, 2.0F, SoundType.STONE);
    public static final BlockEntry GUANOSTONE_CHISELED = rock("guanostone_chiseled", CaveBlockTab.ABYSSAL_CHASM, 1.3F, 2.0F, SoundType.STONE);
    public static final BlockEntry GUANOSTONE_TILES = rock("guanostone_tiles", CaveBlockTab.ABYSSAL_CHASM, 1.3F, 2.0F, SoundType.STONE);
    public static final BlockEntry GUANOSTONE_REDSTONE_ORE = ore("guanostone_redstone_ore", CaveBlockTab.ABYSSAL_CHASM, 3.0F, 5.0F, SoundType.STONE, net.minecraft.init.Items.REDSTONE, 4, 5, 1, 5);
    public static final BlockEntry ABYSSAL_ALTAR = register("abyssal_altar", CaveBlockTab.ABYSSAL_CHASM, new AbyssalAltarBlock());
    public static final BlockEntry GUANO_BLOCK = register("guano_block", CaveBlockTab.FORLORN_HOLLOWS, new GuanoBlock());
    public static final BlockEntry GUANO_LAYER = guanoLayer("guano_layer");
    public static final BlockEntry COPROLITH = rock("coprolith", CaveBlockTab.FORLORN_HOLLOWS, 1.75F, 4.0F, SoundType.STONE);
    public static final BlockEntry COPROLITH_STAIRS = stairs("coprolith_stairs", CaveBlockTab.FORLORN_HOLLOWS, COPROLITH);
    public static final BlockEntry COPROLITH_SLAB = slab("coprolith_slab", CaveBlockTab.FORLORN_HOLLOWS, Material.ROCK, 1.75F, 4.0F, SoundType.STONE);
    public static final BlockEntry COPROLITH_WALL = wall("coprolith_wall", CaveBlockTab.FORLORN_HOLLOWS, 1.75F, 4.0F, SoundType.STONE);
    public static final BlockEntry SMOOTH_COPROLITH = rock("smooth_coprolith", CaveBlockTab.FORLORN_HOLLOWS, 1.75F, 4.0F, SoundType.STONE);
    public static final BlockEntry SMOOTH_COPROLITH_STAIRS = stairs("smooth_coprolith_stairs", CaveBlockTab.FORLORN_HOLLOWS, SMOOTH_COPROLITH);
    public static final BlockEntry SMOOTH_COPROLITH_SLAB = slab("smooth_coprolith_slab", CaveBlockTab.FORLORN_HOLLOWS, Material.ROCK, 1.75F, 4.0F, SoundType.STONE);
    public static final BlockEntry SMOOTH_COPROLITH_WALL = wall("smooth_coprolith_wall", CaveBlockTab.FORLORN_HOLLOWS, 1.75F, 4.0F, SoundType.STONE);
    public static final BlockEntry COPROLITH_COAL_ORE = ore("coprolith_coal_ore", CaveBlockTab.FORLORN_HOLLOWS, 3.0F, 5.0F, SoundType.STONE, net.minecraft.init.Items.COAL, 1, 1, 0, 2);
    public static final BlockEntry POROUS_COPROLITH = register("porous_coprolith", CaveBlockTab.FORLORN_HOLLOWS, new BasicTranslucentBlock(Material.ROCK, 1.75F, 4.0F, SoundType.STONE));
    public static final BlockEntry PEERING_COPROLITH = register("peering_coprolith", CaveBlockTab.FORLORN_HOLLOWS, new BasicTranslucentBlock(Material.ROCK, 1.75F, 4.0F, SoundType.STONE));
    public static final BlockEntry THORNWOOD_LOG = woodPillar("thornwood_log", CaveBlockTab.FORLORN_HOLLOWS, 2.0F, 2.0F);
    public static final BlockEntry THORNWOOD_WOOD = woodPillar("thornwood_wood", CaveBlockTab.FORLORN_HOLLOWS, 2.0F, 2.0F);
    public static final BlockEntry STRIPPED_THORNWOOD_LOG = woodPillar("stripped_thornwood_log", CaveBlockTab.FORLORN_HOLLOWS, 2.0F, 2.0F);
    public static final BlockEntry STRIPPED_THORNWOOD_WOOD = woodPillar("stripped_thornwood_wood", CaveBlockTab.FORLORN_HOLLOWS, 2.0F, 2.0F);
    public static final BlockEntry THORNWOOD_PLANKS = wood("thornwood_planks", CaveBlockTab.FORLORN_HOLLOWS, 2.0F, 3.0F);
    public static final BlockEntry THORNWOOD_STAIRS = stairs("thornwood_stairs", CaveBlockTab.FORLORN_HOLLOWS, THORNWOOD_PLANKS);
    public static final BlockEntry THORNWOOD_SLAB = slab("thornwood_slab", CaveBlockTab.FORLORN_HOLLOWS, Material.WOOD, 2.0F, 3.0F, SoundType.WOOD);
    public static final BlockEntry THORNWOOD_FENCE = fence("thornwood_fence", CaveBlockTab.FORLORN_HOLLOWS, 2.0F, 3.0F);
    public static final BlockEntry THORNWOOD_PRESSURE_PLATE = pressurePlate("thornwood_pressure_plate", CaveBlockTab.FORLORN_HOLLOWS);
    public static final BlockEntry THORNWOOD_TRAPDOOR = trapdoor("thornwood_trapdoor", CaveBlockTab.FORLORN_HOLLOWS);
    public static final BlockEntry THORNWOOD_BUTTON = button("thornwood_button", CaveBlockTab.FORLORN_HOLLOWS);
    public static final BlockEntry THORNWOOD_FENCE_GATE = fenceGate("thornwood_fence_gate", CaveBlockTab.FORLORN_HOLLOWS);
    public static final BlockEntry THORNWOOD_DOOR = door("thornwood_door", CaveBlockTab.FORLORN_HOLLOWS);
    public static final BlockEntry THORNWOOD_SIGN = sign("thornwood_sign", "thornwood", CaveBlockTab.FORLORN_HOLLOWS);
    public static final BlockEntry THORNWOOD_BRANCH = register("thornwood_branch", CaveBlockTab.FORLORN_HOLLOWS, new ThornwoodBranchBlock());
    public static final BlockEntry POTTED_THORNWOOD_BRANCH = blockOnly("potted_thornwood_branch", CaveBlockTab.FORLORN_HOLLOWS, new PottedCavePlantBlock(THORNWOOD_BRANCH.block()));
    public static final BlockEntry BLOCK_OF_CHOCOLATE = edibleRegister("block_of_chocolate", CaveBlockTab.CANDY_CAVITY, new ChocolateBlock(1.0F, 2.0F, ACSoundTypes.DENSE_CANDY), candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry BLOCK_OF_POLISHED_CHOCOLATE = edible("block_of_polished_chocolate", CaveBlockTab.CANDY_CAVITY, 1.0F, 2.0F, ACSoundTypes.DENSE_CANDY, candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry BLOCK_OF_CHISELED_CHOCOLATE = edible("block_of_chiseled_chocolate", CaveBlockTab.CANDY_CAVITY, 1.0F, 2.0F, ACSoundTypes.DENSE_CANDY, candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry BLOCK_OF_FROSTED_CHOCOLATE = edibleRegister("block_of_frosted_chocolate", CaveBlockTab.CANDY_CAVITY, new FrostedChocolateBlock(1.0F, 2.0F, ACSoundTypes.DENSE_CANDY), candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry BLOCK_OF_FROSTING = edible("block_of_frosting", CaveBlockTab.CANDY_CAVITY, 1.0F, 1.0F, ACSoundTypes.SQUISHY_CANDY, candyFood(1, 0.1F, 0.02F));
    public static final BlockEntry BLOCK_OF_VANILLA_FROSTING = edible("block_of_vanilla_frosting", CaveBlockTab.CANDY_CAVITY, 1.0F, 1.0F, ACSoundTypes.SQUISHY_CANDY, candyFood(1, 0.1F, 0.02F));
    public static final BlockEntry BLOCK_OF_CHOCOLATE_FROSTING = edible("block_of_chocolate_frosting", CaveBlockTab.CANDY_CAVITY, 1.0F, 1.0F, ACSoundTypes.SQUISHY_CANDY, candyFood(1, 0.1F, 0.02F));
    public static final BlockEntry CAKE_LAYER = ediblePillar("cake_layer", CaveBlockTab.CANDY_CAVITY, 1.5F, 1.0F, ACSoundTypes.SOFT_CANDY, candyFood(1, 0.1F, 0.02F));
    public static final BlockEntry DOUGH_BLOCK = edible("dough_block", CaveBlockTab.CANDY_CAVITY, 1.0F, 1.5F, SoundType.CLOTH, candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry COOKIE_BLOCK = edible("cookie_block", CaveBlockTab.CANDY_CAVITY, 1.5F, 1.5F, ACSoundTypes.DENSE_CANDY, candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry WAFER_COOKIE_BLOCK = edible("wafer_cookie_block", CaveBlockTab.CANDY_CAVITY, 1.5F, 1.5F, ACSoundTypes.DENSE_CANDY, candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry WAFER_COOKIE_STAIRS = edibleStairs("wafer_cookie_stairs", CaveBlockTab.CANDY_CAVITY, WAFER_COOKIE_BLOCK, candyFood(1, 0.05F, 0.01F));
    public static final BlockEntry WAFER_COOKIE_SLAB = edibleSlab("wafer_cookie_slab", CaveBlockTab.CANDY_CAVITY, Material.CAKE, 1.5F, 1.5F, ACSoundTypes.DENSE_CANDY, candyFood(1, 0.05F, 0.01F));
    public static final BlockEntry WAFER_COOKIE_WALL = edibleWall("wafer_cookie_wall", CaveBlockTab.CANDY_CAVITY, 1.5F, 1.5F, ACSoundTypes.DENSE_CANDY, candyFood(1, 0.05F, 0.01F));
    public static final BlockEntry ROCK_CANDY_WHITE = rockCandy("rock_candy_white");
    public static final BlockEntry ROCK_CANDY_ORANGE = rockCandy("rock_candy_orange");
    public static final BlockEntry ROCK_CANDY_MAGENTA = rockCandy("rock_candy_magenta");
    public static final BlockEntry ROCK_CANDY_LIGHT_BLUE = rockCandy("rock_candy_light_blue");
    public static final BlockEntry ROCK_CANDY_YELLOW = rockCandy("rock_candy_yellow");
    public static final BlockEntry ROCK_CANDY_LIME = rockCandy("rock_candy_lime");
    public static final BlockEntry ROCK_CANDY_PINK = rockCandy("rock_candy_pink");
    public static final BlockEntry ROCK_CANDY_GRAY = rockCandy("rock_candy_gray");
    public static final BlockEntry ROCK_CANDY_LIGHT_GRAY = rockCandy("rock_candy_light_gray");
    public static final BlockEntry ROCK_CANDY_CYAN = rockCandy("rock_candy_cyan");
    public static final BlockEntry ROCK_CANDY_PURPLE = rockCandy("rock_candy_purple");
    public static final BlockEntry ROCK_CANDY_BLUE = rockCandy("rock_candy_blue");
    public static final BlockEntry ROCK_CANDY_BROWN = rockCandy("rock_candy_brown");
    public static final BlockEntry ROCK_CANDY_GREEN = rockCandy("rock_candy_green");
    public static final BlockEntry ROCK_CANDY_RED = rockCandy("rock_candy_red");
    public static final BlockEntry ROCK_CANDY_BLACK = rockCandy("rock_candy_black");
    public static final BlockEntry PURPLE_SODA = fluid("purple_soda", CaveBlockTab.CANDY_CAVITY, new PurpleSodaBlock());
    public static final BlockEntry CANDY_CANE = edibleRegister("candy_cane", CaveBlockTab.CANDY_CAVITY, new SmallCandyCaneBlock(), candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry STRIPPED_CANDY_CANE_BLOCK = edibleRegister("stripped_candy_cane_block", CaveBlockTab.CANDY_CAVITY, CandyCaneBlock.stripped(), candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry CANDY_CANE_BLOCK = edibleRegister("candy_cane_block", CaveBlockTab.CANDY_CAVITY, new CandyCaneBlock(STRIPPED_CANDY_CANE_BLOCK.block()), candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry CHISELED_CANDY_CANE_BLOCK = edibleRegister("chiseled_candy_cane_block", CaveBlockTab.CANDY_CAVITY, new CandyCaneBlock(STRIPPED_CANDY_CANE_BLOCK.block()), candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry STRIPPED_CANDY_CANE_POLE = edibleRegister("stripped_candy_cane_pole", CaveBlockTab.CANDY_CAVITY, CandyCanePoleBlock.stripped(), candyFood(1, 0.05F, 0.01F));
    public static final BlockEntry CANDY_CANE_POLE = edibleRegister("candy_cane_pole", CaveBlockTab.CANDY_CAVITY, new CandyCanePoleBlock(STRIPPED_CANDY_CANE_POLE.block()), candyFood(1, 0.05F, 0.01F));
    public static final BlockEntry LOLLIPOP_BUNCH = edibleRegister("lollipop_bunch", CaveBlockTab.CANDY_CAVITY, new CavePlantBlock(true), candyFood(1, 0.1F, 0.01F));
    public static final BlockEntry SMALL_PEPPERMINT = edibleRegister("small_peppermint", CaveBlockTab.CANDY_CAVITY, new PeppermintBlock(2.0D, 6.0D), candyFood(3, 0.15F, 0.01F));
    public static final BlockEntry LARGE_PEPPERMINT = edibleRegister("large_peppermint", CaveBlockTab.CANDY_CAVITY, new PeppermintBlock(0.0D, 8.0D), candyFood(5, 0.15F, 0.01F));
    public static final BlockEntry VANILLA_ICE_CREAM = edibleRegister("vanilla_ice_cream", CaveBlockTab.CANDY_CAVITY, new IceCreamBlock(), candyFood(4, 0.2F, 0.03F));
    public static final BlockEntry CHOCOLATE_ICE_CREAM = edibleRegister("chocolate_ice_cream", CaveBlockTab.CANDY_CAVITY, new IceCreamBlock(), candyFood(4, 0.2F, 0.03F));
    public static final BlockEntry SWEETBERRY_ICE_CREAM = edibleRegister("sweetberry_ice_cream", CaveBlockTab.CANDY_CAVITY, new IceCreamBlock(), candyFood(4, 0.2F, 0.03F));
    public static final BlockEntry SPRINKLES = sprinkles("sprinkles", CaveBlockTab.CANDY_CAVITY);
    public static final BlockEntry FROSTMINT = frostmint("frostmint");
    public static final BlockEntry SUGAR_GLASS = edibleRegister("sugar_glass", CaveBlockTab.CANDY_CAVITY, new SugarGlassBlock(), candyFood(1, 0.1F, 0.01F));
    public static final BlockEntry LICOROOT = ediblePillar("licoroot", CaveBlockTab.CANDY_CAVITY, 1.0F, 1.5F, SoundType.WOOD, licorootFood(3));
    public static final BlockEntry LICOROOT_VINE = edibleRegister("licoroot_vine", CaveBlockTab.CANDY_CAVITY, new LicorootVineBlock(), licorootFood(1));
    public static final BlockEntry LICOROOT_SPROUT = edibleRegister("licoroot_sprout", CaveBlockTab.CANDY_CAVITY, new LicorootSproutBlock(), licorootFood(1));
    public static final BlockEntry GUMMY_RING_RED = edibleRegister("gummy_ring_red", CaveBlockTab.CANDY_CAVITY, new GummyRingBlock(), candyFood(3, 0.15F, 0.01F));
    public static final BlockEntry GUMMY_RING_GREEN = edibleRegister("gummy_ring_green", CaveBlockTab.CANDY_CAVITY, new GummyRingBlock(), candyFood(3, 0.15F, 0.01F));
    public static final BlockEntry GUMMY_RING_YELLOW = edibleRegister("gummy_ring_yellow", CaveBlockTab.CANDY_CAVITY, new GummyRingBlock(), candyFood(3, 0.15F, 0.01F));
    public static final BlockEntry GUMMY_RING_BLUE = edibleRegister("gummy_ring_blue", CaveBlockTab.CANDY_CAVITY, new GummyRingBlock(), candyFood(3, 0.15F, 0.01F));
    public static final BlockEntry GUMMY_RING_PINK = edibleRegister("gummy_ring_pink", CaveBlockTab.CANDY_CAVITY, new GummyRingBlock(), candyFood(3, 0.15F, 0.01F));
    public static final BlockEntry SUNDROP = edibleRegister("sundrop", CaveBlockTab.CANDY_CAVITY, new SundropBlock(), candyFood(5, 0.2F, 0.05F));
    public static final BlockEntry CONVERSION_CRUCIBLE = register("conversion_crucible", CaveBlockTab.CANDY_CAVITY, new ConversionCrucibleBlock());
    public static final BlockEntry GIANT_SWEETBERRY = edibleRegister("giant_sweetberry", CaveBlockTab.CANDY_CAVITY, new GiantSweetberryBlock(), ACItemRegistry.Food.of(5, 0.2F));
    public static final BlockEntry SWEET_PUFF = edibleRegister("sweet_puff", CaveBlockTab.CANDY_CAVITY, new CavePlantBlock(true), candyFood(1, 0.1F, 0.02F));
    public static final BlockEntry GINGERBREAD_BLOCK = edible("gingerbread_block", CaveBlockTab.CANDY_CAVITY, 1.0F, 1.5F, ACSoundTypes.DENSE_CANDY, candyFood(4, 0.1F, 0.01F));
    public static final BlockEntry GINGERBREAD_STAIRS = edibleStairs("gingerbread_stairs", CaveBlockTab.CANDY_CAVITY, GINGERBREAD_BLOCK, candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry GINGERBREAD_SLAB = edibleSlab("gingerbread_slab", CaveBlockTab.CANDY_CAVITY, Material.CAKE, 1.0F, 1.5F, ACSoundTypes.DENSE_CANDY, candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry GINGERBREAD_WALL = edibleWall("gingerbread_wall", CaveBlockTab.CANDY_CAVITY, 1.0F, 1.5F, ACSoundTypes.DENSE_CANDY, candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry GINGERBREAD_DOOR = edibleRegister("gingerbread_door", CaveBlockTab.CANDY_CAVITY, new GingerbreadDoorBlock(), candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry FROSTED_GINGERBREAD_BLOCK = edible("frosted_gingerbread_block", CaveBlockTab.CANDY_CAVITY, 1.0F, 1.5F, ACSoundTypes.DENSE_CANDY, candyFood(4, 0.1F, 0.01F));
    public static final BlockEntry FROSTED_GINGERBREAD_STAIRS = edibleStairs("frosted_gingerbread_stairs", CaveBlockTab.CANDY_CAVITY, FROSTED_GINGERBREAD_BLOCK, candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry FROSTED_GINGERBREAD_SLAB = edibleSlab("frosted_gingerbread_slab", CaveBlockTab.CANDY_CAVITY, Material.CAKE, 1.0F, 1.5F, ACSoundTypes.DENSE_CANDY, candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry FROSTED_GINGERBREAD_WALL = edibleWall("frosted_gingerbread_wall", CaveBlockTab.CANDY_CAVITY, 1.0F, 1.5F, ACSoundTypes.DENSE_CANDY, candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry FROSTED_GINGERBREAD_DOOR = edibleRegister("frosted_gingerbread_door", CaveBlockTab.CANDY_CAVITY, new GingerbreadDoorBlock(), candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry GINGERBREAD_BRICKS = edible("gingerbread_bricks", CaveBlockTab.CANDY_CAVITY, 1.0F, 1.5F, ACSoundTypes.DENSE_CANDY, candyFood(4, 0.1F, 0.01F));
    public static final BlockEntry GINGERBREAD_BRICK_STAIRS = edibleStairs("gingerbread_brick_stairs", CaveBlockTab.CANDY_CAVITY, GINGERBREAD_BRICKS, candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry GINGERBREAD_BRICK_SLAB = edibleSlab("gingerbread_brick_slab", CaveBlockTab.CANDY_CAVITY, Material.CAKE, 1.0F, 1.5F, ACSoundTypes.DENSE_CANDY, candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry GINGERBREAD_BRICK_WALL = edibleWall("gingerbread_brick_wall", CaveBlockTab.CANDY_CAVITY, 1.0F, 1.5F, ACSoundTypes.DENSE_CANDY, candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry FROSTED_GINGERBREAD_BRICKS = edible("frosted_gingerbread_bricks", CaveBlockTab.CANDY_CAVITY, 1.0F, 1.5F, ACSoundTypes.DENSE_CANDY, candyFood(4, 0.1F, 0.01F));
    public static final BlockEntry FROSTED_GINGERBREAD_BRICK_STAIRS = edibleStairs("frosted_gingerbread_brick_stairs", CaveBlockTab.CANDY_CAVITY, FROSTED_GINGERBREAD_BRICKS, candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry FROSTED_GINGERBREAD_BRICK_SLAB = edibleSlab("frosted_gingerbread_brick_slab", CaveBlockTab.CANDY_CAVITY, Material.CAKE, 1.0F, 1.5F, ACSoundTypes.DENSE_CANDY, candyFood(2, 0.1F, 0.01F));
    public static final BlockEntry FROSTED_GINGERBREAD_BRICK_WALL = edibleWall("frosted_gingerbread_brick_wall", CaveBlockTab.CANDY_CAVITY, 1.0F, 1.5F, ACSoundTypes.DENSE_CANDY, candyFood(2, 0.1F, 0.01F));

    public static final List<BlockEntry> BLOCKS = Collections.unmodifiableList(MUTABLE_BLOCKS);
    public static final List<Block> AUXILIARY_BLOCKS = Collections.unmodifiableList(MUTABLE_AUXILIARY_BLOCKS);

    public static List<BlockEntry> blocksFor(CaveBlockTab tab) {
        List<BlockEntry> matches = new ArrayList<>();
        for (BlockEntry entry : BLOCKS) {
            if (entry.tab() == tab) {
                matches.add(entry);
            }
        }
        return Collections.unmodifiableList(matches);
    }

    private static BlockEntry rock(String name, CaveBlockTab tab, float hardness, float resistance, SoundType sound) {
        return register(name, tab, new BasicCaveBlock(Material.ROCK, hardness, resistance, sound));
    }

    private static BlockEntry cavePainting(String name) {
        return register(name, CaveBlockTab.PRIMORDIAL_CAVES, new CavePaintingBlock());
    }

    private static BlockEntry metal(String name, CaveBlockTab tab, float hardness, float resistance) {
        return register(name, tab, new BasicCaveBlock(Material.IRON, hardness, resistance, SoundType.METAL));
    }

    private static BlockEntry wood(String name, CaveBlockTab tab, float hardness, float resistance) {
        return register(name, tab, new BasicCaveBlock(Material.WOOD, hardness, resistance, SoundType.WOOD));
    }

    private static BlockEntry edible(String name, CaveBlockTab tab, float hardness, float resistance, SoundType sound, ACItemRegistry.Food food) {
        return edibleRegister(name, tab, new BasicCaveBlock(Material.CAKE, hardness, resistance, sound), food);
    }

    private static BlockEntry ediblePillar(String name, CaveBlockTab tab, float hardness, float resistance, SoundType sound, ACItemRegistry.Food food) {
        return edibleRegister(name, tab, new BasicPillarBlock(Material.CAKE, hardness, resistance, sound), food);
    }

    private static BlockEntry rockCandy(String name) {
        return edible(name, CaveBlockTab.CANDY_CAVITY, 1.0F, 1.5F, SoundType.STONE, candyFood(2, 0.1F, 0.01F));
    }

    private static ACItemRegistry.Food candyFood(int healAmount, float saturation, float effectChance) {
        return ACItemRegistry.Food.of(healAmount, saturation).effect(new PotionEffect(ACEffectRegistry.SUGAR_RUSH, 200), effectChance);
    }

    private static ACItemRegistry.Food licorootFood(int healAmount) {
        return ACItemRegistry.Food.of(healAmount, 0.1F).effect(new PotionEffect(MobEffects.NAUSEA, 200), 0.1F);
    }

    private static BlockEntry plantLike(String name, CaveBlockTab tab, float hardness, float resistance) {
        return register(name, tab, new BasicCaveBlock(Material.PLANTS, hardness, resistance, SoundType.PLANT));
    }

    private static BlockEntry pillar(String name, CaveBlockTab tab, float hardness, float resistance, SoundType sound) {
        return register(name, tab, new BasicPillarBlock(Material.ROCK, hardness, resistance, sound));
    }

    private static BlockEntry directional(String name, CaveBlockTab tab, Material material, float hardness, float resistance, SoundType sound) {
        return register(name, tab, new DirectionalFacingBlock(material, hardness, resistance, sound, true));
    }

    private static BlockEntry woodPillar(String name, CaveBlockTab tab, float hardness, float resistance) {
        return register(name, tab, new BasicPillarBlock(Material.WOOD, hardness, resistance, SoundType.WOOD));
    }

    private static BlockEntry glowingPillar(String name, CaveBlockTab tab, float hardness, float resistance, SoundType sound, float light) {
        return register(name, tab, new BasicPillarBlock(Material.ROCK, hardness, resistance, sound).light(light));
    }

    private static BlockEntry energizedGalena(String name) {
        return register(name, CaveBlockTab.MAGNETIC_CAVES, new BasicPillarBlock(Material.ROCK, 3.0F, 10.0F, SoundType.STONE).light(0.3125F));
    }

    private static BlockEntry falling(String name, CaveBlockTab tab, float hardness, float resistance, SoundType sound) {
        return register(name, tab, new BasicFallingBlock(Material.SAND, hardness, resistance, sound));
    }

    private static BlockEntry ore(String name, CaveBlockTab tab, float hardness, float resistance, SoundType sound, Item drop, int minDrop, int maxDrop, int minExperience, int maxExperience) {
        return register(name, tab, new BasicOreBlock(hardness, resistance, sound, drop, minDrop, maxDrop, minExperience, maxExperience));
    }

    private static BlockEntry hazmat(String name) {
        return register(name, CaveBlockTab.TOXIC_CAVES, new HazmatBlock());
    }

    private static BlockEntry lamp(String name, CaveBlockTab tab) {
        return register(name, tab, new BasicCaveBlock(Material.GLASS, 2.0F, 11.0F, SoundType.GLASS).light(1.0F));
    }

    private static BlockEntry guanoLayer(String name) {
        ResourceLocation id = new ResourceLocation(AlexsCaves.MODID, name);
        Block block = new GuanoLayerBlock();
        block.setRegistryName(id);
        block.setTranslationKey(AlexsCaves.MODID + "." + name);
        block.setCreativeTab(CaveBlockTab.FORLORN_HOLLOWS.creativeTab());
        Item item = new GuanoLayerItem(block).setRegistryName(id);
        item.setTranslationKey(AlexsCaves.MODID + "." + name);
        item.setCreativeTab(CaveBlockTab.FORLORN_HOLLOWS.creativeTab());
        BlockEntry entry = new BlockEntry(name, CaveBlockTab.FORLORN_HOLLOWS, block, item);
        MUTABLE_BLOCKS.add(entry);
        return entry;
    }

    private static BlockEntry stairs(String name, CaveBlockTab tab, BlockEntry base) {
        return register(name, tab, new BasicStairsBlock(base.block().getDefaultState()));
    }

    private static BlockEntry edibleStairs(String name, CaveBlockTab tab, BlockEntry base, ACItemRegistry.Food food) {
        BasicStairsBlock block = new BasicStairsBlock(base.block().getDefaultState());
        block.setCreativeTab(tab.creativeTab());
        return edibleRegister(name, tab, block, food);
    }

    private static BlockEntry wall(String name, CaveBlockTab tab, float hardness, float resistance, SoundType sound) {
        return register(name, tab, new BasicWallBlock(Material.ROCK, hardness, resistance, sound));
    }

    private static BlockEntry edibleWall(String name, CaveBlockTab tab, float hardness, float resistance, SoundType sound, ACItemRegistry.Food food) {
        return edibleRegister(name, tab, new BasicWallBlock(Material.CAKE, hardness, resistance, sound), food);
    }

    private static BlockEntry fence(String name, CaveBlockTab tab, float hardness, float resistance) {
        return register(name, tab, new BasicFenceBlock(Material.WOOD, hardness, resistance, SoundType.WOOD));
    }

    private static BlockEntry pressurePlate(String name, CaveBlockTab tab) {
        return register(name, tab, new BasicPressurePlateBlock(0.5F, 0.5F));
    }

    private static BlockEntry fenceGate(String name, CaveBlockTab tab) {
        return register(name, tab, new BasicFenceGateBlock(2.0F, 3.0F));
    }

    private static BlockEntry trapdoor(String name, CaveBlockTab tab) {
        return register(name, tab, new BasicTrapDoorBlock(3.0F));
    }

    private static BlockEntry button(String name, CaveBlockTab tab) {
        return register(name, tab, new BasicButtonBlock(0.5F, 0.5F));
    }

    private static BlockEntry door(String name, CaveBlockTab tab) {
        ResourceLocation id = new ResourceLocation(AlexsCaves.MODID, name);
        Block block = new BasicDoorBlock(3.0F);
        block.setRegistryName(id);
        block.setTranslationKey(AlexsCaves.MODID + "." + name);
        block.setCreativeTab(tab.creativeTab());
        Item item = new ItemDoor(block).setRegistryName(id);
        item.setTranslationKey(AlexsCaves.MODID + "." + name);
        item.setCreativeTab(tab.creativeTab());
        BlockEntry entry = new BlockEntry(name, tab, block, item);
        MUTABLE_BLOCKS.add(entry);
        return entry;
    }

    private static BlockEntry sign(String name, String woodName, CaveBlockTab tab) {
        ResourceLocation id = new ResourceLocation(AlexsCaves.MODID, name);
        CaveStandingSignBlock standing = new CaveStandingSignBlock(woodName);
        CaveWallSignBlock wall = new CaveWallSignBlock(woodName);
        standing.setRegistryName(id);
        standing.setTranslationKey(AlexsCaves.MODID + "." + name);
        standing.setCreativeTab(tab.creativeTab());
        wall.setRegistryName(new ResourceLocation(AlexsCaves.MODID, woodName + "_wall_sign"));
        wall.setTranslationKey(AlexsCaves.MODID + "." + name);
        wall.setCreativeTab(null);
        Item item = new CaveSignItem(standing, wall).setRegistryName(id);
        item.setTranslationKey(AlexsCaves.MODID + "." + name);
        item.setCreativeTab(tab.creativeTab());
        BlockEntry entry = new BlockEntry(name, tab, standing, item);
        MUTABLE_BLOCKS.add(entry);
        MUTABLE_AUXILIARY_BLOCKS.add(wall);
        return entry;
    }

    private static BlockEntry slab(String name, CaveBlockTab tab, Material material, float hardness, float resistance, SoundType sound) {
        ResourceLocation id = new ResourceLocation(AlexsCaves.MODID, name);
        BasicSlabBlock.Half half = new BasicSlabBlock.Half(material, hardness, resistance, sound);
        BasicSlabBlock.Double doubleSlab = new BasicSlabBlock.Double(material, hardness, resistance, sound);
        half.singleSlab(half);
        doubleSlab.singleSlab(half);
        half.setRegistryName(id);
        half.setTranslationKey(AlexsCaves.MODID + "." + name);
        half.setCreativeTab(tab.creativeTab());
        doubleSlab.setRegistryName(new ResourceLocation(AlexsCaves.MODID, "double_" + name));
        doubleSlab.setTranslationKey(AlexsCaves.MODID + "." + name);
        ItemSlab item = (ItemSlab) new ItemSlab(half, half, doubleSlab).setRegistryName(id);
        item.setTranslationKey(AlexsCaves.MODID + "." + name);
        item.setCreativeTab(tab.creativeTab());
        BlockEntry entry = new BlockEntry(name, tab, half, item);
        MUTABLE_BLOCKS.add(entry);
        MUTABLE_AUXILIARY_BLOCKS.add(doubleSlab);
        return entry;
    }

    private static BlockEntry edibleSlab(String name, CaveBlockTab tab, Material material, float hardness, float resistance, SoundType sound, ACItemRegistry.Food food) {
        ResourceLocation id = new ResourceLocation(AlexsCaves.MODID, name);
        BasicSlabBlock.Half half = new BasicSlabBlock.Half(material, hardness, resistance, sound);
        BasicSlabBlock.Double doubleSlab = new BasicSlabBlock.Double(material, hardness, resistance, sound);
        half.singleSlab(half);
        doubleSlab.singleSlab(half);
        half.setRegistryName(id);
        half.setTranslationKey(AlexsCaves.MODID + "." + name);
        half.setCreativeTab(tab.creativeTab());
        doubleSlab.setRegistryName(new ResourceLocation(AlexsCaves.MODID, "double_" + name));
        doubleSlab.setTranslationKey(AlexsCaves.MODID + "." + name);
        Item item = new EdibleSlabItem(half, half, doubleSlab, food).setRegistryName(id);
        item.setTranslationKey(AlexsCaves.MODID + "." + name);
        item.setCreativeTab(tab.creativeTab());
        BlockEntry entry = new BlockEntry(name, tab, half, item);
        MUTABLE_BLOCKS.add(entry);
        MUTABLE_AUXILIARY_BLOCKS.add(doubleSlab);
        return entry;
    }

    private static BlockEntry frostmint(String name) {
        ResourceLocation id = new ResourceLocation(AlexsCaves.MODID, name);
        FrostmintBlock.Half half = new FrostmintBlock.Half();
        FrostmintBlock.Double doubleSlab = new FrostmintBlock.Double();
        half.singleSlab(half);
        doubleSlab.singleSlab(half);
        half.setRegistryName(id);
        half.setTranslationKey(AlexsCaves.MODID + "." + name);
        half.setCreativeTab(CaveBlockTab.CANDY_CAVITY.creativeTab());
        doubleSlab.setRegistryName(new ResourceLocation(AlexsCaves.MODID, "double_" + name));
        doubleSlab.setTranslationKey(AlexsCaves.MODID + "." + name);
        Item item = new EdibleSlabItem(half, half, doubleSlab, candyFood(3, 0.1F, 0.01F)).setRegistryName(id);
        item.setTranslationKey(AlexsCaves.MODID + "." + name);
        item.setCreativeTab(CaveBlockTab.CANDY_CAVITY.creativeTab());
        FROSTMINT_DOUBLE = doubleSlab;
        BlockEntry entry = new BlockEntry(name, CaveBlockTab.CANDY_CAVITY, half, item);
        MUTABLE_BLOCKS.add(entry);
        MUTABLE_AUXILIARY_BLOCKS.add(doubleSlab);
        return entry;
    }

    private static BlockEntry edibleRegister(String name, CaveBlockTab tab, Block block, ACItemRegistry.Food food) {
        ResourceLocation id = new ResourceLocation(AlexsCaves.MODID, name);
        block.setRegistryName(id);
        block.setTranslationKey(AlexsCaves.MODID + "." + name);
        block.setCreativeTab(tab.creativeTab());
        Item item = new EdibleBlockItem(block, food).setRegistryName(id);
        item.setTranslationKey(AlexsCaves.MODID + "." + name);
        item.setCreativeTab(tab.creativeTab());
        BlockEntry entry = new BlockEntry(name, tab, block, item);
        MUTABLE_BLOCKS.add(entry);
        return entry;
    }

    private static BlockEntry sprinkles(String name, CaveBlockTab tab) {
        ResourceLocation id = new ResourceLocation(AlexsCaves.MODID, name);
        Block block = new SprinklesBlock();
        block.setRegistryName(id);
        block.setTranslationKey(AlexsCaves.MODID + "." + name);
        block.setCreativeTab(tab.creativeTab());
        Item item = new SprinklesItem(block, candyFood(1, 0.1F, 0.01F)).setRegistryName(id);
        item.setTranslationKey(AlexsCaves.MODID + "." + name);
        item.setCreativeTab(tab.creativeTab());
        BlockEntry entry = new BlockEntry(name, tab, block, item);
        MUTABLE_BLOCKS.add(entry);
        return entry;
    }

    private static BlockEntry register(String name, CaveBlockTab tab, Block block) {
        ResourceLocation id = new ResourceLocation(AlexsCaves.MODID, name);
        block.setRegistryName(id);
        block.setTranslationKey(AlexsCaves.MODID + "." + name);
        block.setCreativeTab(tab.creativeTab());
        ItemBlock item = (ItemBlock) new ItemBlock(block).setRegistryName(id);
        item.setTranslationKey(AlexsCaves.MODID + "." + name);
        item.setCreativeTab(tab.creativeTab());
        BlockEntry entry = new BlockEntry(name, tab, block, item);
        MUTABLE_BLOCKS.add(entry);
        return entry;
    }

    private static BlockEntry blockOnly(String name, CaveBlockTab tab, Block block) {
        ResourceLocation id = new ResourceLocation(AlexsCaves.MODID, name);
        block.setRegistryName(id);
        block.setTranslationKey(AlexsCaves.MODID + "." + name);
        block.setCreativeTab(null);
        BlockEntry entry = new BlockEntry(name, tab, block, null);
        MUTABLE_BLOCKS.add(entry);
        return entry;
    }

    private static BlockEntry scaffolding(String name, CaveBlockTab tab) {
        ResourceLocation id = new ResourceLocation(AlexsCaves.MODID, name);
        Block block = new MetalScaffoldingBlock();
        block.setRegistryName(id);
        block.setTranslationKey(AlexsCaves.MODID + "." + name);
        block.setCreativeTab(tab.creativeTab());
        ItemBlock item = (ItemBlock) new MetalScaffoldingItem(block).setRegistryName(id);
        item.setTranslationKey(AlexsCaves.MODID + "." + name);
        item.setCreativeTab(tab.creativeTab());
        BlockEntry entry = new BlockEntry(name, tab, block, item);
        MUTABLE_BLOCKS.add(entry);
        return entry;
    }

    private static BlockEntry fluid(String name, CaveBlockTab tab, Block block) {
        ResourceLocation id = new ResourceLocation(AlexsCaves.MODID, name);
        block.setRegistryName(id);
        block.setTranslationKey(AlexsCaves.MODID + "." + name);
        BlockEntry entry = new BlockEntry(name, tab, block, null);
        MUTABLE_BLOCKS.add(entry);
        return entry;
    }

    public enum CaveBlockTab {
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
                    throw new IllegalStateException("Unhandled cave block tab: " + this);
            }
        }
    }

    public static class BlockEntry {
        private final String name;
        private final CaveBlockTab tab;
        private final Block block;
        private final Item item;

        private BlockEntry(String name, CaveBlockTab tab, Block block, Item item) {
            this.name = name;
            this.tab = tab;
            this.block = block;
            this.item = item;
        }

        public String name() {
            return name;
        }

        public CaveBlockTab tab() {
            return tab;
        }

        public Block block() {
            return block;
        }

        public Item item() {
            return item;
        }

        public boolean hasItem() {
            return item != null;
        }
    }

    public static Item signItemFor(String woodName) {
        return "thornwood".equals(woodName) ? THORNWOOD_SIGN.item() : PEWEN_SIGN.item();
    }
}
