package net.awardedbadge813.beaconite813.item;

import net.awardedbadge813.beaconite813.beaconite813;
import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModItems {
    public static final DeferredRegister.Items ITEMS=DeferredRegister
            .createItems(beaconite813.MOD_ID);
    public static void register(IEventBus eventbus) {
        ITEMS.register(eventbus);
    }

    public static final DeferredItem<Item> BEACONITE = ITEMS.register("beaconite",
            () -> new ToggleableItem(new Item.Properties()));
    public static final DeferredItem<Item> REFINED_BEACONITE = ITEMS.register("refined_beaconite",
            () -> new ToggleableItem(new Item.Properties()));
    public static final DeferredItem<Item> PURE_BEACONITE = ITEMS.register("pure_beaconite",
            () -> new ToggleableItem(new Item.Properties()));
    public static final DeferredItem<Item> BEACON_POWDER = ITEMS.register("powdered_beaconite",
            () -> new ToggleableItem(new Item.Properties()));
    public static final DeferredItem<Item> CATALYST = ITEMS.register("refinery_catalyst",
            () -> new ToggleableItem(new Item.Properties()));
    public static final DeferredItem<Item> BEACONITE_SEED = ITEMS.register("beaconite_seed",
            () -> new ItemNameBlockItem(ModBlocks.BEACONITE_CROP.get(), new Item.Properties()));
    public static final DeferredItem<Item> BEACON_BEAM_SHARD = ITEMS.register("beam_shard",
            () -> new ToggleableItem(new Item.Properties()));
    public static final DeferredItem<Item> BEACON_BEAM_ITEM = ITEMS.register("encapsulated_beam",
            () -> new ToggleableItem(new Item.Properties()));
    public static final DeferredItem<Item> QUARRY_TALISMAN = ITEMS.register("quarry_talisman",
            () -> new ToggleableItem(new Item.Properties()));
    public static final DeferredItem<Item> INVERT_TALISMAN = ITEMS.register("inversion_talisman",
            () -> new ToggleableItem(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.beaconite813.invert_talisman_joke.tooltip"));
                    if(tooltipFlag.hasShiftDown()) {
                        tooltipComponents.add(Component.translatable("tooltip.beaconite813.invert_talisman1.tooltip"));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.beaconite813.shift.tooltip"));
                    }
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item>DORMANT_BOTTLE = ITEMS.register("dormant_bottle",
            () -> new ToggleableItem(new Item.Properties()));


    public static final DeferredItem<Item> AURA_MODULE = ITEMS.register("aura_module",
            () -> new ToggleableItem(new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if(tooltipFlag.hasShiftDown()) {
                        tooltipComponents.add(Component.translatable("tooltip.beaconite813.aura_module1.tooltip"));
                        tooltipComponents.add(Component.translatable("tooltip.beaconite813.aura_module2.tooltip"));
                        tooltipComponents.add(Component.translatable("tooltip.beaconite813.aura_module3.tooltip"));
                        tooltipComponents.add(Component.translatable("tooltip.beaconite813.aura_module4.tooltip"));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.beaconite813.shift.tooltip"));
                    }
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> DIFFUSE_MODULE = ITEMS.register("diffuse_module",
            () -> new ToggleableItem(new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if(tooltipFlag.hasShiftDown()) {
                        tooltipComponents.add(Component.translatable("tooltip.beaconite813.diffuse_module1.tooltip"));
                        tooltipComponents.add(Component.translatable("tooltip.beaconite813.diffuse_module2.tooltip"));
                        tooltipComponents.add(Component.translatable("tooltip.beaconite813.diffuse_module3.tooltip"));
                        tooltipComponents.add(Component.translatable("tooltip.beaconite813.diffuse_module4.tooltip"));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.beaconite813.shift.tooltip"));
                    }
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> TRANSMUTE_MODULE = ITEMS.register("transmute_module",
            () -> new ToggleableItem(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if(tooltipFlag.hasShiftDown()) {
                        tooltipComponents.add(Component.translatable("tooltip.beaconite813.transmute_module1.tooltip"));
                        tooltipComponents.add(Component.translatable("tooltip.beaconite813.transmute_module2.tooltip"));
                        tooltipComponents.add(Component.translatable("tooltip.beaconite813.transmute_module3.tooltip"));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.beaconite813.shift.tooltip"));
                    }
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> INFUSE_MODULE = ITEMS.register("infuse_module",
            () -> new ToggleableItem(new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if(tooltipFlag.hasShiftDown()) {
                        tooltipComponents.add(Component.translatable("tooltip.beaconite813.infuse_module1.tooltip"));
                        tooltipComponents.add(Component.translatable("tooltip.beaconite813.infuse_module2.tooltip"));
                        tooltipComponents.add(Component.translatable("tooltip.beaconite813.infuse_module3.tooltip"));
                        tooltipComponents.add(Component.translatable("tooltip.beaconite813.infuse_module4.tooltip"));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.beaconite813.shift.tooltip"));
                    }
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            } );
    public static final DeferredItem<Item> BOTTOMLESS_BOTTLE = ITEMS.register("bottomless_bottle",
            () -> new ToggleableItem(new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.beaconite813.bottomless_bottle_joke.tooltip"));
                    tooltipComponents.add(Component.translatable("tooltip.beaconite813.bottomless_bottle_joke2.tooltip"));
                    if(tooltipFlag.hasShiftDown()) {
                        tooltipComponents.add(Component.translatable("tooltip.beaconite813.bottomless_bottle1.tooltip"));
                    } else {

                        tooltipComponents.add(Component.translatable("tooltip.beaconite813.shift.tooltip"));
                    }
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }

                @Override
                public int getMaxStackSize(ItemStack stack) {
                    return 1;
                }
            });
    public static final DeferredItem<Item> ETHER_FILTER = ITEMS.register("ether_filter",
            () -> new ToggleableItem(new Item.Properties()));

    public static final DeferredItem<Item> AURIC_INTERFERENCE_ICON = ITEMS.register("auric_interference_icon",
            () -> new ToggleableItem(new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.beaconite813.auric_interference.tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> WRATH_ICON = ITEMS.register("wrath_effect_icon",
            () -> new ToggleableItem(new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.beaconite813.wrath_effect.tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> CAPSAICIN_ICON = ITEMS.register("capsaicin_effect_icon",
            () -> new ToggleableItem(new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.beaconite813.capsaicin_effect.tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> MIDAS_ROT_ICON = ITEMS.register("midas_rot_effect_icon",
            () -> new ToggleableItem(new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.beaconite813.midas_rot_effect.tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> HYPERTROPHY_ICON = ITEMS.register("hypertrophy_effect_icon",
            () -> new ToggleableItem(new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.beaconite813.hypertrophy_effect.tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });


    //public static final DeferredItem<Item> BUBBLE_WAND = ITEMS.register("bubble_wand",
     //       () -> new DeferredSpawnEggItem(ModEntities.BUBBLE, 0x81DEF7, 0x60A7F9, new Item.Properties()));





}
