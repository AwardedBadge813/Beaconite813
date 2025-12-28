package net.awardedbadge813.beaconite813.block;

import net.awardedbadge813.beaconite813.beaconite813;


import net.awardedbadge813.beaconite813.block.custom.*;
import net.awardedbadge813.beaconite813.item.ModItems;
import net.awardedbadge813.beaconite813.item.ToggleableItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;


import java.util.List;
import java.util.function.Supplier;



public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(beaconite813.MOD_ID);

    public static final DeferredBlock<Block> BEACONITE_BLOCK =
            registerBlock("beaconite_block",
                    () -> new
                            Block(BlockBehaviour.Properties.of()
                            .strength(5f, 120f)
                            .sound(SoundType.GLASS)
                            .noOcclusion().requiresCorrectToolForDrops()
                    )
            );
    public static final DeferredBlock<Block> CONDENSED_BEACONITE =
            registerBlock("condensed_beaconite",
                    () -> new
                            Block(BlockBehaviour.Properties.of()
                            .strength(10f, 120f)
                            .sound(SoundType.METAL).requiresCorrectToolForDrops()
                    )
            );
    public static final DeferredBlock<Block> BEACONITE_GLASS =
            registerBlock("reinforced_glass",
                    () -> new
                            TransparentBlock(BlockBehaviour.Properties.of()
                            .strength(2f, 120f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()
                            .requiresCorrectToolForDrops()

                    ) {
                                @Override
                                protected boolean propagatesSkylightDown(BlockState p_309084_, BlockGetter p_309133_, BlockPos p_309097_) {
                                    return true;
                                }
                            }
            );
    public static final DeferredBlock<Block> ULTRA_DENSE_BEACONITE =
            registerBlock("ultra_dense_beaconite",
                    () -> new
                            Block(BlockBehaviour.Properties.of()
                            .strength(20f, 120f)
                            .sound(SoundType.METAL).requiresCorrectToolForDrops()
                    )
            );
    public static final DeferredBlock<Block> LIVING_BLOCK =
            registerBlock("living_block",
                    () -> new
                            Block(BlockBehaviour.Properties.of()
                            .strength(0.5f)
                            .sound(SoundType.MUD)
                    )
            );
    public static final DeferredBlock<Block> POLYMORPH_BEACONITE =
            registerBlock("polymorph_block",
                    () -> new
                            Block(BlockBehaviour.Properties.of()
                            .strength(8f)
                            .sound(SoundType.METAL)
                    )
            );
    public static final DeferredBlock<Block> ENGORGED_HEART =
            registerBlock("engorged_heart",
                    () -> new
                            Block(BlockBehaviour.Properties.of()
                            .strength(4f)
                            .sound(SoundType.MUD)
                    )
            );
    public static final DeferredBlock<Block> BLAZING_MAGMA =
            registerBlock("blazing_magma",
                    () -> new
                            Block(BlockBehaviour.Properties.of()
                            .strength(2f)
                            .sound(SoundType.NETHERRACK)
                    )
            );
    public static final DeferredBlock<Block> INFUSED_OBSIDIAN =
            registerBlock("infused_obsidian",
                    () -> new
                            Block(BlockBehaviour.Properties.of()
                            .strength(80f, 10000000f)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops()
                    )
            );
    public static final DeferredBlock<Block> DORMANT_EGG =
            registerBlock("dormant_egg",
                    () -> new
                            Block(BlockBehaviour.Properties.of()
                            .strength(8f, 10000f)
                            .sound(SoundType.STONE)
                            .noOcclusion()
                    )
            );
    public static final DeferredBlock<Block> ANTIMATTER_BLOCK =
            registerBlock("antimatter_block",
                    () -> new
                            Block(BlockBehaviour.Properties.of()
                            .instabreak()
                            .sound(SoundType.HEAVY_CORE)
                    )
            );
    public static final DeferredBlock<Block> WRATHFUL_FLESH =
            registerBlock("wrathful_flesh",
                    () -> new
                            Block(BlockBehaviour.Properties.of()
                            .strength(0.5f)
                            .sound(SoundType.SCULK)
                    ){
                                @Override
                                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                                    tooltipComponents.add(Component.translatable("tooltip.beaconite813.wrath_flesh.tooltip"));
                                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                                }
                            }
            );
    public static final DeferredBlock<Block> COINS_BLOCK =
            registerBlock("coins_block",
                    () -> new
                            Block(BlockBehaviour.Properties.of()
                                    .strength(0.5f)
                                    .sound(SoundType.CHAIN)
                            ));









    //Block Entities

    public static final DeferredBlock<Block> REFINERY =registerBlock("refinery",
            () -> new RefineryBlock(BlockBehaviour.Properties.of().strength(5f).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> UNSTABLE_BEACON =registerBlock("unstable_beacon",
            () -> new UnstableBeaconBlock(BlockBehaviour.Properties.of().strength(20f).sound(SoundType.GLASS).noOcclusion().requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> CONSTRUCTOR =registerBlock("constructor",
            () -> new ConstructorBlock(BlockBehaviour.Properties.of().strength(5f).sound(SoundType.GLASS).noOcclusion().requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> BASE_BEACON_BLOCK =registerBlock("base_beacon_block",
            () -> new BaseBeaconBlock(BlockBehaviour.Properties.of().strength(5f).sound(SoundType.MUD).noOcclusion().requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> BEACONITE_CROP =BLOCKS.register("beaconite_crop",
            () -> new BeaconiteCropBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<Block> VENGEANT_BEACON_BLOCK =registerBlock("vengeant_beacon",
            () -> new VengeantBeaconBlock(BlockBehaviour.Properties.of().strength(5f).sound(SoundType.GLASS).noOcclusion().requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> IGNEOUS_BEACON_BLOCK =registerBlock("igneous_beacon",
            () -> new IgneousBeaconBlock(BlockBehaviour.Properties.of().strength(5f).sound(SoundType.MUD).noOcclusion().requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> REGAL_BEACON_BLOCK =registerBlock("regal_beacon",
            () -> new RegalBeaconBlock(BlockBehaviour.Properties.of().strength(5f).sound(SoundType.CHAIN).noOcclusion().requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> LIVING_BEACON =registerBlock("living_beacon",
            () -> new LivingBeaconBlock(BlockBehaviour.Properties.of().strength(5f).sound(SoundType.MUD).noOcclusion().requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> NEGATIVE_BEACON_BLOCK =registerBlock("negative_beacon",
            () -> new NegativeBeaconBlock(BlockBehaviour.Properties.of().strength(5f).sound(SoundType.HEAVY_CORE).noOcclusion().requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> AMORPH_BEACON_BLOCK =registerBlock("amorph_beacon",
            () -> new AmorphousBeaconBlock(BlockBehaviour.Properties.of().strength(5f).sound(SoundType.GLASS).noOcclusion().requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> ETHER_BEACON_BLOCK =registerBlock("ethereal_beacon",
            () -> new EtherealBeaconBlock(BlockBehaviour.Properties.of().strength(5f).sound(SoundType.AMETHYST_CLUSTER).noOcclusion().requiresCorrectToolForDrops()));




    //below is the setup for creating new block. when making a new block use the above.

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }


    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, ()->new ToggleableBlockItem(block.get(), new Item.Properties()));
    }



    public static void register(IEventBus eventbus) {
        BLOCKS.register(eventbus);
    }
}
