package net.awardedbadge813.beaconite813.util;

import net.awardedbadge813.beaconite813.Config;
import net.awardedbadge813.beaconite813.effect.ModEffects;
import net.awardedbadge813.beaconite813.item.ModItems;
import net.awardedbadge813.beaconite813.item.ToggleableItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class BeaconiteLib {
    public static int restrict (int operand, int lowerBound, int upperBound) {
        //i should be between j(low) and k(high)
        return min(max(operand, lowerBound), upperBound);
    }
    public static boolean safeUpdateBlock(Level level, BlockPos pos, BlockState state) {
        if (!Config.MASTER_DESTROY_TOGGLE.getAsBoolean()) {
            return level.setBlockAndUpdate(pos, state);
        }
        else return false;
    }

    public static HashMap<Item, Integer> defineConfigBasedTable(List<? extends String> list) {
        HashMap<Item, Integer> toReturn = new HashMap<>();
        for (String parseable : list) {
            ResourceLocation location = ResourceLocation.parse(parseable.split("\\|")[0]);
            Item item = BuiltInRegistries.ITEM.get(location);
            toReturn.put(item, Integer.parseInt(parseable.split("\\|")[1]));
        }
        return toReturn;
    }

    private static List<Item> getItemConfigEntries(List<? extends String> list) {
        ArrayList<Item> toReturn = new ArrayList<>();
        for (String parseable : list) {
            ResourceLocation location = ResourceLocation.parse(parseable);
            Item item = BuiltInRegistries.ITEM.get(location);
            toReturn.add(item);
        }
        return toReturn;
    }
    private static List<Item> getAllFirstConfigEntries(List<? extends String> list) {
        ArrayList<Item> toReturn = new ArrayList<>();
        for (String parseable : list) {
            ResourceLocation location = ResourceLocation.parse(parseable.split("\\|")[0]);
            Item item = BuiltInRegistries.ITEM.get(location);
            toReturn.add(item);
        }
        return toReturn;
    }
    public static final HashMap<Item, Integer> goldMap = defineConfigBasedTable(Config.GOLD_TRANSFORMATIONS.get());
    public static final HashMap<Item, Integer> lootMap = defineConfigBasedTable(Config.LOOT_TRANSFORMATIONS.get());
    //used for checking validity of inputted items.
    public static final List<Item> midasValidEntries = getAllFirstConfigEntries(Config.GOLD_TRANSFORMATIONS.get());
    public static final List<Item> hypertrophyValidEntries = getAllFirstConfigEntries(Config.LOOT_TRANSFORMATIONS.get());
    //I need these entries to determine what the items to get are.
    public static final List<Item> midasLootEntries = getAllFirstConfigEntries(Config.MIDAS_ROT_LOOT_AMOUNTS.get());
    public static final HashMap<Item, Integer> midasLootAmounts = defineConfigBasedTable(Config.MIDAS_ROT_LOOT_AMOUNTS.get());
    public static final HashMap<Item, Integer> midasLootWeights = defineConfigBasedTable(Config.MIDAS_ROT_LOOT_WEIGHTS.get());


    //defines the master toggle table.
    public static final List<Item> masterToggleTable = getItemConfigEntries(Config.ENABLED_ITEMS.get());

    public static final HashMap <Item, Holder<MobEffect>> iconTable = defineIconTable();

    public static String getDurationCalc(int ticks) {
        int ticksFinal = ticks % 20;
        int equivSecs = (ticks-ticksFinal)/20;
        int secsFinal = equivSecs%60;
        int equivMins = (equivSecs-secsFinal)/60;
        int minsFinal = equivMins%60;
        int equivHrs = (equivMins-minsFinal)/60;
        return makeTwoDigits(equivHrs)+":"+makeTwoDigits(minsFinal)+":"+makeTwoDigits(secsFinal);
    }
    private static String makeTwoDigits(int value) {
        if (value<10) {
            return "0"+value;
        } else {
            return String.valueOf(value);
        }
    }

    public static boolean effectDisabled(MobEffect effect) {
        //if any icon associated with that effect is both toggleable and disabled, the linked effect is disabled too.
        for (Item item : iconTable.keySet()) {
            if (((ToggleableItem) item).isDisabled() && iconTable.get(item).value()==effect) {
                return true;
            }
        }
        return false;
    }




    public static HashMap<Item, Holder<MobEffect>> defineIconTable() {
        HashMap<Item, Holder<MobEffect>> toReturn = new HashMap<>();
        toReturn.put(ModItems.AURIC_INTERFERENCE_ICON.get(), ModEffects.MARKED_SPLASH);
        toReturn.put(ModItems.WRATH_ICON.get(), ModEffects.WRATH);
        toReturn.put(ModItems.CAPSAICIN_ICON.get(), ModEffects.CAPSAICIN);
        toReturn.put(ModItems.MIDAS_ROT_ICON.get(), ModEffects.MIDAS_ROT);
        toReturn.put(ModItems.HYPERTROPHY_ICON.get(), ModEffects.HYPERTROPHY);
        return toReturn;
    }

}
