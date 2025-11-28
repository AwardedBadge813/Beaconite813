package net.awardedbadge813.beaconite813.Datagen;

import net.awardedbadge813.beaconite813.beaconite813;
import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.awardedbadge813.beaconite813.block.custom.BeaconiteCropBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.function.Function;

public class ModBlockStateProvider  extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, beaconite813.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        BlockWithItem(ModBlocks.LIVING_BLOCK);
        BlockWithItem(ModBlocks.ULTRA_DENSE_BEACONITE);
        BlockWithItem(ModBlocks.POLYMORPH_BEACONITE);
        BlockWithItem(ModBlocks.CONDENSED_BEACONITE);

        makeCrop(((CropBlock) ModBlocks.BEACONITE_CROP.get()), "beaconite_crop_stage", "beaconite_crop_stage");


    }

    private void BlockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    //the warnings on this ae not a big deal. this can be used for more than just one type of crop, so I will not hardcode it.
    private void makeCrop(CropBlock block, String modelName, String textureName) {
        Function<BlockState, ConfiguredModel[]> function = state -> CropStates(state, block, modelName, textureName);
        getVariantBuilder(block).forAllStates(function);
    }
    private ConfiguredModel[] CropStates(BlockState state, CropBlock block, String modelName, String textureName) {
        ConfiguredModel[] models = new ConfiguredModel[1];
        models[0] = new ConfiguredModel(models().crop(modelName+state.getValue(((BeaconiteCropBlock) block).getAgeProperty()),
                ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "block/"+textureName+
                        state.getValue(((BeaconiteCropBlock) block).getAgeProperty()))).renderType("cutout"));

        return models;
    }




}
