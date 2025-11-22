package net.awardedbadge813.beaconite813.block;

import net.awardedbadge813.beaconite813.beaconite813;


import net.awardedbadge813.beaconite813.block.custom.ConstructorBlock;
import net.awardedbadge813.beaconite813.block.custom.RefineryBlock;
import net.awardedbadge813.beaconite813.block.custom.UnstableBeaconBlock;
import net.awardedbadge813.beaconite813.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;


import java.util.function.Supplier;



public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(beaconite813.MOD_ID);

    /*public static final DeferredBlock<Block> REFINERY =registerBlock("refinery",
            () -> new Block(BlockBehaviour.Properties.of().strength(2f).sound(SoundType.STONE).noOcclusion()));
*/

    public static final DeferredBlock<Block> BEACONITEBLOCK =
            registerBlock("beaconite_block",
                    () -> new
                            Block(BlockBehaviour.Properties.of()
                            .strength(5f, 120f)
                            .sound(SoundType.GLASS)
                            .noOcclusion().requiresCorrectToolForDrops()
                    )
            );
    public static final DeferredBlock<Block> CONDENSEDBEACONITE =
            registerBlock("condensed_beaconite",
                    () -> new
                            Block(BlockBehaviour.Properties.of()
                            .strength(10f, 120f)
                            .sound(SoundType.METAL).requiresCorrectToolForDrops()
                    )
            );
    public static final DeferredBlock<Block> BEACONITEGLASS =
            registerBlock("reinforced_glass",
                    () -> new
                            TransparentBlock(BlockBehaviour.Properties.of()
                            .strength(2f, 120f)
                            .sound(SoundType.GLASS)
                            .noOcclusion().requiresCorrectToolForDrops()
                    )
            );
    public static final DeferredBlock<Block> ULTRA_DENSE_BEACONITE =
            registerBlock("ultra_dense_beaconite",
                    () -> new
                            TransparentBlock(BlockBehaviour.Properties.of()
                            .strength(20f, 120f)
                            .sound(SoundType.METAL).requiresCorrectToolForDrops()
                    )
            );







    //Block Entities

    public static final DeferredBlock<Block> REFINERY =registerBlock("refinery",
            () -> new RefineryBlock(BlockBehaviour.Properties.of().strength(5f).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> UNSTABLE_BEACON =registerBlock("unstable_beacon",
            () -> new UnstableBeaconBlock(BlockBehaviour.Properties.of().strength(20f).sound(SoundType.GLASS).noOcclusion().requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> CONSTRUCTOR =registerBlock("constructor",
            () -> new ConstructorBlock(BlockBehaviour.Properties.of().strength(5f).sound(SoundType.GLASS).noOcclusion().requiresCorrectToolForDrops()));




    //below is the setup for creating new block. when making a new block use the above.

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }


    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, ()->new BlockItem(block.get(), new Item.Properties()));
    }



    public static void register(IEventBus eventbus) {
        BLOCKS.register(eventbus);
    }
}
