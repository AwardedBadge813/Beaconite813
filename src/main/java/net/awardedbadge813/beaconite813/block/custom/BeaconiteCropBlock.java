package net.awardedbadge813.beaconite813.block.custom;

import net.awardedbadge813.beaconite813.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BeaconiteCropBlock extends CropBlock {
    public static final int MAX_AGE = 4;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 4);
    private static final VoxelShape[] SHAPE_BY_AGE =
            new VoxelShape[]{
                    Block.box(0.0F, 0.0F, 0.0F, 16.0F, 2.0F, 16.0F),
                    Block.box(0.0F, 0.0F, 0.0F, 16.0F, 4.0F, 16.0F),
                    Block.box(0.0F, 0.0F, 0.0F, 16.0F, 6.0F, 16.0F),
                    Block.box(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F),
                    Block.box(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F)
    };

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE_BY_AGE[this.getAge(state)];
    }

    public BeaconiteCropBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
       builder.add(AGE);
    }

    @Override
    protected @NotNull ItemLike getBaseSeedId() {
        return ModItems.BEACONITE_SEED.get();
    }

    @Override
    public @NotNull IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }
}
