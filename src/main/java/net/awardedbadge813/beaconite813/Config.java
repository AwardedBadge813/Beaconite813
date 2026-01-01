package net.awardedbadge813.beaconite813;


import net.awardedbadge813.beaconite813.util.BeaconiteLib;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.ModConfigSpec;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue REFINERY_COOK_TIME = BUILDER
            .comment("The time it takes a refinery to craft an item. More layers below the refinery divide this time.")
            .defineInRange("refineryCookTime", 10000, 0, Integer.MAX_VALUE);



    public static final ModConfigSpec.IntValue MAX_LEVEL_UNSTABLE_BEACON = BUILDER
            .comment("The amount of levels required to activate an unstable beacon. default is 5.")
            .defineInRange("maxLevelUnstableBeacon", 5, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.BooleanValue UNSTABLE_BEACON_SEES_SKY = BUILDER
            .comment("Whether the unstable beacon will require air blocks above to sky height in order to activate/accept tribute.")
            .define("unstableBeaconSeesSky", true);
    public static final ModConfigSpec.IntValue TIME_EXPLODE = BUILDER
            .comment("The time it takes unstable beacons to explode.")
            .defineInRange("timeToExplode", 400, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue MAX_EXPLODE_RADIUS = BUILDER
            .comment("maximum explosion radius of all exploding events in the mod. recommended not to increase.")
            .defineInRange("MaxExplodeRadius", 100, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.BooleanValue BOMBS_DESTROY_BLOCKS = BUILDER
            .comment("Whether the unstable beacon will destroy blocks. recommended false for servers.")
            .define("BombsDestroyBlocks", true);
    public static final ModConfigSpec.IntValue EXPLOSION_RADIUS = BUILDER
            .comment("The range that unstable beacons and other explosions in this mod will inherit. WARNING: too high of a number may crash your game.")
            .defineInRange("explosionRadius", 25, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue EXPLOSION_NOISE = BUILDER
            .comment("The chance for blocks to be destroyed in the noiseMarked range(extra 5 blocks from the outer radius). Default is 40.")
            .defineInRange("noiseMarkChance", 40, 0, 100);


    public static final ModConfigSpec.IntValue MAX_BLOCK_POTION_TIME = BUILDER
            .comment("The maximum potion time ethereal beacons can hold, additional time after this is truncated. Default is 1 hour.")
            .defineInRange("maxEtherealBeaconTime", 72000, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue MAX_HELD_POTION_TIME = BUILDER
            .comment("The maximum time ethereal potions can get from the ethereal beacon. default is 1 hour.")
            .defineInRange("maxEtherealPotionTime", 72000, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue AURA_DIFFUSE_RADIUS = BUILDER
            .comment("The maximum radius of the ethereal beacon for aura and diffusion effects. default is 20 blocks.")
            .defineInRange("auraDiffuseRadius", 20, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue MAX_GOLD_TIME = BUILDER
            .comment("The maximum time the gold effect can be stored in the Regal Beacon. default is 30 minutes.")
            .defineInRange("maxGoldTime", 36000, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue MAX_LOOT_TIME = BUILDER
            .comment("The maximum time the extra loot effect can be stored in the Regal Beacon. default is 30 minutes.")
            .defineInRange("maxLootTime", 36000, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue GOLD_TIME_MOD = BUILDER
            .comment("multiplier for time given by each gold item in the Regal Beacon. Default is 1. a zero will disable gold input.")
            .defineInRange("goldTimeMultiplier", 1, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue LOOT_TIME_MOD = BUILDER
            .comment("multiplier for time given by each bonus loot item in the Regal Beacon. Default is 1. a zero will disable bonus loot item input.")
            .defineInRange("lootTimeMultiplier", 1, 0, Integer.MAX_VALUE);


    public static final ModConfigSpec.BooleanValue DEV_MODE = BUILDER
            .comment("Activates dev mode, making constructors work without need for items.")
            .define("devMode", false);

    public static final ModConfigSpec.IntValue MAX_LEVEL_BEACON = BUILDER
            .comment("The max level of all beacon pyramids in the game(except constructors). default is 20.")
            .defineInRange("maxBeaconPyramidSize", 20, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.BooleanValue MASTER_DESTROY_TOGGLE = BUILDER
            .comment("stops all block destruction in the mod, except for BlockState updates. may break some things like constructors.")
            .define("masterDestroyToggle", false);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> GOLD_TRANSFORMATIONS = BUILDER
            .comment("The amount of gold time each gold-based item gives. default are vanilla items and golden apples.")
            .defineListAllowEmpty("goldMappingList", List.of("minecraft:gold_nugget|10", "minecraft:gold_ingot|100", "minecraft:gold_block|1000", "minecraft:golden_apple|800", "minecraft:enchanted_golden_apple|10000"), () -> "", Config::validateMapping);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> LOOT_TRANSFORMATIONS = BUILDER
            .comment("The amount of extra loot time each item gives. default are vanilla gems that cannot be easily automated(besides emeralds and stars).")
            .defineListAllowEmpty("lootMappingList", List.of("minecraft:lapis_lazuli|10", "minecraft:lapis_block|100", "minecraft:diamond|100", "minecraft:diamond_block|1000", "minecraft:emerald|10", "minecraft:emerald_block|100", "minecraft:nether_star|1000"), () -> "", Config::validateMapping);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> MIDAS_ROT_LOOT_AMOUNTS = BUILDER
            .comment("The max amount of items that can be dropped from a midas rot inflicted enemy. chance is independent for each roll, so there is a normal distribution of loot drops depending on the chance and amount you pick.")
            .comment("NOTE: ALL ENTRIES PRESENT IN THE LOOT WEIGHTS HAVE TO ALSO BE PRESENT HERE, OR THE GAME WILL CRASH!.")
            .defineListAllowEmpty("midasRotLootAmounts", List.of("minecraft:gold_nugget|10", "minecraft:gold_ingot|2", "minecraft:gold_block|1", "minecraft:golden_apple|3", "minecraft:enchanted_golden_apple|1"), () -> "", Config::validateMapping);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> MIDAS_ROT_LOOT_WEIGHTS = BUILDER
            .comment("The chance out of MidasRotLootBase for each item to drop.")
            .comment("NOTE: ALL ENTRIES PRESENT IN THE LOOT AMOUNTS HAVE TO ALSO BE PRESENT HERE, OR THE GAME WILL CRASH!.")
            .defineListAllowEmpty("midasRotLootWeights", List.of("minecraft:gold_nugget|5000", "minecraft:gold_ingot|500", "minecraft:gold_block|10", "minecraft:golden_apple|100", "minecraft:enchanted_golden_apple|1"), () -> "", Config::validateMapping);

    public static final ModConfigSpec.IntValue MIDAS_ROT_LOOT_BASE = BUILDER
            .comment("The default upper bound for loot weights. this is the denominator that defines the loot chances. Default is 10,000. setting to 0 will guarantee all loot.")
            .defineInRange("MidasRotLootBase", 10000, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue ALLOW_EXTRA_ROLLS = BUILDER
            .comment("Allows extra loot rolls beyond x2 for higher levels of the hypertrophy effect. Default is true.")
            .define("extraRolls", true);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> ENABLED_ITEMS = BUILDER
            .comment("Removing an item from this list disables it, marking it in red and disabling all interactions. THIS DOES NOT REMOVE RECIPES! MAKE SURE TO REMOVE THEIR JSONS OR GET A MOD TO DO IT FOR YOU.")
            .comment("Disabling a beacon will disable its recipe in the polymorph beacon. Disabling the polymorph beacon will disable all polymorph recipes.")
            .comment("Disabling an effect icon will disable the corresponding effect i.e. it will not be applied through normal circumstances and will immediately remove itself if applied.")
            .defineListAllowEmpty("enabledItemsMaster",
                    List.of(
                            "beaconite813:dormant_bottle",
                            "beaconite813:dormant_egg",
                            "beaconite813:beaconite",
                            "beaconite813:refined_beaconite",
                            "beaconite813:pure_beaconite",
                            "beaconite813:powdered_beaconite",
                            "beaconite813:refinery_catalyst",
                            "beaconite813:beaconite_seed",
                            "beaconite813:beam_shard",
                            "beaconite813:encapsulated_beam",
                            "beaconite813:quarry_talisman",
                            "beaconite813:inversion_talisman",
                            "beaconite813:ether_filter",
                            "beaconite813:aura_module",
                            "beaconite813:diffuse_module",
                            "beaconite813:transmute_module",
                            "beaconite813:infuse_module",
                            "beaconite813:bottomless_bottle",
                            "beaconite813:beaconite_block",
                            "beaconite813:reinforced_glass",
                            "beaconite813:condensed_beaconite",
                            "beaconite813:unstable_beacon",
                            "beaconite813:refinery",
                            "beaconite813:ultra_dense_beaconite",
                            "beaconite813:constructor",
                            "beaconite813:polymorph_block",
                            "beaconite813:base_beacon_block",
                            "beaconite813:wrathful_flesh",
                            "beaconite813:blazing_magma",
                            "beaconite813:coins_block",
                            "beaconite813:living_block",
                            "beaconite813:antimatter_block",
                            "beaconite813:engorged_heart",
                            "beaconite813:infused_obsidian",
                            "beaconite813:vengeant_beacon",
                            "beaconite813:igneous_beacon",
                            "beaconite813:regal_beacon",
                            "beaconite813:living_beacon",
                            "beaconite813:negative_beacon",
                            "beaconite813:amorph_beacon",
                            "beaconite813:ethereal_beacon",
                            "beaconite813:auric_interference_icon",
                            "beaconite813:wrath_effect_icon",
                            "beaconite813:capsaicin_effect_icon",
                            "beaconite813:midas_rot_effect_icon",
                            "beaconite813:hypertrophy_effect_icon"


                    ), () -> "", Config::validateItemOrBlock);


/*
    public static final ModConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER
            .comment("Whether to log the dirt block on common setup")
            .define("logDirtBlock", true);

    public static final ModConfigSpec.IntValue MAGIC_NUMBER = BUILDER
            .comment("A magic number")
            .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);



    public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
            .comment("What you want the introduction message to be for the magic number")
            .define("magicNumberIntroduction", "The magic number is... ");

    // a list of strings that are treated as resource locations for items
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), () -> "", Config::validateItemName);



*/

    private static boolean validateItemOrBlock(final Object item) {
        boolean itemValid = item instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
        //boolean blockValid = item instanceof String itemName && BuiltInRegistries.BLOCK.containsKey(ResourceLocation.parse(itemName));
        return itemValid;
    }

    private static boolean validateMapping(final Object itemToInteger) {
        String[] split = ((String)itemToInteger).split("\\|");
        String getLeft = split[0];
        String getRight = split[1];
        boolean intValid = false;
        boolean itemValid = getLeft instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
        // quick and dirty method to check if the parsed integer is valid without risking a crash.
        // any issues with the parse and ItemValid returns false instead.
        try{
            intValid = Integer.parseInt(getRight)>0;
        } catch (Exception InvalidInteger) {
            return false;
        }
        return itemValid && intValid;
    }


    static final ModConfigSpec SPEC = BUILDER.build();
}
