package net.awardedbadge813.beaconite813.block.custom;

import com.mojang.serialization.MapCodec;
import net.awardedbadge813.beaconite813.entity.ModBlockEntities;
import net.awardedbadge813.beaconite813.entity.NegativeBeaconBlockEntity;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class NegativeBeaconBlock extends BaseEntityBlock {
    public NegativeBeaconBlock(Properties properties) {
        super(properties);
    }
    public static final MapCodec<NegativeBeaconBlock> CODEC = simpleCodec(NegativeBeaconBlock::new);

    public @NotNull MapCodec<NegativeBeaconBlock> codec() {
        return CODEC;
    }



    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new NegativeBeaconBlockEntity(pos, state);
    }

    @Override
    protected void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(state.getBlock()!=newState.getBlock()) {
            level.removeBlockEntity(pos);
            level.updateNeighbourForOutputSignal(pos, this);
        }
    }
//
    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.beaconite813.negative_beacon_joke.tooltip"));
        if(Screen.hasShiftDown()){
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.negative_beacon1.tooltip"));
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.negative_beacon2.tooltip"));
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.negative_beacon3.tooltip"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.shift.tooltip"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        if(level.isClientSide()) {
            return null;
        }

        return createTickerHelper(blockEntityType, ModBlockEntities.NEGATIVE_BEACON_BE.get(),
                (level1, blockPos, blockState, blockEntity) -> blockEntity.tick(level1, blockPos, blockState));

    }

    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }
}
