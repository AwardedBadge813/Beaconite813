package net.awardedbadge813.beaconite813;

import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue REFINERYCOOKTIME = BUILDER
            .comment("The time it takes a refinery to craft an item.")
            .defineInRange("refineryCookTime", 800, 0, Integer.MAX_VALUE);



    public static final ModConfigSpec.IntValue MAXLEVELUNSTABLEBEACON = BUILDER
            .comment("The amount of levels required to activate an unstable beacon. default is 5.")
            .defineInRange("maxLevelUnstableBeacon", 5, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.BooleanValue UNSTABLEBEACOONSEESSKY = BUILDER
            .comment("Whether the unstable beacon will require air block above to sky height in order to activate/accept tribute.")
            .define("unstableBeaconSeesSky", true);
    public static final ModConfigSpec.IntValue TIME_EXPLODE = BUILDER
            .comment("The time it takes unstable beacons to explode.")
            .defineInRange("timeToExplode", 400, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.BooleanValue BOMBSDESTROYBLOCKS = BUILDER
            .comment("Whether the unstable beacon will destroy block. recommended for servers.")
            .define("BombsDestroyBlocks", true);
    public static final ModConfigSpec.IntValue EXPLOSION_RADIUS = BUILDER
            .comment("The range that unstable beacons and other explosions in this mod will inherit. WARNING: too high of a number may crash your game.")
            .defineInRange("explosionRadius", 25, 0, Integer.MAX_VALUE);


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

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }


}
