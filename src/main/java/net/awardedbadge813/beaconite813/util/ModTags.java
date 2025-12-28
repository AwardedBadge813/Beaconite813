package net.awardedbadge813.beaconite813.util;

import net.awardedbadge813.beaconite813.beaconite813;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
    public static class Items {
        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, name));
        }
        public static final TagKey<Item> ETHEREAL_MODULES = createTag("ethereal_modules");
        public static final TagKey<Item> ETHEREAL_INFUSIBLE = createTag("ethereal_infusible");
    }
}
