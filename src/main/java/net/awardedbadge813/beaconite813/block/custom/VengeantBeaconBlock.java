package net.awardedbadge813.beaconite813.block.custom;

import com.mojang.serialization.MapCodec;
import net.awardedbadge813.beaconite813.entity.ModBlockEntities;
import net.awardedbadge813.beaconite813.entity.VengeantBeaconBlockEntity;
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

public class VengeantBeaconBlock extends BaseEntityBlock {
    public VengeantBeaconBlock(Properties properties) {
        super(properties);
    }
    public static final MapCodec<VengeantBeaconBlock> CODEC = simpleCodec(VengeantBeaconBlock::new);

    public @NotNull MapCodec<VengeantBeaconBlock> codec() {
        return CODEC;
    }



    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new VengeantBeaconBlockEntity(pos, state);
    }

    @Override
    protected void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(state.getBlock()!=newState.getBlock()) {
            level.updateNeighbourForOutputSignal(pos, this);
        }

    }

    // this beacon is not called the infernal beacon because it just sounds better.
    // I see no reason to refactor everything just to change the name, everything in the code checks out.
    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        if(Screen.hasShiftDown()){
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.vengeant_beacon1.tooltip"));
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.vengeant_beacon2.tooltip"));
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.vengeant_beacon3.tooltip"));
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.vengeant_beacon4.tooltip"));
        }
        if (Screen.hasControlDown()){
            tooltipComponents.add(Component.translatable("item.beaconite813.wrath_effect_icon"));
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.wrath_effect.tooltip"));
        }
        if(!Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.shift.tooltip"));
        }
        if(!Screen.hasControlDown()) {
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.ctrl.tooltip"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        if(level.isClientSide()) {
            return null;
        }

        return createTickerHelper(blockEntityType, ModBlockEntities.VENGEANT_BEACON_BE.get(),
                (level1, blockPos, blockState, blockEntity) -> blockEntity.tick(level1, blockPos, blockState));

    }

    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }
}
