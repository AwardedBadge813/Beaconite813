package net.awardedbadge813.beaconite813.block.custom;

import net.awardedbadge813.beaconite813.Fluids.ModFluidTypes;
import net.awardedbadge813.beaconite813.Fluids.ModFluids;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import org.jetbrains.annotations.Nullable;

public class ZwoopBlock extends LiquidBlock {
    public ZwoopBlock(BaseFlowingFluid.Source source, Properties properties) {
        super(source, properties);
    }

    @Override
    protected boolean canBeReplaced(BlockState state, Fluid fluid) {
        return true;
    }
    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        return true;
    }



}
